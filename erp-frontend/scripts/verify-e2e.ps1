[CmdletBinding()]
param()

$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
Set-Location -LiteralPath $repoRoot

& powershell -ExecutionPolicy Bypass -File (Join-Path $PSScriptRoot 'verify.ps1') -ToolchainOnly
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$backendUrl = 'http://127.0.0.1:8088/auth/tenant/require'
try {
    $response = Invoke-WebRequest -UseBasicParsing -Uri $backendUrl -TimeoutSec 15
    if ($response.StatusCode -ne 200) {
        throw "Unexpected HTTP status $($response.StatusCode)."
    }
}
catch {
    throw "ERP backend is not ready at $backendUrl. Start the local backend before running E2E tests. $($_.Exception.Message)"
}

& npx --yes 'pnpm@9.15.9' run test:e2e
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host 'Frontend E2E verification completed: login, dashboard APIs, authorized menu, and logout.'
