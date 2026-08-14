[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$TenantName,
    [string]$Username = 'admin',
    [string]$Password = 'admin'
)

$ErrorActionPreference = 'Stop'
$baseUri = $BaseUrl.TrimEnd('/')
$parsedBaseUri = [Uri]$baseUri
if ($parsedBaseUri.Host -notin @('127.0.0.1', 'localhost')) {
    throw 'The error-contract probe is restricted to a local API endpoint.'
}
if (-not $TenantName) {
    # Keep the source ASCII-only for Windows PowerShell 5 UTF-8 compatibility.
    $TenantName = -join [char[]](0x4E0A, 0x6D77, 0x51EF, 0x5954, 0x822A, 0x7A7A, 0x6280, 0x672F, 0x6709, 0x9650, 0x516C, 0x53F8)
}

Add-Type -AssemblyName System.Net.Http
$handler = [System.Net.Http.HttpClientHandler]::new()
$handler.UseCookies = $false
$client = [System.Net.Http.HttpClient]::new($handler)
$client.Timeout = [TimeSpan]::FromSeconds(30)

function Invoke-ContractRequest {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [ValidateSet('Get', 'Post')][string]$Method = 'Get',
        [string]$Token,
        [hashtable]$FormBody
    )

    $request = [System.Net.Http.HttpRequestMessage]::new(
        [System.Net.Http.HttpMethod]::$Method,
        "$baseUri$Path"
    )
    if ($Token) {
        $request.Headers.Add('X-Auth-Token', $Token)
    }
    if ($FormBody) {
        $pairs = [System.Collections.Generic.List[System.Collections.Generic.KeyValuePair[string,string]]]::new()
        foreach ($entry in $FormBody.GetEnumerator()) {
            $pairs.Add([System.Collections.Generic.KeyValuePair[string,string]]::new(
                [string]$entry.Key,
                [string]$entry.Value
            ))
        }
        $request.Content = [System.Net.Http.FormUrlEncodedContent]::new($pairs)
    }

    try {
        $response = $client.SendAsync($request).GetAwaiter().GetResult()
        try {
            $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            $payload = if ($rawBody) { $rawBody | ConvertFrom-Json } else { $null }
            return [pscustomobject]@{
                Status = [int]$response.StatusCode
                Payload = $payload
                RawBody = $rawBody
            }
        } finally {
            $response.Dispose()
        }
    } finally {
        $request.Dispose()
    }
}

function Assert-ContractResponse {
    param(
        [Parameter(Mandatory = $true)]$Response,
        [Parameter(Mandatory = $true)][int]$Status,
        [Parameter(Mandatory = $true)][int]$Code,
        [string]$Message
    )

    if ($Response.Status -ne $Status -or $Response.Payload.code -ne $Code) {
        throw "Expected HTTP $Status / ERP $Code, got HTTP $($Response.Status): $($Response.RawBody)"
    }
    if ($Message -and $Response.Payload.msg -ne $Message) {
        throw "Expected message '$Message', got: $($Response.RawBody)"
    }
    if ($Code -ne 200 -and -not $Response.Payload.traceId) {
        throw "ERP error response did not include traceId: $($Response.RawBody)"
    }
}

try {
    $login = Invoke-ContractRequest -Path '/auth/login' -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    Assert-ContractResponse -Response $login -Status 200 -Code 200
    $token = [string]$login.Payload.data.token
    if (-not $token) { throw 'Login succeeded without returning data.token.' }

    $success = Invoke-ContractRequest -Path '/basedata/storecenter/query?pageIndex=1&pageSize=1' -Token $token
    Assert-ContractResponse -Response $success -Status 200 -Code 200

    $business = Invoke-ContractRequest -Path '/basedata/storecenter?id=v119-missing' -Token $token
    Assert-ContractResponse -Response $business -Status 409 -Code 500 -Message (-join [char[]](0x4ED3,0x5E93,0x4E0D,0x5B58,0x5728,0xFF01))

    $validation = Invoke-ContractRequest -Path '/basedata/storecenter?id=' -Token $token
    Assert-ContractResponse -Response $validation -Status 400 -Code 400 -Message (-join [char[]](0x49,0x44,0x4E0D,0x80FD,0x4E3A,0x7A7A,0xFF01))

    $unauthenticated = Invoke-ContractRequest -Path '/auth/info'
    Assert-ContractResponse -Response $unauthenticated -Status 401 -Code 401

    $wrongMethod = Invoke-ContractRequest -Path '/basedata/storecenter/query' -Method Post -Token $token
    if ($wrongMethod.Status -ne 405) {
        throw "Expected HTTP 405 for an unsupported method, got HTTP $($wrongMethod.Status): $($wrongMethod.RawBody)"
    }

    Write-Host "Error-contract verification passed for $baseUri (200/400/401/409/405)."
} finally {
    $client.Dispose()
}
