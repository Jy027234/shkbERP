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
$baseUri = $BaseUrl.TrimEnd('/')
$parsedBaseUri = [Uri]$baseUri
if ($parsedBaseUri.Host -notin @('127.0.0.1', 'localhost')) {
    throw 'The write probe is restricted to a local API endpoint.'
}
if (-not $TenantName) {
    # Keep the source ASCII-only for Windows PowerShell 5 UTF-8 compatibility.
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
        $output = $Sql | & docker exec -i $DbContainer mysql "-u$DbUsername" "-p$DbPassword" -N -B $Database 2>$null
        $exitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    if ($exitCode -ne 0) {
        throw "Smoke database command failed: $($output -join [Environment]::NewLine)"
    }
    if ($ReturnOutput) { return @($output) }
}

function Invoke-ErpJson {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [ValidateSet('Get', 'Post', 'Patch', 'Delete')][string]$Method = 'Get',
        [hashtable]$Headers,
        [string]$JsonBody,
        [hashtable]$FormBody
    )

    $arguments = @{
        Uri = $Uri
        Method = $Method
        TimeoutSec = 30
    }
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

$prefix = 'v117-'
$orderId = $null
$orderDetailId = $null
$sheetId = $null
$cleanupSql = @"
DELETE FROM op_logs WHERE extra LIKE '%V1.17 smoke%';
DELETE FROM tbl_order_time_line WHERE order_id IN (SELECT id FROM tbl_material_out_sheet WHERE description LIKE 'V1.17 smoke%');
DELETE FROM tbl_product_stock_log WHERE biz_id IN (SELECT id FROM tbl_material_out_sheet WHERE description LIKE 'V1.17 smoke%');
DELETE FROM tbl_material_out_sheet_detail_serial WHERE sheet_id IN (SELECT id FROM tbl_material_out_sheet WHERE description LIKE 'V1.17 smoke%');
DELETE FROM tbl_material_out_sheet_detail WHERE sheet_id IN (SELECT id FROM tbl_material_out_sheet WHERE description LIKE 'V1.17 smoke%');
DELETE FROM tbl_material_out_sheet WHERE description LIKE 'V1.17 smoke%';
DELETE FROM shkb_material_order_detail WHERE order_id IN (SELECT id FROM shkb_material_order WHERE material_apply_id='$($prefix)apply');
DELETE FROM shkb_material_order WHERE material_apply_id='$($prefix)apply';
DELETE FROM shkb_contract_task_material_apply WHERE id='$($prefix)apply' OR task_id='$($prefix)task';
DELETE FROM shkb_contract_task_non_part_file WHERE task_id='$($prefix)task';
DELETE FROM shkb_contract_task_non_part_product WHERE task_id='$($prefix)task';
DELETE FROM shkb_contract_task_work_card_product WHERE task_id='$($prefix)task';
DELETE FROM tbl_product_stock_serial WHERE product_id='$($prefix)product';
DELETE FROM tbl_product_stock_batch WHERE product_id='$($prefix)product' AND sc_id='$($prefix)sc';
DELETE FROM tbl_product_stock_log WHERE product_id='$($prefix)product' AND biz_code LIKE 'V117%';
DELETE FROM tbl_product_stock WHERE product_id='$($prefix)product' AND sc_id='$($prefix)sc';
DELETE FROM shkb_contract_task WHERE id='$($prefix)task';
DELETE FROM shkb_contract WHERE id='$($prefix)contract';
DELETE FROM base_data_product WHERE id='$($prefix)product';
DELETE FROM base_data_store_center WHERE id='$($prefix)sc';
DELETE FROM base_data_machine_type WHERE id='$($prefix)machine';
"@

try {
    Invoke-SmokeSql -Sql $cleanupSql
    Invoke-SmokeSql -Sql @"
INSERT INTO base_data_machine_type (id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$($prefix)machine','V117-MT','V1.17 machine',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_store_center (id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$($prefix)sc','V117-SC','V117 warehouse',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product (id,code,name,short_name,sku_code,external_code,category_id,brand_id,part_number_id,machine_type_id,product_type,tax_rate,sale_tax_rate,spec,unit,weight,volume,available,is_batch,is_serial,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$($prefix)product','V117-P','V1.17 material',NULL,'V117-SKU',NULL,'1','1',NULL,'$($prefix)machine',1,13,13,'V117-SPEC','EA',NULL,NULL,1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO shkb_contract (id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time,part_number_id,contract_time,customer_id,contract_type,contract_status) VALUES
('$($prefix)contract','V117-CONTRACT','V1.17 contract',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),'$($prefix)product',NOW(),'1',1,0);
INSERT INTO shkb_contract_task (id,contract_id,task_status,material_status,create_by,create_by_id,create_time,update_by,update_by_id,update_time,is_material_issued) VALUES
('$($prefix)task','$($prefix)contract','executing','pending','smoke','smoke',NOW(),'smoke','smoke',NOW(),0);
INSERT INTO shkb_contract_task_non_part_product (id,task_id,product_id,quantity,reason) VALUES
('$($prefix)nonpart','$($prefix)task','$($prefix)product',2,'V1.17 smoke material');
INSERT INTO tbl_product_stock (id,sc_id,product_id,stock_num,tax_price,tax_amount) VALUES
('$($prefix)stock','$($prefix)sc','$($prefix)product',10,5.000000,50.00);
"@

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    if (-not $login.data.token) { throw 'Login succeeded without returning data.token.' }
    $headers = @{ 'X-Auth-Token' = $login.data.token }

    $invalidNonPartAccepted = $false
    try {
        $invalid = Invoke-RestMethod -Uri "$baseUri/shkb/contract-task/non-part/save?taskId=$($prefix)task&productId=$($prefix)product&quantity=0" `
            -Method Post -Headers $headers -TimeoutSec 30
        $invalidNonPartAccepted = $invalid.code -eq 200
    } catch {
        $invalidNonPartAccepted = $false
    }
    if ($invalidNonPartAccepted) { throw 'A zero non-part quantity was incorrectly accepted.' }

    $applyBody = @{ taskId = "$($prefix)task"; remark = 'V1.17 smoke application' } | ConvertTo-Json
    $createdApply = Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/material-apply/create" -Method Post -Headers $headers -JsonBody $applyBody
    $applyId = [string]$createdApply.data
    if (-not $applyId) { throw 'Material application creation returned an empty ID.' }
    Invoke-SmokeSql -Sql "UPDATE shkb_contract_task_material_apply SET id='$($prefix)apply', apply_code='V117-APPLY' WHERE id='$applyId';"
    $applyId = "$($prefix)apply"

    $applications = Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/material-apply/query?pageIndex=1&pageSize=20&applyCode=V117-APPLY&hasMaterialOrder=false" -Headers $headers
    if ($applications.data.datas.Count -ne 1 -or $applications.data.datas[0].hasMaterialOrder) {
        throw 'The new material application was not returned as order-free.'
    }

    $approveBody = @{ ids = @($applyId); approved = $true; comment = 'V1.17 smoke approved' } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/material-apply/approve" -Method Post -Headers $headers -JsonBody $approveBody | Out-Null

    $orderBody = @{ materialApplyId = $applyId; scId = "$($prefix)sc"; description = 'V1.17 smoke order' } | ConvertTo-Json
    $createdOrder = Invoke-ErpJson -Uri "$baseUri/material/order" -Method Post -Headers $headers -JsonBody $orderBody
    $orderId = [string]$createdOrder.data
    if (-not $orderId) { throw 'Material-order creation returned an empty ID.' }

    $order = Invoke-ErpJson -Uri "$baseUri/material/order?id=$orderId" -Headers $headers
    if ($order.data.totalNum -ne 2 -or $order.data.isOutFinish -or $order.data.details.Count -ne 1) {
        throw "Material-order detail mapping failed: $($order.data | ConvertTo-Json -Compress -Depth 5)"
    }
    $orderDetailId = [string]$order.data.details[0].id
    if ($order.data.details[0].orderNum -ne 2 -or $order.data.details[0].outNum -ne 0) {
        throw 'Material-order quantities were initialized incorrectly.'
    }

    $applications = Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/material-apply/query?pageIndex=1&pageSize=20&applyCode=V117-APPLY&hasMaterialOrder=true" -Headers $headers
    if ($applications.data.datas.Count -ne 1 -or -not $applications.data.datas[0].hasMaterialOrder) {
        throw 'The material application was not marked as having an order.'
    }

    $invalidSheetBody = @{
        scId = "$($prefix)sc"
        materialOrderId = $orderId
        description = 'V1.17 smoke invalid sheet'
        details = @(@{ productId = "$($prefix)product"; outNum = 0; orderNum = 2; materialOrderDetailId = $orderDetailId })
    } | ConvertTo-Json -Depth 5
    $invalidSheetAccepted = $false
    try {
        $invalidSheet = Invoke-RestMethod -Uri "$baseUri/material/out/sheet" -Method Post -Headers $headers `
            -ContentType 'application/json' -Body $invalidSheetBody -TimeoutSec 30
        $invalidSheetAccepted = $invalidSheet.code -eq 200
    } catch {
        $invalidSheetAccepted = $false
    }
    if ($invalidSheetAccepted) { throw 'A zero outbound quantity was incorrectly accepted.' }

    $sheetBody = @{
        scId = "$($prefix)sc"
        materialDate = '2026-08-13'
        materialOrderId = $orderId
        description = 'V1.17 smoke outbound sheet'
        details = @(@{
            productId = "$($prefix)product"
            outNum = 2
            orderNum = 2
            taxPrice = 5
            materialOrderDetailId = $orderDetailId
        })
    } | ConvertTo-Json -Depth 5
    $createdSheet = Invoke-ErpJson -Uri "$baseUri/material/out/sheet" -Method Post -Headers $headers -JsonBody $sheetBody
    $sheetId = [string]$createdSheet.data
    if (-not $sheetId) { throw 'Outbound-sheet creation returned an empty ID.' }

    $sheet = Invoke-ErpJson -Uri "$baseUri/material/out/sheet?id=$sheetId" -Headers $headers
    if ($sheet.data.status -ne 0 -or $sheet.data.totalNum -ne 2 -or $sheet.data.details.Count -ne 1) {
        throw "Outbound-sheet detail mapping failed: $($sheet.data | ConvertTo-Json -Compress -Depth 6)"
    }

    $passBody = @{ id = $sheetId; description = 'V1.17 smoke issued' } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/material/out/sheet/approve/pass" -Method Patch -Headers $headers -JsonBody $passBody | Out-Null

    $sheet = Invoke-ErpJson -Uri "$baseUri/material/out/sheet?id=$sheetId" -Headers $headers
    if ($sheet.data.status -ne 1) { throw 'Outbound sheet was not marked issued.' }
    $order = Invoke-ErpJson -Uri "$baseUri/material/order?id=$orderId" -Headers $headers
    if (-not $order.data.isOutFinish -or $order.data.details[0].outNum -ne 2) {
        throw 'Material-order completion was not synchronized after outbound approval.'
    }

    $state = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT stock_num FROM tbl_product_stock WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product';
SELECT quantity FROM tbl_product_stock_batch WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product' AND batch_number='DEFAULT';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id='$sheetId' AND stock_batch_id IS NOT NULL;
SELECT material_status FROM shkb_contract_task WHERE id='$($prefix)task';
"@
    if ($state.Count -ne 4 -or [int]$state[0] -ne 8 -or [int]$state[1] -ne 8 -or [int]$state[2] -lt 1 -or $state[3] -ne 'completed') {
        throw "Inventory or task state did not synchronize: $($state -join ',')"
    }

    $duplicateAccepted = $false
    try {
        $duplicate = Invoke-RestMethod -Uri "$baseUri/material/order" -Method Post -Headers $headers `
            -ContentType 'application/json' -Body $orderBody -TimeoutSec 30
        $duplicateAccepted = $duplicate.code -eq 200
    } catch {
        $duplicateAccepted = $false
    }
    if ($duplicateAccepted) { throw 'A duplicate material order was incorrectly accepted.' }

    Write-Host "Material-flow write verification passed: apply=$applyId order=$orderId sheet=$sheetId, validation, inventory decrement, completion and duplicate guards."
}
finally {
    Invoke-SmokeSql -Sql $cleanupSql
}
