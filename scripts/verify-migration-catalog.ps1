[CmdletBinding()]
param(
    [ValidateSet('All', 'ExistingDatabase')]
    [string]$Plan = 'All',
    [switch]$AsJson
)

$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
$catalogPath = Join-Path $repoRoot 'docs\governance\migration-catalog.json'
$failures = [System.Collections.Generic.List[string]]::new()

function Add-Failure {
    param([Parameter(Mandatory = $true)][string]$Message)

    $failures.Add($Message)
}

function Get-CanonicalMigrationHash {
    <#
        Versioned SQL is Git text content and may be checked out as CRLF on
        Windows or LF on Linux.  The catalog deliberately hashes every byte
        except the physical representation of line endings, so the same Git
        content has one portable checksum while all non-line-ending changes
        remain detectable.
    #>
    param([Parameter(Mandatory = $true)][string]$Path)

    [byte[]]$sourceBytes = [System.IO.File]::ReadAllBytes($Path)
    $canonicalBytes = [System.Collections.Generic.List[byte]]::new($sourceBytes.Length)

    for ($index = 0; $index -lt $sourceBytes.Length; $index++) {
        if ($sourceBytes[$index] -eq [byte]13) {
            $canonicalBytes.Add([byte]10)
            if (($index + 1) -lt $sourceBytes.Length -and $sourceBytes[$index + 1] -eq [byte]10) {
                $index++
            }
            continue
        }
        $canonicalBytes.Add($sourceBytes[$index])
    }

    $sha256 = [System.Security.Cryptography.SHA256]::Create()
    try {
        return ([System.BitConverter]::ToString($sha256.ComputeHash($canonicalBytes.ToArray()))).Replace('-', '').ToLowerInvariant()
    }
    finally {
        $sha256.Dispose()
    }
}

function Get-RiskTokens {
    param([Parameter(Mandatory = $true)][string]$Sql)

    $patterns = [ordered]@{
        'drop-table-or-database' = '(?im)^\s*DROP\s+(?!TEMPORARY\s+TABLE\b)(?:TABLE|DATABASE|SCHEMA)\b'
        'drop-column' = '(?im)^\s*(?:ALTER\s+TABLE\b.*\s+)?DROP\s+COLUMN\b'
        'drop-index-or-constraint' = '(?im)^\s*(?:ALTER\s+TABLE\b.*\s+)?DROP\s+(?:INDEX|PRIMARY\s+KEY|FOREIGN\s+KEY|CONSTRAINT)\b'
        'truncate' = '(?im)^\s*TRUNCATE\s+(?:TABLE\s+)?'
        'delete-data' = '(?im)^\s*DELETE\s+FROM\b'
        'update-data' = '(?im)^\s*UPDATE\s+'
        'temporary-table-cleanup' = '(?im)^\s*DROP\s+TEMPORARY\s+TABLE\b'
    }

    $tokens = [System.Collections.Generic.List[string]]::new()
    foreach ($pair in $patterns.GetEnumerator()) {
        if ($Sql -match $pair.Value) {
            $tokens.Add($pair.Key)
        }
    }
    return @($tokens | Sort-Object -Unique)
}

function Get-NormalizedRelativePath {
    param(
        [Parameter(Mandatory = $true)][string]$Root,
        [Parameter(Mandatory = $true)][string]$Path
    )

    $rootWithSeparator = $Root.TrimEnd('\', '/') + [System.IO.Path]::DirectorySeparatorChar
    if (-not $Path.StartsWith($rootWithSeparator, [System.StringComparison]::OrdinalIgnoreCase)) {
        throw "Path '$Path' is outside expected root '$Root'."
    }
    return $Path.Substring($rootWithSeparator.Length).Replace('\', '/')
}

function Get-MigrationVersionKey {
    param([Parameter(Mandatory = $true)][string]$Version)

    if ($Version -notmatch '^[0-9]+(?:\.[0-9]+)*$') {
        return $null
    }
    return (($Version -split '\.' | ForEach-Object { '{0:D8}' -f [int]$_ }) -join '.')
}

if (-not (Test-Path -LiteralPath $catalogPath)) {
    Add-Failure "Migration catalog is missing: $catalogPath"
    $catalog = $null
}
else {
    try {
        $catalog = Get-Content -LiteralPath $catalogPath -Raw -Encoding UTF8 | ConvertFrom-Json
    }
    catch {
        Add-Failure "Cannot parse migration catalog: $($_.Exception.Message)"
        $catalog = $null
    }
}

$actualMigrations = @()
$migrationRoot = $null
if ($catalog) {
    if ($catalog.schemaVersion -ne 1) {
        Add-Failure "Unsupported migration catalog schemaVersion '$($catalog.schemaVersion)'."
    }
    if ([string]$catalog.checksumAlgorithm -ne 'sha256-lf-bytes-v1') {
        Add-Failure "Unsupported migration catalog checksumAlgorithm '$($catalog.checksumAlgorithm)'."
    }
    if ($catalog.policy.automaticExecution -ne $false) {
        Add-Failure 'Migration catalog must explicitly state automaticExecution=false.'
    }
    if ($catalog.policy.productionDeploymentAllowed -ne $false) {
        Add-Failure 'Migration catalog must keep productionDeploymentAllowed=false.'
    }

    $migrationRoot = Join-Path $repoRoot $catalog.migrationRoot
    if (-not (Test-Path -LiteralPath $migrationRoot)) {
        Add-Failure "Migration root does not exist: $migrationRoot"
    }
    else {
        $actualMigrations = @(
            Get-ChildItem -LiteralPath $migrationRoot -Recurse -File -Filter 'V*__*.sql' |
                Sort-Object FullName |
                ForEach-Object {
                    [pscustomobject]@{
                        Path = Get-NormalizedRelativePath -Root $migrationRoot -Path $_.FullName
                        FullName = $_.FullName
                    }
                }
        )
    }
}

$validClassifications = @(
    'new-install-only',
    'historical-baseline-only',
    'existing-database-delta'
)
$entryByPath = @{}
$catalogEntries = @()
if ($catalog) {
    $catalogEntries = @($catalog.entries)
    if ($catalogEntries.Count -eq 0) {
        Add-Failure 'Migration catalog has no entries.'
    }

    foreach ($entry in $catalogEntries) {
        $path = [string]$entry.path
        if ([string]::IsNullOrWhiteSpace($path) -or $path -match '(^|/)\.\.(/|$)') {
            Add-Failure "Migration catalog contains an invalid path '$path'."
            continue
        }
        if ($entryByPath.ContainsKey($path)) {
            Add-Failure "Migration catalog contains a duplicate path '$path'."
            continue
        }
        $entryByPath[$path] = $entry

        if ($validClassifications -notcontains [string]$entry.classification) {
            Add-Failure "Migration '$path' has invalid classification '$($entry.classification)'."
        }
        if ([string]::IsNullOrWhiteSpace([string]$entry.sha256) -or $entry.sha256 -notmatch '^[a-f0-9]{64}$') {
            Add-Failure "Migration '$path' must declare a lowercase SHA-256 hash."
        }

        $declaredTokens = @()
        if ($null -ne $entry.riskTokens) {
            $declaredTokens = @($entry.riskTokens | ForEach-Object { [string]$_ } | Sort-Object -Unique)
        }
        foreach ($token in $declaredTokens) {
            if ($token -notin @(
                'drop-table-or-database',
                'drop-column',
                'drop-index-or-constraint',
                'truncate',
                'delete-data',
                'update-data',
                'temporary-table-cleanup'
            )) {
                Add-Failure "Migration '$path' declares unknown risk token '$token'."
            }
        }

        if ($entry.classification -eq 'existing-database-delta') {
            $forbiddenTokens = @(
                'drop-table-or-database',
                'drop-column',
                'drop-index-or-constraint',
                'truncate'
            )
            foreach ($token in $declaredTokens) {
                if ($forbiddenTokens -contains $token) {
                    Add-Failure "Existing-database migration '$path' declares forbidden irreversible token '$token'."
                }
            }
            if (($declaredTokens -contains 'delete-data' -or $declaredTokens -contains 'update-data') -and
                $entry.controlledDataMutation -ne $true) {
                Add-Failure "Existing-database migration '$path' mutates data but is not marked controlledDataMutation=true."
            }
        }
    }

    $existingPlan = @($catalog.existingDatabasePlan | ForEach-Object { [string]$_ })
    if ($existingPlan.Count -eq 0) {
        Add-Failure 'existingDatabasePlan must not be empty.'
    }
    $previousVersionByScope = @{}
    foreach ($path in $existingPlan) {
        if (-not $entryByPath.ContainsKey($path)) {
            Add-Failure "existingDatabasePlan references unknown migration '$path'."
            continue
        }
        $planEntry = $entryByPath[$path]
        if ($planEntry.classification -ne 'existing-database-delta') {
            Add-Failure "existingDatabasePlan may only contain existing-database-delta migrations: '$path'."
        }
        $versionKey = Get-MigrationVersionKey -Version ([string]$planEntry.version)
        if (-not $versionKey) {
            Add-Failure "existingDatabasePlan migration '$path' has an invalid version '$($planEntry.version)'."
        }
        elseif ($previousVersionByScope.ContainsKey($planEntry.scope) -and
            [string]::CompareOrdinal($versionKey, $previousVersionByScope[$planEntry.scope]) -le 0) {
            Add-Failure "existingDatabasePlan is not strictly ordered for scope '$($planEntry.scope)' at '$path'."
        }
        else {
            $previousVersionByScope[$planEntry.scope] = $versionKey
        }
    }
    foreach ($entry in $catalogEntries | Where-Object { $_.classification -eq 'existing-database-delta' }) {
        if ($existingPlan -notcontains $entry.path) {
            Add-Failure "Existing-database migration '$($entry.path)' is missing from existingDatabasePlan."
        }
    }
    foreach ($duplicate in @($existingPlan | Group-Object | Where-Object Count -gt 1)) {
        Add-Failure "existingDatabasePlan contains duplicate migration '$($duplicate.Name)'."
    }
}

$actualPaths = @($actualMigrations | ForEach-Object Path)
foreach ($migration in $actualMigrations) {
    if (-not $entryByPath.ContainsKey($migration.Path)) {
        Add-Failure "Migration file is not cataloged: $($migration.Path)"
        continue
    }

    $entry = $entryByPath[$migration.Path]
    $actualHash = Get-CanonicalMigrationHash -Path $migration.FullName
    if ($actualHash -ne [string]$entry.sha256) {
        Add-Failure "Migration checksum differs from catalog: $($migration.Path)"
    }

    $actualTokens = @(Get-RiskTokens -Sql (Get-Content -LiteralPath $migration.FullName -Raw -Encoding UTF8))
    $declaredTokens = @()
    if ($null -ne $entry.riskTokens) {
        $declaredTokens = @($entry.riskTokens | ForEach-Object { [string]$_ } | Sort-Object -Unique)
    }
    if (($actualTokens -join '|') -ne ($declaredTokens -join '|')) {
        Add-Failure "Migration risk tokens differ from catalog: $($migration.Path) actual=[$($actualTokens -join ',')] declared=[$($declaredTokens -join ',')]"
    }

    $scope = $migration.Path.Split('/')[0]
    if ($entry.scope -ne $scope) {
        Add-Failure "Migration scope differs from catalog: $($migration.Path) declares '$($entry.scope)'."
    }
}

foreach ($entry in $catalogEntries) {
    if ($actualPaths -notcontains $entry.path) {
        Add-Failure "Catalog entry has no migration file: $($entry.path)"
    }
}

$selectedPaths = if ($Plan -eq 'ExistingDatabase' -and $catalog) {
    @($catalog.existingDatabasePlan | ForEach-Object { [string]$_ })
}
else {
    @($catalogEntries | ForEach-Object { [string]$_.path })
}

$result = [ordered]@{
    passed = ($failures.Count -eq 0)
    plan = $Plan
    catalog = 'docs/governance/migration-catalog.json'
    migrationCount = $actualMigrations.Count
    selectedPaths = $selectedPaths
    failures = @($failures)
}

if ($AsJson) {
    $result | ConvertTo-Json -Depth 6
}
else {
    if ($failures.Count -gt 0) {
        foreach ($failure in $failures) {
            Write-Error $failure -ErrorAction Continue
        }
        Write-Host "Migration catalog verification failed with $($failures.Count) issue(s)." -ForegroundColor Red
    }
    else {
        $planDescription = if ($Plan -eq 'ExistingDatabase') { 'existing database deployment plan' } else { 'all migrations' }
        Write-Host "Migration catalog verification passed for $planDescription ($($actualMigrations.Count) cataloged files)." -ForegroundColor Green
    }
}

if ($failures.Count -gt 0) {
    exit 1
}
