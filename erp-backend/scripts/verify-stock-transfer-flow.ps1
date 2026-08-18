[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$TenantName,
    [string]$Username = 'admin',
    [string]$Password = 'admin',
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$Database = 'shkb_platform',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Net.Http
$baseUri = $BaseUrl.TrimEnd('/')
$parsedBaseUri = [Uri]$baseUri
if ($parsedBaseUri.Host -notin @('127.0.0.1', 'localhost')) {
    throw 'The stock-transfer write probe is restricted to a local API endpoint.'
}
if ($DbContainer -ne 'xingyun-smoke-mysql' -or $Database -ne 'shkb_platform') {
    throw 'The stock-transfer write probe is restricted to the local smoke database.'
}
if (-not $TenantName) {
    $TenantName = -join [char[]](0x4E0A, 0x6D77, 0x51EF, 0x5954, 0x822A, 0x7A7A, 0x6280, 0x672F, 0x6709, 0x9650, 0x516C, 0x53F8)
}

function Invoke-SmokeSql {
    param(
        [Parameter(Mandatory = $true)][string]$Sql,
        [switch]$ReturnOutput
    )

    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $output = $Sql | & docker exec -i $DbContainer mysql "-u$DbUsername" "-p$DbPassword" -N -B $Database 2>&1
        $exitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    $cleanOutput = @($output | Where-Object { [string]$_ -notmatch '^mysql: \[Warning\]' })
    if ($exitCode -ne 0) {
        throw "Smoke database command failed: $($cleanOutput -join [Environment]::NewLine)"
    }
    if ($ReturnOutput) { return $cleanOutput }
}

function Invoke-ErpJson {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [ValidateSet('Get', 'Post', 'Patch', 'Put', 'Delete')][string]$Method = 'Get',
        [hashtable]$Headers,
        [string]$JsonBody,
        [hashtable]$FormBody
    )

    $arguments = @{ Uri = $Uri; Method = $Method; TimeoutSec = 30 }
    if ($Headers) { $arguments.Headers = $Headers }
    if ($JsonBody) {
        $arguments.Body = $JsonBody
        $arguments.ContentType = 'application/json'
    } elseif ($FormBody) {
        $arguments.Body = $FormBody
        $arguments.ContentType = 'application/x-www-form-urlencoded'
    }
    $payload = Invoke-RestMethod @arguments
    if ($payload.code -ne 200) {
        throw "$Method $Uri returned ERP code $($payload.code): $($payload.msg) (traceId=$($payload.traceId))."
    }
    return $payload
}

function Assert-ErpRejected {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [Parameter(Mandatory = $true)][hashtable]$Headers,
        [ValidateSet('Post', 'Patch', 'Put')][string]$Method = 'Post',
        [Parameter(Mandatory = $true)][string]$JsonBody
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $request = [System.Net.Http.HttpRequestMessage]::new(
        [System.Net.Http.HttpMethod]::new($Method.ToUpperInvariant()), $Uri)
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$request.Headers.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    $request.Content = [System.Net.Http.StringContent]::new(
        $JsonBody, [Text.Encoding]::UTF8, 'application/json')
    try {
        $response = $client.SendAsync($request).GetAwaiter().GetResult()
        try {
            $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            $body = if ($rawBody) { $rawBody | ConvertFrom-Json } else { $null }
            if ([int]$response.StatusCode -notin @(400, 409) -or $null -eq $body -or
                $body.code -eq 200 -or -not $body.msg) {
                throw "Expected HTTP 400/409 but received HTTP $([int]$response.StatusCode): $rawBody"
            }
        } finally {
            $response.Dispose()
        }
    } finally {
        $request.Dispose()
        $client.Dispose()
    }
}

function Invoke-ConcurrentReceive {
    param(
        [Parameter(Mandatory = $true)][string]$OrderId,
        [Parameter(Mandatory = $true)][string]$ProductId,
        [Parameter(Mandatory = $true)][string]$Token,
        [Parameter(Mandatory = $true)][int]$ReceiveNum
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $client.DefaultRequestHeaders.Add('X-Auth-Token', $Token)
    $requests = New-Object System.Collections.Generic.List[System.Net.Http.HttpRequestMessage]
    $tasks = New-Object System.Collections.Generic.List[System.Threading.Tasks.Task[System.Net.Http.HttpResponseMessage]]
    try {
        for ($index = 0; $index -lt 2; $index++) {
            $request = [System.Net.Http.HttpRequestMessage]::new(
                [System.Net.Http.HttpMethod]::new('PATCH'),
                "$baseUri/stock/transfer/sc/receive")
            $body = @{
                id = $OrderId
                products = @(@{ productId = $ProductId; receiveNum = $ReceiveNum })
            } | ConvertTo-Json -Depth 6 -Compress
            $request.Content = [System.Net.Http.StringContent]::new(
                $body, [System.Text.Encoding]::UTF8, 'application/json')
            $requests.Add($request)
            $tasks.Add($client.SendAsync($request))
        }

        [System.Threading.Tasks.Task]::WaitAll([System.Threading.Tasks.Task[]]$tasks.ToArray())
        $results = @()
        foreach ($task in $tasks) {
            $response = $task.GetAwaiter().GetResult()
            try {
                $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
                $results += [pscustomobject]@{
                    HttpStatus = [int]$response.StatusCode
                    Body = $rawBody | ConvertFrom-Json
                }
            } finally {
                $response.Dispose()
            }
        }
        return @($results)
    } finally {
        foreach ($request in $requests) { $request.Dispose() }
        $client.Dispose()
    }
}

$runKey = [Guid]::NewGuid().ToString('N').Substring(0, 6)
$prefix = "v135st$runKey"
$sourceScId = "$prefix-src"
$targetScId = "$prefix-dst"
$productId = "$prefix-p1"
$batchProductId = "$prefix-p2"
$serialProductId = "$prefix-p3"
$descriptionPrefix = "V1.35 stock transfer $runKey"
$cleanupSql = @"
DELETE FROM sys_mq_inbox WHERE event_id IN (
  SELECT id FROM sys_mq_outbox WHERE payload LIKE '%$prefix%');
DELETE FROM sys_mq_outbox WHERE payload LIKE '%$prefix%';
DELETE FROM op_logs WHERE extra LIKE '%$descriptionPrefix%' OR extra LIKE '%$prefix%';
DELETE FROM tbl_order_time_line WHERE BINARY order_id IN (
  SELECT BINARY id FROM tbl_sc_transfer_order WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_product_stock_log WHERE BINARY biz_id IN (
  SELECT BINARY id FROM tbl_sc_transfer_order WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_sc_transfer_order_detail_receive WHERE BINARY order_id IN (
  SELECT BINARY id FROM tbl_sc_transfer_order WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_sc_transfer_order_detail_batch WHERE BINARY order_id IN (
  SELECT BINARY id FROM tbl_sc_transfer_order WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_sc_transfer_order_detail_serial WHERE BINARY order_id IN (
  SELECT BINARY id FROM tbl_sc_transfer_order WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_sc_transfer_order_detail WHERE BINARY order_id IN (
  SELECT BINARY id FROM tbl_sc_transfer_order WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_sc_transfer_order WHERE description LIKE '$descriptionPrefix%';
DELETE FROM tbl_product_stock_serial WHERE product_id IN ('$productId','$batchProductId','$serialProductId');
DELETE FROM tbl_product_stock_batch WHERE product_id IN ('$productId','$batchProductId','$serialProductId')
  AND sc_id IN ('$sourceScId','$targetScId');
DELETE FROM tbl_product_stock WHERE product_id IN ('$productId','$batchProductId','$serialProductId')
  AND sc_id IN ('$sourceScId','$targetScId');
DELETE FROM base_data_product_purchase WHERE id IN ('$productId','$batchProductId','$serialProductId');
DELETE FROM base_data_product WHERE id IN ('$productId','$batchProductId','$serialProductId');
DELETE FROM base_data_store_center WHERE id IN ('$sourceScId','$targetScId');
"@

$token = $null
try {
    Invoke-SmokeSql -Sql $cleanupSql
    Invoke-SmokeSql -Sql @"
INSERT INTO base_data_store_center
(id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$sourceScId','V135-$runKey-SRC','V1.35 source',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$targetScId','V135-$runKey-DST','V1.35 target',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product
(id,code,name,sku_code,category_id,brand_id,product_type,tax_rate,sale_tax_rate,spec,unit,available,is_batch,is_serial,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$productId','V135-$runKey-P1','V1.35 normal product','V135-$runKey-SKU1','1','1',1,13,13,'FLOW','EA',1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$batchProductId','V135-$runKey-P2','V1.35 batch product','V135-$runKey-SKU2','1','1',1,13,13,'FLOW','EA',1,1,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$serialProductId','V135-$runKey-P3','V1.35 serial product','V135-$runKey-SKU3','1','1',1,13,13,'FLOW','EA',1,0,1,'smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product_purchase (id,price) VALUES ('$productId',10),('$batchProductId',10),('$serialProductId',10);
INSERT INTO tbl_product_stock (id,sc_id,product_id,stock_num,tax_price,tax_amount) VALUES
('$prefix-stock1','$sourceScId','$productId',10,10,100),
('$prefix-stock2','$targetScId','$productId',1,10,10),
('$prefix-stock3','$sourceScId','$batchProductId',6,10,60),
('$prefix-stock4','$targetScId','$batchProductId',0,10,0),
('$prefix-stock5','$sourceScId','$serialProductId',3,10,30),
('$prefix-stock6','$targetScId','$serialProductId',0,10,0);
INSERT INTO tbl_product_stock_batch
(id,sc_id,product_id,quantity,batch_number,shelf_location,production_date,expiry_date,supplier_id,create_time) VALUES
('$prefix-batch1','$sourceScId','$batchProductId',4,'V135-BATCH','A-01','2026-08-01','2027-08-01',NULL,NOW()),
('$prefix-batch2','$sourceScId','$batchProductId',2,'V135-BATCH2','A-02','2026-08-01','2027-08-01',NULL,NOW()),
('$prefix-sbatch','$sourceScId','$serialProductId',3,'V135-SBATCH','S-01','2026-08-01','2027-08-01',NULL,NOW());
INSERT INTO tbl_product_stock_serial
(id,product_id,serial_number,stock_status,batch_id,production_date,expiry_date,shelf_location,supplier_id,create_time) VALUES
('$prefix-ser1','$serialProductId','V135-S1',1,'$prefix-sbatch','2026-08-01','2027-08-01','S-01',NULL,NOW()),
('$prefix-ser2','$serialProductId','V135-S2',1,'$prefix-sbatch','2026-08-01','2027-08-01','S-01',NULL,NOW()),
('$prefix-ser3','$serialProductId','V135-S3',1,'$prefix-sbatch','2026-08-01','2027-08-01','S-01',NULL,NOW());
"@

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    $token = [string]$login.data.token
    if (-not $token) { throw 'Login succeeded without returning data.token.' }
    $headers = @{ 'X-Auth-Token' = $token }

    $negativeDirect = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix negative direct"
        products = @(@{ productId = $productId; transferNum = -2 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc/approve/pass/direct" -Headers $headers `
        -JsonBody ($negativeDirect | ConvertTo-Json -Depth 6)

    $duplicate = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix duplicate"
        products = @(
            @{ productId = $productId; transferNum = 1 },
            @{ productId = $productId; transferNum = 1 }
        )
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc" -Headers $headers `
        -JsonBody ($duplicate | ConvertTo-Json -Depth 6)

    $missingWarehouse = @{
        sourceScId = "$prefix-missing"
        targetScId = $targetScId
        description = "$descriptionPrefix missing warehouse"
        products = @(@{ productId = $productId; transferNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc" -Headers $headers `
        -JsonBody ($missingWarehouse | ConvertTo-Json -Depth 6)

    $missingProduct = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix missing product"
        products = @(@{ productId = "$prefix-missing"; transferNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc" -Headers $headers `
        -JsonBody ($missingProduct | ConvertTo-Json -Depth 6)

    $batchDirect = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix batch rejected"
        products = @(@{ productId = $batchProductId; transferNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc/approve/pass/direct" -Headers $headers `
        -JsonBody ($batchDirect | ConvertTo-Json -Depth 6)

    $guardResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT COUNT(*) FROM tbl_sc_transfer_order WHERE description LIKE '$descriptionPrefix%';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$sourceScId' AND product_id='$productId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$sourceScId' AND product_id='$batchProductId';
SELECT SUM(quantity) FROM tbl_product_stock_batch WHERE sc_id='$sourceScId' AND product_id='$batchProductId';
"@)
    if ($guardResult.Count -ne 4 -or [int]$guardResult[0] -ne 0 -or
        [int]$guardResult[1] -ne 10 -or [int]$guardResult[2] -ne 6 -or
        [int]$guardResult[3] -ne 6) {
        throw "Rejected stock transfers changed data: $($guardResult -join ',')"
    }

    $normalDirect = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix normal"
        products = @(@{ productId = $productId; transferNum = 4 })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/transfer/sc/approve/pass/direct" -Method Post `
        -Headers $headers -JsonBody ($normalDirect | ConvertTo-Json -Depth 6) | Out-Null
    $orderId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql `
        "SELECT id FROM tbl_sc_transfer_order WHERE description='$descriptionPrefix normal';")[0])
    if (-not $orderId) { throw 'Normal stock-transfer order was not persisted.' }

    $approved = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$orderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$sourceScId' AND product_id='$productId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$productId';
SELECT receive_num FROM tbl_sc_transfer_order_detail WHERE order_id='$orderId';
"@)
    if ($approved.Count -ne 4 -or [int]$approved[0] -ne 3 -or
        [int]$approved[1] -ne 6 -or [int]$approved[2] -ne 1 -or
        [int]$approved[3] -ne 0) {
        throw "Approval did not preserve the two-stage transfer flow: $($approved -join ',')"
    }

    $negativeReceive = @{
        id = $orderId
        products = @(@{ productId = $productId; receiveNum = -1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch `
        -Headers $headers -JsonBody ($negativeReceive | ConvertTo-Json -Depth 6)

    $unknownReceive = @{
        id = $orderId
        products = @(@{ productId = "$prefix-missing"; receiveNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch `
        -Headers $headers -JsonBody ($unknownReceive | ConvertTo-Json -Depth 6)

    $afterRejectedReceive = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$productId';
SELECT receive_num FROM tbl_sc_transfer_order_detail WHERE order_id='$orderId';
SELECT COUNT(*) FROM tbl_sc_transfer_order_detail_receive WHERE order_id='$orderId';
"@)
    if ($afterRejectedReceive.Count -ne 3 -or [int]$afterRejectedReceive[0] -ne 1 -or
        [int]$afterRejectedReceive[1] -ne 0 -or [int]$afterRejectedReceive[2] -ne 0) {
        throw "Rejected receipt changed transfer data: $($afterRejectedReceive -join ',')"
    }

    $partialReceive = @{
        id = $orderId
        products = @(@{ productId = $productId; receiveNum = 2 })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch -Headers $headers `
        -JsonBody ($partialReceive | ConvertTo-Json -Depth 6) | Out-Null
    $partial = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$orderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$productId';
SELECT receive_num FROM tbl_sc_transfer_order_detail WHERE order_id='$orderId';
"@)
    if ($partial.Count -ne 3 -or [int]$partial[0] -ne 9 -or
        [int]$partial[1] -ne 3 -or [int]$partial[2] -ne 2) {
        throw "Partial receipt did not move target stock 1 to 3: $($partial -join ',')"
    }

    $receiveResults = @(Invoke-ConcurrentReceive -OrderId $orderId -ProductId $productId `
        -Token $token -ReceiveNum 2)
    $successCount = @($receiveResults | Where-Object { $_.HttpStatus -eq 200 -and $_.Body.code -eq 200 }).Count
    if ($successCount -ne 1) {
        throw "Concurrent receipt expected exactly one success: $($receiveResults | ConvertTo-Json -Depth 5 -Compress)"
    }
    $completed = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$orderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$sourceScId' AND product_id='$productId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$productId';
SELECT receive_num FROM tbl_sc_transfer_order_detail WHERE order_id='$orderId';
SELECT COUNT(*) FROM tbl_sc_transfer_order_detail_receive WHERE order_id='$orderId';
"@)
    if ($completed.Count -ne 5 -or [int]$completed[0] -ne 12 -or
        [int]$completed[1] -ne 6 -or [int]$completed[2] -ne 5 -or
        [int]$completed[3] -ne 4 -or [int]$completed[4] -ne 2) {
        throw "Concurrent receipt was not serialized safely: $($completed -join ',')"
    }

    # ---- 批次管理航材调拨：逐批次指定；审核扣转出、收货加转入；在途=未收数量 ----
    $batchDup = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix batch dup"
        products = @(@{
            productId = $batchProductId; transferNum = 2
            batchDetails = @(
                @{ batchNumber = 'V135-BATCH'; transferNum = 1 },
                @{ batchNumber = 'V135-BATCH'; transferNum = 1 }
            )
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc" -Headers $headers `
        -JsonBody ($batchDup | ConvertTo-Json -Depth 8)

    $batchSum = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix batch sum mismatch"
        products = @(@{
            productId = $batchProductId; transferNum = 3
            batchDetails = @(
                @{ batchNumber = 'V135-BATCH'; transferNum = 1 },
                @{ batchNumber = 'V135-BATCH2'; transferNum = 1 }
            )
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc" -Headers $headers `
        -JsonBody ($batchSum | ConvertTo-Json -Depth 8)

    $batchTransfer = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix batch transfer"
        products = @(@{
            productId = $batchProductId; transferNum = 3
            batchDetails = @(
                @{ batchNumber = 'V135-BATCH'; transferNum = 2 },
                @{ batchNumber = 'V135-BATCH2'; transferNum = 1 }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/transfer/sc/approve/pass/direct" -Method Post `
        -Headers $headers -JsonBody ($batchTransfer | ConvertTo-Json -Depth 8) | Out-Null
    $batchOrderId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql `
        "SELECT id FROM tbl_sc_transfer_order WHERE description='$descriptionPrefix batch transfer';")[0])
    $batchApproved = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$batchOrderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$sourceScId' AND product_id='$batchProductId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$sourceScId' AND product_id='$batchProductId' AND batch_number='V135-BATCH';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$sourceScId' AND product_id='$batchProductId' AND batch_number='V135-BATCH2';
SELECT SUM(received_num) FROM tbl_sc_transfer_order_detail_batch WHERE order_id='$batchOrderId';
"@)
    if ($batchApproved.Count -ne 5 -or [int]$batchApproved[0] -ne 3 -or
        [int]$batchApproved[1] -ne 3 -or [int]$batchApproved[2] -ne 2 -or
        [int]$batchApproved[3] -ne 1 -or [int]$batchApproved[4] -ne 0) {
        throw "Batch transfer approval failed (in-transit must remain unreceived): $($batchApproved -join ',')"
    }

    $batchGhostReceive = @{
        id = $batchOrderId
        products = @(@{
            productId = $batchProductId; receiveNum = 1
            batchDetails = @(@{ batchNumber = 'V135-GHOST'; receiveNum = 1 })
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch `
        -Headers $headers -JsonBody ($batchGhostReceive | ConvertTo-Json -Depth 8)
    $batchGhostGuard = @(Invoke-SmokeSql -ReturnOutput -Sql `
        "SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$batchProductId';")
    if ($batchGhostGuard.Count -ne 1 -or [int]$batchGhostGuard[0] -ne 0) {
        throw "Rejected ghost batch receipt changed target stock: $($batchGhostGuard -join ',')"
    }

    $batchPartialReceive = @{
        id = $batchOrderId
        products = @(@{
            productId = $batchProductId; receiveNum = 1
            batchDetails = @(@{ batchNumber = 'V135-BATCH'; receiveNum = 1 })
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch -Headers $headers `
        -JsonBody ($batchPartialReceive | ConvertTo-Json -Depth 8) | Out-Null
    $batchPartial = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$batchOrderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$batchProductId';
SELECT received_num FROM tbl_sc_transfer_order_detail_batch WHERE order_id='$batchOrderId' AND batch_number='V135-BATCH';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$targetScId' AND product_id='$batchProductId' AND batch_number='V135-BATCH';
"@)
    if ($batchPartial.Count -ne 4 -or [int]$batchPartial[0] -ne 9 -or
        [int]$batchPartial[1] -ne 1 -or [int]$batchPartial[2] -ne 1 -or
        [int]$batchPartial[3] -ne 1) {
        throw "Batch partial receipt failed (in-transit accounting): $($batchPartial -join ',')"
    }

    $batchFinalReceive = @{
        id = $batchOrderId
        products = @(@{
            productId = $batchProductId; receiveNum = 2
            batchDetails = @(
                @{ batchNumber = 'V135-BATCH'; receiveNum = 1 },
                @{ batchNumber = 'V135-BATCH2'; receiveNum = 1 }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch -Headers $headers `
        -JsonBody ($batchFinalReceive | ConvertTo-Json -Depth 8) | Out-Null
    $batchFinal = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$batchOrderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$batchProductId';
SELECT SUM(received_num) FROM tbl_sc_transfer_order_detail_batch WHERE order_id='$batchOrderId';
"@)
    if ($batchFinal.Count -ne 3 -or [int]$batchFinal[0] -ne 12 -or
        [int]$batchFinal[1] -ne 3 -or [int]$batchFinal[2] -ne 3) {
        throw "Batch final receipt failed: $($batchFinal -join ',')"
    }

    # ---- 序列号管理航材调拨：逐序列号指定；转出置在途、收货切换到转入仓批次 ----
    $serialDup = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix serial dup"
        products = @(@{
            productId = $serialProductId; transferNum = 2
            serialDetails = @(
                @{ serialNumber = 'V135-S1'; batchNumber = 'V135-SBATCH' },
                @{ serialNumber = 'V135-S1'; batchNumber = 'V135-SBATCH' }
            )
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc" -Headers $headers `
        -JsonBody ($serialDup | ConvertTo-Json -Depth 8)

    $serialTransfer = @{
        sourceScId = $sourceScId
        targetScId = $targetScId
        description = "$descriptionPrefix serial transfer"
        products = @(@{
            productId = $serialProductId; transferNum = 2
            serialDetails = @(
                @{ serialNumber = 'V135-S1'; batchNumber = 'V135-SBATCH' },
                @{ serialNumber = 'V135-S2'; batchNumber = 'V135-SBATCH' }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/transfer/sc/approve/pass/direct" -Method Post `
        -Headers $headers -JsonBody ($serialTransfer | ConvertTo-Json -Depth 8) | Out-Null
    $serialOrderId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql `
        "SELECT id FROM tbl_sc_transfer_order WHERE description='$descriptionPrefix serial transfer';")[0])
    $serialApproved = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$serialOrderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$sourceScId' AND product_id='$serialProductId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$sourceScId' AND product_id='$serialProductId' AND batch_number='V135-SBATCH';
SELECT stock_status FROM tbl_product_stock_serial WHERE product_id='$serialProductId' AND serial_number='V135-S1';
SELECT stock_status FROM tbl_product_stock_serial WHERE product_id='$serialProductId' AND serial_number='V135-S2';
SELECT COUNT(*) FROM tbl_sc_transfer_order_detail_serial WHERE order_id='$serialOrderId' AND transfer_status=1;
"@)
    if ($serialApproved.Count -ne 6 -or [int]$serialApproved[0] -ne 3 -or
        [int]$serialApproved[1] -ne 1 -or [int]$serialApproved[2] -ne 1 -or
        [int]$serialApproved[3] -ne 0 -or [int]$serialApproved[4] -ne 0 -or
        [int]$serialApproved[5] -ne 2) {
        throw "Serial transfer approval failed (in-transit serials): $($serialApproved -join ',')"
    }

    $serialGhostReceive = @{
        id = $serialOrderId
        products = @(@{
            productId = $serialProductId; receiveNum = 1
            serialDetails = @(@{ serialNumber = 'V135-S3' })
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch `
        -Headers $headers -JsonBody ($serialGhostReceive | ConvertTo-Json -Depth 8)
    $serialGhostGuard = @(Invoke-SmokeSql -ReturnOutput -Sql `
        "SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$serialProductId';")
    if ($serialGhostGuard.Count -ne 1 -or [int]$serialGhostGuard[0] -ne 0) {
        throw "Rejected mismatched serial receipt changed target stock: $($serialGhostGuard -join ',')"
    }

    $serialPartialReceive = @{
        id = $serialOrderId
        products = @(@{
            productId = $serialProductId; receiveNum = 1
            serialDetails = @(@{ serialNumber = 'V135-S1' })
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch -Headers $headers `
        -JsonBody ($serialPartialReceive | ConvertTo-Json -Depth 8) | Out-Null
    $serialPartial = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$serialOrderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$serialProductId';
SELECT stock_status FROM tbl_product_stock_serial WHERE product_id='$serialProductId' AND serial_number='V135-S1';
SELECT batch_id FROM tbl_product_stock_serial WHERE product_id='$serialProductId' AND serial_number='V135-S1';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$targetScId' AND product_id='$serialProductId' AND batch_number='V135-SBATCH';
SELECT transfer_status FROM tbl_sc_transfer_order_detail_serial WHERE order_id='$serialOrderId' AND serial_number='V135-S1';
"@)
    if ($serialPartial.Count -ne 6 -or [int]$serialPartial[0] -ne 9 -or
        [int]$serialPartial[1] -ne 1 -or [int]$serialPartial[2] -ne 1 -or
        [int]$serialPartial[4] -ne 1 -or [int]$serialPartial[5] -ne 2) {
        throw "Serial partial receipt failed: $($serialPartial -join ',')"
    }
    # 校验序列号已切换到转入仓批次（batch_id 与转入仓 V135-SBATCH 一致）
    $targetBatchId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql `
        "SELECT id FROM tbl_product_stock_batch WHERE sc_id='$targetScId' AND product_id='$serialProductId' AND batch_number='V135-SBATCH';")[0])
    if ([string]$serialPartial[3] -ne $targetBatchId) {
        throw "Received serial was not switched to the target batch: $($serialPartial -join ',')"
    }

    $serialFinalReceive = @{
        id = $serialOrderId
        products = @(@{
            productId = $serialProductId; receiveNum = 1
            serialDetails = @(@{ serialNumber = 'V135-S2' })
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/transfer/sc/receive" -Method Patch -Headers $headers `
        -JsonBody ($serialFinalReceive | ConvertTo-Json -Depth 8) | Out-Null
    $serialFinal = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_sc_transfer_order WHERE id='$serialOrderId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$targetScId' AND product_id='$serialProductId';
SELECT stock_status FROM tbl_product_stock_serial WHERE product_id='$serialProductId' AND serial_number='V135-S2';
SELECT COUNT(*) FROM tbl_sc_transfer_order_detail_serial WHERE order_id='$serialOrderId' AND transfer_status=2;
"@)
    if ($serialFinal.Count -ne 4 -or [int]$serialFinal[0] -ne 12 -or
        [int]$serialFinal[1] -ne 2 -or [int]$serialFinal[2] -ne 1 -or
        [int]$serialFinal[3] -ne 2) {
        throw "Serial final receipt failed: $($serialFinal -join ',')"
    }

    Write-Host 'Stock-transfer verification passed: guards, normal two-stage 10->6 and 1->5 with concurrent duplicate receipt, batch per-batch in-transit accounting and partial/final receipt, serial per-serial in-transit switching with mismatch 退回.'
} finally {
    if ($token) {
        try {
            Invoke-ErpJson -Uri "$baseUri/auth/logout" -Method Post `
                -Headers @{ 'X-Auth-Token' = $token } | Out-Null
        } catch {
            Write-Warning "Logout cleanup failed: $($_.Exception.Message)"
        }
    }
    Invoke-SmokeSql -Sql $cleanupSql
}
