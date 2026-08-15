[CmdletBinding()]
param(
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$SourceDatabase = 'shkb_platform',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577',
    [string]$EvidencePath,
    [switch]$AsJson
)

$ErrorActionPreference = 'Stop'
$backendRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $backendRoot '..')).Path

if ($DbContainer -ne 'xingyun-smoke-mysql') {
    throw 'Release restore rehearsal is restricted to the local xingyun-smoke-mysql container.'
}
foreach ($value in @($SourceDatabase, $DbUsername)) {
    if ($value -notmatch '^[A-Za-z0-9_]+$') {
        throw 'Database and username may contain only letters, digits, and underscores.'
    }
}
if ($SourceDatabase -match '^shkb_release_verify_') {
    throw 'SourceDatabase may not be a generated release rehearsal clone.'
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

function Invoke-MySqlScalar {
    param(
        [Parameter(Mandatory = $true)][string]$Sql,
        [string]$DatabaseName
    )

    $arguments = @(
        'exec', $DbContainer, 'mysql', "-u$DbUsername", "-p$DbPassword", '-N', '-B'
    )
    if ($DatabaseName) {
        $arguments += @('-D', $DatabaseName)
    }
    $arguments += @('-e', $Sql)
    $rows = Invoke-DockerCommand -Arguments $arguments -ReturnOutput |
        ForEach-Object { ([string]$_).Trim() } |
        Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
    if (@($rows).Count -eq 0) {
        return ''
    }
    return [string](@($rows)[@($rows).Count - 1])
}

function Test-Database {
    param([Parameter(Mandatory = $true)][string]$DatabaseName)

    return ([int](Invoke-MySqlScalar -Sql (
        "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name = '$DatabaseName';"
    )) -eq 1)
}

function Test-Table {
    param(
        [Parameter(Mandatory = $true)][string]$DatabaseName,
        [Parameter(Mandatory = $true)][string]$Table
    )

    return ([int](Invoke-MySqlScalar -Sql (
        "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = '$DatabaseName' AND table_name = '$Table';"
    )) -eq 1)
}

function Test-Column {
    param(
        [Parameter(Mandatory = $true)][string]$DatabaseName,
        [Parameter(Mandatory = $true)][string]$Table,
        [Parameter(Mandatory = $true)][string]$Column
    )

    return ([int](Invoke-MySqlScalar -Sql (
        "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = '$DatabaseName' AND table_name = '$Table' AND column_name = '$Column';"
    )) -eq 1)
}

function Get-DumpHash {
    param([Parameter(Mandatory = $true)][string]$Command)

    $line = Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'sh', '-c', $Command) -ReturnOutput |
        Select-Object -Last 1
    $hash = (([string]$line).Trim() -split '\s+')[0]
    if ($hash -notmatch '^[a-f0-9]{64}$') {
        throw "Unable to parse SHA-256 from dump command output '$line'."
    }
    return $hash
}

$timestamp = Get-Date -Format 'yyyyMMddHHmmssfff'
$suffix = [guid]::NewGuid().ToString('N').Substring(0, 6)
$cloneDatabase = 'shkb_release_verify_{0}_{1}' -f $timestamp, $suffix
$containerWorkDirectory = "/tmp/$cloneDatabase"
if ($cloneDatabase -notmatch '^shkb_release_verify_\d{17}_[a-f0-9]{6}$' -or
    $containerWorkDirectory -notmatch '^/tmp/shkb_release_verify_\d{17}_[a-f0-9]{6}$') {
    throw 'Generated cleanup target did not match the expected restricted pattern.'
}

if ([string]::IsNullOrWhiteSpace($EvidencePath)) {
    $EvidencePath = Join-Path ([System.IO.Path]::GetTempPath()) "kberp-release-rehearsal\$SourceDatabase-$timestamp.json"
}
$evidenceDirectory = Split-Path -Parent $EvidencePath
if ([string]::IsNullOrWhiteSpace($evidenceDirectory)) {
    throw 'EvidencePath must include a directory.'
}
New-Item -ItemType Directory -Force -Path $evidenceDirectory | Out-Null

$sourceDump = "$containerWorkDirectory/source.sql.gz"
$migrationDirectory = "$containerWorkDirectory/migrations"
$userArg = "-u$DbUsername"
$passwordArg = "-p$DbPassword"
$auth = "$userArg $passwordArg"
$dumpOptions = '--skip-comments --hex-blob --single-transaction --no-tablespaces --set-gtid-purged=OFF'
$sourceHash = $null
$restoreHash = $null
$catalogHash = $null
$preflightBeforePath = Join-Path $evidenceDirectory "$cloneDatabase-before.json"
$preflightAfterPath = Join-Path $evidenceDirectory "$cloneDatabase-after.json"
$migrationRuns = [System.Collections.Generic.List[object]]::new()
$postMigrationChecks = [System.Collections.Generic.List[object]]::new()
$cleanupFailures = [System.Collections.Generic.List[string]]::new()
$runFailure = $null
$cloneCreated = $false
$containerWorkCreated = $false

try {
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
    if (-not (Test-Database -DatabaseName $SourceDatabase)) {
        throw "Source local database '$SourceDatabase' does not exist."
    }

    $catalogVerifier = Join-Path $repoRoot 'scripts\verify-migration-catalog.ps1'
    & $catalogVerifier -Plan ExistingDatabase -AsJson | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw 'Migration catalog verification failed before restore rehearsal.'
    }
    $catalogPath = Join-Path $repoRoot 'docs\governance\migration-catalog.json'
    $catalogHash = (Get-FileHash -LiteralPath $catalogPath -Algorithm SHA256).Hash.ToLowerInvariant()
    $catalog = Get-Content -LiteralPath $catalogPath -Raw -Encoding UTF8 | ConvertFrom-Json
    $migrationPlan = @($catalog.existingDatabasePlan | ForEach-Object { [string]$_ })
    if ($migrationPlan.Count -eq 0) {
        throw 'Migration catalog has no existing-database plan.'
    }
    $migrationRoot = Join-Path $repoRoot $catalog.migrationRoot
    if (-not (Test-Path -LiteralPath $migrationRoot)) {
        throw "Migration root is missing: $migrationRoot"
    }

    Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'mkdir', '-p', $migrationDirectory)
    $containerWorkCreated = $true
    Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'sh', '-c',
        "mysqldump $auth $dumpOptions $SourceDatabase | gzip -c > $sourceDump"
    )
    Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'gzip', '-t', $sourceDump)
    $sourceHash = Get-DumpHash -Command "gzip -dc $sourceDump | sha256sum"

    Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'mysql', $userArg, $passwordArg, '-e',
        "CREATE DATABASE $cloneDatabase CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    )
    $cloneCreated = $true
    Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'sh', '-c',
        "gzip -dc $sourceDump | mysql $auth --init-command=SET@@SESSION.sql_mode=0x4E4F5F454E47494E455F535542535449545554494F4E $cloneDatabase"
    )
    $restoreHash = Get-DumpHash -Command "mysqldump $auth $dumpOptions $cloneDatabase | sha256sum"
    if ($sourceHash -ne $restoreHash) {
        throw "Restore hash mismatch: source=$sourceHash restored=$restoreHash"
    }

    $preflightScript = Join-Path $backendRoot 'scripts\verify-release-preflight.ps1'
    & $preflightScript -DbContainer $DbContainer -Database $cloneDatabase -DbUsername $DbUsername -DbPassword $DbPassword -EvidencePath $preflightBeforePath -AsJson | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw 'Migration preflight failed against the restored clone before applying deltas.'
    }

    foreach ($migrationPath in $migrationPlan) {
        $hostMigration = Join-Path $migrationRoot $migrationPath.Replace('/', '\')
        if (-not (Test-Path -LiteralPath $hostMigration)) {
            throw "Approved migration is missing: $migrationPath"
        }
        $containerMigration = "$migrationDirectory/$([System.IO.Path]::GetFileName($hostMigration))"
        Invoke-DockerCommand -Arguments @('cp', $hostMigration, ($DbContainer + ':' + $containerMigration))
    }

    foreach ($round in 1..2) {
        foreach ($migrationPath in $migrationPlan) {
            $fileName = [System.IO.Path]::GetFileName($migrationPath)
            $containerMigration = "$migrationDirectory/$fileName"
            Invoke-DockerCommand -Arguments @(
                'exec', $DbContainer, 'sh', '-c',
                "mysql $auth $cloneDatabase < $containerMigration"
            )
            $migrationRuns.Add([pscustomobject][ordered]@{
                round = $round
                path = $migrationPath
                sha256 = (Get-FileHash -LiteralPath (Join-Path $migrationRoot $migrationPath.Replace('/', '\')) -Algorithm SHA256).Hash.ToLowerInvariant()
            })
        }
    }

    & $preflightScript -DbContainer $DbContainer -Database $cloneDatabase -DbUsername $DbUsername -DbPassword $DbPassword -EvidencePath $preflightAfterPath -AsJson | Out-Null
    if ($LASTEXITCODE -ne 0) {
        throw 'Migration preflight failed against the clone after applying deltas.'
    }

    foreach ($expected in @(
        [pscustomobject]@{ Table = 'sys_mq_outbox'; Column = $null },
        [pscustomobject]@{ Table = 'sys_mq_inbox'; Column = $null },
        [pscustomobject]@{ Table = 'shkb_machine_info'; Column = $null },
        [pscustomobject]@{ Table = 'shkb_contract_task_repair_status_record'; Column = $null },
        [pscustomobject]@{ Table = 'tbl_receive_sheet_detail'; Column = 'batch_number' },
        [pscustomobject]@{ Table = 'tbl_receive_sheet_detail'; Column = 'serial_number_list' },
        [pscustomobject]@{ Table = 'tbl_purchase_return_detail'; Column = 'serial_number_list' }
    )) {
        $passed = if ($expected.Column) {
            Test-Column -DatabaseName $cloneDatabase -Table $expected.Table -Column $expected.Column
        }
        else {
            Test-Table -DatabaseName $cloneDatabase -Table $expected.Table
        }
        $name = if ($expected.Column) { "$($expected.Table).$($expected.Column)" } else { $expected.Table }
        $postMigrationChecks.Add([pscustomobject][ordered]@{
            name = $name
            passed = $passed
        })
        if (-not $passed) {
            throw "Expected migration output is missing from restored clone: $name"
        }
    }
}
catch {
    $runFailure = $_
}
finally {
    if ($cloneCreated) {
        try {
            Invoke-DockerCommand -Arguments @(
                'exec', $DbContainer, 'mysql', $userArg, $passwordArg, '-e', "DROP DATABASE IF EXISTS $cloneDatabase;"
            )
        }
        catch {
            $cleanupFailures.Add("Failed to remove generated clone '$cloneDatabase': $($_.Exception.Message)")
        }
    }
    if ($containerWorkCreated) {
        try {
            Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'rm', '-rf', $containerWorkDirectory)
        }
        catch {
            $cleanupFailures.Add("Failed to remove generated container work directory '$containerWorkDirectory': $($_.Exception.Message)")
        }
    }
}

$result = [ordered]@{
    passed = ($null -eq $runFailure -and $cleanupFailures.Count -eq 0)
    checkedAt = (Get-Date).ToUniversalTime().ToString('o')
    scope = 'local-isolated-only'
    sourceDatabase = $SourceDatabase
    cloneDatabase = $cloneDatabase
    sourceDumpSha256 = $sourceHash
    restoredDumpSha256 = $restoreHash
    migrationCatalogSha256 = $catalogHash
    migrationRuns = @($migrationRuns)
    postMigrationChecks = @($postMigrationChecks)
    preflightEvidence = @(
        $preflightBeforePath,
        $preflightAfterPath
    )
    failure = if ($runFailure) { $runFailure.Exception.Message } else { $null }
    cleanupFailures = @($cleanupFailures)
}

$result | ConvertTo-Json -Depth 8 | Set-Content -LiteralPath $EvidencePath -Encoding UTF8
if ($AsJson) {
    $result | ConvertTo-Json -Depth 8
}
elseif ($result.passed) {
    Write-Host "Local restore rehearsal passed: source=$SourceDatabase clone=$cloneDatabase sha256=$sourceHash" -ForegroundColor Green
    Write-Host "Evidence: $EvidencePath"
}
else {
    Write-Host "Local restore rehearsal failed. Evidence: $EvidencePath" -ForegroundColor Red
}

if ($runFailure) {
    throw $runFailure
}
if ($cleanupFailures.Count -gt 0) {
    throw ($cleanupFailures -join [Environment]::NewLine)
}
