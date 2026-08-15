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
    throw 'The stocktake-flow write probe is restricted to a local API endpoint.'
}
if ($DbContainer -ne 'xingyun-smoke-mysql' -or $Database -ne 'shkb_platform') {
    throw 'The stocktake-flow write probe is restricted to the local smoke database.'
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

$runKey = [Guid]::NewGuid().ToString('N').Substring(0, 6)
$prefix = "v126st$runKey"
$scId = "$prefix-sc1"
$otherScId = "$prefix-sc2"
$productId = "$prefix-p1"
$removedProductId = "$prefix-p2"
$batchProductId = "$prefix-p3"
$normalPlanId = "$prefix-plan1"
$batchPlanId = "$prefix-plan2"
$descriptionPrefix = "V1.26 stocktake flow $runKey"
$cleanupSql = @"
DELETE FROM sys_mq_inbox WHERE event_id IN (
  SELECT id FROM sys_mq_outbox WHERE payload LIKE '%$prefix%');
DELETE FROM sys_mq_outbox WHERE payload LIKE '%$prefix%';
DELETE FROM op_logs WHERE extra LIKE '%$descriptionPrefix%';
DELETE FROM tbl_order_time_line WHERE order_id IN (
  SELECT id FROM tbl_take_stock_sheet WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_product_stock_log WHERE product_id IN ('$productId','$removedProductId','$batchProductId');
DELETE FROM tbl_take_stock_sheet_detail WHERE sheet_id IN (
  SELECT id FROM tbl_take_stock_sheet WHERE plan_id IN ('$normalPlanId','$batchPlanId'));
DELETE FROM tbl_take_stock_sheet WHERE plan_id IN ('$normalPlanId','$batchPlanId');
DELETE FROM tbl_take_stock_plan_detail WHERE plan_id IN ('$normalPlanId','$batchPlanId');
DELETE FROM tbl_take_stock_plan WHERE id IN ('$normalPlanId','$batchPlanId');
DELETE FROM tbl_product_stock_serial WHERE product_id IN ('$productId','$removedProductId','$batchProductId');
DELETE FROM tbl_product_stock_batch WHERE product_id IN ('$productId','$removedProductId','$batchProductId')
  AND sc_id IN ('$scId','$otherScId');
DELETE FROM tbl_product_stock WHERE product_id IN ('$productId','$removedProductId','$batchProductId')
  AND sc_id IN ('$scId','$otherScId');
DELETE FROM base_data_product_purchase WHERE id IN ('$productId','$removedProductId','$batchProductId');
DELETE FROM base_data_product WHERE id IN ('$productId','$removedProductId','$batchProductId');
DELETE FROM base_data_store_center WHERE id IN ('$scId','$otherScId');
"@

$token = $null
try {
    Invoke-SmokeSql -Sql $cleanupSql
    Invoke-SmokeSql -Sql @"
INSERT INTO base_data_store_center
(id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$scId','V126-$runKey-SC1','V126 warehouse 1',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$otherScId','V126-$runKey-SC2','V126 warehouse 2',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product
(id,code,name,sku_code,category_id,brand_id,product_type,tax_rate,sale_tax_rate,spec,unit,available,is_batch,is_serial,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$productId','V126-$runKey-P1','V126 product 1','V126-$runKey-SKU1','1','1',1,13,13,'FLOW','EA',1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$removedProductId','V126-$runKey-P2','V126 product 2','V126-$runKey-SKU2','1','1',1,13,13,'FLOW','EA',1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$batchProductId','V126-$runKey-P3','V126 product 3','V126-$runKey-SKU3','1','1',1,13,13,'FLOW','EA',1,1,0,'smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product_purchase (id,price) VALUES
('$productId',10),('$removedProductId',10),('$batchProductId',10);
INSERT INTO tbl_product_stock
(id,sc_id,product_id,stock_num,tax_price,tax_amount) VALUES
('$prefix-stock1','$scId','$productId',5,10,50),
('$prefix-stock2','$scId','$removedProductId',2,10,20),
('$prefix-stock3','$scId','$batchProductId',4,10,40);
INSERT INTO tbl_product_stock_batch
(id,sc_id,product_id,quantity,batch_number,shelf_location,production_date,expiry_date,supplier_id,create_time) VALUES
('$prefix-batch','$scId','$batchProductId',4,'V126-BATCH','A-01','2026-08-01','2027-08-01',NULL,NOW());
INSERT INTO tbl_take_stock_plan
(id,code,sc_id,take_type,biz_id,take_status,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$normalPlanId','V126-$runKey-PLAN1','$scId',1,NULL,0,'$descriptionPrefix normal','smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$batchPlanId','V126-$runKey-PLAN2','$scId',1,NULL,0,'$descriptionPrefix batch','smoke','smoke',NOW(),'smoke','smoke',NOW());
"@

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    $token = [string]$login.data.token
    if (-not $token) { throw 'Login succeeded without returning data.token.' }
    $headers = @{ 'X-Auth-Token' = $token }

    $invalidWarehouseUpdate = "$baseUri/stock/take/plan?id=$normalPlanId&scId=$otherScId&description=$([Uri]::EscapeDataString($descriptionPrefix + ' moved'))"
    Assert-ErpRejected -Uri $invalidWarehouseUpdate -Method Put -Headers $headers
    $planWarehouse = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT sc_id FROM tbl_take_stock_plan WHERE id='$normalPlanId';")
    if ($planWarehouse.Count -ne 1 -or $planWarehouse[0] -ne $scId) {
        throw 'Rejected warehouse change still modified the stocktake snapshot warehouse.'
    }

    $duplicateSheet = @{
        planId = $normalPlanId
        description = "$descriptionPrefix duplicate"
        products = @(
            @{ productId = $productId; takeNum = 3 },
            @{ productId = $productId; takeNum = 3 }
        )
    }
    Assert-ErpRejected -Uri "$baseUri/stock/take/sheet" -Headers $headers -JsonBody ($duplicateSheet | ConvertTo-Json -Depth 6)

    $negativeSheet = @{
        planId = $normalPlanId
        description = "$descriptionPrefix negative"
        products = @(@{ productId = $productId; takeNum = -1 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/take/sheet" -Headers $headers -JsonBody ($negativeSheet | ConvertTo-Json -Depth 6)

    $normalSheet = @{
        planId = $normalPlanId
        description = "$descriptionPrefix normal sheet"
        products = @(
            @{ productId = $productId; takeNum = 3 },
            @{ productId = $removedProductId; takeNum = 2 }
        )
    }
    Invoke-ErpJson -Uri "$baseUri/stock/take/sheet" -Method Post -Headers $headers -JsonBody ($normalSheet | ConvertTo-Json -Depth 6) | Out-Null
    $normalSheetId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql "SELECT id FROM tbl_take_stock_sheet WHERE plan_id='$normalPlanId';")[0])
    if (-not $normalSheetId) { throw 'Stocktake sheet creation did not persist a sheet.' }

    $updateSheet = @{
        id = $normalSheetId
        description = "$descriptionPrefix normal sheet updated"
        products = @(@{ productId = $productId; takeNum = 3 })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/take/sheet" -Method Put -Headers $headers -JsonBody ($updateSheet | ConvertTo-Json -Depth 6) | Out-Null
    $planProducts = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT product_id FROM tbl_take_stock_plan_detail WHERE plan_id='$normalPlanId' ORDER BY product_id;")
    if ($planProducts.Count -ne 1 -or $planProducts[0] -ne $productId) {
        throw "Simple stocktake retained an unused product snapshot: $($planProducts -join ',')"
    }

    Invoke-ErpJson -Uri "$baseUri/stock/take/sheet/approve/pass" -Method Patch -Headers $headers -FormBody @{ id = $normalSheetId } | Out-Null
    Invoke-ErpJson -Uri "$baseUri/stock/take/plan/diff" -Method Patch -Headers $headers -FormBody @{ id = $normalPlanId } | Out-Null

    $missingProductHandle = @{
        id = $normalPlanId
        description = "$descriptionPrefix missing product"
        allowChangeNum = $true
        autoChangeStock = $true
        products = @(@{ productId = $removedProductId; takeNum = 3 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/take/plan/handle" -Method Patch -Headers $headers -JsonBody ($missingProductHandle | ConvertTo-Json -Depth 6)
    $normalPlanStatus = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT take_status FROM tbl_take_stock_plan WHERE id='$normalPlanId';")
    if ($normalPlanStatus.Count -ne 1 -or [int]$normalPlanStatus[0] -ne 6) {
        throw 'Rejected incomplete difference payload did not preserve DIFF_CREATED state.'
    }

    $normalHandle = @{
        id = $normalPlanId
        description = "$descriptionPrefix normal handled"
        allowChangeNum = $true
        autoChangeStock = $true
        products = @(@{ productId = $productId; takeNum = 3; description = 'counted' })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/take/plan/handle" -Method Patch -Headers $headers -JsonBody ($normalHandle | ConvertTo-Json -Depth 6) | Out-Null
    $normalResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT take_status FROM tbl_take_stock_plan WHERE id='$normalPlanId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$productId';
"@)
    if ($normalResult.Count -ne 2 -or [int]$normalResult[0] -ne 9 -or [int]$normalResult[1] -ne 3) {
        throw "Normal stocktake difference was not applied atomically: $($normalResult -join ',')"
    }

    $batchSheet = @{
        planId = $batchPlanId
        description = "$descriptionPrefix batch sheet"
        products = @(@{ productId = $batchProductId; takeNum = 2 })
    }
    Invoke-ErpJson -Uri "$baseUri/stock/take/sheet" -Method Post -Headers $headers -JsonBody ($batchSheet | ConvertTo-Json -Depth 6) | Out-Null
    $batchSheetId = [string](@(Invoke-SmokeSql -ReturnOutput -Sql "SELECT id FROM tbl_take_stock_sheet WHERE plan_id='$batchPlanId';")[0])
    Invoke-ErpJson -Uri "$baseUri/stock/take/sheet/approve/pass" -Method Patch -Headers $headers -FormBody @{ id = $batchSheetId } | Out-Null
    Invoke-ErpJson -Uri "$baseUri/stock/take/plan/diff" -Method Patch -Headers $headers -FormBody @{ id = $batchPlanId } | Out-Null

    $batchHandle = @{
        id = $batchPlanId
        description = "$descriptionPrefix batch rejected"
        allowChangeNum = $true
        autoChangeStock = $true
        products = @(@{ productId = $batchProductId; takeNum = 2 })
    }
    Assert-ErpRejected -Uri "$baseUri/stock/take/plan/handle" -Method Patch -Headers $headers -JsonBody ($batchHandle | ConvertTo-Json -Depth 6)
    $batchResult = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT take_status FROM tbl_take_stock_plan WHERE id='$batchPlanId';
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$batchProductId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$batchProductId';
"@)
    if ($batchResult.Count -ne 3 -or [int]$batchResult[0] -ne 6 -or [int]$batchResult[1] -ne 4 -or [int]$batchResult[2] -ne 4) {
        throw "Batch-managed stocktake corrupted inventory before rejection: $($batchResult -join ',')"
    }

    Write-Host "Stocktake-flow verification passed: normal plan=$normalPlanId, batch plan=$batchPlanId; immutable warehouse, input guards, stale-detail cleanup, atomic normal adjustment and traceability guard verified."
} finally {
    if ($token) {
        try {
            Invoke-ErpJson -Uri "$baseUri/auth/logout" -Method Post -Headers @{ 'X-Auth-Token' = $token } | Out-Null
        } catch {
            Write-Warning "Logout cleanup failed: $($_.Exception.Message)"
        }
    }
    Invoke-SmokeSql -Sql $cleanupSql
}
