[CmdletBinding()]
param(
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$Database = 'shkb_platform',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577',
    [switch]$KeepArtifact
)

$ErrorActionPreference = 'Stop'

if ($DbContainer -ne 'xingyun-smoke-mysql') {
    throw 'This recovery rehearsal is restricted to the local xingyun-smoke-mysql container.'
}
foreach ($value in @($Database, $DbUsername)) {
    if ($value -notmatch '^[A-Za-z0-9_]+$') {
        throw 'Database and username may contain only letters, digits, and underscores.'
    }
}
if ($DbPassword -notmatch '^[A-Za-z0-9._@-]+$') {
    throw 'The smoke password contains unsupported shell characters.'
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
    } finally {
        $ErrorActionPreference = $previousPreference
    }
    if ($exitCode -ne 0) {
        throw "Docker command failed: $($output -join [Environment]::NewLine)"
    }
    if ($ReturnOutput) {
        return @($output)
    }
}

$running = (Invoke-DockerCommand -Arguments @(
    'inspect', '--format', '{{.State.Running}}', $DbContainer
) -ReturnOutput | Select-Object -First 1).Trim()
$labelsJson = (Invoke-DockerCommand -Arguments @(
    'inspect', '--format', '{{json .Config.Labels}}', $DbContainer
) -ReturnOutput | Select-Object -First 1).Trim()
$labels = $labelsJson | ConvertFrom-Json
$composeService = $labels.'com.docker.compose.service'
if ($running -ne 'true' -or $composeService -ne 'mysql') {
    throw 'The expected local smoke MySQL service is not running.'
}

$timestamp = Get-Date -Format 'yyyyMMddHHmmssfff'
$restoreDatabase = "shkb_restore_verify_$timestamp"
$containerBackup = "/tmp/$restoreDatabase.sql.gz"
$artifactDirectory = Join-Path ([System.IO.Path]::GetTempPath()) 'kberp-backup-verify'
$hostBackup = Join-Path $artifactDirectory "$restoreDatabase.sql.gz"
$userArg = "-u$DbUsername"
$passwordArg = "-p$DbPassword"
$auth = "$userArg $passwordArg"
$dumpOptions = '--skip-comments --hex-blob --single-transaction --no-tablespaces --set-gtid-purged=OFF'
$restoreCreated = $false

New-Item -ItemType Directory -Path $artifactDirectory -Force | Out-Null

try {
    $sourceExists = Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'mysql', $userArg, $passwordArg, '-N', '-B', '-e',
        "SELECT COUNT(*) FROM information_schema.schemata WHERE schema_name='$Database';"
    ) -ReturnOutput
    if (($sourceExists | Select-Object -Last 1).Trim() -ne '1') {
        throw "Source smoke database '$Database' does not exist."
    }

    Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'sh', '-c',
        "mysqldump $auth $dumpOptions $Database | gzip -c > $containerBackup"
    )
    Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'gzip', '-t', $containerBackup)
    Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'mysql', $userArg, $passwordArg, '-e',
        "CREATE DATABASE ``$restoreDatabase`` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
    )
    $restoreCreated = $true
    Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'sh', '-c',
        "gzip -dc $containerBackup | mysql $auth --init-command=SET@@SESSION.sql_mode=0x4E4F5F454E47494E455F535542535449545554494F4E $restoreDatabase"
    )

    $sourceHashLine = Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'sh', '-c', "gzip -dc $containerBackup | sha256sum"
    ) -ReturnOutput | Select-Object -Last 1
    $restoredHashLine = Invoke-DockerCommand -Arguments @(
        'exec', $DbContainer, 'sh', '-c',
        "mysqldump $auth $dumpOptions $restoreDatabase | sha256sum"
    ) -ReturnOutput | Select-Object -Last 1
    $sourceHash = ($sourceHashLine -split '\s+')[0]
    $restoredHash = ($restoredHashLine -split '\s+')[0]
    if (-not $sourceHash -or $sourceHash -ne $restoredHash) {
        throw "Restore hash mismatch: source=$sourceHash restored=$restoredHash"
    }

    Invoke-DockerCommand -Arguments @('cp', "${DbContainer}:$containerBackup", $hostBackup)
    $artifact = Get-Item -LiteralPath $hostBackup
    Write-Host "Backup and restore rehearsal passed: sha256=$sourceHash size=$($artifact.Length) bytes" -ForegroundColor Green
    if ($KeepArtifact) {
        Write-Host "Backup artifact retained at $hostBackup"
    }
} finally {
    if ($restoreCreated) {
        Invoke-DockerCommand -Arguments @(
            'exec', $DbContainer, 'mysql', $userArg, $passwordArg, '-e', "DROP DATABASE IF EXISTS ``$restoreDatabase``;"
        )
    }
    Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'rm', '-f', $containerBackup)
    if (-not $KeepArtifact -and (Test-Path -LiteralPath $hostBackup)) {
        Remove-Item -LiteralPath $hostBackup -Force
    }
}
