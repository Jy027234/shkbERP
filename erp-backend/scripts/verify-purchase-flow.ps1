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
    throw 'The purchase-flow write probe is restricted to a local API endpoint.'
}
if ($DbContainer -ne 'xingyun-smoke-mysql' -or $Database -ne 'shkb_platform') {
    throw 'The purchase-flow write probe is restricted to the local smoke database.'
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
        [ValidateSet('Get', 'Post', 'Patch', 'Delete')][string]$Method = 'Get',
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
        [Parameter(Mandatory = $true)][string]$JsonBody,
        [ValidateSet('Post', 'Patch')][string]$Method = 'Post'
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $httpMethod = if ($Method -eq 'Patch') { [System.Net.Http.HttpMethod]::new('PATCH') } else { [System.Net.Http.HttpMethod]::Post }
    $request = [System.Net.Http.HttpRequestMessage]::new($httpMethod, $Uri)
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$request.Headers.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    $request.Content = [System.Net.Http.StringContent]::new($JsonBody, [Text.Encoding]::UTF8, 'application/json')
    try {
        $response = $client.SendAsync($request).GetAwaiter().GetResult()
        try {
            $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            $body = if ($rawBody) { $rawBody | ConvertFrom-Json } else { $null }
            if ([int]$response.StatusCode -notin @(400, 409) -or $null -eq $body -or $body.code -eq 200 -or -not $body.msg) {
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
$prefix = "v124pf$runKey"
$scId = "$prefix-sc"
$supplierId = "$prefix-sup"
$productId = "$prefix-p1"
$otherProductId = "$prefix-p2"
$otherOrderId = "$prefix-po2"
$otherOrderDetailId = "$prefix-pod2"
$otherReceiveId = "$prefix-rs2"
$otherReceiveDetailId = "$prefix-rsd2"
$descriptionPrefix = 'V1.24 purchase flow'
$serialNumbers = 1..6 | ForEach-Object { "V125-$runKey-SN$_" }
$cleanupSql = @"
DELETE FROM sys_mq_inbox WHERE event_id IN (
  SELECT id FROM sys_mq_outbox WHERE payload LIKE '%v124pf%' OR payload LIKE '%V124-%');
DELETE FROM sys_mq_outbox WHERE payload LIKE '%v124pf%' OR payload LIKE '%V124-%';
DELETE FROM op_logs WHERE extra LIKE '%$descriptionPrefix%';
DELETE FROM tbl_order_time_line WHERE order_id IN (
  SELECT id FROM tbl_purchase_return WHERE description LIKE '$descriptionPrefix%'
  UNION SELECT id FROM tbl_receive_sheet WHERE description LIKE '$descriptionPrefix%'
  UNION SELECT id FROM tbl_purchase_order WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_product_stock_log WHERE product_id IN ('$productId','$otherProductId');
DELETE FROM tbl_order_pay_type WHERE order_id COLLATE utf8mb4_0900_ai_ci IN (
  SELECT id FROM tbl_purchase_return WHERE description LIKE '$descriptionPrefix%'
  UNION SELECT id FROM tbl_purchase_order WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_purchase_return_detail WHERE return_id IN (SELECT id FROM tbl_purchase_return WHERE description LIKE '$descriptionPrefix%');
DELETE FROM tbl_purchase_return WHERE description LIKE '$descriptionPrefix%';
DELETE FROM tbl_receive_sheet_detail WHERE sheet_id IN (SELECT id FROM tbl_receive_sheet WHERE description LIKE '$descriptionPrefix%') OR id='$otherReceiveDetailId';
DELETE FROM tbl_receive_sheet WHERE description LIKE '$descriptionPrefix%' OR id='$otherReceiveId';
DELETE FROM tbl_purchase_order_detail WHERE order_id IN (SELECT id FROM tbl_purchase_order WHERE description LIKE '$descriptionPrefix%') OR id='$otherOrderDetailId';
DELETE FROM tbl_purchase_order WHERE description LIKE '$descriptionPrefix%' OR id='$otherOrderId';
DELETE FROM tbl_product_stock_serial WHERE product_id IN ('$productId','$otherProductId');
DELETE FROM tbl_product_stock_batch WHERE product_id IN ('$productId','$otherProductId') AND sc_id='$scId';
DELETE FROM tbl_product_stock WHERE product_id IN ('$productId','$otherProductId') AND sc_id='$scId';
DELETE FROM base_data_product_purchase WHERE id IN ('$productId','$otherProductId');
DELETE FROM base_data_product WHERE id IN ('$productId','$otherProductId');
DELETE FROM base_data_supplier WHERE id='$supplierId';
DELETE FROM base_data_store_center WHERE id='$scId';
"@

try {
    Invoke-SmokeSql -Sql $cleanupSql

    $traceColumns = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT COUNT(*) FROM information_schema.columns
WHERE table_schema='$Database' AND table_name='tbl_receive_sheet_detail'
AND column_name IN ('batch_number','serial_number_list','production_date','expiry_date');
"@)
    if ($traceColumns.Count -ne 1 -or [int]$traceColumns[0] -ne 4) {
        throw 'V1.24 traceability columns are not applied to the local smoke database.'
    }

    $returnSerialColumns = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT COUNT(*) FROM information_schema.columns
WHERE table_schema='$Database' AND table_name='tbl_purchase_return_detail'
AND column_name='serial_number_list';
"@)
    if ($returnSerialColumns.Count -ne 1 -or [int]$returnSerialColumns[0] -ne 1) {
        throw 'V1.25 purchase-return serial traceability is not applied to the local smoke database.'
    }

    Invoke-SmokeSql -Sql @"
INSERT INTO base_data_store_center
(id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$scId','V124-$runKey-SC','V124 warehouse',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_supplier
(id,code,name,mnemonic_code,manage_type,settle_type,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$supplierId','V124-$runKey-SUP','V124 supplier','V124',1,2,1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product
(id,code,name,sku_code,category_id,brand_id,product_type,tax_rate,sale_tax_rate,spec,unit,available,is_batch,is_serial,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$productId','V124-$runKey-P1','V124 product 1','V124-$runKey-SKU1','1','1',1,13,13,'FLOW','EA',1,1,1,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$otherProductId','V124-$runKey-P2','V124 product 2','V124-$runKey-SKU2','1','1',1,13,13,'FLOW','EA',1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product_purchase (id,price) VALUES
('$productId',10),('$otherProductId',20);
INSERT INTO tbl_purchase_order
(id,code,sc_id,supplier_id,total_num,total_gift_num,total_amount,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time,status) VALUES
('$otherOrderId','V124-$runKey-PO2','$scId','$supplierId',5,0,100,'$descriptionPrefix other order','smoke','smoke',NOW(),'smoke','smoke',NOW(),2);
INSERT INTO tbl_purchase_order_detail
(id,order_id,product_id,order_num,tax_price,is_gift,tax_rate,description,order_no,receive_num) VALUES
('$otherOrderDetailId','$otherOrderId','$otherProductId',5,20,0,13,'smoke',1,0);
INSERT INTO tbl_receive_sheet
(id,code,sc_id,supplier_id,payment_date,receive_date,purchase_order_id,total_num,total_gift_num,total_amount,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time,status,settle_status) VALUES
('$otherReceiveId','V124-$runKey-RS2','$scId','$supplierId',CURRENT_DATE,CURRENT_DATE,'$otherOrderId',5,0,100,'$descriptionPrefix other receive','smoke','smoke',NOW(),'smoke','smoke',NOW(),2,0);
INSERT INTO tbl_receive_sheet_detail
(id,sheet_id,product_id,order_num,tax_price,is_gift,tax_rate,description,order_no,purchase_order_detail_id,return_num,batch_number) VALUES
('$otherReceiveDetailId','$otherReceiveId','$otherProductId',5,20,0,13,'smoke',1,'$otherOrderDetailId',0,'DEFAULT');
"@

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    $headers = @{ 'X-Auth-Token' = $login.data.token }

    $config = Invoke-ErpJson -Uri "$baseUri/purchase/config" -Headers $headers
    if (-not $config.data.receiveRequirePurchase -or -not $config.data.purchaseReturnRequireReceive) {
        throw 'The local purchase config must require linked purchase and receive documents for this probe.'
    }

    $orderBody = @{
        scId = $scId
        supplierId = $supplierId
        expectArriveDate = '2026-08-31'
        products = @(@{ productId = $productId; purchasePrice = 10; purchaseNum = 10; description = 'flow line' })
        payTypes = @(@{ id = '1'; payAmount = 100; text = 'flow payment' })
        description = "$descriptionPrefix order"
    } | ConvertTo-Json -Depth 6
    $createdOrder = Invoke-ErpJson -Uri "$baseUri/purchase/order" -Method Post -Headers $headers -JsonBody $orderBody
    $orderId = [string]$createdOrder.data
    if (-not $orderId) { throw 'Purchase-order creation returned an empty ID.' }
    Invoke-ErpJson -Uri "$baseUri/purchase/order/approve/pass" -Method Patch -Headers $headers -JsonBody (@{ id = $orderId; description = "$descriptionPrefix order approved" } | ConvertTo-Json) | Out-Null
    $order = Invoke-ErpJson -Uri "$baseUri/purchase/order?id=$orderId" -Headers $headers
    $orderDetailId = [string]$order.data.details[0].id
    if (-not $orderDetailId) {
        throw 'Purchase-order detail was not initialized correctly.'
    }

    $receiveBase = @{
        scId = $scId
        supplierId = $supplierId
        allowModifyPaymentDate = $false
        receiveDate = '2026-08-15'
        purchaseOrderId = $orderId
        required = $true
        description = "$descriptionPrefix receive"
    }
    $invalidReceive = $receiveBase.Clone()
    $invalidReceive.products = @(@{ productId = $productId; receiveNum = 1; purchaseOrderDetailId = 'missing-purchase-detail' })
    Assert-ErpRejected -Uri "$baseUri/purchase/receive/sheet" -Headers $headers -JsonBody ($invalidReceive | ConvertTo-Json -Depth 6)
    $invalidReceive.products = @(@{ productId = $otherProductId; receiveNum = 1; purchaseOrderDetailId = $otherOrderDetailId })
    Assert-ErpRejected -Uri "$baseUri/purchase/receive/sheet" -Headers $headers -JsonBody ($invalidReceive | ConvertTo-Json -Depth 6)
    $invalidReceive.products = @(@{ productId = $otherProductId; receiveNum = 1; purchaseOrderDetailId = $orderDetailId })
    Assert-ErpRejected -Uri "$baseUri/purchase/receive/sheet" -Headers $headers -JsonBody ($invalidReceive | ConvertTo-Json -Depth 6)

    $receiveBody = $receiveBase.Clone()
    $receiveBody.products = @(@{
        productId = $productId
        receiveNum = 6
        purchaseOrderDetailId = $orderDetailId
        batchNumber = 'V124-BATCH'
        serialNumberList = ($serialNumbers -join ',')
        productionDate = '2026-08-01'
        expiryDate = '2027-08-01'
        description = 'flow receipt line'
    })
    $createdReceive = Invoke-ErpJson -Uri "$baseUri/purchase/receive/sheet" -Method Post -Headers $headers -JsonBody ($receiveBody | ConvertTo-Json -Depth 6)
    $receiveId = [string]$createdReceive.data
    if (-not $receiveId) { throw 'Receive-sheet creation returned an empty ID.' }
    $receive = Invoke-ErpJson -Uri "$baseUri/purchase/receive/sheet?id=$receiveId" -Headers $headers
    $receiveDetailId = [string]$receive.data.details[0].id
    if ($receive.data.details[0].batchNumber -ne 'V124-BATCH' -or $receive.data.details[0].productionDate -ne '2026-08-01') {
        throw 'Receive-sheet traceability fields did not round-trip through the API.'
    }

    $overReceive = $receiveBase.Clone()
    $overReceive.description = "$descriptionPrefix over receive"
    $overReceive.products = @(@{ productId = $productId; receiveNum = 5; purchaseOrderDetailId = $orderDetailId })
    Assert-ErpRejected -Uri "$baseUri/purchase/receive/sheet" -Headers $headers -JsonBody ($overReceive | ConvertTo-Json -Depth 6)
    Invoke-ErpJson -Uri "$baseUri/purchase/receive/sheet/approve/pass" -Method Patch -Headers $headers -JsonBody (@{ id = $receiveId; description = "$descriptionPrefix received" } | ConvertTo-Json) | Out-Null

    $stockAfterReceive = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$productId';")
    if ($stockAfterReceive.Count -ne 1 -or [int]$stockAfterReceive[0] -ne 6) {
        throw "Receipt approval did not add six units to stock: $($stockAfterReceive -join ',')"
    }
    $serialsAfterReceive = @(Invoke-SmokeSql -ReturnOutput -Sql "SELECT CONCAT(serial_number, ':', stock_status) FROM tbl_product_stock_serial WHERE product_id='$productId' ORDER BY serial_number;")
    if ($serialsAfterReceive.Count -ne 6 -or @($serialsAfterReceive | Where-Object { $_ -notmatch ':1$' }).Count -ne 0) {
        throw "Receipt approval did not create six in-stock serials: $($serialsAfterReceive -join ',')"
    }
    $availableSerials = Invoke-ErpJson -Uri "$baseUri/purchase/return/available-serials?receiveSheetDetailId=$receiveDetailId" -Headers $headers
    if (@($availableSerials.data).Count -ne 6) {
        throw "Available-serial endpoint returned $(@($availableSerials.data).Count) rows instead of six."
    }

    $returnBase = @{
        scId = $scId
        supplierId = $supplierId
        receiveSheetId = $receiveId
        required = $true
        payTypes = @(@{ id = '1'; payAmount = 20; text = 'flow refund' })
        description = "$descriptionPrefix return"
    }
    $invalidReturn = $returnBase.Clone()
    $invalidReturn.products = @(@{ productId = $productId; returnNum = 1; receiveSheetDetailId = 'missing-receive-detail' })
    Assert-ErpRejected -Uri "$baseUri/purchase/return" -Headers $headers -JsonBody ($invalidReturn | ConvertTo-Json -Depth 6)
    $invalidReturn.products = @(@{ productId = $otherProductId; returnNum = 1; receiveSheetDetailId = $otherReceiveDetailId })
    Assert-ErpRejected -Uri "$baseUri/purchase/return" -Headers $headers -JsonBody ($invalidReturn | ConvertTo-Json -Depth 6)
    $invalidReturn.products = @(@{ productId = $otherProductId; returnNum = 1; receiveSheetDetailId = $receiveDetailId })
    Assert-ErpRejected -Uri "$baseUri/purchase/return" -Headers $headers -JsonBody ($invalidReturn | ConvertTo-Json -Depth 6)
    $invalidReturn.products = @(@{ productId = $productId; returnNum = 2; receiveSheetDetailId = $receiveDetailId; serialNumberList = $serialNumbers[0] })
    Assert-ErpRejected -Uri "$baseUri/purchase/return" -Headers $headers -JsonBody ($invalidReturn | ConvertTo-Json -Depth 6)
    $invalidReturn.products = @(@{ productId = $productId; returnNum = 2; receiveSheetDetailId = $receiveDetailId; serialNumberList = "$($serialNumbers[0]),$($serialNumbers[0])" })
    Assert-ErpRejected -Uri "$baseUri/purchase/return" -Headers $headers -JsonBody ($invalidReturn | ConvertTo-Json -Depth 6)
    $invalidReturn.products = @(@{ productId = $productId; returnNum = 1; receiveSheetDetailId = $receiveDetailId; serialNumberList = "V125-$runKey-NOT-FROM-RECEIPT" })
    Assert-ErpRejected -Uri "$baseUri/purchase/return" -Headers $headers -JsonBody ($invalidReturn | ConvertTo-Json -Depth 6)

    $returnBody = $returnBase.Clone()
    $returnBody.products = @(@{ productId = $productId; returnNum = 2; receiveSheetDetailId = $receiveDetailId; serialNumberList = "$($serialNumbers[0]),$($serialNumbers[1])"; description = 'flow return line' })
    $createdReturn = Invoke-ErpJson -Uri "$baseUri/purchase/return" -Method Post -Headers $headers -JsonBody ($returnBody | ConvertTo-Json -Depth 6)
    $returnId = [string]$createdReturn.data
    if (-not $returnId) { throw 'Purchase-return creation returned an empty ID.' }
    $returnDetail = Invoke-ErpJson -Uri "$baseUri/purchase/return?id=$returnId" -Headers $headers
    if ($returnDetail.data.details[0].serialNumberList -ne "$($serialNumbers[0]),$($serialNumbers[1])") {
        throw 'Purchase-return serial selection did not round-trip through the API.'
    }

    $overReturn = $returnBase.Clone()
    $overReturn.description = "$descriptionPrefix over return"
    $overReturn.payTypes = @(@{ id = '1'; payAmount = 50; text = 'over refund' })
    $overReturn.products = @(@{ productId = $productId; returnNum = 5; receiveSheetDetailId = $receiveDetailId })
    Assert-ErpRejected -Uri "$baseUri/purchase/return" -Headers $headers -JsonBody ($overReturn | ConvertTo-Json -Depth 6)
    Invoke-ErpJson -Uri "$baseUri/purchase/return/approve/pass" -Method Patch -Headers $headers -JsonBody (@{ id = $returnId; description = "$descriptionPrefix returned" } | ConvertTo-Json) | Out-Null

    $returnedSerialState = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_status FROM tbl_product_stock_serial WHERE serial_number='$($serialNumbers[0])';
SELECT stock_status FROM tbl_product_stock_serial WHERE serial_number='$($serialNumbers[1])';
SELECT stock_status FROM tbl_product_stock_serial WHERE serial_number='$($serialNumbers[2])';
"@)
    if ($returnedSerialState.Count -ne 3 -or [int]$returnedSerialState[0] -ne 0 -or [int]$returnedSerialState[1] -ne 0 -or [int]$returnedSerialState[2] -ne 1) {
        throw "Partial return updated the wrong serial state: $($returnedSerialState -join ',')"
    }

    $usedSerialReturn = $returnBase.Clone()
    $usedSerialReturn.description = "$descriptionPrefix used serial"
    $usedSerialReturn.products = @(@{ productId = $productId; returnNum = 1; receiveSheetDetailId = $receiveDetailId; serialNumberList = $serialNumbers[0] })
    Assert-ErpRejected -Uri "$baseUri/purchase/return" -Headers $headers -JsonBody ($usedSerialReturn | ConvertTo-Json -Depth 6)

    $raceReturn = $returnBase.Clone()
    $raceReturn.description = "$descriptionPrefix serial race"
    $raceReturn.products = @(@{ productId = $productId; returnNum = 2; receiveSheetDetailId = $receiveDetailId; serialNumberList = "$($serialNumbers[2]),$($serialNumbers[3])" })
    $createdRaceReturn = Invoke-ErpJson -Uri "$baseUri/purchase/return" -Method Post -Headers $headers -JsonBody ($raceReturn | ConvertTo-Json -Depth 6)
    $raceReturnId = [string]$createdRaceReturn.data
    Invoke-SmokeSql -Sql "UPDATE tbl_product_stock_serial SET stock_status=0 WHERE serial_number='$($serialNumbers[3])';"
    Assert-ErpRejected -Uri "$baseUri/purchase/return/approve/pass" -Method Patch -Headers $headers -JsonBody (@{ id = $raceReturnId; description = "$descriptionPrefix race rejected" } | ConvertTo-Json)
    $raceRollbackState = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$productId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$productId' AND batch_number='V124-BATCH';
SELECT stock_status FROM tbl_product_stock_serial WHERE serial_number='$($serialNumbers[2])';
SELECT status FROM tbl_purchase_return WHERE id='$raceReturnId';
"@)
    if ($raceRollbackState.Count -ne 4 -or [int]$raceRollbackState[0] -ne 4 -or [int]$raceRollbackState[1] -ne 4 -or [int]$raceRollbackState[2] -ne 1 -or [int]$raceRollbackState[3] -ne 0) {
        throw "Serial race did not roll back the purchase return transaction: $($raceRollbackState -join ',')"
    }
    Invoke-SmokeSql -Sql "UPDATE tbl_product_stock_serial SET stock_status=1 WHERE serial_number='$($serialNumbers[3])';"

    $finalState = @(Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$scId' AND product_id='$productId';
SELECT receive_num FROM tbl_purchase_order_detail WHERE id='$orderDetailId';
SELECT return_num FROM tbl_receive_sheet_detail WHERE id='$receiveDetailId';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$scId' AND product_id='$productId' AND batch_number='V124-BATCH';
"@)
    if ($finalState.Count -ne 4 -or [int]$finalState[0] -ne 4 -or [int]$finalState[1] -ne 6 -or [int]$finalState[2] -ne 4 -or [int]$finalState[3] -ne 4) {
        throw "Purchase-flow state is inconsistent: $($finalState -join ',')"
    }

    Write-Host "Purchase-flow verification passed: order=$orderId receive=$receiveId return=$returnId; linkage guards, batch/serial traceability, partial serial return and transactional rollback verified."
} finally {
    Invoke-SmokeSql -Sql $cleanupSql
}
