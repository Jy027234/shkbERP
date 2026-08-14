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
    $TenantName = -join [char[]](0x4E0A, 0x6D77, 0x51EF, 0x5954, 0x822A, 0x7A7A, 0x6280, 0x672F, 0x6709, 0x9650, 0x516C, 0x53F8)
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
$probes = @(
    @{ Name = 'machine info'; Uri = "$baseUri/machine/info/query?pageIndex=1&pageSize=20" },
    @{ Name = 'tightening tasks'; Uri = "$baseUri/machine/task/tightening/query?pageIndex=1&pageSize=20" },
    @{ Name = 'magnetic tasks'; Uri = "$baseUri/machine/task/magnetic/query?pageIndex=1&pageSize=20" }
)

foreach ($probe in $probes) {
    $payload = Invoke-ErpJson -Uri $probe.Uri -Headers $headers
    if ($null -eq $payload.data.PSObject.Properties['datas']) {
        throw "$($probe.Name) response is missing data.datas."
    }
}

Write-Host 'Machine-task API verification passed: machine info, tightening and magnetic management endpoints.'
