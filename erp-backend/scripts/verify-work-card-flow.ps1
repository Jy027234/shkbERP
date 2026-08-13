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
    $TenantName = -join [char[]](0x6D4B, 0x8BD5, 0x79DF, 0x6237)
}

function Invoke-SmokeSql {
    param([Parameter(Mandatory = $true)][string]$Sql)

    # mysql writes its command-line password warning to stderr even on success.
    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $output = $Sql | & docker exec -i $DbContainer mysql "-u$DbUsername" "-p$DbPassword" $Database 2>$null
        $exitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    if ($exitCode -ne 0) {
        throw "Smoke database command failed: $($output -join [Environment]::NewLine)"
    }
}

function Invoke-ErpJson {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [ValidateSet('Get', 'Post')][string]$Method = 'Get',
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

$cardCode = 'V116-SMOKE-WC'
$cardId = $null
$cardProductId = $null
$cleanupSql = @"
DELETE FROM shkb_contract_task_work_card_product WHERE task_id='v116-task';
DELETE FROM shkb_contract_task_work_card WHERE task_id='v116-task';
DELETE FROM shkb_contract_task_material_apply WHERE task_id='v116-task';
DELETE FROM shkb_work_card_file WHERE id='11600000000000000000000000000001';
DELETE FROM shkb_work_card_product WHERE work_card_id IN (SELECT id FROM shkb_work_card WHERE code='$cardCode');
DELETE FROM shkb_work_card WHERE code='$cardCode';
DELETE FROM shkb_contract_task WHERE id='v116-task';
DELETE FROM base_data_product WHERE id IN ('v116-part','v116-replacement');
DELETE FROM base_data_repair_type WHERE id='v116-repair';
DELETE FROM base_data_machine_type WHERE id IN ('v116-machine-part','v116-machine-replace');
"@

try {
    Invoke-SmokeSql -Sql $cleanupSql
    Invoke-SmokeSql -Sql @"
INSERT INTO base_data_machine_type (id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('v116-machine-part','V116-MT-P','V116 part machine',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW()),
('v116-machine-replace','V116-MT-R','V116 replacement machine',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_repair_type (id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('v116-repair','V116-RT','V116 repair',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product (id,code,name,short_name,sku_code,external_code,category_id,brand_id,part_number_id,machine_type_id,product_type,tax_rate,sale_tax_rate,spec,unit,weight,volume,available,is_batch,is_serial,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('v116-part','V116-PN','V116 card part',NULL,'V116-SKU-PN',NULL,'1','1',NULL,'v116-machine-part',1,13,13,'PN-SPEC','EA',NULL,NULL,1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('v116-replacement','V116-RP','V116 replacement',NULL,'V116-SKU-RP',NULL,'1','1',NULL,'v116-machine-replace',1,13,13,'RP-SPEC','PCS',NULL,NULL,1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO shkb_contract_task (id,contract_id,create_by,create_by_id,create_time,update_by,update_by_id,update_time,is_material_issued) VALUES
('v116-task','v116-contract-missing','smoke','smoke',NOW(),'smoke','smoke',NOW(),0);
"@

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    if (-not $login.data.token) {
        throw 'Login succeeded without returning data.token.'
    }
    $headers = @{ 'X-Auth-Token' = $login.data.token }

    $cardBody = @{
        code = $cardCode
        name = 'V1.16 smoke work card'
        partNumberId = 'v116-part'
        repairTypeId = 'v116-repair'
        approvalDate = '2026-08-13'
        available = $true
        description = 'temporary V1.16 verification'
        version = 'A'
    } | ConvertTo-Json
    $created = Invoke-ErpJson -Uri "$baseUri/shkb/work-card" -Method Post -Headers $headers -JsonBody $cardBody
    $cardId = [string]$created.data
    if (-not $cardId) { throw 'Work-card creation returned an empty ID.' }

    $filtered = Invoke-ErpJson -Uri "$baseUri/shkb/work-card/query?pageIndex=1&pageSize=20&partNumberCode=V116-PN" -Headers $headers
    if ($filtered.data.datas.Count -ne 1) {
        throw 'The part-number filter did not return exactly one work card.'
    }
    $card = $filtered.data.datas[0]
    if ($card.id -ne $cardId -or $card.partNumber -ne 'V116-PN' -or $card.machineTypeName -ne 'V116 part machine') {
        throw 'Work-card part-number mapping is incorrect.'
    }

    $addBody = @{ workCardId = $cardId; productIds = @('v116-replacement') } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/work-card/product/add" -Method Post -Headers $headers -JsonBody $addBody | Out-Null
    $products = Invoke-ErpJson -Uri "$baseUri/shkb/work-card/products?workCardId=$cardId" -Headers $headers
    if ($products.data.Count -ne 1 -or $products.data[0].quantity -ne 1 -or $products.data[0].productCode -ne 'V116-RP') {
        throw 'Initial replacement-product mapping failed.'
    }
    $cardProductId = [string]$products.data[0].id

    $quantityBody = @{
        workCardId = $cardId
        products = @(@{ id = $cardProductId; quantity = 3 })
    } | ConvertTo-Json -Depth 5
    Invoke-ErpJson -Uri "$baseUri/shkb/work-card/product/update-quantity" -Method Post -Headers $headers -JsonBody $quantityBody | Out-Null
    $products = Invoke-ErpJson -Uri "$baseUri/shkb/work-card/products?workCardId=$cardId" -Headers $headers
    if ($products.data[0].quantity -ne 3) {
        throw 'The positive replacement-product quantity was not persisted.'
    }

    $invalidBody = @{
        workCardId = $cardId
        products = @(@{ id = $cardProductId; quantity = 0 })
    } | ConvertTo-Json -Depth 5
    $invalidAccepted = $false
    try {
        $invalid = Invoke-RestMethod -Uri "$baseUri/shkb/work-card/product/update-quantity" -Method Post `
            -Headers $headers -ContentType 'application/json' -Body $invalidBody -TimeoutSec 30
        $invalidAccepted = $invalid.code -eq 200
    } catch {
        $invalidAccepted = $false
    }
    if ($invalidAccepted) { throw 'A zero replacement-product quantity was incorrectly accepted.' }
    $products = Invoke-ErpJson -Uri "$baseUri/shkb/work-card/products?workCardId=$cardId" -Headers $headers
    if ($products.data[0].quantity -ne 3) {
        throw 'The rejected quantity changed persisted data.'
    }

    Invoke-SmokeSql -Sql "INSERT INTO shkb_work_card_file (id,url,work_card_id,create_time,file_name,content_type,file_suffix,file_size) VALUES ('11600000000000000000000000000001','/smoke/work-card.txt','$cardId',NOW(),'work-card.txt','text/plain','txt','9B');"
    $files = Invoke-ErpJson -Uri "$baseUri/shkb/work-card/attachment/list?workCardId=$cardId" -Headers $headers
    if ($files.data.Count -ne 1 -or $files.data[0].fileName -ne 'work-card.txt' -or $files.data[0].contentType -ne 'text/plain') {
        throw 'Work-card attachment mapping failed.'
    }

    $taskCardBody = @{ taskId = 'v116-task'; workCardIds = @($cardId) } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/work-card/add" -Method Post -Headers $headers -JsonBody $taskCardBody | Out-Null
    $taskCards = Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/work-cards?taskId=v116-task" -Headers $headers
    if ($taskCards.data.Count -ne 1 -or $taskCards.data[0].workCardCode -ne $cardCode) {
        throw 'Task work-card mapping failed.'
    }

    $fallback = Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/replacement-parts?taskId=v116-task" -Headers $headers
    $part = $fallback.data[0]
    if ($fallback.data.Count -ne 1 -or $part.quantity -ne 3 -or $part.partNumberCode -ne 'V116-PN' `
        -or $part.productCode -ne 'V116-RP' -or $part.machineTypeName -ne 'V116 part machine' `
        -or $part.productMachineTypeName -ne 'V116 replacement machine') {
        throw "Fallback replacement-part mapping failed: $($part | ConvertTo-Json -Compress)"
    }

    $invalidSnapshotBody = @{
        taskId = 'v116-task'
        products = @(@{ workCardId = $cardId; productId = 'v116-replacement'; quantity = 0 })
    } | ConvertTo-Json -Depth 5
    $invalidSnapshotAccepted = $false
    try {
        $invalidSnapshot = Invoke-RestMethod -Uri "$baseUri/shkb/contract-task/replacement-parts/save" -Method Post `
            -Headers $headers -ContentType 'application/json' -Body $invalidSnapshotBody -TimeoutSec 30
        $invalidSnapshotAccepted = $invalidSnapshot.code -eq 200
    } catch {
        $invalidSnapshotAccepted = $false
    }
    if ($invalidSnapshotAccepted) { throw 'A zero task snapshot quantity was incorrectly accepted.' }

    $snapshotBody = @{
        taskId = 'v116-task'
        products = @(@{ workCardId = $cardId; productId = 'v116-replacement'; quantity = 4 })
    } | ConvertTo-Json -Depth 5
    Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/replacement-parts/save" -Method Post -Headers $headers -JsonBody $snapshotBody | Out-Null
    $specific = Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/replacement-parts?taskId=v116-task" -Headers $headers
    $part = $specific.data[0]
    if ($specific.data.Count -ne 1 -or $part.quantity -ne 4 -or $part.partNumberCode -ne 'V116-PN' `
        -or $part.productCode -ne 'V116-RP' -or $part.machineTypeName -ne 'V116 part machine' `
        -or $part.productMachineTypeName -ne 'V116 replacement machine') {
        throw "Task-specific replacement-part mapping failed: $($part | ConvertTo-Json -Compress)"
    }

    Write-Host "Work-card flow verification passed: card=$cardId product=$cardProductId, quantity validation, attachment, task association, fallback and snapshot mappings."
}
finally {
    Invoke-SmokeSql -Sql $cleanupSql
}
