[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$TenantName,
    [string]$Username = 'admin',
    [string]$Password = 'admin',
    [string]$SimpleApiSecret = $env:SIMPLE_OPEN_API_SECRET,
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$Database = 'shkb_platform',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Net.Http
$baseUri = $BaseUrl.TrimEnd('/')
$parsedBaseUri = [Uri]$baseUri
if ($parsedBaseUri.Host -notin @('127.0.0.1', 'localhost')) {
    throw 'The machine-task write probe is restricted to a local API endpoint.'
}
if ($DbContainer -ne 'xingyun-smoke-mysql' -or $Database -ne 'shkb_platform') {
    throw 'The machine-task write probe is restricted to the local smoke database.'
}
if ([string]::IsNullOrWhiteSpace($SimpleApiSecret)) {
    throw 'SimpleApiSecret (or SIMPLE_OPEN_API_SECRET) is required for device report verification.'
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
        $output = $Sql | & docker exec -i $DbContainer mysql "-u$DbUsername" "-p$DbPassword" -N $Database 2>$null
        $exitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    if ($exitCode -ne 0) {
        throw "Smoke database command failed: $($output -join [Environment]::NewLine)"
    }
    return @($output)
}

function Get-SimpleAuth {
    $minute = Get-Date -Format 'yyyyMMddHHmm'
    $bytes = [Text.Encoding]::UTF8.GetBytes($SimpleApiSecret + $minute)
    $md5 = [Security.Cryptography.MD5]::Create()
    try {
        return -join ($md5.ComputeHash($bytes) | ForEach-Object { $_.ToString('x2') })
    } finally {
        $md5.Dispose()
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
        [string]$JsonBody
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $request = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::Post, $Uri)
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$request.Headers.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    if ($JsonBody) {
        $request.Content = [System.Net.Http.StringContent]::new(
            $JsonBody,
            [Text.Encoding]::UTF8,
            'application/json'
        )
    }

    try {
        $response = $client.SendAsync($request).GetAwaiter().GetResult()
        try {
            $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            $body = if ($rawBody) { $rawBody | ConvertFrom-Json } else { $null }
            if ([int]$response.StatusCode -ne 409 -or $null -eq $body -or $body.code -eq 200 -or -not $body.msg) {
                throw "Expected an HTTP 409 business rejection but received HTTP $([int]$response.StatusCode): $rawBody"
            }
        } finally {
            $response.Dispose()
        }
    } finally {
        $request.Dispose()
        $client.Dispose()
    }
}

$tighteningId = 'v122-tightening-smoke'
$tighteningTaskId = 'v122-platform-task'
$magneticId = 'v122-magnetic-smoke'
$magneticTaskId = 'v122-magnetic-task'
$cleanupSql = @"
DELETE FROM shkb_machine_task_tightening WHERE id='$tighteningId' OR task_id='$tighteningTaskId';
DELETE FROM shkb_machine_task_magnetic_powder WHERE id='$magneticId' OR task_id='$magneticTaskId';
"@

try {
    Invoke-SmokeSql -Sql $cleanupSql | Out-Null
    $seedSql = @"
INSERT INTO shkb_machine_task_tightening
  (id,task_id,machine_task_status,task_type,contract_no,part_no,serial_no,create_time)
VALUES
  ('$tighteningId','$tighteningTaskId',0,0,'V122-CONTRACT','V122-PART','V122-SERIAL',NOW());
INSERT INTO shkb_machine_task_magnetic_powder
  (id,task_id,contract_no,part_no,create_time,serial_no,machine_task_status)
VALUES
  ('$magneticId','$magneticTaskId','V122-CONTRACT','V122-PART',NOW(),'V122-SERIAL',1);
"@
    Invoke-SmokeSql -Sql $seedSql | Out-Null

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    $userHeaders = @{ 'X-Auth-Token' = $login.data.token }
    $deviceHeaders = @{
        'X-Simple-Auth' = Get-SimpleAuth
        'X-Tenant-Id' = '1000'
    }

    $firstReport = @{
        taskId = $tighteningTaskId
        reportData = @{
            header = @{ TechnologyName = 'V1.22 smoke'; Operator = 'codex' }
            data = @(@{ ID = '1'; ScrewNo = '1'; FinalStatus = 'OK' })
        }
    } | ConvertTo-Json -Depth 8 -Compress
    Invoke-ErpJson -Uri "$baseUri/machine/task/tightening/report" -Method Post -Headers $deviceHeaders -JsonBody $firstReport | Out-Null
    Invoke-ErpJson -Uri "$baseUri/machine/task/tightening/report" -Method Post -Headers $deviceHeaders -JsonBody $firstReport | Out-Null

    $conflictingReport = @{
        taskId = $tighteningTaskId
        reportData = @{
            header = @{ TechnologyName = 'V1.22 smoke'; Operator = 'conflicting-device' }
            data = @(@{ ID = '1'; ScrewNo = '1'; FinalStatus = 'NG' })
        }
    } | ConvertTo-Json -Depth 8 -Compress
    Assert-ErpConflict -Uri "$baseUri/machine/task/tightening/report" -Headers $deviceHeaders `
        -JsonBody $conflictingReport

    $row = @(Invoke-SmokeSql -Sql "SELECT machine_task_status,task_type,JSON_UNQUOTE(JSON_EXTRACT(report_data,'$.header.Operator')) FROM shkb_machine_task_tightening WHERE id='$tighteningId';")
    if ($row.Count -ne 1 -or $row[0] -ne "1`t0`tcodex") {
        throw "Tightening report was overwritten or stored incorrectly: $($row -join ', ')"
    }

    Assert-ErpConflict -Uri "$baseUri/machine/task/magnetic/send?taskId=$magneticTaskId" -Headers $userHeaders

    $magnetic = @(Invoke-SmokeSql -Sql "SELECT machine_task_status,send_time IS NULL FROM shkb_machine_task_magnetic_powder WHERE id='$magneticId';")
    if ($magnetic.Count -ne 1 -or $magnetic[0] -ne "1`t1") {
        throw "Rejected magnetic resend changed persisted state: $($magnetic -join ', ')"
    }

    Write-Host 'Machine-task write verification passed: idempotent retry, conflicting report rejection and magnetic resend guard.'
} finally {
    Invoke-SmokeSql -Sql $cleanupSql | Out-Null
}
