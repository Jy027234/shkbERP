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
    # Keep the source ASCII-only so Windows PowerShell 5 does not misread UTF-8 without BOM.
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
$endDate = Get-Date
$startDate = $endDate.AddDays(-30)
$maintenanceUri = "$baseUri/shkb/dashboard/maintenance-type-data"

foreach ($contractType in 1..3) {
    $query = 'contractType={0}&startDate={1}&endDate={2}' -f `
        $contractType, $startDate.ToString('yyyy-MM-dd'), $endDate.ToString('yyyy-MM-dd')
    $payload = Invoke-ErpJson -Uri "$maintenanceUri`?$query" -Headers $headers
    foreach ($property in 'chartData', 'statisticsData', 'tableData') {
        if ($null -eq $payload.data.PSObject.Properties[$property]) {
            throw "Maintenance type $contractType response is missing data.$property."
        }
    }
}

Invoke-ErpJson -Uri "$baseUri/shkb/dashboard/inventory-data" -Headers $headers | Out-Null
Invoke-ErpJson -Uri "$baseUri/shkb/dashboard/tools-device-data" -Headers $headers | Out-Null

Write-Host 'Dashboard API verification passed: three maintenance types, inventory, tools and devices.'
