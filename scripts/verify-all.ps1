[CmdletBinding()]
param(
    [switch]$Full,
    [switch]$Install
)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path -Parent $PSScriptRoot

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

Write-Host 'Backend and frontend verification passed.' -ForegroundColor Green

