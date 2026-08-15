[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateNotNullOrEmpty()]
    [string]$BackupPath,
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[A-Fa-f0-9]{64}$')]
    [string]$ExpectedSha256,
    [ValidatePattern('^[A-Za-z0-9._-]+$')]
    [string]$BackupLabel = 'authorized-production-backup',
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577',
    [string]$EvidencePath,
    [switch]$AsJson
)

$ErrorActionPreference = 'Stop'
$backendRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path

if ($DbContainer -ne 'xingyun-smoke-mysql') {
    throw 'Production backup-copy verification is restricted to the local xingyun-smoke-mysql container.'
}
foreach ($value in @($DbUsername)) {
    if ($value -notmatch '^[A-Za-z0-9_]+$') {
        throw 'Database username may contain only letters, digits, and underscores.'
    }
}
if ($DbPassword -notmatch '^[A-Za-z0-9._@-]+$') {
    throw 'The local smoke password contains unsupported shell characters.'
}

$resolvedBackupPath = (Resolve-Path -LiteralPath $BackupPath).Path
if (-not $resolvedBackupPath.EndsWith('.sql.gz', [System.StringComparison]::OrdinalIgnoreCase)) {
    throw 'BackupPath must reference a local .sql.gz logical backup.'
}
$backupFile = Get-Item -LiteralPath $resolvedBackupPath
$backupSha256 = (Get-FileHash -LiteralPath $resolvedBackupPath -Algorithm SHA256).Hash.ToLowerInvariant()
if ($backupSha256 -ne $ExpectedSha256.ToLowerInvariant()) {
    throw "Backup SHA-256 mismatch: expected=$($ExpectedSha256.ToLowerInvariant()) actual=$backupSha256"
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

function Get-JsonEvidence {
    param([Parameter(Mandatory = $true)][string]$Path)

    if (-not (Test-Path -LiteralPath $Path)) {
        return $null
    }
    return Get-Content -LiteralPath $Path -Raw -Encoding UTF8 | ConvertFrom-Json
}

$timestamp = Get-Date -Format 'yyyyMMddHHmmssfff'
$suffix = [guid]::NewGuid().ToString('N').Substring(0, 6)
$sourceDatabase = 'shkb_production_copy_{0}_{1}' -f $timestamp, $suffix
$containerWorkDirectory = "/tmp/$sourceDatabase"
if ($sourceDatabase -notmatch '^shkb_production_copy_\d{17}_[a-f0-9]{6}$' -or
    $containerWorkDirectory -notmatch '^/tmp/shkb_production_copy_\d{17}_[a-f0-9]{6}$') {
    throw 'Generated local-only cleanup targets did not match the expected restricted pattern.'
}

if ([string]::IsNullOrWhiteSpace($EvidencePath)) {
    $EvidencePath = Join-Path ([System.IO.Path]::GetTempPath()) "kberp-production-backup-copy\$BackupLabel-$timestamp.json"
}
$evidenceDirectory = Split-Path -Parent $EvidencePath
if ([string]::IsNullOrWhiteSpace($evidenceDirectory)) {
    throw 'EvidencePath must include a directory.'
}
New-Item -ItemType Directory -Force -Path $evidenceDirectory | Out-Null

$containerBackupPath = "$containerWorkDirectory/source.sql.gz"
$userArg = "-u$DbUsername"
$passwordArg = "-p$DbPassword"
$auth = "$userArg $passwordArg"
$payloadSha256 = $null
$preflightEvidencePath = Join-Path $evidenceDirectory "$BackupLabel-$timestamp-preflight.json"
$restoreEvidencePath = Join-Path $evidenceDirectory "$BackupLabel-$timestamp-restore.json"
$preflightResult = $null
$restoreResult = $null
$containerWorkCreated = $false
$sourceDatabaseCreated = $false
$sourceDatabaseRemoved = $false
$containerWorkRemoved = $false
$runFailure = $null
$cleanupFailures = [System.Collections.Generic.List[string]]::new()

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

    Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'mkdir', '-p', $containerWorkDirectory)
    $containerWorkCreated = $true
    Invoke-DockerCommand -Arguments @('cp', $resolvedBackupPath, ($DbContainer + ':' + $containerBackupPath))
    Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'gzip', '-t', $containerBackupPath)
    $payloadSha256 = Get-DumpHash -Command "gzip -dc $containerBackupPath | sha256sum"

    Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'mysql', $userArg, $passwordArg, '-e',
        "CREATE DATABASE $sourceDatabase CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    )
    $sourceDatabaseCreated = $true
    Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'sh', '-c',
        "gzip -dc $containerBackupPath | mysql $auth --init-command=SET@@SESSION.sql_mode=0x4E4F5F454E47494E455F535542535449545554494F4E $sourceDatabase"
    )

    $preflightScript = Join-Path $backendRoot 'scripts\verify-release-preflight.ps1'
    & powershell -NoProfile -ExecutionPolicy Bypass -File $preflightScript `
        -DbContainer $DbContainer -Database $sourceDatabase -DbUsername $DbUsername -DbPassword $DbPassword `
        -EvidencePath $preflightEvidencePath -AsJson | Out-Null
    $preflightExitCode = $LASTEXITCODE
    $preflightResult = Get-JsonEvidence -Path $preflightEvidencePath
    if ($preflightExitCode -ne 0 -or $null -eq $preflightResult -or $preflightResult.passed -ne $true) {
        throw 'Migration preflight failed against the restored production backup copy.'
    }

    $restoreScript = Join-Path $backendRoot 'scripts\verify-release-restore.ps1'
    & powershell -NoProfile -ExecutionPolicy Bypass -File $restoreScript `
        -DbContainer $DbContainer -SourceDatabase $sourceDatabase -DbUsername $DbUsername -DbPassword $DbPassword `
        -EvidencePath $restoreEvidencePath -AsJson | Out-Null
    $restoreExitCode = $LASTEXITCODE
    $restoreResult = Get-JsonEvidence -Path $restoreEvidencePath
    if ($restoreExitCode -ne 0 -or $null -eq $restoreResult -or $restoreResult.passed -ne $true) {
        throw 'Restore rehearsal failed against the restored production backup copy.'
    }
}
catch {
    $runFailure = $_.Exception
}
finally {
    if ($sourceDatabaseCreated) {
        try {
            Invoke-DockerCommand -Arguments @(
                'exec', $DbContainer, 'mysql', $userArg, $passwordArg, '-e', "DROP DATABASE IF EXISTS $sourceDatabase;"
            )
            $sourceDatabaseRemoved = $true
        }
        catch {
            $cleanupFailures.Add("Failed to remove generated local source database '$sourceDatabase': $($_.Exception.Message)")
        }
    }
    if ($containerWorkCreated) {
        try {
            Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'rm', '-rf', $containerWorkDirectory)
            $containerWorkRemoved = $true
        }
        catch {
            $cleanupFailures.Add("Failed to remove generated local container work directory '$containerWorkDirectory': $($_.Exception.Message)")
        }
    }
}

$manualConfirmations = if ($null -eq $preflightResult) { @() } else { @($preflightResult.manualConfirmations) }
$result = [ordered]@{
    schemaVersion = 1
    passed = ($null -eq $runFailure -and $cleanupFailures.Count -eq 0)
    checkedAt = (Get-Date).ToUniversalTime().ToString('o')
    scope = 'authorized-production-backup-copy-restored-locally-only'
    sourceBackup = [ordered]@{
        label = $BackupLabel
        fileName = $backupFile.Name
        bytes = $backupFile.Length
        compressedSha256 = $backupSha256
        uncompressedSha256 = $payloadSha256
    }
    localIsolation = [ordered]@{
        container = $DbContainer
        generatedSourceDatabase = $sourceDatabase
        sourceDatabaseRemoved = $sourceDatabaseRemoved
        containerWorkDirectoryRemoved = $containerWorkRemoved
    }
    preflight = $preflightResult
    restoreRehearsal = $restoreResult
    manualConfirmations = $manualConfirmations
    productionDeploymentAllowed = $false
    productionDeploymentReason = 'Technical backup-copy verification does not replace business confirmation, a rollback plan, or an explicit production change-window approval.'
    failure = if ($runFailure) { $runFailure.Message } else { $null }
    cleanupFailures = @($cleanupFailures)
}

$result | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath $EvidencePath -Encoding UTF8
if ($AsJson) {
    $result | ConvertTo-Json -Depth 12
}
elseif ($result.passed) {
    Write-Host "Production backup-copy verification passed locally: source=$sourceDatabase" -ForegroundColor Green
    foreach ($confirmation in $manualConfirmations) {
        Write-Warning $confirmation
    }
    Write-Host "Evidence: $EvidencePath"
}
else {
    Write-Host "Production backup-copy verification failed. Evidence: $EvidencePath" -ForegroundColor Red
}

if ($runFailure) {
    throw $runFailure
}
if ($cleanupFailures.Count -gt 0) {
    throw ($cleanupFailures -join [Environment]::NewLine)
}
