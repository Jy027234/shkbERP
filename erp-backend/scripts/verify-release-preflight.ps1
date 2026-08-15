[CmdletBinding()]
param(
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$Database = 'shkb_platform',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577',
    [string]$EvidencePath,
    [switch]$AsJson
)

$ErrorActionPreference = 'Stop'
$backendRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $backendRoot '..')).Path
$checks = [System.Collections.Generic.List[object]]::new()
$failures = [System.Collections.Generic.List[string]]::new()
$manualConfirmations = [System.Collections.Generic.List[string]]::new()

if ($DbContainer -ne 'xingyun-smoke-mysql') {
    throw 'Release preflight is restricted to the local xingyun-smoke-mysql container.'
}
foreach ($value in @($Database, $DbUsername)) {
    if ($value -notmatch '^[A-Za-z0-9_]+$') {
        throw 'Database and username may contain only letters, digits, and underscores.'
    }
}
if ($DbPassword -notmatch '^[A-Za-z0-9._@-]+$') {
    throw 'The local smoke password contains unsupported shell characters.'
}

function Invoke-DockerCommand {
    param(
        [Parameter(Mandatory = $true)][string[]]$Arguments,
        [switch]$ReturnOutput
    )

    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $output = & docker @Arguments 2>&1
        $exitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $previousPreference
    }
    if ($exitCode -ne 0) {
        throw "Docker command failed: $($output -join [Environment]::NewLine)"
    }
    if ($ReturnOutput) {
        return @($output)
    }
}

function Invoke-MySqlRows {
    param(
        [Parameter(Mandatory = $true)][string]$Sql,
        [switch]$UseDatabase
    )

    $arguments = @(
        'exec', $DbContainer, 'mysql', "-u$DbUsername", "-p$DbPassword", '-N', '-B'
    )
    if ($UseDatabase) {
        $arguments += @('-D', $Database)
    }
    $arguments += @('-e', $Sql)
    $rows = Invoke-DockerCommand -Arguments $arguments -ReturnOutput
    return @(
        $rows |
            ForEach-Object { ([string]$_).Trim() } |
            Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
    )
}

function Invoke-MySqlScalar {
    param(
        [Parameter(Mandatory = $true)][string]$Sql,
        [switch]$UseDatabase
    )

    $rows = Invoke-MySqlRows -Sql $Sql -UseDatabase:$UseDatabase
    if ($rows.Count -eq 0) {
        return ''
    }
    return [string]$rows[$rows.Count - 1]
}

function Add-Check {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][bool]$Passed,
        [Parameter(Mandatory = $true)][string]$Detail
    )

    $checks.Add([pscustomobject][ordered]@{
        name = $Name
        passed = $Passed
        detail = $Detail
    })
    if (-not $Passed) {
        $failures.Add(('{0}: {1}' -f $Name, $Detail))
    }
}

function Test-Table {
    param([Parameter(Mandatory = $true)][string]$Table)

    $count = Invoke-MySqlScalar -Sql (
        "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '$Database' AND table_name = '$Table';"
    )
    return ([int]$count -eq 1)
}

function Test-RequiredColumns {
    param(
        [Parameter(Mandatory = $true)][string]$Table,
        [Parameter(Mandatory = $true)][string[]]$Columns,
        [Parameter(Mandatory = $true)][string]$Purpose
    )

    if (-not (Test-Table -Table $Table)) {
        Add-Check -Name "table:$Table" -Passed $false -Detail "$Purpose requires table '$Table'."
        return
    }

    $quotedColumns = ($Columns | ForEach-Object { "'$_'" }) -join ', '
    $found = @(
        Invoke-MySqlRows -Sql (
            "SELECT column_name FROM information_schema.columns WHERE table_schema = '$Database' AND table_name = '$Table' AND column_name IN ($quotedColumns);"
        )
    )
    $missing = @($Columns | Where-Object { $found -notcontains $_ })
    $columnDetail = if ($missing.Count -eq 0) {
        "$Purpose prerequisites are present."
    }
    else {
        "$Purpose is missing column(s): $($missing -join ', ')."
    }
    Add-Check -Name "columns:$Table" -Passed ($missing.Count -eq 0) -Detail $columnDetail
}

function Test-DuplicateValues {
    param(
        [Parameter(Mandatory = $true)][string]$Table,
        [Parameter(Mandatory = $true)][string]$Column,
        [Parameter(Mandatory = $true)][bool]$IgnoreNull,
        [Parameter(Mandatory = $true)][string]$Migration
    )

    if (-not (Test-Table -Table $Table)) {
        Add-Check -Name "duplicates:$Table.$Column" -Passed $true -Detail (
            "Table is absent and will be created by the approved plan before $Migration adds its constraint."
        )
        return
    }

    $where = if ($IgnoreNull) { " WHERE $Column IS NOT NULL" } else { '' }
    $sql = (
        "SELECT COUNT(*) FROM (SELECT $Column FROM $Table$where GROUP BY $Column HAVING COUNT(*) > 1) AS duplicate_values;"
    )
    $duplicates = [int](Invoke-MySqlScalar -Sql $sql -UseDatabase)
    $duplicateDetail = if ($duplicates -eq 0) {
        "$Migration can add or retain the unique key."
    }
    else {
        "$Migration would fail because $duplicates duplicate key value(s) exist; reconcile them in the restored copy first."
    }
    Add-Check -Name "duplicates:$Table.$Column" -Passed ($duplicates -eq 0) -Detail $duplicateDetail
}

$timestamp = Get-Date -Format 'yyyyMMddHHmmssfff'
if ([string]::IsNullOrWhiteSpace($EvidencePath)) {
    $EvidencePath = Join-Path ([System.IO.Path]::GetTempPath()) "kberp-release-preflight\$Database-$timestamp.json"
}
$evidenceDirectory = Split-Path -Parent $EvidencePath
if ([string]::IsNullOrWhiteSpace($evidenceDirectory)) {
    throw 'EvidencePath must include a directory.'
}
New-Item -ItemType Directory -Force -Path $evidenceDirectory | Out-Null

$catalogPath = Join-Path $repoRoot 'docs\governance\migration-catalog.json'
if (-not (Test-Path -LiteralPath $catalogPath)) {
    throw "Migration catalog is missing: $catalogPath"
}
$catalog = Get-Content -LiteralPath $catalogPath -Raw -Encoding UTF8 | ConvertFrom-Json
$plan = @($catalog.existingDatabasePlan | ForEach-Object { [string]$_ })
if ($plan.Count -eq 0) {
    throw 'Migration catalog has no existing-database plan.'
}

$running = (Invoke-DockerCommand -Arguments @(
    'inspect', '--format', '{{.State.Running}}', $DbContainer
) -ReturnOutput | Select-Object -First 1).Trim()
$labelsJson = (Invoke-DockerCommand -Arguments @(
    'inspect', '--format', '{{json .Config.Labels}}', $DbContainer
) -ReturnOutput | Select-Object -First 1).Trim()
$labels = $labelsJson | ConvertFrom-Json
if ($running -ne 'true' -or $labels.'com.docker.compose.service' -ne 'mysql') {
    throw 'The expected local smoke MySQL service is not running.'
}

$databaseExists = ([int](Invoke-MySqlScalar -Sql (
    "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = '$Database';"
)) -eq 1)
$databaseDetail = if ($databaseExists) { 'Target local database exists.' } else { 'Target local database does not exist.' }
Add-Check -Name "database:$Database" -Passed $databaseExists -Detail $databaseDetail

if ($databaseExists) {
    Test-RequiredColumns -Table 'base_data_product' -Columns @('brand_id') -Purpose 'V1.13'
    Test-RequiredColumns -Table 'tbl_product_stock_log' -Columns @('biz_type') -Purpose 'V1.17'
    Test-RequiredColumns -Table 'tbl_receive_sheet_detail' -Columns @('return_num') -Purpose 'V1.24'
    Test-RequiredColumns -Table 'tbl_purchase_return_detail' -Columns @('receive_sheet_detail_id') -Purpose 'V1.25'

    Test-RequiredColumns -Table 'tenant' -Columns @('id', 'name') -Purpose 'V1.21'
    Test-RequiredColumns -Table 'sys_module_tenant' -Columns @('tenant_id', 'module_id') -Purpose 'V1.21'
    Test-RequiredColumns -Table 'sys_menu' -Columns @(
        'id', 'code', 'name', 'title', 'icon', 'component_type', 'component', 'request_param',
        'parent_id', 'sys_module_id', 'path', 'no_cache', 'display', 'hidden', 'permission',
        'is_special', 'available', 'description', 'create_by', 'create_by_id', 'create_time',
        'update_by', 'update_by_id', 'update_time'
    ) -Purpose 'V1.21'
    Test-RequiredColumns -Table 'sys_role' -Columns @(
        'id', 'code', 'name', 'permission', 'available', 'description', 'create_by', 'create_by_id',
        'create_time', 'update_by', 'update_by_id', 'update_time'
    ) -Purpose 'V1.21'
    Test-RequiredColumns -Table 'sys_role_menu' -Columns @('id', 'role_id', 'menu_id') -Purpose 'V1.21'

    if (Test-Table -Table 'tenant') {
        $tenantCount = [int](Invoke-MySqlScalar -Sql "SELECT COUNT(*) FROM tenant WHERE id = '1000';" -UseDatabase)
        $tenantDetail = if ($tenantCount -eq 1) { 'V1.21 target tenant is present.' } else { 'V1.21 requires exactly one tenant with id 1000.' }
        Add-Check -Name 'tenant:1000' -Passed ($tenantCount -eq 1) -Detail $tenantDetail
    }

    if (Test-Table -Table 'sys_module_tenant') {
        $removedModuleRelations = [int](Invoke-MySqlScalar -Sql (
            "SELECT COUNT(*) FROM sys_module_tenant WHERE tenant_id = 1000 AND module_id IN (7, 12, 15);"
        ) -UseDatabase)
        $manualConfirmations.Add(
            "V1.21 will remove $removedModuleRelations tenant-module relation(s) for tenant 1000 and modules 7, 12, 15; business owner confirmation is required."
        )
    }

    Test-DuplicateValues -Table 'shkb_machine_task_tightening' -Column 'task_id' -IgnoreNull $true -Migration 'V1.22'
    Test-DuplicateValues -Table 'shkb_machine_task_magnetic_powder' -Column 'task_id' -IgnoreNull $true -Migration 'V1.22'
    Test-DuplicateValues -Table 'shkb_contract_task' -Column 'contract_id' -IgnoreNull $true -Migration 'V1.23'
}

$result = [ordered]@{
    passed = ($failures.Count -eq 0)
    checkedAt = (Get-Date).ToUniversalTime().ToString('o')
    scope = 'local-isolated-only'
    container = $DbContainer
    database = $Database
    migrationPlan = $plan
    checks = @($checks)
    manualConfirmations = @($manualConfirmations)
    failures = @($failures)
}

$result | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $EvidencePath -Encoding UTF8
if ($AsJson) {
    $result | ConvertTo-Json -Depth 8
}
elseif ($failures.Count -eq 0) {
    Write-Host "Release migration preflight passed for local database '$Database'." -ForegroundColor Green
    foreach ($confirmation in $manualConfirmations) {
        Write-Warning $confirmation
    }
    Write-Host "Evidence: $EvidencePath"
}
else {
    foreach ($failure in $failures) {
        Write-Error $failure -ErrorAction Continue
    }
    Write-Host "Release migration preflight failed with $($failures.Count) issue(s). Evidence: $EvidencePath" -ForegroundColor Red
}

if ($failures.Count -gt 0) {
    exit 1
}
