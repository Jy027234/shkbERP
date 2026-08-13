[CmdletBinding()]
param(
    [switch]$Install,
    [switch]$ToolchainOnly
)

$ErrorActionPreference = 'Stop'
$requiredNodeMajor = 24
$pnpmVersion = '9.15.9'
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
Set-Location -LiteralPath $repoRoot

$package = Get-Content -Raw -LiteralPath 'package.json' | ConvertFrom-Json
if ($package.packageManager -ne "pnpm@$pnpmVersion") {
    throw "package.json packageManager must be pnpm@$pnpmVersion."
}
if ($package.engines.node -ne '>=24 <25') {
    throw 'package.json must pin the Node.js 24.x engine range.'
}
if ((Get-Content -LiteralPath 'pnpm-lock.yaml' -TotalCount 1) -ne "lockfileVersion: '9.0'") {
    throw 'pnpm-lock.yaml must use lockfileVersion 9.0.'
}
if ((Test-Path -LiteralPath 'package-lock.json') -or (Test-Path -LiteralPath 'yarn.lock')) {
    throw 'Do not add npm or Yarn lockfiles; pnpm-lock.yaml is authoritative.'
}

$node = Get-Command node -ErrorAction SilentlyContinue
if (-not $node) {
    throw 'Node.js 24 LTS is required.'
}

$nodeVersion = (& node --version).Trim()
if ($LASTEXITCODE -ne 0 -or $nodeVersion -notmatch '^v(\d+)\.') {
    throw "Unable to parse Node.js version: $nodeVersion"
}
if ([int]$Matches[1] -ne $requiredNodeMajor) {
    throw "Node.js 24.x is required; current version is $nodeVersion. Follow .nvmrc or .node-version."
}

$npx = Get-Command npx -ErrorAction SilentlyContinue
if (-not $npx) {
    throw 'npx is missing. Install the complete Node.js 24 LTS distribution.'
}

Write-Host "Using Node.js $nodeVersion and pnpm $pnpmVersion"
& npx --yes "pnpm@$pnpmVersion" --version
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
if ($ToolchainOnly) {
    Write-Host 'Frontend toolchain verification passed.'
    exit 0
}

if ($Install) {
    & npx --yes "pnpm@$pnpmVersion" install --frozen-lockfile
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}
elseif (-not (Test-Path -LiteralPath 'node_modules')) {
    throw 'node_modules is missing. Run this script with -Install first.'
}

& npx --yes "pnpm@$pnpmVersion" run type:check
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

& npx --yes "pnpm@$pnpmVersion" test
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

& npx --yes "pnpm@$pnpmVersion" run build
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

Write-Host 'Frontend baseline verification completed: pinned toolchain, type check, tests, and production build.'
Write-Warning 'Review all Vite warnings. Type errors, test failures, and missing-export warnings are regressions.'
