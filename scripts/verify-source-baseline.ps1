[CmdletBinding()]
param(
    [switch]$Release
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot
$failures = [System.Collections.Generic.List[string]]::new()
$warnings = [System.Collections.Generic.List[string]]::new()

function Add-Failure([string]$Message) {
    $failures.Add($Message)
}

function Add-Warning([string]$Message) {
    $warnings.Add($Message)
}

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    Add-Failure 'git is not available on PATH.'
}
else {
    $gitRoot = (& git -C $repoRoot rev-parse --show-toplevel 2>$null)
    if ($LASTEXITCODE -ne 0) {
        Add-Failure 'The script is not running inside a Git repository.'
    }
    elseif ((Resolve-Path $gitRoot).Path -ne (Resolve-Path $repoRoot).Path) {
        Add-Failure "Expected the shkbERP repository root, but Git resolved '$gitRoot'."
    }

    $origin = (& git -C $repoRoot config --get remote.origin.url 2>$null)
    if ($origin -notin @(
        'git@github.com:Jy027234/shkbERP.git',
        'https://github.com/Jy027234/shkbERP.git',
        'https://github.com/Jy027234/shkbERP'
    )) {
        Add-Failure "Unexpected origin '$origin'. The only release repository is Jy027234/shkbERP."
    }

    $dirty = @(& git -C $repoRoot status --porcelain)
    if ($dirty.Count -gt 0) {
        if ($Release) {
            Add-Failure 'Release mode requires a clean Git worktree.'
        }
        else {
            Add-Warning "The worktree contains $($dirty.Count) changed path(s); development checks may continue, release may not."
        }
    }

    if ($Release) {
        $tags = @(& git -C $repoRoot tag --points-at HEAD)
        if ($tags.Count -eq 0) {
            Add-Failure 'Release mode requires HEAD to have an immutable version tag.'
        }
    }
}

foreach ($requiredPath in @(
    'erp-backend/AGENTS.md',
    'erp-backend/UPGRADE.md',
    'erp-frontend/AGENTS.md',
    'erp-frontend/UPGRADE.md',
    'docs/governance/SOURCE_OF_TRUTH.md',
    'docs/governance/BACKEND_RECONCILIATION.md',
    'docs/governance/SHKB_MODULE_MATRIX.md',
    'docs/governance/source-baseline.json',
    'erp-backend/scripts/verify-menu-baseline.ps1',
    'erp-backend/xingyun-api/src/main/resources/db/migration/tenant/V1.21__shkb_menu_permission_baseline.sql'
)) {
    if (-not (Test-Path -LiteralPath (Join-Path $repoRoot $requiredPath))) {
        Add-Failure "Required governed file is missing: $requiredPath"
    }
}

foreach ($nestedGit in @('erp-backend/.git', 'erp-frontend/.git')) {
    if (Test-Path -LiteralPath (Join-Path $repoRoot $nestedGit)) {
        Add-Failure "Nested Git metadata is forbidden in the canonical repository: $nestedGit"
    }
}

$baselinePath = Join-Path $repoRoot 'docs/governance/source-baseline.json'
if (Test-Path -LiteralPath $baselinePath) {
    try {
        $baseline = Get-Content -LiteralPath $baselinePath -Raw | ConvertFrom-Json
        if ($baseline.canonical.repository -ne 'git@github.com:Jy027234/shkbERP.git') {
            Add-Failure 'The governance baseline names an unexpected canonical repository.'
        }
        if ($Release -and $baseline.release.deploymentAllowed -ne $true) {
            Add-Failure 'Production deployment is locked by source-baseline.json because reconciliation is incomplete.'
        }
        if ($Release -and $baseline.reconciliation.status -ne 'ready') {
            Add-Failure "Reconciliation status is '$($baseline.reconciliation.status)', not 'ready'."
        }
    }
    catch {
        Add-Failure "Cannot parse source-baseline.json: $($_.Exception.Message)"
    }
}

$frontendPackagePath = Join-Path $repoRoot 'erp-frontend/package.json'
if (Test-Path -LiteralPath $frontendPackagePath) {
    try {
        $frontendPackage = Get-Content -LiteralPath $frontendPackagePath -Raw | ConvertFrom-Json
        if ($frontendPackage.packageManager -ne 'pnpm@9.15.9') {
            Add-Failure "Frontend packageManager must be pnpm@9.15.9, found '$($frontendPackage.packageManager)'."
        }
    }
    catch {
        Add-Failure "Cannot parse erp-frontend/package.json: $($_.Exception.Message)"
    }
}

$nodeVersionPath = Join-Path $repoRoot 'erp-frontend/.node-version'
if ((Test-Path -LiteralPath $nodeVersionPath) -and ((Get-Content -LiteralPath $nodeVersionPath -Raw).Trim() -notmatch '^24(?:\.|$)')) {
    Add-Failure 'Frontend .node-version must select Node.js 24.'
}

$backendPomPath = Join-Path $repoRoot 'erp-backend/pom.xml'
if (Test-Path -LiteralPath $backendPomPath) {
    $backendPom = Get-Content -LiteralPath $backendPomPath -Raw
    if ($backendPom -notmatch '<java\.version>25</java\.version>') {
        Add-Failure 'Backend pom.xml must declare java.version 25.'
    }
    if ($backendPom -notmatch '<version>3\.5\.[0-9]+</version>') {
        Add-Failure 'Backend pom.xml must remain on the governed Spring Boot 3.5.x line.'
    }
}

$migrationRoot = Join-Path $repoRoot 'erp-backend/xingyun-api/src/main/resources/db/migration'
foreach ($scope in @('platform', 'tenant')) {
    $scopePath = Join-Path $migrationRoot $scope
    if (-not (Test-Path -LiteralPath $scopePath)) {
        Add-Failure "Migration scope is missing: $scope"
        continue
    }

    $versions = Get-ChildItem -LiteralPath $scopePath -File -Filter 'V*__*.sql' | ForEach-Object {
        if ($_.Name -match '^V(?<version>[0-9]+(?:\.[0-9]+)*)__') {
            [pscustomobject]@{ Version = $Matches.version; Name = $_.Name }
        }
        else {
            Add-Failure "Invalid migration filename in ${scope}: $($_.Name)"
        }
    }

    foreach ($duplicate in @($versions | Group-Object Version | Where-Object Count -gt 1)) {
        Add-Failure "Duplicate $scope migration version V$($duplicate.Name): $($duplicate.Group.Name -join ', ')"
    }
}

foreach ($warning in $warnings) {
    Write-Warning $warning
}

if ($failures.Count -gt 0) {
    foreach ($failure in $failures) {
        Write-Error $failure -ErrorAction Continue
    }
    Write-Host "Source baseline verification failed with $($failures.Count) issue(s)." -ForegroundColor Red
    exit 1
}

$mode = if ($Release) { 'release' } else { 'development' }
Write-Host "Source baseline verification passed in $mode mode." -ForegroundColor Green
