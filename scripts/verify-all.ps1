[CmdletBinding()]
param(
    [switch]$Full,
    [switch]$Install,
    [switch]$Release,
    [switch]$Candidate
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot

if ($Release -and -not $Full) {
    throw 'Release verification requires -Full.'
}
if ($Candidate -and -not $Full) {
    throw 'Release candidate verification requires -Full.'
}
if ($Release -and $Candidate) {
    throw 'Release and Candidate modes are mutually exclusive.'
}

$sourceScript = Join-Path $repoRoot 'scripts\verify-source-baseline.ps1'
$sourceArgs = @{}
if ($Release) {
    $sourceArgs.Release = $true
}
if ($Candidate) {
    $sourceArgs.Candidate = $true
}

Push-Location $repoRoot
try {
    & $sourceScript @sourceArgs
    if (-not $?) {
        throw 'Source baseline verification failed.'
    }
}
finally {
    Pop-Location
}

$migrationScript = Join-Path $repoRoot 'scripts\verify-migration-catalog.ps1'
$migrationArgs = @{}
Push-Location $repoRoot
try {
    & $migrationScript @migrationArgs
    if (-not $?) {
        throw 'Migration catalog verification failed.'
    }
}
finally {
    Pop-Location
}

$backendScript = Join-Path $repoRoot 'erp-backend\scripts\verify.ps1'
$backendArgs = @{}
if ($Full) {
    $backendArgs.Full = $true
}

Push-Location (Join-Path $repoRoot 'erp-backend')
try {
    & $backendScript @backendArgs
    if (-not $?) {
        throw 'Backend verification failed.'
    }
}
finally {
    Pop-Location
}

$frontendScript = Join-Path $repoRoot 'erp-frontend\scripts\verify.ps1'
$frontendArgs = @{}
if ($Install) {
    $frontendArgs.Install = $true
}

Push-Location (Join-Path $repoRoot 'erp-frontend')
try {
    & $frontendScript @frontendArgs
    if (-not $?) {
        throw 'Frontend verification failed.'
    }
}
finally {
    Pop-Location
}

Write-Host 'Source governance, backend and frontend verification passed.' -ForegroundColor Green
