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
    throw 'The equipment write probe is restricted to a local API endpoint.'
}
if ($DbContainer -ne 'xingyun-smoke-mysql' -or $Database -ne 'shkb_platform') {
    throw 'The equipment write probe is restricted to the local smoke database.'
}
if ($ApiContainer -ne 'kberp-api') {
    throw 'The equipment write probe only cleans uploads from the local kberp-api container.'
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

function Assert-ErpConflict {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [Parameter(Mandatory = $true)][hashtable]$Headers,
        [ValidateSet('Post', 'Put')][string]$Method = 'Post',
        [string]$JsonBody
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $request = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new($Method), $Uri)
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

function Send-ErpMultipart {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [Parameter(Mandatory = $true)][hashtable]$Headers,
        [Parameter(Mandatory = $true)][hashtable]$Fields,
        [string]$FileName,
        [string]$FileText,
        [int]$ExpectedStatus = 200
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$client.DefaultRequestHeaders.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    $content = [System.Net.Http.MultipartFormDataContent]::new()
    foreach ($entry in $Fields.GetEnumerator()) {
        $content.Add([System.Net.Http.StringContent]::new([string]$entry.Value), [string]$entry.Key)
    }
    if ($FileName) {
        $fileContent = [System.Net.Http.ByteArrayContent]::new([Text.Encoding]::UTF8.GetBytes($FileText))
        $fileContent.Headers.ContentType = [System.Net.Http.Headers.MediaTypeHeaderValue]::new('text/plain')
        $content.Add($fileContent, 'files', $FileName)
    }
    try {
        $response = $client.PostAsync($Uri, $content).GetAwaiter().GetResult()
        try {
            $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            if ([int]$response.StatusCode -ne $ExpectedStatus) {
                throw "Multipart POST expected HTTP $ExpectedStatus but received HTTP $([int]$response.StatusCode): $rawBody"
            }
            $payload = if ($rawBody) { $rawBody | ConvertFrom-Json } else { $null }
            if ($null -eq $payload -or ($ExpectedStatus -eq 200 -and $payload.code -ne 200) -or ($ExpectedStatus -eq 409 -and ($payload.code -eq 200 -or -not $payload.msg))) {
                throw "Multipart POST returned an invalid ERP response: $rawBody"
            }
            return $payload
        } finally {
            $response.Dispose()
        }
    } finally {
        $content.Dispose()
        $client.Dispose()
    }
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

$toolCode = 'EQUIPMENT-FLOW-TOOL'
$deviceCode = 'EQUIPMENT-FLOW-DEVICE'
$missingId = 'equipment-flow-missing-parent'
$capturedUploadUrls = [System.Collections.Generic.List[string]]::new()
$cleanupSql = @"
DELETE FROM shkb_tool_record_file WHERE record_id IN (
  SELECT id FROM shkb_tool_record WHERE tool_id IN (SELECT id FROM shkb_tool WHERE code='$toolCode'));
DELETE FROM shkb_tool_record WHERE tool_id IN (SELECT id FROM shkb_tool WHERE code='$toolCode');
DELETE FROM shkb_tool_file WHERE tool_id IN (SELECT id FROM shkb_tool WHERE code='$toolCode');
DELETE FROM shkb_device_record WHERE device_id IN (SELECT id FROM shkb_device WHERE code='$deviceCode');
DELETE FROM shkb_device_file WHERE device_id IN (SELECT id FROM shkb_device WHERE code='$deviceCode');
DELETE FROM shkb_tool WHERE code='$toolCode';
DELETE FROM shkb_device WHERE code='$deviceCode';
"@

function Get-EquipmentSmokeUploadUrls {
    return @(Invoke-SmokeSql -Sql @"
SELECT f.url FROM shkb_tool_file f JOIN shkb_tool t ON t.id=f.tool_id WHERE t.code='$toolCode'
UNION ALL
SELECT f.url FROM shkb_tool_record_file f JOIN shkb_tool_record r ON r.id=f.record_id
  JOIN shkb_tool t ON t.id=r.tool_id WHERE t.code='$toolCode'
UNION ALL
SELECT f.url FROM shkb_device_file f JOIN shkb_device d ON d.id=f.device_id WHERE d.code='$deviceCode';
"@)
}

try {
    foreach ($url in @(Get-EquipmentSmokeUploadUrls)) { Remove-SmokeUploadUrl -Url ([string]$url) }
    Invoke-SmokeSql -Sql $cleanupSql | Out-Null

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    $headers = @{ 'X-Auth-Token' = $login.data.token }

    $missingRecord = @{
        deviceId = $missingId
        maintenancenUser = 'equipment smoke'
        maintenanceTime = '2026-08-15'
        description = 'must be rejected'
    } | ConvertTo-Json
    Assert-ErpConflict -Uri "$baseUri/shkb/device/record" -Headers $headers -JsonBody $missingRecord
    Send-ErpMultipart -Uri "$baseUri/shkb/tool/record" -Headers $headers -ExpectedStatus 409 -Fields @{
        toolId = $missingId
        maintenancenUser = 'equipment smoke'
        maintenanceTime = '2026-08-15'
        certificateNumber = 'MISSING'
    } | Out-Null
    Send-ErpMultipart -Uri "$baseUri/shkb/tool/attachment/upload" -Headers $headers -ExpectedStatus 409 -Fields @{ toolId = $missingId } -FileName 'orphan-tool.txt' -FileText 'must not be stored' | Out-Null
    Send-ErpMultipart -Uri "$baseUri/shkb/device/attachment/upload" -Headers $headers -ExpectedStatus 409 -Fields @{ deviceId = $missingId } -FileName 'orphan-device.txt' -FileText 'must not be stored' | Out-Null

    $toolCreate = Send-ErpMultipart -Uri "$baseUri/shkb/tool" -Headers $headers -Fields @{
        managementArea = 'FLOW'
        name = 'Equipment flow tool'
        code = $toolCode
        certificateNumber = 'TOOL-CERT-1'
        specification = 'FLOW-SPEC'
        model = 'FLOW-MODEL'
        standard = 'FLOW-STANDARD'
        precision = '0.01'
        storageLocation = 'FLOW-LOCATION'
        lastMaintenanceTime = '2026-08-01'
        nextMaintenanceTime = '2026-08-31'
        calibrationPeriod = 30
        lastMaintenanceUnit = 'FLOW-UNIT'
        maintenancenUser = 'equipment smoke'
        recordCertificateNumber = 'RECORD-CERT-1'
        available = 'true'
        description = 'temporary equipment flow verification'
    } -FileName 'tool-initial-record.txt' -FileText 'initial tool record attachment'
    $toolId = [string]$toolCreate.data
    if (-not $toolId) { throw 'Tool creation did not return an ID.' }

    $toolDetail = Invoke-ErpJson -Uri "$baseUri/shkb/tool/$toolId" -Headers $headers
    if ($toolDetail.data.lastMaintenanceTime -ne '2026-08-01' -or $toolDetail.data.nextMaintenanceTime -ne '2026-08-31') {
        throw 'Tool creation did not calculate the measurement dates in days.'
    }
    $initialRecords = Invoke-ErpJson -Uri "$baseUri/shkb/tool/record/query?pageIndex=1&pageSize=20&toolId=$toolId" -Headers $headers
    if ($initialRecords.data.datas.Count -ne 1 -or $initialRecords.data.datas[0].attachments.Count -ne 1) {
        throw 'Tool creation did not create its initial measurement record and attachment.'
    }
    $capturedUploadUrls.Add([string]$initialRecords.data.datas[0].attachments[0].url)

    $laterRecord = Send-ErpMultipart -Uri "$baseUri/shkb/tool/record" -Headers $headers -Fields @{
        toolId = $toolId
        maintenancenUser = 'equipment smoke 2'
        maintenanceTime = '2026-08-10'
        certificateNumber = 'RECORD-CERT-2'
        description = 'later measurement'
    } -FileName 'tool-later-record.txt' -FileText 'later tool record attachment'
    $laterRecordId = [string]$laterRecord.data
    $toolAfterRecord = Invoke-ErpJson -Uri "$baseUri/shkb/tool/$toolId" -Headers $headers
    if ($toolAfterRecord.data.certificateNumber -ne 'RECORD-CERT-2' -or $toolAfterRecord.data.lastMaintenanceTime -ne '2026-08-10' -or $toolAfterRecord.data.nextMaintenanceTime -ne '2026-09-09') {
        throw 'The latest tool measurement did not update certificate and maintenance dates.'
    }

    Send-ErpMultipart -Uri "$baseUri/shkb/tool/record/update" -Headers $headers -Fields @{
        id = $laterRecordId
        toolId = $missingId
        maintenancenUser = 'equipment smoke 2'
        maintenanceTime = '2026-08-11'
        certificateNumber = 'MOVE-MUST-FAIL'
        description = 'must be rejected'
    } -ExpectedStatus 409 | Out-Null
    Send-ErpMultipart -Uri "$baseUri/shkb/tool/record/update" -Headers $headers -Fields @{
        id = $laterRecordId
        toolId = $toolId
        maintenancenUser = 'equipment smoke updated'
        maintenanceTime = '2026-08-11'
        certificateNumber = 'RECORD-CERT-2U'
        description = 'updated measurement'
    } | Out-Null
    $toolAfterUpdate = Invoke-ErpJson -Uri "$baseUri/shkb/tool/$toolId" -Headers $headers
    if ($toolAfterUpdate.data.certificateNumber -ne 'RECORD-CERT-2U' -or $toolAfterUpdate.data.nextMaintenanceTime -ne '2026-09-10') {
        throw 'Updating the latest tool measurement did not resynchronize the tool.'
    }

    $toolFile = Send-ErpMultipart -Uri "$baseUri/shkb/tool/attachment/upload" -Headers $headers -Fields @{ toolId = $toolId } -FileName 'tool-master.txt' -FileText 'tool master attachment'
    $toolFileId = [string]$toolFile.data[0]
    $toolFiles = Invoke-ErpJson -Uri "$baseUri/shkb/tool/attachment/list?toolId=$toolId" -Headers $headers
    if ($toolFiles.data.Count -ne 1 -or $toolFiles.data[0].id -ne $toolFileId) { throw 'Tool attachment upload/list mismatch.' }
    $capturedUploadUrls.Add([string]$toolFiles.data[0].url)

    $records = Invoke-ErpJson -Uri "$baseUri/shkb/tool/record/query?pageIndex=1&pageSize=20&toolId=$toolId" -Headers $headers
    $laterRecordResult = @($records.data.datas | Where-Object { $_.id -eq $laterRecordId })
    if ($laterRecordResult.Count -ne 1 -or $laterRecordResult[0].attachments.Count -ne 1) { throw 'Updated tool record attachment is missing.' }
    $laterAttachmentId = [string]$laterRecordResult[0].attachments[0].id
    $capturedUploadUrls.Add([string]$laterRecordResult[0].attachments[0].url)
    Invoke-ErpJson -Uri "$baseUri/shkb/tool/record/attachment/$laterAttachmentId" -Method Delete -Headers $headers | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/tool/record/$laterRecordId" -Method Delete -Headers $headers | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/tool/attachment/$toolFileId" -Method Delete -Headers $headers | Out-Null

    $deviceBody = @{
        code = $deviceCode
        name = 'Equipment flow device'
        managementArea = 'FLOW'
        maintenanceProject = 'Flow maintenance'
        maintenanceInterval = 30
        maintenanceCard = 'FLOW-CARD'
        lastMaintenanceTime = '2026-08-01'
        available = $true
        description = 'temporary equipment flow verification'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/device" -Method Post -Headers $headers -JsonBody $deviceBody | Out-Null
    $deviceQuery = Invoke-ErpJson -Uri "$baseUri/shkb/device/query?pageIndex=1&pageSize=20&code=$deviceCode" -Headers $headers
    if ($deviceQuery.data.datas.Count -ne 1) { throw 'Device creation did not produce exactly one query result.' }
    $deviceId = [string]$deviceQuery.data.datas[0].id
    $deviceDetail = Invoke-ErpJson -Uri "$baseUri/shkb/device/$deviceId" -Headers $headers
    if ($deviceDetail.data.nextMaintenanceTime -ne '2026-08-31') { throw 'Device maintenance interval is not calculated in days.' }

    $deviceRecordBody = @{
        deviceId = $deviceId
        maintenancenUser = 'equipment smoke'
        maintenanceTime = '2026-08-12'
        description = 'device maintenance'
    } | ConvertTo-Json
    $deviceRecord = Invoke-ErpJson -Uri "$baseUri/shkb/device/record" -Method Post -Headers $headers -JsonBody $deviceRecordBody
    $deviceRecordId = [string]$deviceRecord.data
    $deviceMoveBody = @{
        id = $deviceRecordId
        deviceId = $missingId
        maintenancenUser = 'equipment smoke'
        maintenanceTime = '2026-08-13'
        description = 'must be rejected'
    } | ConvertTo-Json
    Assert-ErpConflict -Uri "$baseUri/shkb/device/record" -Method Put -Headers $headers -JsonBody $deviceMoveBody
    $deviceRecordUpdate = @{
        id = $deviceRecordId
        deviceId = $deviceId
        maintenancenUser = 'equipment smoke updated'
        maintenanceTime = '2026-08-13'
        description = 'device maintenance updated'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/device/record" -Method Put -Headers $headers -JsonBody $deviceRecordUpdate | Out-Null
    $deviceRecords = Invoke-ErpJson -Uri "$baseUri/shkb/device/record/query?pageIndex=1&pageSize=20&deviceId=$deviceId" -Headers $headers
    if ($deviceRecords.data.datas.Count -ne 1 -or $deviceRecords.data.datas[0].maintenanceTime -ne '2026-08-13') {
        throw 'Device maintenance record create/update/query flow is inconsistent.'
    }

    $deviceFile = Send-ErpMultipart -Uri "$baseUri/shkb/device/attachment/upload" -Headers $headers -Fields @{ deviceId = $deviceId } -FileName 'device-master.txt' -FileText 'device master attachment'
    $deviceFileId = [string]$deviceFile.data[0]
    $deviceFiles = Invoke-ErpJson -Uri "$baseUri/shkb/device/attachment/list?deviceId=$deviceId" -Headers $headers
    if ($deviceFiles.data.Count -ne 1 -or $deviceFiles.data[0].id -ne $deviceFileId) { throw 'Device attachment upload/list mismatch.' }
    $capturedUploadUrls.Add([string]$deviceFiles.data[0].url)
    Invoke-ErpJson -Uri "$baseUri/shkb/device/attachment/$deviceFileId" -Method Delete -Headers $headers | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/device/record/$deviceRecordId" -Method Delete -Headers $headers | Out-Null

    $remainingToolRecords = Invoke-ErpJson -Uri "$baseUri/shkb/tool/record/query?pageIndex=1&pageSize=20&toolId=$toolId" -Headers $headers
    $initialRecordId = [string]$remainingToolRecords.data.datas[0].id
    $initialAttachmentId = [string]$remainingToolRecords.data.datas[0].attachments[0].id
    Invoke-ErpJson -Uri "$baseUri/shkb/tool/record/attachment/$initialAttachmentId" -Method Delete -Headers $headers | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/tool/record/$initialRecordId" -Method Delete -Headers $headers | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/tool?id=$toolId" -Method Delete -Headers $headers | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/device/$deviceId" -Method Delete -Headers $headers | Out-Null

    $residual = Invoke-SmokeSql -Sql @"
SELECT
  (SELECT COUNT(*) FROM shkb_tool WHERE code='$toolCode') +
  (SELECT COUNT(*) FROM shkb_device WHERE code='$deviceCode');
"@
    $residualCount = [int]([string]$residual[0]).Trim()
    if ($residualCount -ne 0) { throw "Equipment flow API cleanup left $residualCount parent rows behind." }

    Write-Host 'Equipment write-flow verification passed: parent integrity, tools, measurements, devices, maintenance records, and attachments.'
} finally {
    $databaseUrls = @()
    try { $databaseUrls = @(Get-EquipmentSmokeUploadUrls) } catch { Write-Warning $_.Exception.Message }
    foreach ($url in @(@($capturedUploadUrls) + @($databaseUrls)) | Select-Object -Unique) {
        if ($url) {
            try { Remove-SmokeUploadUrl -Url ([string]$url) } catch { Write-Warning $_.Exception.Message }
        }
    }
    try { Invoke-SmokeSql -Sql $cleanupSql | Out-Null } catch { Write-Warning $_.Exception.Message }
}
