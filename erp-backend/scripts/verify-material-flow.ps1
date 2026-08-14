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

$applications = Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/material-apply/query?pageIndex=1&pageSize=20" -Headers $headers
$orders = Invoke-ErpJson -Uri "$baseUri/material/order/query?pageIndex=1&pageSize=20" -Headers $headers
$filteredOrders = Invoke-ErpJson -Uri "$baseUri/material/order/query?pageIndex=1&pageSize=20&sortField=createTime&sortOrder=desc&contractCode=schema-smoke-missing&createTimeStart=2000-01-01%2000%3A00%3A00&isOutFinish=false" -Headers $headers
$outSheets = Invoke-ErpJson -Uri "$baseUri/material/out/sheet/query?pageIndex=1&pageSize=20" -Headers $headers
$batches = Invoke-ErpJson -Uri "$baseUri/material/out/sheet/batch/stock?scId=schema-smoke-missing&productId=schema-smoke-missing" -Headers $headers
$serials = Invoke-ErpJson -Uri "$baseUri/material/out/sheet/serial/stock?scId=schema-smoke-missing&productId=schema-smoke-missing" -Headers $headers
$nonParts = Invoke-ErpJson -Uri "$baseUri/shkb/contract-task/non-part/list?taskId=schema-smoke-missing" -Headers $headers

foreach ($result in @($applications, $orders, $filteredOrders, $outSheets)) {
    if ($null -eq $result.data.PSObject.Properties['datas']) {
        throw 'Paged material response is missing data.datas.'
    }
}
foreach ($result in @($batches, $serials, $nonParts)) {
    if ($result.data.Count -ne 0) {
        throw 'The missing-parent material probe must return an empty list.'
    }
}

Write-Host 'Material-flow API verification passed: applications, filtered orders, outbound sheets, batch/serial stock, and non-parts.'
