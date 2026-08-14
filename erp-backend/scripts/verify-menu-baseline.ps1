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
    $TenantName = -join [char[]](
        0x4E0A, 0x6D77, 0x51EF, 0x5954, 0x822A, 0x7A7A,
        0x6280, 0x672F, 0x6709, 0x9650, 0x516C, 0x53F8
    )
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

$token = $null
try {
    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -Body @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    $token = [string]$login.data.token
    if (-not $token) {
        throw 'Login succeeded without returning data.token.'
    }

    $menus = Invoke-ErpJson -Uri "$baseUri/auth/menus" -Headers @{ 'X-Auth-Token' = $token }
    $visibleRoots = @(
        $menus.data |
            Where-Object { $_.meta.hideMenu -ne $true } |
            ForEach-Object { [string]$_.path }
    )
    $expectedVisibleRoots = @(
        '/contract',
        '/maintenance',
        '/work-card',
        '/machine-task',
        '/equipment',
        '/material',
        '/hr',
        '/system',
        '/base-data',
        '/product',
        '/purchase',
        '/stock',
        '/take',
        '/take-adjust'
    )

    if (($visibleRoots -join '|') -ne ($expectedVisibleRoots -join '|')) {
        throw "Visible root menu mismatch. Expected $($expectedVisibleRoots -join ', '), got $($visibleRoots -join ', ')."
    }

    $allRootPaths = @($menus.data | ForEach-Object { [string]$_.path })
    foreach ($excludedPath in '/retail', '/development', '/logistics') {
        if ($allRootPaths -contains $excludedPath) {
            throw "Excluded tenant module is still returned as root menu: $excludedPath"
        }
    }

    Write-Host "Menu baseline verification passed for $baseUri ($($visibleRoots.Count) visible business roots)."
} finally {
    if ($token) {
        try {
            Invoke-ErpJson -Uri "$baseUri/auth/logout" -Method Post -Headers @{ 'X-Auth-Token' = $token } | Out-Null
        } catch {
            Write-Warning "Logout cleanup failed: $($_.Exception.Message)"
        }
    }
}
