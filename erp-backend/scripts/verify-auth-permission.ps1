[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$TenantName,
    [string]$AdminUsername = 'admin',
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
    throw 'The auth/permission probe is restricted to a local API endpoint.'
}
if (-not $TenantName) {
    # Keep the source ASCII-only for Windows PowerShell 5 UTF-8 compatibility.
    $TenantName = -join [char[]](0x6D4B, 0x8BD5, 0x79DF, 0x6237)
}

$testUserId = 'v120-user'
$testUsername = 'v120-limited'
$testRoleId = 'v120-role'
$testUserRoleId = 'v120-user-role'
$testRoleMenuPrefix = 'v120-rm-'
$limitedToken = $null
$adminToken = $null

function Invoke-SmokeSql {
    param(
        [Parameter(Mandatory = $true)][string]$Sql,
        [switch]$ReturnOutput
    )

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
    if ($ReturnOutput) { return @($output) }
}

Add-Type -AssemblyName System.Net.Http
$handler = [System.Net.Http.HttpClientHandler]::new()
$handler.UseCookies = $false
$client = [System.Net.Http.HttpClient]::new($handler)
$client.Timeout = [TimeSpan]::FromSeconds(30)

function Invoke-ApiRequest {
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

function Assert-ApiResponse {
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

function Login-User {
    param([Parameter(Mandatory = $true)][string]$Username)

    $login = Invoke-ApiRequest -Path '/auth/login' -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    Assert-ApiResponse -Response $login -Status 200 -Code 200
    $token = [string]$login.Payload.data.token
    if (-not $token) { throw "Login for $Username succeeded without returning data.token." }
    return $token
}

$cleanupSql = @"
DELETE FROM op_logs WHERE create_by_id='$testUserId';
DELETE FROM sys_menu_collect WHERE user_id='$testUserId';
DELETE FROM sys_user_dept WHERE user_id='$testUserId';
DELETE FROM sys_role_menu WHERE role_id='$testRoleId' OR id LIKE '$testRoleMenuPrefix%';
DELETE FROM sys_user_role WHERE user_id='$testUserId' OR role_id='$testRoleId';
DELETE FROM sys_user WHERE id='$testUserId' OR username='$testUsername';
DELETE FROM sys_role WHERE id='$testRoleId' OR code='V120-LIMITED';
"@

try {
    Invoke-SmokeSql -Sql $cleanupSql
    Invoke-SmokeSql -Sql @"
INSERT INTO sys_user
  (id,code,name,username,password,email,telephone,gender,available,lock_status,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time)
SELECT
  '$testUserId','V120-USER','V1.20 limited user','$testUsername',password,NULL,NULL,0,1,0,
  'V1.20 auth permission smoke','smoke','smoke',NOW(),'smoke','smoke',NOW()
FROM sys_user WHERE username='$AdminUsername';
INSERT INTO sys_role
  (id,code,name,permission,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time)
VALUES
  ('$testRoleId','V120-LIMITED','V1.20 limited','v120:login',1,'V1.20 auth permission smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO sys_user_role (id,user_id,role_id)
VALUES ('$testUserRoleId','$testUserId','$testRoleId');
INSERT INTO sys_role_menu (id,role_id,menu_id) VALUES
  ('$($testRoleMenuPrefix)root','$testRoleId','2000'),
  ('$($testRoleMenuPrefix)query','$testRoleId','2000002');
"@

    $seedState = Invoke-SmokeSql -Sql @"
SELECT CONCAT(
  (SELECT COUNT(*) FROM sys_user WHERE id='$testUserId'), ':',
  (SELECT COUNT(*) FROM sys_role WHERE id='$testRoleId'), ':',
  (SELECT COUNT(*) FROM sys_user_role WHERE user_id='$testUserId'), ':',
  (SELECT COUNT(*) FROM sys_role_menu WHERE role_id='$testRoleId'));
"@ -ReturnOutput
    if ([string]$seedState -ne '1:1:1:2') {
        throw "Limited-user seed was incomplete: $($seedState -join ' | ')"
    }

    $adminToken = Login-User -Username $AdminUsername
    $adminUsers = Invoke-ApiRequest -Path '/system/user/query?pageIndex=1&pageSize=1' -Token $adminToken
    Assert-ApiResponse -Response $adminUsers -Status 200 -Code 200

    $invalid = Invoke-ApiRequest -Path '/auth/info' -Token 'v120-invalid-token'
    Assert-ApiResponse -Response $invalid -Status 401 -Code 401

    $limitedToken = Login-User -Username $testUsername
    $limitedInfo = Invoke-ApiRequest -Path '/auth/info' -Token $limitedToken
    Assert-ApiResponse -Response $limitedInfo -Status 200 -Code 200
    $limitedPermissions = @($limitedInfo.Payload.data.roles)
    if ($limitedPermissions -notcontains 'base-data:store-center:query' -or
        $limitedPermissions -contains 'system:user:query' -or
        $limitedPermissions -contains 'admin') {
        throw "Limited user received unexpected permissions: $($limitedPermissions -join ',')"
    }

    $limitedStoreCenters = Invoke-ApiRequest -Path '/basedata/storecenter/query?pageIndex=1&pageSize=1' -Token $limitedToken
    Assert-ApiResponse -Response $limitedStoreCenters -Status 200 -Code 200

    $limitedUsers = Invoke-ApiRequest -Path '/system/user/query?pageIndex=1&pageSize=1' -Token $limitedToken
    Assert-ApiResponse -Response $limitedUsers -Status 403 -Code 403 `
        -Message (-join [char[]](0x65E0,0x7CFB,0x7EDF,0x6743,0x9650,0xFF01))

    $limitedMenus = Invoke-ApiRequest -Path '/auth/menus' -Token $limitedToken
    Assert-ApiResponse -Response $limitedMenus -Status 200 -Code 200
    $menuJson = $limitedMenus.Payload.data | ConvertTo-Json -Compress -Depth 20
    if ($menuJson -notmatch 'base-data' -or $menuJson -notmatch 'store-center' -or
        $menuJson -match 'system/user') {
        throw "Limited menu tree did not match assigned permissions: $menuJson"
    }

    $logout = Invoke-ApiRequest -Path '/auth/logout' -Method Post -Token $limitedToken
    Assert-ApiResponse -Response $logout -Status 200 -Code 200
    $expired = Invoke-ApiRequest -Path '/auth/info' -Token $limitedToken
    Assert-ApiResponse -Response $expired -Status 401 -Code 401
    $limitedToken = $null

    Invoke-SmokeSql -Sql "UPDATE sys_user SET lock_status=1 WHERE id='$testUserId';"
    $lockedLogin = Invoke-ApiRequest -Path '/auth/login' -Method Post -FormBody @{
        tenantName = $TenantName
        username = $testUsername
        password = $Password
    }
    Assert-ApiResponse -Response $lockedLogin -Status 401 -Code 419 `
        -Message (-join [char[]](0x8D26,0x6237,0x5DF2,0x9501,0x5B9A,0xFF0C,0x4E0D,0x5141,0x8BB8,0x767B,0x5F55,0xFF01))

    Invoke-SmokeSql -Sql "UPDATE sys_user SET lock_status=0, available=0 WHERE id='$testUserId';"
    $disabledLogin = Invoke-ApiRequest -Path '/auth/login' -Method Post -FormBody @{
        tenantName = $TenantName
        username = $testUsername
        password = $Password
    }
    Assert-ApiResponse -Response $disabledLogin -Status 401 -Code 419 `
        -Message (-join [char[]](0x8D26,0x6237,0x5DF2,0x505C,0x7528,0xFF0C,0x4E0D,0x5141,0x8BB8,0x767B,0x5F55,0xFF01))

    Write-Host "Auth/permission verification passed for $baseUri (admin, limited access, 403, invalid/logout token 401, locked/disabled login)."
} finally {
    try {
        if ($limitedToken) {
            Invoke-ApiRequest -Path '/auth/logout' -Method Post -Token $limitedToken | Out-Null
        }
        if ($adminToken) {
            Invoke-ApiRequest -Path '/auth/logout' -Method Post -Token $adminToken | Out-Null
        }
    } finally {
        try {
            Invoke-SmokeSql -Sql $cleanupSql
        } finally {
            $client.Dispose()
            $handler.Dispose()
        }
    }
}
