[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$TenantName,
    [string]$Username = 'admin',
    [string]$Password = 'admin',
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$ApiContainer = 'kberp-api',
    [string]$Database = 'shkb_platform',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Net.Http
$baseUri = $BaseUrl.TrimEnd('/')
$parsedBaseUri = [Uri]$baseUri
if ($parsedBaseUri.Host -notin @('127.0.0.1', 'localhost')) {
    throw 'The contract write probe is restricted to a local API endpoint.'
}
if ($DbContainer -ne 'xingyun-smoke-mysql' -or $Database -ne 'shkb_platform') {
    throw 'The contract write probe is restricted to the local smoke database.'
}
if ($ApiContainer -ne 'kberp-api') {
    throw 'The contract write probe only cleans uploads from the local kberp-api container.'
}
if (-not $TenantName) {
    # Keep the source ASCII-only for Windows PowerShell 5 UTF-8 compatibility.
    $TenantName = -join [char[]](0x4E0A, 0x6D77, 0x51EF, 0x5954, 0x822A, 0x7A7A, 0x6280, 0x672F, 0x6709, 0x9650, 0x516C, 0x53F8)
}

function Invoke-SmokeSql {
    param([Parameter(Mandatory = $true)][string]$Sql)

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
    return @($output)
}

function Invoke-ErpJson {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [ValidateSet('Get', 'Post', 'Put', 'Delete')][string]$Method = 'Get',
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
        throw "$Method $Uri returned ERP code $($payload.code): $($payload.msg)."
    }
    return $payload
}

function Remove-SmokeUploadUrl {
    param([Parameter(Mandatory = $true)][string]$Url)

    $path = $Url
    $absoluteUri = $null
    if ([Uri]::TryCreate($path, [UriKind]::Absolute, [ref]$absoluteUri)) {
        $path = $absoluteUri.AbsolutePath
    }
    if ($path -notmatch '^/oss/1000/[0-9]{4}/[0-9]{2}/[0-9]{2}/[a-f0-9]{32}\.[A-Za-z0-9]+$') {
        throw "Refusing to clean an unexpected smoke upload URL: $Url"
    }
    $target = '/opt/data/upload' + $path.Substring(4)
    & docker exec $ApiContainer test -f $target
    if ($LASTEXITCODE -eq 0) {
        & docker exec $ApiContainer rm -f -- $target
        if ($LASTEXITCODE -ne 0) {
            throw "Failed to remove smoke upload: $target"
        }
    }
}

function Test-SmokeUploadUrlExists {
    param([Parameter(Mandatory = $true)][string]$Url)

    $path = $Url
    $absoluteUri = $null
    if ([Uri]::TryCreate($path, [UriKind]::Absolute, [ref]$absoluteUri)) {
        $path = $absoluteUri.AbsolutePath
    }
    if ($path -notmatch '^/oss/1000/[0-9]{4}/[0-9]{2}/[0-9]{2}/[a-f0-9]{32}\.[A-Za-z0-9]+$') {
        throw "Refusing to probe an unexpected smoke upload URL: $Url"
    }
    $target = '/opt/data/upload' + $path.Substring(4)
    & docker exec $ApiContainer test -f $target 2>$null
    return ($LASTEXITCODE -eq 0)
}

function Remove-ContractSmokeUploads {
    $urls = @(Invoke-SmokeSql -Sql @"
SELECT f.url
  FROM shkb_contract_file f
  JOIN shkb_contract c ON c.id=f.contract_id
 WHERE c.code IN ('$contractCode','$updatedCode');
"@)
    foreach ($url in $urls) {
        Remove-SmokeUploadUrl -Url ([string]$url)
    }
}

function Assert-ErpConflict {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [Parameter(Mandatory = $true)][hashtable]$Headers,
        [ValidateSet('Post', 'Put')][string]$Method = 'Post',
        [string]$JsonBody
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $request = [System.Net.Http.HttpRequestMessage]::new(
        [System.Net.Http.HttpMethod]::new($Method),
        $Uri
    )
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$request.Headers.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    if ($JsonBody) {
        $request.Content = [System.Net.Http.StringContent]::new($JsonBody, [Text.Encoding]::UTF8, 'application/json')
    }
    try {
        $response = $client.SendAsync($request).GetAwaiter().GetResult()
        try {
            $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            $body = if ($rawBody) { $rawBody | ConvertFrom-Json } else { $null }
            if ([int]$response.StatusCode -ne 409 -or $null -eq $body -or $body.code -eq 200 -or -not $body.msg) {
                throw "Expected HTTP 409 but received HTTP $([int]$response.StatusCode): $rawBody"
            }
        } finally {
            $response.Dispose()
        }
    } finally {
        $request.Dispose()
        $client.Dispose()
    }
}

function Send-ContractAttachment {
    param(
        [Parameter(Mandatory = $true)][string]$ContractId,
        [Parameter(Mandatory = $true)][hashtable]$Headers
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$client.DefaultRequestHeaders.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    $content = [System.Net.Http.MultipartFormDataContent]::new()
    $contractContent = [System.Net.Http.StringContent]::new($ContractId)
    $fileContent = [System.Net.Http.ByteArrayContent]::new([Text.Encoding]::UTF8.GetBytes('V1.23 contract attachment smoke'))
    $fileContent.Headers.ContentType = [System.Net.Http.Headers.MediaTypeHeaderValue]::new('text/plain')
    $content.Add($contractContent, 'contractId')
    $content.Add($fileContent, 'files', 'v123-contract.txt')
    try {
        $response = $client.PostAsync("$baseUri/shkb/contract/attachment/upload", $content).GetAwaiter().GetResult()
        try {
            $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            if ([int]$response.StatusCode -ne 200) {
                throw "Attachment upload returned HTTP $([int]$response.StatusCode): $rawBody"
            }
            $payload = $rawBody | ConvertFrom-Json
            if ($payload.code -ne 200 -or $payload.data.Count -ne 1) {
                throw "Attachment upload returned an invalid response: $rawBody"
            }
            return [string]$payload.data[0]
        } finally {
            $response.Dispose()
        }
    } finally {
        $content.Dispose()
        $client.Dispose()
    }
}

$contractCode = 'V123-CONTRACT-SMOKE'
$updatedCode = 'V123-CONTRACT-SMOKE-U'
$contractId = $null
$taskId = $null
$cleanupSql = @"
DELETE FROM shkb_contract_task_repair_status_record WHERE task_id IN (
  SELECT id FROM shkb_contract_task WHERE contract_id IN (
    SELECT id FROM shkb_contract WHERE code IN ('$contractCode','$updatedCode')));
DELETE FROM shkb_contract_task_work_card_product WHERE task_id IN (
  SELECT id FROM shkb_contract_task WHERE contract_id IN (
    SELECT id FROM shkb_contract WHERE code IN ('$contractCode','$updatedCode')));
DELETE FROM shkb_contract_task_work_card WHERE task_id IN (
  SELECT id FROM shkb_contract_task WHERE contract_id IN (
    SELECT id FROM shkb_contract WHERE code IN ('$contractCode','$updatedCode')));
DELETE FROM shkb_contract_task WHERE contract_id IN (
  SELECT id FROM shkb_contract WHERE code IN ('$contractCode','$updatedCode'));
DELETE FROM shkb_contract_file WHERE contract_id IN (
  SELECT id FROM shkb_contract WHERE code IN ('$contractCode','$updatedCode'));
DELETE FROM shkb_contract_repair WHERE contract_id IN (
  SELECT id FROM shkb_contract WHERE code IN ('$contractCode','$updatedCode'));
DELETE FROM shkb_contract WHERE code IN ('$contractCode','$updatedCode');
DELETE FROM base_data_repair_type WHERE id='v123-repair';
DELETE FROM base_data_customer WHERE id='v123-customer';
DELETE FROM base_data_product WHERE id='v123-part';
DELETE FROM base_data_machine_type WHERE id='v123-machine';
"@

try {
    Remove-ContractSmokeUploads
    Invoke-SmokeSql -Sql $cleanupSql | Out-Null
    Invoke-SmokeSql -Sql @"
INSERT INTO base_data_machine_type
  (id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time)
VALUES ('v123-machine','V123-MT','V123 machine',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product
  (id,code,name,short_name,sku_code,category_id,brand_id,machine_type_id,product_type,tax_rate,sale_tax_rate,available,is_batch,is_serial,create_by,create_by_id,create_time,update_by,update_by_id,update_time)
VALUES ('v123-part','V123-PN','V123 part',NULL,'V123-SKU','1','1','v123-machine',1,13,13,1,0,0,'smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_customer
  (id,code,name,mnemonic_code,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time)
VALUES ('v123-customer','V123-C','V123 customer','V123',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_repair_type
  (id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time)
VALUES ('v123-repair','V123-RT','V123 repair',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
"@ | Out-Null

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    $headers = @{ 'X-Auth-Token' = $login.data.token }

    $createBody = @{
        code = $contractCode
        name = 'V1.23 contract smoke'
        contractType = 1
        customerId = 'v123-customer'
        partNumberId = 'v123-part'
        repairTypeIds = @('v123-repair')
        serialNumber = 'V123-SN'
        contractTime = '2026-08-15 09:00:00'
        storageTime = '2026-08-15 10:00:00'
        plannedCompletionTime = '2026-08-20 18:00:00'
        contractPrice = 123.45
        replacementPartPrice = 6.78
        description = 'temporary contract flow verification'
    } | ConvertTo-Json -Depth 5
    Invoke-ErpJson -Uri "$baseUri/shkb/contract" -Method Post -Headers $headers -JsonBody $createBody | Out-Null

    $query = Invoke-ErpJson -Uri "$baseUri/shkb/contract/query?pageIndex=1&pageSize=20&code=$contractCode" -Headers $headers
    if ($query.data.datas.Count -ne 1) { throw 'Contract creation did not produce exactly one query result.' }
    $contractId = [string]$query.data.datas[0].id
    $detail = Invoke-ErpJson -Uri "$baseUri/shkb/contract?id=$contractId" -Headers $headers
    if ($detail.data.contractStatus -ne 0 -or $detail.data.repairTypes.Count -ne 1 -or $detail.data.repairTypes[0].id -ne 'v123-repair') {
        throw 'Created contract detail or repair-type mapping is incorrect.'
    }

    $updateBody = @{
        id = $contractId
        code = $updatedCode
        name = 'V1.23 contract smoke updated'
        available = $true
        contractType = 1
        customerId = 'v123-customer'
        partNumberId = 'v123-part'
        repairTypeIds = @('v123-repair')
        serialNumber = 'V123-SN-U'
        contractTime = '2026-08-15 09:00:00'
        storageTime = '2026-08-15 10:00:00'
        plannedCompletionTime = '2026-08-21 18:00:00'
        contractPrice = 200.00
        replacementPartPrice = 10.00
        description = 'updated by contract flow verification'
    } | ConvertTo-Json -Depth 5
    Invoke-ErpJson -Uri "$baseUri/shkb/contract" -Method Put -Headers $headers -JsonBody $updateBody | Out-Null
    $detail = Invoke-ErpJson -Uri "$baseUri/shkb/contract?id=$contractId" -Headers $headers
    if ($detail.data.code -ne $updatedCode -or $detail.data.serialNumber -ne 'V123-SN-U') {
        throw 'Contract update was not persisted.'
    }

    Assert-ErpConflict -Uri "$baseUri/shkb/contract/attachment/upload?contractId=v123-missing" -Headers $headers
    $attachmentId = Send-ContractAttachment -ContractId $contractId -Headers $headers
    $files = Invoke-ErpJson -Uri "$baseUri/shkb/contract/attachment/list?contractId=$contractId" -Headers $headers
    if ($files.data.Count -ne 1 -or $files.data[0].id -ne $attachmentId -or $files.data[0].fileName -ne 'v123-contract.txt') {
        throw 'Contract attachment upload/list mapping failed.'
    }
    $attachmentUrl = [string]$files.data[0].url
    if (-not (Test-SmokeUploadUrlExists -Url $attachmentUrl)) {
        throw 'Contract attachment physical file missing after upload.'
    }
    Invoke-ErpJson -Uri "$baseUri/shkb/contract/attachment/$attachmentId" -Method Delete -Headers $headers | Out-Null
    if (Test-SmokeUploadUrlExists -Url $attachmentUrl) {
        throw 'Contract attachment physical file still present after delete.'
    }
    Remove-SmokeUploadUrl -Url $attachmentUrl

    $taskBody = @{ contractId = $contractId } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/contract/create-task" -Method Post -Headers $headers -JsonBody $taskBody | Out-Null
    Assert-ErpConflict -Uri "$baseUri/shkb/contract/create-task" -Headers $headers -JsonBody $taskBody

    $taskRows = @(Invoke-SmokeSql -Sql "SELECT id,task_status,repair_status,material_status FROM shkb_contract_task WHERE contract_id='$contractId';")
    if ($taskRows.Count -ne 1) { throw 'Contract task creation did not produce exactly one task.' }
    $taskColumns = $taskRows[0] -split "`t"
    $taskId = $taskColumns[0]
    if ($taskColumns[1] -ne 'WAIT_EVALUATION' -or $taskColumns[2] -ne 'WAIT_CHECK' -or $taskColumns[3] -ne 'pending') {
        throw "Initial task states are incorrect: $($taskRows[0])"
    }
    $recordCount = @(Invoke-SmokeSql -Sql "SELECT COUNT(*) FROM shkb_contract_task_repair_status_record WHERE task_id='$taskId';")
    if ($recordCount.Count -ne 1 -or $recordCount[0] -ne '1') {
        throw 'Initial repair-status record was not created exactly once.'
    }

    $query = Invoke-ErpJson -Uri "$baseUri/shkb/contract/query?pageIndex=1&pageSize=20&code=$updatedCode" -Headers $headers
    if ($query.data.datas[0].contractStatus -ne 1 -or $query.data.datas[0].taskStatus -ne 'WAIT_EVALUATION') {
        throw 'Contract list did not reflect the generated task state.'
    }

    Write-Host "Contract flow verification passed: contract=$contractId task=$taskId, create/update, attachment ownership, initial task state and duplicate guard."
} finally {
    Remove-ContractSmokeUploads
    Invoke-SmokeSql -Sql $cleanupSql | Out-Null
}
