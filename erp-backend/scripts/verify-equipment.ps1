[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$TenantName,
    [string]$Username = 'admin',
    [string]$Password = 'admin'
)

$ErrorActionPreference = 'Stop'
$baseUri = $BaseUrl.TrimEnd('/')
if (-not $TenantName) {
    # Keep the source ASCII-only for Windows PowerShell 5 UTF-8 compatibility.
    $TenantName = -join [char[]](0x6D4B, 0x8BD5, 0x79DF, 0x6237)
}

function Invoke-ErpJson {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [ValidateSet('Get', 'Post')][string]$Method = 'Get',
        [hashtable]$Headers,
        [hashtable]$Body
    )

    $arguments = @{
        Uri = $Uri
        Method = $Method
        UseBasicParsing = $true
        TimeoutSec = 30
    }
    if ($Headers) { $arguments.Headers = $Headers }
    if ($Body) {
        $arguments.Body = $Body
        $arguments.ContentType = 'application/x-www-form-urlencoded'
    }

    $response = Invoke-WebRequest @arguments
    if ($response.StatusCode -ne 200) {
        throw "$Method $Uri returned HTTP $($response.StatusCode)."
    }

    $payload = $response.Content | ConvertFrom-Json
    if ($payload.code -ne 200) {
        throw "$Method $Uri returned ERP code $($payload.code): $($payload.msg) (traceId=$($payload.traceId))."
    }
    return $payload
}

$login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -Body @{
    tenantName = $TenantName
    username = $Username
    password = $Password
}
if (-not $login.data.token) {
    throw 'Login succeeded without returning data.token.'
}

$headers = @{ 'X-Auth-Token' = $login.data.token }
$tool = Invoke-ErpJson -Uri "$baseUri/shkb/tool/query?pageIndex=1&pageSize=20" -Headers $headers
$toolFiles = Invoke-ErpJson -Uri "$baseUri/shkb/tool/attachment/list?toolId=schema-smoke-missing" -Headers $headers
$toolRecords = Invoke-ErpJson -Uri "$baseUri/shkb/tool/record/query?pageIndex=1&pageSize=20&toolId=schema-smoke-missing" -Headers $headers
$device = Invoke-ErpJson -Uri "$baseUri/shkb/device/query?pageIndex=1&pageSize=20" -Headers $headers
$deviceFiles = Invoke-ErpJson -Uri "$baseUri/shkb/device/attachment/list?deviceId=schema-smoke-missing" -Headers $headers
$deviceRecords = Invoke-ErpJson -Uri "$baseUri/shkb/device/record/query?pageIndex=1&pageSize=20&deviceId=schema-smoke-missing" -Headers $headers

foreach ($result in @($tool, $toolRecords, $device, $deviceRecords)) {
    if ($null -eq $result.data.PSObject.Properties['datas']) {
        throw 'Paged response is missing data.datas.'
    }
}
foreach ($result in @($toolFiles, $deviceFiles)) {
    if ($result.data.Count -ne 0) {
        throw 'The missing-parent attachment probe must return an empty list.'
    }
}
if ($toolRecords.data.datas.Count -ne 0 -or $deviceRecords.data.datas.Count -ne 0) {
    throw 'The missing-parent record probe must return an empty page.'
}

Write-Host 'Equipment API verification passed: tool/device queries, attachments, and maintenance records.'
