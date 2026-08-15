[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidateNotNullOrEmpty()]
    [string]$BackupPath,
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[A-Fa-f0-9]{64}$')]
    [string]$ExpectedSha256,
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^[A-Za-z0-9][A-Za-z0-9._/-]*(?::[A-Za-z0-9._-]+)?$')]
    [string]$CandidateImage,
    [ValidatePattern('^[A-Za-z0-9._-]+$')]
    [string]$BackupLabel = 'authorized-production-backup-api',
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577',
    [ValidatePattern('^[A-Za-z0-9][A-Za-z0-9_.-]*$')]
    [string]$RuntimeTemplateContainer = 'kberp-api',
    [ValidateRange(1024, 65535)]
    [int]$HostPort = 8090,
    [ValidateRange(30, 300)]
    [int]$StartupTimeoutSeconds = 180,
    [ValidateRange(0, 15)]
    [int]$IsolatedRedisDatabase = 14,
    [ValidateRange(0, 15)]
    [int]$IsolatedTokenRedisDatabase = 15,
    [string]$EvidencePath,
    [switch]$AsJson
)

$ErrorActionPreference = 'Stop'
$backendRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $backendRoot '..')).Path

if ($DbContainer -ne 'xingyun-smoke-mysql') {
    throw 'Production backup API acceptance is restricted to the local xingyun-smoke-mysql container.'
}
if ($DbUsername -notmatch '^[A-Za-z0-9_]+$') {
    throw 'Database username may contain only letters, digits, and underscores.'
}
if ($DbPassword -notmatch '^[A-Za-z0-9._@-]+$') {
    throw 'The local smoke password contains unsupported shell characters.'
}

# This key is deliberately read only from the current process environment. It must never
# be passed on the command line, emitted in JSON evidence, written to a file, or committed.
$juggSecretKey = [Environment]::GetEnvironmentVariable('JUGG_SECRET_KEY', 'Process')
if ([string]::IsNullOrWhiteSpace($juggSecretKey)) {
    throw 'JUGG_SECRET_KEY must be injected into the current process by an approved secret manager before production-backup API acceptance can start.'
}

$occupiedPort = @(
    Get-NetTCPConnection -State Listen -LocalPort $HostPort -ErrorAction SilentlyContinue
)
if ($occupiedPort.Count -gt 0) {
    throw "Local host port $HostPort is already in use; choose an unused local-only port."
}
if ($IsolatedRedisDatabase -eq $IsolatedTokenRedisDatabase) {
    throw 'The isolated business Redis database and isolated token Redis database must be different.'
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
        # Docker output can contain environment-derived details. Keep it out of console and evidence.
        throw "Docker operation '$($Arguments[0])' failed with exit code $exitCode."
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
        throw 'Unable to parse a local logical-dump SHA-256.'
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

function Invoke-ChildVerification {
    param(
        [Parameter(Mandatory = $true)][string]$ScriptPath,
        [Parameter(Mandatory = $true)][string[]]$Arguments,
        [Parameter(Mandatory = $true)][string]$FailureMessage
    )

    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $ignoredOutput = & powershell -NoProfile -ExecutionPolicy Bypass -File $ScriptPath @Arguments 2>&1
        $exitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $previousPreference
    }
    if ($exitCode -ne 0) {
        throw $FailureMessage
    }
}

function Get-TemplateEnvironment {
    $templateJson = Invoke-DockerCommand -Arguments @('inspect', $RuntimeTemplateContainer) -ReturnOutput
    $template = $templateJson | ConvertFrom-Json
    if ($null -eq $template -or $template.Count -ne 1) {
        throw 'The local runtime template container cannot be inspected.'
    }
    if ($template[0].State.Running -ne $true -or $template[0].HostConfig.NetworkMode -ne 'smoke_default') {
        throw 'The runtime template container must be a running local smoke-network container.'
    }

    $environment = @{}
    foreach ($entry in @($template[0].Config.Env)) {
        $parts = [string]$entry -split '=', 2
        if ($parts.Count -eq 2) {
            $environment[$parts[0]] = $parts[1]
        }
    }
    foreach ($requiredName in @('SHKB_DB_URL', 'SHKB_DB_USERNAME', 'SHKB_DB_PASSWORD')) {
        if (-not $environment.ContainsKey($requiredName) -or [string]::IsNullOrWhiteSpace($environment[$requiredName])) {
            throw "The local runtime template container is missing required database setting '$requiredName'."
        }
    }
    if ($environment['SHKB_DB_URL'] -notmatch '^jdbc:mysql://xingyun-smoke-mysql(?::\d+)?/') {
        throw 'The runtime template database URL must target the local xingyun-smoke-mysql service.'
    }
    foreach ($redisSetting in @('SHKB_REDIS_HOST', 'SPRING_DATA_REDIS_HOST')) {
        if ($environment.ContainsKey($redisSetting) -and
            -not [string]::IsNullOrWhiteSpace($environment[$redisSetting]) -and
            $environment[$redisSetting] -ne 'xingyun-smoke-redis') {
            throw "The runtime template setting '$redisSetting' must target the local xingyun-smoke-redis service."
        }
    }
    if ($environment.ContainsKey('SHKB_RABBITMQ_ADDRESSES') -and
        -not [string]::IsNullOrWhiteSpace($environment['SHKB_RABBITMQ_ADDRESSES']) -and
        $environment['SHKB_RABBITMQ_ADDRESSES'] -notmatch '^xingyun-smoke-rabbitmq(?::\d+)?(?:,xingyun-smoke-rabbitmq(?::\d+)?)*$') {
        throw 'The runtime template RabbitMQ addresses must target the local xingyun-smoke-rabbitmq service.'
    }
    return [pscustomobject]@{
        Container = $template[0]
        Environment = $environment
    }
}

function Wait-ForLocalApiReady {
    param([Parameter(Mandatory = $true)][string]$BaseUrl)

    $deadline = (Get-Date).AddSeconds($StartupTimeoutSeconds)
    $attempts = 0
    while ((Get-Date) -lt $deadline) {
        $attempts++
        try {
            $response = Invoke-WebRequest -UseBasicParsing -Uri "$BaseUrl/readyz" -TimeoutSec 5
            if ($response.StatusCode -eq 200) {
                return [pscustomobject]@{ Ready = $true; Attempts = $attempts }
            }
        }
        catch {
            # Readiness has not yet reached UP; do not capture response bodies from the restored copy.
        }
        Start-Sleep -Seconds 3
    }
    return [pscustomobject]@{ Ready = $false; Attempts = $attempts }
}

function Get-ApiStartupFailureKind {
    param([Parameter(Mandatory = $true)][string]$ContainerName)

    $logs = Invoke-DockerCommand -Arguments @('logs', '--tail', '500', $ContainerName) -ReturnOutput
    $joined = $logs -join [Environment]::NewLine
    if ($joined -match 'BadPaddingException|CryptoException') {
        return 'jugg-secret-decryption-failed'
    }
    if ($joined -match 'Access denied|Communications link failure|Unknown database|doesn.t exist|Unknown column') {
        return 'database-startup-failed'
    }
    if ($joined -match 'Unable to connect.*Redis|Redis.*(refused|failed)') {
        return 'redis-startup-failed'
    }
    if ($joined -match 'Unable to connect.*Rabbit|Rabbit.*(refused|failed)') {
        return 'rabbitmq-startup-failed'
    }
    if ($joined -match 'Address already in use|Port.*already in use') {
        return 'api-port-conflict'
    }
    if ($joined -match 'OutOfMemoryError|Java heap space') {
        return 'api-memory-exhausted'
    }
    return 'api-not-ready-unclassified'
}

function Invoke-ReadOnlyApiProbe {
    param(
        [Parameter(Mandatory = $true)][string]$Name,
        [Parameter(Mandatory = $true)][string]$ScriptPath,
        [Parameter(Mandatory = $true)][string]$BaseUrl,
        [Parameter(Mandatory = $true)][System.Collections.Generic.List[object]]$Checks
    )

    $previousPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $ignoredOutput = & powershell -NoProfile -ExecutionPolicy Bypass -File $ScriptPath -BaseUrl $BaseUrl 2>&1
        $exitCode = $LASTEXITCODE
    }
    finally {
        $ErrorActionPreference = $previousPreference
    }
    if ($exitCode -ne 0) {
        throw "Core API acceptance probe '$Name' failed."
    }
    $Checks.Add([ordered]@{ name = $Name; passed = $true })
}

$timestamp = Get-Date -Format 'yyyyMMddHHmmssfff'
$suffix = [guid]::NewGuid().ToString('N').Substring(0, 6)
$sourceDatabase = 'shkb_production_api_{0}_{1}' -f $timestamp, $suffix
$containerWorkDirectory = "/tmp/$sourceDatabase"
$apiContainer = 'kberp-production-api-{0}' -f $suffix
if ($sourceDatabase -notmatch '^shkb_production_api_\d{17}_[a-f0-9]{6}$' -or
    $containerWorkDirectory -notmatch '^/tmp/shkb_production_api_\d{17}_[a-f0-9]{6}$' -or
    $apiContainer -notmatch '^kberp-production-api-[a-f0-9]{6}$') {
    throw 'Generated local-only cleanup targets did not match the expected restricted pattern.'
}

if ([string]::IsNullOrWhiteSpace($EvidencePath)) {
    $EvidencePath = Join-Path ([System.IO.Path]::GetTempPath()) "kberp-production-backup-api\$BackupLabel-$timestamp.json"
}
$evidenceDirectory = Split-Path -Parent $EvidencePath
if ([string]::IsNullOrWhiteSpace($evidenceDirectory)) {
    throw 'EvidencePath must include a directory.'
}
New-Item -ItemType Directory -Force -Path $evidenceDirectory | Out-Null

$template = Get-TemplateEnvironment
$sourceDbUrl = [string]$template.Environment['SHKB_DB_URL']
if ($sourceDbUrl -notmatch '^(jdbc:mysql://[^/]+)/[^?]+(.*)$') {
    throw 'The local runtime template JDBC URL does not have the expected MySQL database form.'
}
$isolatedDbUrl = $Matches[1] + "/$sourceDatabase" + $Matches[2]

$containerBackupPath = "$containerWorkDirectory/source.sql.gz"
$migrationDirectory = "$containerWorkDirectory/migrations"
$userArg = "-u$DbUsername"
$passwordArg = "-p$DbPassword"
$auth = "$userArg $passwordArg"
$baseUrl = "http://127.0.0.1:$HostPort"
$payloadSha256 = $null
$catalogHash = $null
$preflightBeforePath = Join-Path $evidenceDirectory "$BackupLabel-$timestamp-preflight-before.json"
$preflightAfterPath = Join-Path $evidenceDirectory "$BackupLabel-$timestamp-preflight-after.json"
$restoreEvidencePath = Join-Path $evidenceDirectory "$BackupLabel-$timestamp-restore.json"
$preflightBefore = $null
$preflightAfter = $null
$restoreResult = $null
$candidateImageId = $null
$apiStartup = $null
$apiStartupFailureKind = $null
$migrationRuns = [System.Collections.Generic.List[object]]::new()
$apiChecks = [System.Collections.Generic.List[object]]::new()
$containerWorkCreated = $false
$sourceDatabaseCreated = $false
$apiContainerStarted = $false
$sourceDatabaseRemoved = $false
$containerWorkRemoved = $false
$apiContainerRemoved = $false
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

    $candidateImageId = (Invoke-DockerCommand -Arguments @(
        'image', 'inspect', '--format', '{{.Id}}', $CandidateImage
    ) -ReturnOutput | Select-Object -First 1).Trim()
    if ($candidateImageId -notmatch '^sha256:[a-f0-9]{64}$') {
        throw 'Candidate image inspection did not return an immutable image ID.'
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
    Invoke-ChildVerification -ScriptPath $preflightScript -Arguments @(
        '-DbContainer', $DbContainer, '-Database', $sourceDatabase, '-DbUsername', $DbUsername, '-DbPassword', $DbPassword,
        '-EvidencePath', $preflightBeforePath, '-AsJson'
    ) -FailureMessage 'Migration preflight failed against the restored production backup copy.'
    $preflightBefore = Get-JsonEvidence -Path $preflightBeforePath
    if ($null -eq $preflightBefore -or $preflightBefore.passed -ne $true) {
        throw 'Migration preflight did not produce a successful evidence record.'
    }

    $restoreScript = Join-Path $backendRoot 'scripts\verify-release-restore.ps1'
    Invoke-ChildVerification -ScriptPath $restoreScript -Arguments @(
        '-DbContainer', $DbContainer, '-SourceDatabase', $sourceDatabase, '-DbUsername', $DbUsername, '-DbPassword', $DbPassword,
        '-EvidencePath', $restoreEvidencePath, '-AsJson'
    ) -FailureMessage 'Restore rehearsal failed against the restored production backup copy.'
    $restoreResult = Get-JsonEvidence -Path $restoreEvidencePath
    if ($null -eq $restoreResult -or $restoreResult.passed -ne $true) {
        throw 'Restore rehearsal did not produce a successful evidence record.'
    }

    $catalogVerifier = Join-Path $repoRoot 'scripts\verify-migration-catalog.ps1'
    Invoke-ChildVerification -ScriptPath $catalogVerifier -Arguments @('-Plan', 'ExistingDatabase', '-AsJson') `
        -FailureMessage 'Migration catalog verification failed before preparing the local API acceptance copy.'
    $catalogPath = Join-Path $repoRoot 'docs\governance\migration-catalog.json'
    $catalogHash = (Get-FileHash -LiteralPath $catalogPath -Algorithm SHA256).Hash.ToLowerInvariant()
    $catalog = Get-Content -LiteralPath $catalogPath -Raw -Encoding UTF8 | ConvertFrom-Json
    $migrationRoot = Join-Path $repoRoot $catalog.migrationRoot
    $migrationPlan = @($catalog.existingDatabasePlan | ForEach-Object { [string]$_ })
    if ($migrationPlan.Count -eq 0) {
        throw 'Migration catalog has no existing-database plan.'
    }

    Invoke-DockerCommand -Arguments @('exec', $DbContainer, 'mkdir', '-p', $migrationDirectory)
    foreach ($migrationPath in $migrationPlan) {
        $hostMigration = Join-Path $migrationRoot $migrationPath.Replace('/', '\')
        if (-not (Test-Path -LiteralPath $hostMigration)) {
            throw "Approved migration is missing: $migrationPath"
        }
        $containerMigration = "$migrationDirectory/$([System.IO.Path]::GetFileName($hostMigration))"
        Invoke-DockerCommand -Arguments @('cp', $hostMigration, ($DbContainer + ':' + $containerMigration))
        Invoke-DockerCommand -Arguments @(
            'exec', $DbContainer, 'sh', '-c', "mysql $auth $sourceDatabase < $containerMigration"
        )
        $migrationRuns.Add([ordered]@{ path = $migrationPath; applied = $true })
    }

    Invoke-ChildVerification -ScriptPath $preflightScript -Arguments @(
        '-DbContainer', $DbContainer, '-Database', $sourceDatabase, '-DbUsername', $DbUsername, '-DbPassword', $DbPassword,
        '-EvidencePath', $preflightAfterPath, '-AsJson'
    ) -FailureMessage 'Migration preflight failed after preparing the local API acceptance copy.'
    $preflightAfter = Get-JsonEvidence -Path $preflightAfterPath
    if ($null -eq $preflightAfter -or $preflightAfter.passed -ne $true) {
        throw 'Post-migration preflight did not produce a successful evidence record.'
    }

    $overrides = [ordered]@{
        'SHKB_DB_URL' = $isolatedDbUrl
        'JUGG_SECRET_KEY' = $juggSecretKey
        'SHKB_REDIS_DATABASE' = [string]$IsolatedRedisDatabase
        'SA_TOKEN_ALONE_REDIS_DATABASE' = [string]$IsolatedTokenRedisDatabase
        'APP_RABBITMQ_OUTBOX_ENABLED' = 'false'
        'SPRING_QUARTZ_AUTO_STARTUP' = 'false'
        'SPRING_RABBITMQ_LISTENER_SIMPLE_AUTO_STARTUP' = 'false'
        'SPRING_RABBITMQ_LISTENER_DIRECT_AUTO_STARTUP' = 'false'
    }
    $runArguments = [System.Collections.Generic.List[string]]::new()
    foreach ($argument in @(
        'run', '-d', '--name', $apiContainer, '--network', 'smoke_default',
        '-p', "127.0.0.1:${HostPort}:8088", '--label', 'kberp.local-production-backup-api=true'
    )) {
        $runArguments.Add($argument)
    }
    foreach ($entry in @($template.Container.Config.Env)) {
        $parts = [string]$entry -split '=', 2
        if ($parts.Count -eq 2 -and -not $overrides.Contains($parts[0])) {
            $runArguments.Add('--env')
            $runArguments.Add($entry)
        }
    }
    foreach ($override in $overrides.GetEnumerator()) {
        $runArguments.Add('--env')
        $runArguments.Add("$($override.Key)=$($override.Value)")
    }
    $runArguments.Add($CandidateImage)
    Invoke-DockerCommand -Arguments $runArguments.ToArray()
    $apiContainerStarted = $true

    $apiStartup = Wait-ForLocalApiReady -BaseUrl $baseUrl
    if ($apiStartup.Ready -ne $true) {
        $apiStartupFailureKind = Get-ApiStartupFailureKind -ContainerName $apiContainer
        throw "The isolated candidate API did not reach readiness within $StartupTimeoutSeconds seconds ($apiStartupFailureKind)."
    }

    foreach ($probe in @(
        [ordered]@{ name = 'health'; script = 'verify-health.ps1' },
        [ordered]@{ name = 'menu-baseline'; script = 'verify-menu-baseline.ps1' },
        [ordered]@{ name = 'dashboard'; script = 'verify-dashboard.ps1' },
        [ordered]@{ name = 'contract'; script = 'verify-contract.ps1' },
        [ordered]@{ name = 'equipment'; script = 'verify-equipment.ps1' },
        [ordered]@{ name = 'work-card'; script = 'verify-work-card.ps1' },
        [ordered]@{ name = 'material-read'; script = 'verify-material-flow.ps1' },
        [ordered]@{ name = 'machine-task'; script = 'verify-machine-task.ps1' }
    )) {
        Invoke-ReadOnlyApiProbe -Name $probe.name `
            -ScriptPath (Join-Path $backendRoot (Join-Path 'scripts' $probe.script)) `
            -BaseUrl $baseUrl -Checks $apiChecks
    }
}
catch {
    $runFailure = $_.Exception
}
finally {
    if ($apiContainerStarted) {
        try {
            Invoke-DockerCommand -Arguments @('rm', '-f', $apiContainer)
            $apiContainerRemoved = $true
        }
        catch {
            $cleanupFailures.Add("Failed to remove generated local API container '$apiContainer': $($_.Exception.Message)")
        }
    }
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

$manualConfirmations = if ($null -eq $preflightBefore) { @() } else { @($preflightBefore.manualConfirmations) }
$result = [ordered]@{
    schemaVersion = 1
    passed = ($null -eq $runFailure -and $cleanupFailures.Count -eq 0)
    checkedAt = (Get-Date).ToUniversalTime().ToString('o')
    scope = 'authorized-production-backup-copy-local-api-acceptance-only'
    sourceBackup = [ordered]@{
        label = $BackupLabel
        fileName = $backupFile.Name
        bytes = $backupFile.Length
        compressedSha256 = $backupSha256
        uncompressedSha256 = $payloadSha256
    }
    candidate = [ordered]@{
        image = $CandidateImage
        imageId = $candidateImageId
        localOnly = $true
    }
    encryptedDataContinuity = [ordered]@{
        juggSecretInjectedFromProcessEnvironment = $true
        rawSecretPersisted = $false
        secretFingerprintPersisted = $false
        startupValidated = if ($null -eq $apiStartup) { $false } else { $apiStartup.Ready -eq $true }
        startupFailureKind = $apiStartupFailureKind
    }
    localIsolation = [ordered]@{
        mysqlContainer = $DbContainer
        localApiContainer = $apiContainer
        localApiPort = $HostPort
        isolatedRedisDatabase = $IsolatedRedisDatabase
        isolatedTokenRedisDatabase = $IsolatedTokenRedisDatabase
        generatedSourceDatabase = $sourceDatabase
        backgroundBusinessWritersDisabled = @('outbox', 'quartz', 'rabbitmq-listeners')
        sourceDatabaseRemoved = $sourceDatabaseRemoved
        localApiContainerRemoved = $apiContainerRemoved
        containerWorkDirectoryRemoved = $containerWorkRemoved
    }
    preflightBefore = $preflightBefore
    restoreRehearsal = $restoreResult
    migrationCatalogSha256 = $catalogHash
    appliedMigrationPlan = @($migrationRuns)
    preflightAfter = $preflightAfter
    coreApiAcceptance = [ordered]@{
        candidateReadinessPassed = if ($null -eq $apiStartup) { $false } else { $apiStartup.Ready -eq $true }
        readinessAttempts = if ($null -eq $apiStartup) { 0 } else { $apiStartup.Attempts }
        probes = @($apiChecks)
        businessWriteFlowsExecuted = $false
        authenticationMayWriteOnlyToIsolatedRedisOrAuditState = $true
    }
    manualConfirmations = $manualConfirmations
    productionDeploymentAllowed = $false
    productionDeploymentReason = 'Local API acceptance does not replace V1.21 business confirmation, full business-owner acceptance, a rollback plan, or explicit production change-window approval.'
    failure = if ($runFailure) { $runFailure.Message } else { $null }
    cleanupFailures = @($cleanupFailures)
}

$result | ConvertTo-Json -Depth 14 | Set-Content -LiteralPath $EvidencePath -Encoding UTF8
if ($AsJson) {
    $result | ConvertTo-Json -Depth 14
}
elseif ($result.passed) {
    Write-Host "Production backup API acceptance passed locally: $($apiChecks.Count) core probes." -ForegroundColor Green
    foreach ($confirmation in $manualConfirmations) {
        Write-Warning $confirmation
    }
    Write-Host "Evidence: $EvidencePath"
}
else {
    Write-Host "Production backup API acceptance failed. Evidence: $EvidencePath" -ForegroundColor Red
}

if ($runFailure) {
    throw $runFailure
}
if ($cleanupFailures.Count -gt 0) {
    throw ($cleanupFailures -join [Environment]::NewLine)
}
