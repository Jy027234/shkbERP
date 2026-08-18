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
    throw 'The HR write probe is restricted to a local API endpoint.'
}
if ($DbContainer -ne 'xingyun-smoke-mysql' -or $Database -ne 'shkb_platform') {
    throw 'The HR write probe is restricted to the local smoke database.'
}
if ($ApiContainer -ne 'kberp-api') {
    throw 'The HR write probe only cleans uploads from the local kberp-api container.'
}
if (-not $TenantName) {
    # Shanghai Kaiben Aviation Technology Co., Ltd.
    $TenantName = -join [char[]](0x4E0A, 0x6D77, 0x51EF, 0x5954, 0x822A, 0x7A7A, 0x6280, 0x672F, 0x6709, 0x9650, 0x516C, 0x53F8)
}

$runId = [guid]::NewGuid().ToString('N').Substring(0, 8)
$prefix = 'HR-FLOW-' + $runId
$empCode1 = $prefix + '-E1'
$empCode2 = $prefix + '-E2'
$courseName1 = $prefix + '-COURSE-1'
$courseName2 = $prefix + '-COURSE-2'
$projectName1 = $prefix + '-PROJ-1'
$missingId = $prefix + '-MISSING'
$testUserId = 'hr-flow-user-' + $runId
$testUsername = 'hr-flow-limited-' + $runId
$testRoleId = 'hr-flow-role-' + $runId
$testUserRoleId = 'hr-flow-ur-' + $runId
$testRoleMenuPrefix = 'hr-flow-rm-' + $runId + '-'
$capturedUploadUrls = [System.Collections.Generic.List[string]]::new()

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
        throw "$Method $Uri returned ERP code $($payload.code): $($payload.msg) (traceId=$($payload.traceId))."
    }
    return $payload
}

function Assert-ErpHttpStatus {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [Parameter(Mandatory = $true)][int]$ExpectedStatus,
        [Parameter(Mandatory = $true)][hashtable]$Headers,
        [ValidateSet('Get', 'Post', 'Put', 'Delete')][string]$Method = 'Get',
        [string]$JsonBody,
        [int]$ExpectedCode = 0
    )
    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $httpMethod = switch ($Method) {
        'Get'    { [System.Net.Http.HttpMethod]::Get }
        'Post'   { [System.Net.Http.HttpMethod]::Post }
        'Put'    { [System.Net.Http.HttpMethod]::Put }
        'Delete' { [System.Net.Http.HttpMethod]::Delete }
        default  { [System.Net.Http.HttpMethod]::Get }
    }
    $request = [System.Net.Http.HttpRequestMessage]::new($httpMethod, $Uri)
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
            if ([int]$response.StatusCode -ne $ExpectedStatus) {
                throw "Expected HTTP $ExpectedStatus but received HTTP $([int]$response.StatusCode): $rawBody"
            }
            if ($null -eq $body -or ($null -eq $body.code)) {
                throw "ERP error response has no body/code: $rawBody"
            }
            $effectiveCode = if ($ExpectedCode -ne 0) { $ExpectedCode } else { $ExpectedStatus }
            if ([int]$body.code -ne $effectiveCode) {
                throw "Expected ERP code $effectiveCode but got $($body.code): $rawBody"
            }
            if (-not $body.traceId) {
                throw "ERP error response did not include traceId: $rawBody"
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
        [hashtable]$Fields,
        [string]$FileName,
        [string]$FileText,
        [string]$FileFieldName = 'file',
        [int]$ExpectedStatus = 200,
        [ValidateSet('Post', 'Put')][string]$Method = 'Post'
    )
    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$client.DefaultRequestHeaders.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    $content = [System.Net.Http.MultipartFormDataContent]::new()
    if ($Fields) {
        foreach ($entry in $Fields.GetEnumerator()) {
            $content.Add([System.Net.Http.StringContent]::new([string]$entry.Value), [string]$entry.Key)
        }
    }
    if ($FileName) {
        $fileContent = [System.Net.Http.ByteArrayContent]::new([Text.Encoding]::UTF8.GetBytes($FileText))
        $fileContent.Headers.ContentType = [System.Net.Http.Headers.MediaTypeHeaderValue]::new('text/plain')
        $content.Add($fileContent, $FileFieldName, $FileName)
    }
    try {
        $task = if ($Method -eq 'Put') { $client.PutAsync($Uri, $content) } else { $client.PostAsync($Uri, $content) }
        $response = $task.GetAwaiter().GetResult()
        try {
            $rawBody = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            if ([int]$response.StatusCode -ne $ExpectedStatus) {
                throw "Multipart $Method expected HTTP $ExpectedStatus but received HTTP $([int]$response.StatusCode): $rawBody"
            }
            $payload = if ($rawBody) { $rawBody | ConvertFrom-Json } else { $null }
            if ($ExpectedStatus -eq 200 -and ($null -eq $payload -or $payload.code -ne 200)) {
                throw "Multipart $Method returned an invalid ERP response: $rawBody"
            }
            if ($ExpectedStatus -eq 409 -and ($null -eq $payload -or [int]$payload.code -ne 500 -or -not $payload.traceId)) {
                throw "Multipart $Method business rejection missing ERP 500 body: $rawBody"
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

function Get-ErpBytes {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [Parameter(Mandatory = $true)][hashtable]$Headers,
        [ValidateSet('Get', 'Post')][string]$Method = 'Get',
        [string]$JsonBody
    )
    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    foreach ($entry in $Headers.GetEnumerator()) {
        [void]$client.DefaultRequestHeaders.TryAddWithoutValidation([string]$entry.Key, [string]$entry.Value)
    }
    $httpMethod = if ($Method -eq 'Post') { [System.Net.Http.HttpMethod]::Post } else { [System.Net.Http.HttpMethod]::Get }
    $request = [System.Net.Http.HttpRequestMessage]::new($httpMethod, $Uri)
    if ($JsonBody) {
        $request.Content = [System.Net.Http.StringContent]::new($JsonBody, [Text.Encoding]::UTF8, 'application/json')
    }
    try {
        $response = $client.SendAsync($request).GetAwaiter().GetResult()
        try {
            $bytes = $response.Content.ReadAsByteArrayAsync().GetAwaiter().GetResult()
            $contentType = [string]$response.Content.Headers.ContentType
            $status = [int]$response.StatusCode
            return [pscustomobject]@{ Status = $status; Bytes = $bytes; ContentType = $contentType }
        } finally {
            $response.Dispose()
        }
    } finally {
        $request.Dispose()
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
        Write-Warning "Skipping unexpected smoke upload URL: $Url"
        return
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

function Get-HrUploadUrls {
    return @(Invoke-SmokeSql -Sql @"
SELECT file_url FROM shkb_employee_file WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%')
UNION ALL SELECT file_url FROM shkb_training_course_file WHERE course_id IN (SELECT id FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%')
UNION ALL SELECT url FROM shkb_training_implementation WHERE course_id IN (SELECT id FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%') AND url IS NOT NULL
UNION ALL SELECT credential_file_url FROM shkb_person_authorization WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%') AND credential_file_url IS NOT NULL
UNION ALL SELECT file_url FROM shkb_person_authorization_file WHERE authorization_id IN (SELECT id FROM shkb_person_authorization WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%'));
"@ -ReturnOutput)
}

$cleanupSql = @"
DELETE FROM shkb_employee_file WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%');
DELETE FROM shkb_employee_certificate WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%');
DELETE FROM shkb_employee_training WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%');
DELETE FROM shkb_training_participant WHERE implementation_id IN (SELECT id FROM shkb_training_implementation WHERE course_id IN (SELECT id FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%'));
DELETE FROM shkb_training_implementation WHERE course_id IN (SELECT id FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%');
DELETE FROM shkb_training_course_file WHERE course_id IN (SELECT id FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%');
DELETE FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%';
DELETE FROM shkb_authorization_required_course WHERE project_id IN (SELECT id FROM shkb_authorization_project WHERE project_name LIKE 'HR-FLOW-%');
DELETE FROM shkb_person_authorization_file WHERE authorization_id IN (SELECT id FROM shkb_person_authorization WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%'));
DELETE FROM shkb_person_authorization_project WHERE authorization_id IN (SELECT id FROM shkb_person_authorization WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%'));
DELETE FROM shkb_person_authorization WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%');
DELETE FROM shkb_authorization_project WHERE project_name LIKE 'HR-FLOW-%';
DELETE FROM shkb_employee WHERE code LIKE 'HR-FLOW-%';
"@

$cleanupAuthSql = @"
DELETE FROM sys_user_role WHERE user_id='$testUserId' OR role_id='$testRoleId';
DELETE FROM sys_role_menu WHERE role_id='$testRoleId' OR id LIKE '$testRoleMenuPrefix%';
DELETE FROM sys_user WHERE id='$testUserId' OR username='$testUsername';
DELETE FROM sys_role WHERE id='$testRoleId' OR code='HR-FLOW-LIMITED';
"@

function Get-ResidualCount {
    $rows = Invoke-SmokeSql -Sql @"
SELECT
  (SELECT COUNT(*) FROM shkb_employee WHERE code LIKE 'HR-FLOW-%') +
  (SELECT COUNT(*) FROM shkb_employee_file WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%')) +
  (SELECT COUNT(*) FROM shkb_employee_certificate WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%')) +
  (SELECT COUNT(*) FROM shkb_employee_training WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%')) +
  (SELECT COUNT(*) FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%') +
  (SELECT COUNT(*) FROM shkb_training_course_file WHERE course_id IN (SELECT id FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%')) +
  (SELECT COUNT(*) FROM shkb_training_implementation WHERE course_id IN (SELECT id FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%')) +
  (SELECT COUNT(*) FROM shkb_training_participant WHERE implementation_id IN (SELECT id FROM shkb_training_implementation WHERE course_id IN (SELECT id FROM shkb_training_course WHERE course_name LIKE 'HR-FLOW-%'))) +
  (SELECT COUNT(*) FROM shkb_authorization_project WHERE project_name LIKE 'HR-FLOW-%') +
  (SELECT COUNT(*) FROM shkb_authorization_required_course WHERE project_id IN (SELECT id FROM shkb_authorization_project WHERE project_name LIKE 'HR-FLOW-%')) +
  (SELECT COUNT(*) FROM shkb_person_authorization WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%')) +
  (SELECT COUNT(*) FROM shkb_person_authorization_project WHERE authorization_id IN (SELECT id FROM shkb_person_authorization WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%'))) +
  (SELECT COUNT(*) FROM shkb_person_authorization_file WHERE authorization_id IN (SELECT id FROM shkb_person_authorization WHERE employee_id IN (SELECT id FROM shkb_employee WHERE code LIKE 'HR-FLOW-%'))) +
  (SELECT COUNT(*) FROM sys_user WHERE id='$testUserId') +
  (SELECT COUNT(*) FROM sys_role WHERE id='$testRoleId') +
  (SELECT COUNT(*) FROM sys_user_role WHERE user_id='$testUserId') +
  (SELECT COUNT(*) FROM sys_role_menu WHERE role_id='$testRoleId');
"@ -ReturnOutput
    return [int]([string]$rows[0]).Trim()
}

try {
    foreach ($url in @(Get-HrUploadUrls)) { Remove-SmokeUploadUrl -Url ([string]$url) }
    Invoke-SmokeSql -Sql $cleanupSql | Out-Null
    Invoke-SmokeSql -Sql $cleanupAuthSql | Out-Null

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username   = $Username
        password   = $Password
    }
    $adminHeaders = @{ 'X-Auth-Token' = $login.data.token }

    Invoke-SmokeSql -Sql @"
INSERT INTO sys_user
  (id,code,name,username,password,email,telephone,gender,available,lock_status,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time)
SELECT
  '$testUserId','HR-FLOW-LIMITED-USER','HR flow limited','$testUsername',password,NULL,NULL,0,1,0,
  'HR flow permission smoke','smoke','smoke',NOW(),'smoke','smoke',NOW()
FROM sys_user WHERE username='$Username';
INSERT INTO sys_role
  (id,code,name,permission,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time)
VALUES
  ('$testRoleId','HR-FLOW-LIMITED','HR flow limited','hr-flow:login',1,'HR flow permission smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO sys_user_role (id,user_id,role_id) VALUES ('$testUserRoleId','$testUserId','$testRoleId');
INSERT INTO sys_role_menu (id,role_id,menu_id) VALUES ('$($testRoleMenuPrefix)root','$testRoleId','2000');
"@ | Out-Null
    $seedState = Invoke-SmokeSql -Sql @"
SELECT CONCAT(
  (SELECT COUNT(*) FROM sys_user WHERE id='$testUserId'), ':',
  (SELECT COUNT(*) FROM sys_role WHERE id='$testRoleId'), ':',
  (SELECT COUNT(*) FROM sys_user_role WHERE user_id='$testUserId'), ':',
  (SELECT COUNT(*) FROM sys_role_menu WHERE role_id='$testRoleId'));
"@ -ReturnOutput
    if ([string]$seedState -ne '1:1:1:1') {
        throw "HR limited-user seed was incomplete: $($seedState -join ' | ')"
    }
    $limitedLogin = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username   = $testUsername
        password   = $Password
    }
    $limitedHeaders = @{ 'X-Auth-Token' = $limitedLogin.data.token }

    Assert-ErpHttpStatus -Uri "$baseUri/shkb/employee/query?pageIndex=1&pageSize=1" -ExpectedStatus 401 -Headers @{ 'X-Auth-Token' = 'hr-flow-invalid-token' }

    Assert-ErpHttpStatus -Uri "$baseUri/shkb/employee/query?pageIndex=1&pageSize=1" -ExpectedStatus 403 -Headers $limitedHeaders

    $emp1Body = @{
        code        = $empCode1
        name        = 'HR Flow Employee One'
        gender      = 1
        status      = 1
        phone       = '13800000001'
        position    = 'Technician'
        entryDate   = '2026-08-01'
        description = 'temporary HR flow verification'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/employee" -Method Post -Headers $adminHeaders -JsonBody $emp1Body | Out-Null
    Assert-ErpHttpStatus -Uri "$baseUri/shkb/employee" -Method Post -ExpectedStatus 409 -ExpectedCode 500 -Headers $adminHeaders -JsonBody $emp1Body
    $empQuery = Invoke-ErpJson -Uri "$baseUri/shkb/employee/query?pageIndex=1&pageSize=20&code=$empCode1" -Headers $adminHeaders
    if ($empQuery.data.datas.Count -ne 1) { throw 'Employee create/query mismatch.' }
    $emp1Id = [string]$empQuery.data.datas[0].id
    $empDetail = Invoke-ErpJson -Uri "$baseUri/shkb/employee/$emp1Id" -Headers $adminHeaders
    if ($empDetail.data.code -ne $empCode1) { throw 'Employee detail mismatch.' }
    $empUpdate = @{
        id          = $emp1Id
        code        = $empCode1
        name        = 'HR Flow Employee One Updated'
        gender      = 1
        status      = 1
        position    = 'Senior Technician'
        description = 'updated'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/employee" -Method Put -Headers $adminHeaders -JsonBody $empUpdate | Out-Null
    $empAfterUpdate = Invoke-ErpJson -Uri "$baseUri/shkb/employee/$emp1Id" -Headers $adminHeaders
    if ($empAfterUpdate.data.name -ne 'HR Flow Employee One Updated') { throw 'Employee update did not persist.' }
    $leaveBody = @{
        id          = $emp1Id
        leaveDate   = '2026-08-31'
        leaveReason = 'HR flow verification leave'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/employee/leave" -Method Put -Headers $adminHeaders -JsonBody $leaveBody | Out-Null
    $empAfterLeave = Invoke-ErpJson -Uri "$baseUri/shkb/employee/$emp1Id" -Headers $adminHeaders
    if ($empAfterLeave.data.status -ne 0) { throw 'Employee leave did not set status 0.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/employee/statistics" -Headers $adminHeaders | Out-Null

    $empFile = Send-ErpMultipart -Uri "$baseUri/shkb/employee-file/upload" -Headers $adminHeaders -Fields @{ employeeId = $emp1Id } -FileName 'employee-flow.txt' -FileText 'employee attachment' -FileFieldName 'files'
    $empFileId = [string]$empFile.data[0]
    $empFiles = Invoke-ErpJson -Uri "$baseUri/shkb/employee-file/list?employeeId=${emp1Id}" -Headers $adminHeaders
    if ($empFiles.data.Count -ne 1 -or [string]$empFiles.data[0].id -ne $empFileId) { throw 'Employee attachment upload/list mismatch.' }
    $capturedUploadUrls.Add([string]$empFiles.data[0].fileUrl)
    $empDownload = Get-ErpBytes -Uri "$baseUri/shkb/employee-file/download/$empFileId" -Headers $adminHeaders
    if ($empDownload.Status -ne 200 -or $empDownload.Bytes.Length -lt 5) { throw 'Employee attachment download failed.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-file/$empFileId" -Method Delete -Headers $adminHeaders | Out-Null
    Send-ErpMultipart -Uri "$baseUri/shkb/employee-file/upload" -Headers $adminHeaders -ExpectedStatus 409 -Fields @{ employeeId = $missingId } -FileName 'orphan-emp.txt' -FileText 'must not be stored' -FileFieldName 'files' | Out-Null

    $emp2Body = @{
        code        = $empCode2
        name        = 'HR Flow Employee Two'
        gender      = 2
        status      = 1
        phone       = '13800000002'
        entryDate   = '2026-08-01'
        description = 'temporary HR flow verification'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/employee" -Method Post -Headers $adminHeaders -JsonBody $emp2Body | Out-Null
    $emp2Query = Invoke-ErpJson -Uri "$baseUri/shkb/employee/query?pageIndex=1&pageSize=20&code=$empCode2" -Headers $adminHeaders
    if ($emp2Query.data.datas.Count -ne 1) { throw 'Second employee create/query mismatch.' }
    $emp2Id = [string]$emp2Query.data.datas[0].id

    $certBody = @{
        employeeId      = $emp2Id
        certificateType = 'TYPE-A'
        certificateName = 'HR Flow Certificate'
        certificateNo   = $prefix + '-CERT-1'
        issueOrg        = 'Flow Org'
        issueDate       = '2026-08-01'
        validStartDate  = '2026-08-01'
        validEndDate    = '2028-08-01'
        status          = 1
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-certificate" -Method Post -Headers $adminHeaders -JsonBody $certBody | Out-Null
    $certQuery = Invoke-ErpJson -Uri "$baseUri/shkb/employee-certificate/query" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; employeeId = $emp2Id } | ConvertTo-Json -Compress)
    if ($certQuery.data.datas.Count -ne 1) { throw 'Certificate create/query mismatch.' }
    $certId = [string]$certQuery.data.datas[0].id
    $certByEmp = Invoke-ErpJson -Uri "$baseUri/shkb/employee-certificate/employee/$emp2Id" -Headers $adminHeaders
    if ($certByEmp.data.Count -ne 1) { throw 'Certificate by-employee query mismatch.' }
    $certUpdate = @{
        id              = $certId
        employeeId      = $emp2Id
        certificateType = 'TYPE-B'
        certificateName = 'HR Flow Certificate Updated'
        certificateNo   = $prefix + '-CERT-1'
        issueOrg        = 'Flow Org'
        issueDate       = '2026-08-01'
        validStartDate  = '2026-08-01'
        validEndDate    = '2028-08-01'
        status          = 1
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-certificate" -Method Put -Headers $adminHeaders -JsonBody $certUpdate | Out-Null
    $certAfter = Invoke-ErpJson -Uri "$baseUri/shkb/employee-certificate/$certId" -Headers $adminHeaders
    if ($certAfter.data.certificateType -ne 'TYPE-B') { throw 'Certificate update did not persist.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-certificate/statistics" -Headers $adminHeaders | Out-Null
    $certExport = Get-ErpBytes -Uri "$baseUri/shkb/employee-certificate/export" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; employeeId = $emp2Id } | ConvertTo-Json -Compress)
    if ($certExport.Status -ne 200 -or $certExport.Bytes.Length -lt 100) { throw 'Certificate export did not return a workbook.' }
    Assert-ErpHttpStatus -Uri "$baseUri/shkb/employee-certificate" -Method Put -ExpectedStatus 409 -ExpectedCode 500 -Headers $adminHeaders -JsonBody (@{ id = $missingId; employeeId = $emp2Id; certificateType = 'TYPE-A'; certificateName = 'Missing' } | ConvertTo-Json -Compress)

    $trainRecBody = @{
        employeeId     = $emp2Id
        trainingName   = 'HR Flow Training Record'
        trainingType   = 'INTERNAL'
        trainingOrg    = 'Flow Org'
        startDate      = '2026-08-01'
        endDate        = '2026-08-02'
        trainingHours  = 8
        trainingResult = 'PASS'
        description    = 'temporary HR flow verification'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-training" -Method Post -Headers $adminHeaders -JsonBody $trainRecBody | Out-Null
    $trainRecQuery = Invoke-ErpJson -Uri "$baseUri/shkb/employee-training/query" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; employeeId = $emp2Id } | ConvertTo-Json -Compress)
    if ($trainRecQuery.data.datas.Count -ne 1) { throw 'Employee training create/query mismatch.' }
    $trainRecId = [string]$trainRecQuery.data.datas[0].id
    $trainRecUpdate = @{
        id             = $trainRecId
        employeeId     = $emp2Id
        trainingName   = 'HR Flow Training Record Updated'
        trainingType   = 'INTERNAL'
        trainingOrg    = 'Flow Org'
        startDate      = '2026-08-01'
        endDate        = '2026-08-02'
        trainingHours  = 9
        trainingResult = 'PASS'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-training" -Method Put -Headers $adminHeaders -JsonBody $trainRecUpdate | Out-Null
    $trainRecAfter = Invoke-ErpJson -Uri "$baseUri/shkb/employee-training/$trainRecId" -Headers $adminHeaders
    if ($trainRecAfter.data.trainingName -ne 'HR Flow Training Record Updated') { throw 'Employee training update did not persist.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-training/statistics" -Headers $adminHeaders | Out-Null
    $trainRecExport = Get-ErpBytes -Uri "$baseUri/shkb/employee-training/export" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; employeeId = $emp2Id } | ConvertTo-Json -Compress)
    if ($trainRecExport.Status -ne 200 -or $trainRecExport.Bytes.Length -lt 100) { throw 'Employee training export did not return a workbook.' }

    $course1Body = @{
        courseName  = $courseName1
        courseType  = 'FLOW'
        status      = 1
        instructor  = 'Flow Instructor'
        description = 'temporary HR flow verification'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/training-course" -Method Post -Headers $adminHeaders -JsonBody $course1Body | Out-Null
    $courseQuery = Invoke-ErpJson -Uri "$baseUri/training-course/query" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; keyword = $courseName1 } | ConvertTo-Json -Compress)
    if ($courseQuery.data.datas.Count -ne 1) { throw 'Course create/query mismatch.' }
    $course1Id = [string]$courseQuery.data.datas[0].id
    $courseUpdate = @{
        id          = $course1Id
        courseName  = $courseName1
        courseType  = 'FLOW'
        status      = 1
        instructor  = 'Flow Instructor Updated'
        description = 'updated'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/training-course" -Method Put -Headers $adminHeaders -JsonBody $courseUpdate | Out-Null
    $courseAfter = Invoke-ErpJson -Uri "$baseUri/training-course/$course1Id" -Headers $adminHeaders
    if ($courseAfter.data.instructor -ne 'Flow Instructor Updated') { throw 'Course update did not persist.' }
    Invoke-ErpJson -Uri "$baseUri/training-course/status?id=${course1Id}&status=0" -Method Put -Headers $adminHeaders | Out-Null
    $enabledAfterDisable = Invoke-ErpJson -Uri "$baseUri/training-course/list/enabled" -Headers $adminHeaders
    if (@($enabledAfterDisable.data | Where-Object { $_.id -eq $course1Id }).Count -ne 0) { throw 'Disabled course still appears in enabled list.' }
    Invoke-ErpJson -Uri "$baseUri/training-course/status?id=${course1Id}&status=1" -Method Put -Headers $adminHeaders | Out-Null
    $enabledAfterEnable = Invoke-ErpJson -Uri "$baseUri/training-course/list/enabled" -Headers $adminHeaders
    if (@($enabledAfterEnable.data | Where-Object { $_.id -eq $course1Id }).Count -ne 1) { throw 'Enabled course missing from enabled list.' }
    $courseExport = Get-ErpBytes -Uri "$baseUri/training-course/export" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; keyword = $courseName1 } | ConvertTo-Json -Compress)
    if ($courseExport.Status -ne 200 -or $courseExport.Bytes.Length -lt 100) { throw 'Course export did not return a workbook.' }
    $courseDoc = Send-ErpMultipart -Uri "$baseUri/training-course/file/upload?courseId=${course1Id}" -Headers $adminHeaders -Fields @{ description = 'course doc' } -FileName 'course-doc.txt' -FileText 'course document'
    $courseDocs = Invoke-ErpJson -Uri "$baseUri/training-course/file/list?courseId=${course1Id}" -Headers $adminHeaders
    if ($courseDocs.data.Count -ne 1) { throw 'Course document upload/list mismatch.' }
    $capturedUploadUrls.Add([string]$courseDocs.data[0].fileUrl)
    Invoke-ErpJson -Uri "$baseUri/training-course/file/$($courseDocs.data[0].id)" -Method Delete -Headers $adminHeaders | Out-Null

    $course2Body = @{
        courseName = $courseName2
        courseType = 'FLOW'
        status     = 1
        instructor = 'Flow Instructor 2'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/training-course" -Method Post -Headers $adminHeaders -JsonBody $course2Body | Out-Null
    $course2Query = Invoke-ErpJson -Uri "$baseUri/training-course/query" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; keyword = $courseName2 } | ConvertTo-Json -Compress)
    if ($course2Query.data.datas.Count -ne 1) { throw 'Second course create/query mismatch.' }
    $course2Id = [string]$course2Query.data.datas[0].id

    $implBody = @{
        courseId         = $course2Id
        planStartDate    = '2026-09-01'
        planEndDate      = '2026-09-03'
        trainingLocation = 'Flow Room'
        instructor       = 'Flow Instructor 2'
        description      = 'temporary HR flow verification'
    } | ConvertTo-Json
    $implCreate = Invoke-ErpJson -Uri "$baseUri/training-implementation" -Method Post -Headers $adminHeaders -JsonBody $implBody
    $implId = [string]$implCreate.data
    if (-not $implId) { throw 'Implementation create did not return an ID.' }
    $implDetail = Invoke-ErpJson -Uri "$baseUri/training-implementation/$implId" -Headers $adminHeaders
    if ($implDetail.data.status -ne 0) { throw 'Implementation initial status is not planned (0).' }
    $implUpdate = @{
        id               = $implId
        courseId         = $course2Id
        planStartDate    = '2026-09-02'
        planEndDate      = '2026-09-04'
        trainingLocation = 'Flow Room 2'
        instructor       = 'Flow Instructor 2'
        description      = 'updated'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/training-implementation" -Method Put -Headers $adminHeaders -JsonBody $implUpdate | Out-Null

    $participantBody = ConvertTo-Json -InputObject @(
        @{
            implementationId = $implId
            employeeId       = $emp2Id
            trainingResult   = 'PENDING'
            status           = 1
        }
    ) -Depth 5 -Compress
    Invoke-ErpJson -Uri "$baseUri/training-participant/batch" -Method Post -Headers $adminHeaders -JsonBody $participantBody | Out-Null
    $participantQuery = Invoke-ErpJson -Uri "$baseUri/training-participant/query" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; implementationId = $implId } | ConvertTo-Json -Compress)
    if ($participantQuery.data.datas.Count -ne 1) { throw 'Participant batch create/query mismatch.' }
    $participantId = [string]$participantQuery.data.datas[0].id

    Assert-ErpHttpStatus -Uri "$baseUri/training-implementation/complete?id=${implId}" -Method Put -ExpectedStatus 409 -ExpectedCode 500 -Headers $adminHeaders

    Invoke-ErpJson -Uri "$baseUri/training-implementation/start?id=${implId}" -Method Put -Headers $adminHeaders | Out-Null
    $implStarted = Invoke-ErpJson -Uri "$baseUri/training-implementation/$implId" -Headers $adminHeaders
    if ($implStarted.data.status -ne 1) { throw 'Implementation start did not set status 1.' }
    Assert-ErpHttpStatus -Uri "$baseUri/training-implementation/start?id=${implId}" -Method Put -ExpectedStatus 409 -ExpectedCode 500 -Headers $adminHeaders

    $beforeTrainingCount = Invoke-SmokeSql -Sql "SELECT COUNT(*) FROM shkb_employee_training WHERE employee_id='$emp2Id' AND training_name='$courseName2';" -ReturnOutput
    $beforeCount = [int]([string]$beforeTrainingCount[0]).Trim()

    $participantResults = ConvertTo-Json -InputObject @(
        @{
            participantId  = $participantId
            employeeId     = $emp2Id
            employeeName   = 'HR Flow Employee Two'
            trainingResult = 'PASS'
            certificateNo  = $prefix + '-TCERT'
        }
    ) -Depth 5 -Compress
    Send-ErpMultipart -Uri "$baseUri/training-implementation/complete?id=${implId}" -Method Put -Headers $adminHeaders -Fields @{
        actualEndDate      = '2026-09-04'
        participantResults = $participantResults
        trainingType       = 'INTERNAL'
        trainingOrg        = 'Flow Org'
        trainingHours      = '16'
        trainingContent    = 'completed via HR flow'
    } -FileName 'impl-cert.txt' -FileText 'implementation certificate' | Out-Null
    $implCompleted = Invoke-ErpJson -Uri "$baseUri/training-implementation/$implId" -Headers $adminHeaders
    if ($implCompleted.data.status -ne 2) { throw 'Implementation complete did not set status 2.' }
    Assert-ErpHttpStatus -Uri "$baseUri/training-implementation/complete?id=${implId}" -Method Put -ExpectedStatus 409 -ExpectedCode 500 -Headers $adminHeaders
    $afterTrainingCount = Invoke-SmokeSql -Sql "SELECT COUNT(*) FROM shkb_employee_training WHERE employee_id='$emp2Id' AND training_name='$courseName2' AND training_result='PASS';" -ReturnOutput
    $afterCount = [int]([string]$afterTrainingCount[0]).Trim()
    if ($afterCount -ne $beforeCount + 1) {
        throw "Implementation complete did not create the employee training record transactionally ($beforeCount -> $afterCount)."
    }
    $participantAfter = Invoke-ErpJson -Uri "$baseUri/training-participant/query" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; implementationId = $implId } | ConvertTo-Json -Compress)
    if ([string]$participantAfter.data.datas[0].trainingResult -ne 'PASS') { throw 'Participant result was not persisted on complete.' }

    $impl2Body = @{
        courseId         = $course2Id
        planStartDate    = '2026-10-01'
        planEndDate      = '2026-10-03'
        trainingLocation = 'Flow Room'
        instructor       = 'Flow Instructor 2'
    } | ConvertTo-Json
    $impl2Create = Invoke-ErpJson -Uri "$baseUri/training-implementation" -Method Post -Headers $adminHeaders -JsonBody $impl2Body
    $impl2Id = [string]$impl2Create.data
    Invoke-ErpJson -Uri "$baseUri/training-implementation/start?id=${impl2Id}" -Method Put -Headers $adminHeaders | Out-Null
    Invoke-ErpJson -Uri "$baseUri/training-implementation/cancel?id=${impl2Id}" -Method Put -Headers $adminHeaders | Out-Null
    $impl2After = Invoke-ErpJson -Uri "$baseUri/training-implementation/$impl2Id" -Headers $adminHeaders
    if ($impl2After.data.status -ne 3) { throw 'Implementation cancel did not set status 3.' }
    Assert-ErpHttpStatus -Uri "$baseUri/training-implementation/$impl2Id" -Method Delete -ExpectedStatus 409 -ExpectedCode 500 -Headers $adminHeaders

    $projBody = @{
        projectName        = $projectName1
        authorizationItem  = 'FLOW-ITEM'
        validityPeriod     = 12
        validityUnit       = 'month'
        status             = 1
        description        = 'temporary HR flow verification'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project" -Method Post -Headers $adminHeaders -JsonBody $projBody | Out-Null
    $projQuery = Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project/query" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; keyword = $projectName1 } | ConvertTo-Json -Compress)
    if ($projQuery.data.datas.Count -ne 1) { throw 'Authorization project create/query mismatch.' }
    $projId = [string]$projQuery.data.datas[0].id
    $projUpdate = @{
        id               = $projId
        projectName      = $projectName1
        authorizationItem = 'FLOW-ITEM-UPDATED'
        validityPeriod   = 24
        validityUnit     = 'month'
        status           = 1
        description      = 'updated'
    } | ConvertTo-Json
    Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project" -Method Put -Headers $adminHeaders -JsonBody $projUpdate | Out-Null
    $projAfter = Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project/$projId" -Headers $adminHeaders
    if ($projAfter.data.authorizationItem -ne 'FLOW-ITEM-UPDATED') { throw 'Authorization project update did not persist.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project/required-courses/$projId" -Method Post -Headers $adminHeaders -JsonBody (ConvertTo-Json -InputObject @($course2Id) -Compress) | Out-Null
    $requiredCourses = Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project/required-courses/$projId" -Headers $adminHeaders
    if ($requiredCourses.data.Count -ne 1 -or [string]$requiredCourses.data[0] -ne $course2Id) { throw 'Required course save/query mismatch.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project/status?id=${projId}&status=0" -Method Put -Headers $adminHeaders | Out-Null
    $projEnabled = Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project/list/enabled" -Headers $adminHeaders
    if (@($projEnabled.data | Where-Object { $_.id -eq $projId }).Count -ne 0) { throw 'Disabled authorization project still enabled.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project/status?id=${projId}&status=1" -Method Put -Headers $adminHeaders | Out-Null
    $projExport = Get-ErpBytes -Uri "$baseUri/shkb/authorization-project/export" -Method Post -Headers $adminHeaders -JsonBody (@{ pageIndex = 1; pageSize = 20; keyword = $projectName1 } | ConvertTo-Json -Compress)
    if ($projExport.Status -ne 200 -or $projExport.Bytes.Length -lt 100) { throw 'Authorization project export did not return a workbook.' }

    $projJson = ConvertTo-Json -InputObject @(
        @{
            projectId         = $projId
            projectName       = $projectName1
            authorizationDate = '2026-08-01'
            expiryDate        = '2027-08-01'
        }
    ) -Depth 5 -Compress
    $paCreate = Send-ErpMultipart -Uri "$baseUri/shkb/person-authorization" -Headers $adminHeaders -Fields @{
        employeeId  = $emp2Id
        description = 'temporary HR flow verification'
        projects    = $projJson
    } -FileName 'credential-flow.txt' -FileText 'credential attachment'
    $paId = [string]$paCreate.data
    if (-not $paId) { throw 'Person authorization create did not return an ID.' }
    $paDetail = Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/$paId" -Headers $adminHeaders
    if ($paDetail.data.status -ne 1) { throw 'Person authorization initial status is not normal (1).' }
    $paByEmp = Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/employee/$emp2Id" -Headers $adminHeaders
    if ($paByEmp.data.Count -ne 1) { throw 'Person authorization by-employee query mismatch.' }
    $validity = Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/check-validity/$paId" -Headers $adminHeaders
    if ($validity.data.isValid -ne $true) { throw 'Person authorization validity check returned invalid.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization?id=${paId}&description=updated-description" -Method Put -Headers $adminHeaders | Out-Null
    $paAfterUpdate = Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/$paId" -Headers $adminHeaders
    if ($paAfterUpdate.data.description -ne 'updated-description') { throw 'Person authorization update did not persist.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/projects?id=${paId}" -Method Put -Headers $adminHeaders -JsonBody ($projJson) | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/extend/${paId}?projectId=${projId}&expiryDate=2028-08-01" -Method Post -Headers $adminHeaders | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/revoke/$paId" -Method Post -Headers $adminHeaders | Out-Null
    $validityAfterRevoke = Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/check-validity/$paId" -Headers $adminHeaders
    if ($validityAfterRevoke.data.isValid -ne $false) { throw 'Revoked authorization still reports valid.' }
    Assert-ErpHttpStatus -Uri "$baseUri/shkb/person-authorization/revoke/$paId" -Method Post -ExpectedStatus 409 -ExpectedCode 500 -Headers $adminHeaders

    $paFile = Send-ErpMultipart -Uri "$baseUri/shkb/person-authorization/file/upload?authorizationId=${paId}" -Headers $adminHeaders -FileName 'pa-attachment.txt' -FileText 'person authorization attachment' -FileFieldName 'files'
    $paFileId = [string]$paFile.data[0]
    $paFiles = Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/file/list?authorizationId=${paId}" -Headers $adminHeaders
    if ($paFiles.data.Count -ne 1 -or [string]$paFiles.data[0].id -ne $paFileId) { throw 'Person authorization attachment upload/list mismatch.' }
    $capturedUploadUrls.Add([string]$paFiles.data[0].fileUrl)
    $paDownload = Get-ErpBytes -Uri "$baseUri/shkb/person-authorization/file/download/$paFileId" -Headers $adminHeaders
    if ($paDownload.Status -ne 200 -or $paDownload.Bytes.Length -lt 5) { throw 'Person authorization attachment download failed.' }
    Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/file/$paFileId" -Method Delete -Headers $adminHeaders | Out-Null

    $evilFileId = 'hr-flow-evil-' + $runId
    Invoke-SmokeSql -Sql @"
INSERT INTO shkb_person_authorization_file (id,authorization_id,file_name,file_type,file_url,file_size,create_by,create_by_id,create_time)
VALUES ('$evilFileId','$paId','evil.txt','text/plain','/uploads/../../etc/passwd',10,'smoke','smoke',NOW());
"@ | Out-Null
    Assert-ErpHttpStatus -Uri "$baseUri/shkb/person-authorization/file/download/$evilFileId" -ExpectedStatus 409 -ExpectedCode 500 -Headers $adminHeaders
    Invoke-SmokeSql -Sql "DELETE FROM shkb_person_authorization_file WHERE id='$evilFileId';" | Out-Null

    Invoke-ErpJson -Uri "$baseUri/shkb/person-authorization/$paId" -Method Delete -Headers $adminHeaders | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/authorization-project/$projId" -Method Delete -Headers $adminHeaders | Out-Null
    Invoke-ErpJson -Uri "$baseUri/training-course/$course1Id" -Method Delete -Headers $adminHeaders | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-certificate/$certId" -Method Delete -Headers $adminHeaders | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/employee-training/$trainRecId" -Method Delete -Headers $adminHeaders | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/employee/$emp2Id" -Method Delete -Headers $adminHeaders | Out-Null
    Invoke-ErpJson -Uri "$baseUri/shkb/employee/$emp1Id" -Method Delete -Headers $adminHeaders | Out-Null

    Invoke-SmokeSql -Sql $cleanupSql | Out-Null
    Invoke-SmokeSql -Sql $cleanupAuthSql | Out-Null
    $residual = Get-ResidualCount
    if ($residual -ne 0) { throw "HR flow cleanup left $residual fixture rows behind." }

    Write-Host "HR write-flow verification passed ($BaseUrl): employee, certificate, training record, course, implementation transaction, authorization project, person authorization, permissions, and attachment safety."
} finally {
    $databaseUrls = @()
    try { $databaseUrls = @(Get-HrUploadUrls) } catch { Write-Warning $_.Exception.Message }
    foreach ($url in @(@($capturedUploadUrls) + @($databaseUrls)) | Select-Object -Unique) {
        if ($url) {
            try { Remove-SmokeUploadUrl -Url ([string]$url) } catch { Write-Warning $_.Exception.Message }
        }
    }
    try { Invoke-SmokeSql -Sql $cleanupSql | Out-Null } catch { Write-Warning $_.Exception.Message }
    try { Invoke-SmokeSql -Sql $cleanupAuthSql | Out-Null } catch { Write-Warning $_.Exception.Message }
}
