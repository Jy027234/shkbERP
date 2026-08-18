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
    throw 'The stock-adjust write probe is restricted to a local API endpoint.'
}
if ($DbContainer -ne 'xingyun-smoke-mysql' -or $Database -ne 'shkb_platform') {
    throw 'The stock-adjust write probe is restricted to the local smoke database.'
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
        [string]$JsonBody
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $request = [System.Net.Http.HttpRequestMessage]::new(
        [System.Net.Http.HttpMethod]::new($Method.ToUpperInvariant()), $Uri)
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$request.Headers.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    if ($JsonBody) {
        $request.Content = [System.Net.Http.StringContent]::new(
            $JsonBody, [Text.Encoding]::UTF8, 'application/json')
    }
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

function Invoke-ConcurrentApproval {
    param(
        [Parameter(Mandatory = $true)][string]$SheetId,
        [Parameter(Mandatory = $true)][string]$Token,
        [Parameter(Mandatory = $true)][string]$Description
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
                "$baseUri/stock/adjust/approve/pass")
            $body = @{ id = $SheetId; description = $Description } | ConvertTo-Json -Compress
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
                $results += $rawBody | ConvertFrom-Json
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
$prefix = "v134sa$runKey"
$scId = "$prefix-sc"
$productId = "$prefix-p1"
$batchProductId = "$prefix-p2"
$serialProductId = "$prefix-p3"
$descriptionPrefix = "V1.34 stock adjust $runKey"
$cleanupSql = @"
DELETE FROM sys_mq_inbox WHERE event_id IN (
  SELECT id FROM sys_mq_outbox WHERE payload LIKE '%$prefix%');
DELETE FROM sys_mq_outbox WHERE payload LIKE '%$prefix%';
DELETE FROM op_logs WHERE extra LIKE '%$descriptionPrefix%' OR extra LIKE '%$prefix%';
DELETE FROM tbl_order_time_line WHERE order_id IN (
  SELECT id FROM tbl_stock_adjust_sheet WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_product_stock_log WHERE biz_id IN (
  SELECT id FROM tbl_stock_adjust_sheet WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_stock_adjust_sheet_detail_batch WHERE sheet_id IN (
  SELECT id FROM tbl_stock_adjust_sheet WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_stock_adjust_sheet_detail_serial WHERE sheet_id IN (
  SELECT id FROM tbl_stock_adjust_sheet WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_stock_adjust_sheet_detail WHERE sheet_id IN (
  SELECT id FROM tbl_stock_adjust_sheet WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_stock_adjust_sheet WHERE description LIKE '$descriptionPrefix%';
DELETE FROM tbl_product_stock_serial WHERE product_id IN ('$productId','$batchProductId','$serialProductId');
DELETE FROM tbl_product_stock_batch WHERE product_id IN ('$productId','$batchProductId','$serialProductId') AND sc_id='$scId';
DELETE FROM tbl_product_stock WHERE product_id IN ('$productId','$batchProductId','$serialProductId') AND sc_id='$scId';
DELETE FROM base_data_product_purchase WHERE id IN ('$productId','$batchProductId','$serialProductId');
DELETE FROM base_data_product WHERE id IN ('$productId','$batchProductId','$serialProductId');
DELETE FROM base_data_store_center WHERE id='$scId';
"@

$token = $null
try {
    Invoke-SmokeSql -Sql $cleanupSql
    Invoke-SmokeSql -Sql @"
INSERT INTO base_data_store_center
(id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$scId','V134-$runKey-SC','V1.34 warehouse',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product
(id,code,name,sku_code,category_id,brand_id,product_type,tax_rate,sale_tax_rate,spec,unit,available,is_batch,is_serial,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$productId','V134-$runKey-P1','V1.34 product 1','V134-$runKey-SKU1','1','1',1,13,13,'FLOW','EA',1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$batchProductId','V134-$runKey-P2','V1.34 batch product','V134-$runKey-SKU2','1','1',1,13,13,'FLOW','EA',1,1,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$serialProductId','V134-$runKey-P3','V1.34 serial product','V134-$runKey-SKU3','1','1',1,13,13,'FLOW','EA',1,0,1,'smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product_purchase (id,price) VALUES ('$productId',10),('$batchProductId',10),('$serialProductId',10);
INSERT INTO tbl_product_stock (id,sc_id,product_id,stock_num,tax_price,tax_amount) VALUES
('$prefix-stock1','$scId','$productId',5,10,50),
('$prefix-stock2','$scId','$batchProductId',4,10,40),
('$prefix-stock3','$scId','$serialProductId',3,10,30);
INSERT INTO tbl_product_stock_batch
(id,sc_id,product_id,quantity,batch_number,shelf_location,production_date,expiry_date,supplier_id,create_time) VALUES
('$prefix-batch','$scId','$batchProductId',4,'V134-BATCH','A-01','2026-08-01','2027-08-01',NULL,NOW()),
('$prefix-sbatch','$scId','$serialProductId',3,'V134-SBATCH','S-01','2026-08-01','2027-08-01',NULL,NOW());
INSERT INTO tbl_product_stock_serial
(id,product_id,serial_number,stock_status,batch_id,production_date,expiry_date,shelf_location,supplier_id,create_time) VALUES
('$prefix-ser1','$serialProductId','V134-S1',1,'$prefix-sbatch','2026-08-01','2027-08-01','S-01',NULL,NOW()),
('$prefix-ser2','$serialProductId','V134-S2',1,'$prefix-sbatch','2026-08-01','2027-08-01','S-01',NULL,NOW()),
('$prefix-ser3','$serialProductId','V134-S3',1,'$prefix-sbatch','2026-08-01','2027-08-01','S-01',NULL,NOW());
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
        scId = $scId
        bizType = 0
        reasonId = '1'
        description = "$descriptionPrefix negative direct"
        products = @(@{ productId = $productId; stockNum = -2 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust/approve/pass/direct" -Headers $headers `
        -JsonBody ($negativeDirect | ConvertTo-Json -Depth 6)

    $duplicate = @{
        scId = $scId
        bizType = 0
        reasonId = '1'
        description = "$descriptionPrefix duplicate"
        products = @(
            @{ productId = $productId; stockNum = 1 },
            @{ productId = $productId; stockNum = 1 }
        )
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($duplicate | ConvertTo-Json -Depth 6)

    $missingWarehouse = @{
        scId = "$prefix-missing"
        bizType = 0
        reasonId = '1'
        description = "$descriptionPrefix missing warehouse"
        products = @(@{ productId = $productId; stockNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($missingWarehouse | ConvertTo-Json -Depth 6)

    $missingReason = @{
        scId = $scId
        bizType = 0
        reasonId = "$prefix-missing"
        description = "$descriptionPrefix missing reason"
        products = @(@{ productId = $productId; stockNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($missingReason | ConvertTo-Json -Depth 6)

    $missingProduct = @{
        scId = $scId
        bizType = 0
        reasonId = '1'
        description = "$descriptionPrefix missing product"
        products = @(@{ productId = "$prefix-missing"; stockNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($missingProduct | ConvertTo-Json -Depth 6)

    $batchDirect = @{
        scId = $scId
        bizType = 2
        reasonId = '1'
        description = "$descriptionPrefix batch rejected"
        products = @(@{ productId = $batchProductId; stockNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust/approve/pass/direct" -Headers $headers `
        -JsonBody ($batchDirect | ConvertTo-Json -Depth 6)
    $guardResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT COUNT(*) FROM tbl_stock_adjust_sheet WHERE description LIKE '$descriptionPrefix%';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$productId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$batchProductId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$batchProductId';
"@)
    if ($guardResult.Count -ne 4 -or [int]$guardResult[0] -ne 0 -or
        [int]$guardResult[1] -ne 5 -or [int]$guardResult[2] -ne 4 -or [int]$guardResult[3] -ne 4) {
        throw "Rejected stock adjustments changed data: $($guardResult -join ',')"
    }

    $normalIn = @{
        scId = $scId
        bizType = 0
        reasonId = '1'
        description = "$descriptionPrefix normal in"
        products = @(@{ productId = $productId; stockNum = 2 })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/adjust/approve/pass/direct" -Method Post -Headers $headers `
        -JsonBody ($normalIn | ConvertTo-Json -Depth 6) | Out-Null
    $afterIn = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$productId';")
    if ($afterIn.Count -ne 1 -or [int]$afterIn[0] -ne 7) {
        throw "Normal stock-adjust IN did not change stock 5 to 7: $($afterIn -join ',')"
    }

    $normalOut = @{
        scId = $scId
        bizType = 2
        reasonId = '1'
        description = "$descriptionPrefix normal out"
        products = @(@{ productId = $productId; stockNum = 3 })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/adjust" -Method Post -Headers $headers `
        -JsonBody ($normalOut | ConvertTo-Json -Depth 6) | Out-Null
    $outSheetId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql "SELECT id FROM tbl_stock_adjust_sheet WHERE description='$descriptionPrefix normal out';")[0])
    if (-not $outSheetId) { throw 'Normal stock-adjust OUT draft was not persisted.' }

    $approvalResults = @(Invoke-ConcurrentApproval -SheetId $outSheetId -Token $token `
        -Description "$descriptionPrefix normal out approved")
    $successCount = @($approvalResults | Where-Object { $_.code -eq 200 }).Count
    if ($successCount -ne 1) {
        throw "Concurrent approval expected exactly one success: $($approvalResults | ConvertTo-Json -Compress)"
    }
    $outResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_stock_adjust_sheet WHERE id='$outSheetId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$productId';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id='$outSheetId';
"@)
    if ($outResult.Count -ne 3 -or [int]$outResult[0] -ne 3 -or
        [int]$outResult[1] -ne 4 -or [int]$outResult[2] -ne 1) {
        throw "Concurrent stock-adjust approval was not idempotent: $($outResult -join ',')"
    }

    # ---- 批次管理航材调整：入库/出库都允许批次明细；缺明细/重复/合计不符拒绝 ----
    $batchNoDetails = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix batch in no details"
        products = @(@{ productId = $batchProductId; stockNum = 2 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($batchNoDetails | ConvertTo-Json -Depth 6)

    $batchDup = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix batch dup"
        products = @(@{
            productId = $batchProductId; stockNum = 2
            batchDetails = @(
                @{ batchNumber = 'V134-BATCH'; stockNum = 1 },
                @{ batchNumber = 'V134-BATCH'; stockNum = 1 }
            )
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($batchDup | ConvertTo-Json -Depth 8)

    $batchSum = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix batch sum mismatch"
        products = @(@{
            productId = $batchProductId; stockNum = 3
            batchDetails = @(
                @{ batchNumber = 'V134-BATCH'; stockNum = 1 },
                @{ batchNumber = 'V134-BATCH2'; stockNum = 1 }
            )
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($batchSum | ConvertTo-Json -Depth 8)

    $batchIn = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix batch in"
        products = @(@{
            productId = $batchProductId; stockNum = 3
            batchDetails = @(
                @{ batchNumber = 'V134-BATCH'; stockNum = 2 },
                @{ batchNumber = 'V134-BATCH2'; stockNum = 1 }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/adjust/approve/pass/direct" -Method Post -Headers $headers `
        -JsonBody ($batchIn | ConvertTo-Json -Depth 8) | Out-Null
    $batchInResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$batchProductId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$batchProductId' AND batch_number='V134-BATCH';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$batchProductId' AND batch_number='V134-BATCH2';
"@)
    if ($batchInResult.Count -ne 3 -or [int]$batchInResult[0] -ne 7 -or
        [int]$batchInResult[1] -ne 6 -or [int]$batchInResult[2] -ne 1) {
        throw "Batch stock-adjust IN failed: $($batchInResult -join ',')"
    }

    $batchOut = @{
        scId = $scId; bizType = 2; reasonId = '1'
        description = "$descriptionPrefix batch out"
        products = @(@{
            productId = $batchProductId; stockNum = 2
            batchDetails = @(
                @{ batchNumber = 'V134-BATCH'; stockNum = 2 }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/adjust" -Method Post -Headers $headers `
        -JsonBody ($batchOut | ConvertTo-Json -Depth 8) | Out-Null
    $batchOutSheetId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql "SELECT id FROM tbl_stock_adjust_sheet WHERE description='$descriptionPrefix batch out';")[0])
    Invoke-ErpJson -Uri "$baseUri/stock/adjust/approve/pass" -Method Patch -Headers $headers `
        -JsonBody (@{ id = $batchOutSheetId; description = "$descriptionPrefix batch out approved" } | ConvertTo-Json -Compress) | Out-Null
    $batchOutResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$batchProductId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$batchProductId' AND batch_number='V134-BATCH';
"@)
    if ($batchOutResult.Count -ne 2 -or [int]$batchOutResult[0] -ne 5 -or [int]$batchOutResult[1] -ne 4) {
        throw "Batch stock-adjust OUT failed: $($batchOutResult -join ',')"
    }

    $batchOutMissing = @{
        scId = $scId; bizType = 2; reasonId = '1'
        description = "$descriptionPrefix batch out missing"
        products = @(@{
            productId = $batchProductId; stockNum = 1
            batchDetails = @(
                @{ batchNumber = 'V134-GHOST'; stockNum = 1 }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/adjust" -Method Post -Headers $headers `
        -JsonBody ($batchOutMissing | ConvertTo-Json -Depth 8) | Out-Null
    $batchOutMissingSheetId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql "SELECT id FROM tbl_stock_adjust_sheet WHERE description='$descriptionPrefix batch out missing';")[0])
    Assert-ErpRejected -Uri "$baseUri/stock/adjust/approve/pass" -Method Patch -Headers $headers `
        -JsonBody (@{ id = $batchOutMissingSheetId; description = "$descriptionPrefix batch out missing approved" } | ConvertTo-Json -Compress)
    $batchMissingGuard = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$batchProductId';")
    if ($batchMissingGuard.Count -ne 1 -or [int]$batchMissingGuard[0] -ne 5) {
        throw "Rejected missing-batch OUT changed stock: $($batchMissingGuard -join ',')"
    }

    # ---- 序列号管理航材调整：一条序列号一条明细；不允许状态直跳/重复 ----
    $serialNoDetails = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix serial no details"
        products = @(@{ productId = $serialProductId; stockNum = 1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($serialNoDetails | ConvertTo-Json -Depth 6)

    $serialDup = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix serial dup"
        products = @(@{
            productId = $serialProductId; stockNum = 2
            serialDetails = @(
                @{ serialNumber = 'V134-S4'; batchNumber = 'V134-SBATCH' },
                @{ serialNumber = 'V134-S4'; batchNumber = 'V134-SBATCH' }
            )
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($serialDup | ConvertTo-Json -Depth 8)

    $serialCount = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix serial count mismatch"
        products = @(@{
            productId = $serialProductId; stockNum = 2
            serialDetails = @(
                @{ serialNumber = 'V134-S4'; batchNumber = 'V134-SBATCH' }
            )
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust" -Headers $headers `
        -JsonBody ($serialCount | ConvertTo-Json -Depth 8)

    $serialInDup = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix serial in dup"
        products = @(@{
            productId = $serialProductId; stockNum = 1
            serialDetails = @(
                @{ serialNumber = 'V134-S1'; batchNumber = 'V134-SBATCH' }
            )
        })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/adjust/approve/pass/direct" -Method Post -Headers $headers `
        -JsonBody ($serialInDup | ConvertTo-Json -Depth 8)
    $serialInDupGuard = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$serialProductId';")
    if ($serialInDupGuard.Count -ne 1 -or [int]$serialInDupGuard[0] -ne 3) {
        throw "Rejected duplicate serial IN changed stock: $($serialInDupGuard -join ',')"
    }

    $serialIn = @{
        scId = $scId; bizType = 0; reasonId = '1'
        description = "$descriptionPrefix serial in"
        products = @(@{
            productId = $serialProductId; stockNum = 1
            serialDetails = @(
                @{ serialNumber = 'V134-S4'; batchNumber = 'V134-SBATCH' }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/adjust/approve/pass/direct" -Method Post -Headers $headers `
        -JsonBody ($serialIn | ConvertTo-Json -Depth 8) | Out-Null
    $serialInResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$serialProductId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$serialProductId' AND batch_number='V134-SBATCH';
SELECT stock_status FROM tbl_product_stock_serial WHERE product_id='$serialProductId' AND serial_number='V134-S4';
"@)
    if ($serialInResult.Count -ne 3 -or [int]$serialInResult[0] -ne 4 -or
        [int]$serialInResult[1] -ne 4 -or [int]$serialInResult[2] -ne 1) {
        throw "Serial stock-adjust IN failed: $($serialInResult -join ',')"
    }

    $serialOut = @{
        scId = $scId; bizType = 2; reasonId = '1'
        description = "$descriptionPrefix serial out"
        products = @(@{
            productId = $serialProductId; stockNum = 1
            serialDetails = @(
                @{ serialNumber = 'V134-S2'; batchNumber = 'V134-SBATCH' }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/adjust" -Method Post -Headers $headers `
        -JsonBody ($serialOut | ConvertTo-Json -Depth 8) | Out-Null
    $serialOutSheetId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql "SELECT id FROM tbl_stock_adjust_sheet WHERE description='$descriptionPrefix serial out';")[0])
    Invoke-ErpJson -Uri "$baseUri/stock/adjust/approve/pass" -Method Patch -Headers $headers `
        -JsonBody (@{ id = $serialOutSheetId; description = "$descriptionPrefix serial out approved" } | ConvertTo-Json -Compress) | Out-Null
    $serialOutResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$serialProductId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$serialProductId' AND batch_number='V134-SBATCH';
SELECT stock_status FROM tbl_product_stock_serial WHERE product_id='$serialProductId' AND serial_number='V134-S2';
"@)
    if ($serialOutResult.Count -ne 3 -or [int]$serialOutResult[0] -ne 3 -or
        [int]$serialOutResult[1] -ne 3 -or [int]$serialOutResult[2] -ne 0) {
        throw "Serial stock-adjust OUT failed: $($serialOutResult -join ',')"
    }

    $serialOutDup = @{
        scId = $scId; bizType = 2; reasonId = '1'
        description = "$descriptionPrefix serial out dup"
        products = @(@{
            productId = $serialProductId; stockNum = 1
            serialDetails = @(
                @{ serialNumber = 'V134-S2'; batchNumber = 'V134-SBATCH' }
            )
        })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/adjust" -Method Post -Headers $headers `
        -JsonBody ($serialOutDup | ConvertTo-Json -Depth 8) | Out-Null
    $serialOutDupSheetId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql "SELECT id FROM tbl_stock_adjust_sheet WHERE description='$descriptionPrefix serial out dup';")[0])
    Assert-ErpRejected -Uri "$baseUri/stock/adjust/approve/pass" -Method Patch -Headers $headers `
        -JsonBody (@{ id = $serialOutDupSheetId; description = "$descriptionPrefix serial out dup approved" } | ConvertTo-Json -Compress)
    $serialOutDupGuard = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$serialProductId';")
    if ($serialOutDupGuard.Count -ne 1 -or [int]$serialOutDupGuard[0] -ne 3) {
        throw "Rejected duplicate serial OUT changed stock: $($serialOutDupGuard -join ',')"
    }

    Write-Host "Stock-adjust verification passed: input/reference guards, normal 5->7->4 with concurrent duplicate approval, batch IN/OUT with per-batch adjustments, serial IN/OUT with state-transition guards, and missing/duplicate detail rejections verified."
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
