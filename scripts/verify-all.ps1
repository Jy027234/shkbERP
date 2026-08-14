[CmdletBinding()]
param(
    [switch]$Full,
    [switch]$Install,
    [switch]$Release
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot

if ($Release -and -not $Full) {
    throw 'Release verification requires -Full.'
}

$sourceArgs = @('-ExecutionPolicy', 'Bypass', '-File', '.\scripts\verify-source-baseline.ps1')
if ($Release) {
    $sourceArgs += '-Release'
}

Push-Location $repoRoot
try {
    & powershell @sourceArgs
    if ($LASTEXITCODE -ne 0) {
        throw "Source baseline verification failed with exit code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}

$backendArgs = @('-ExecutionPolicy', 'Bypass', '-File', '.\scripts\verify.ps1')
if ($Full) {
    $backendArgs += '-Full'
}

Push-Location (Join-Path $repoRoot 'erp-backend')
try {
    & powershell @backendArgs
    if ($LASTEXITCODE -ne 0) {
        throw "Backend verification failed with exit code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}

$frontendArgs = @('-ExecutionPolicy', 'Bypass', '-File', '.\scripts\verify.ps1')
if ($Install) {
    $frontendArgs += '-Install'
}

Push-Location (Join-Path $repoRoot 'erp-frontend')
try {
    & powershell @frontendArgs
    if ($LASTEXITCODE -ne 0) {
        throw "Frontend verification failed with exit code $LASTEXITCODE."
    }
}
finally {
    Pop-Location
}

Write-Host 'Source governance, backend and frontend verification passed.' -ForegroundColor Green
