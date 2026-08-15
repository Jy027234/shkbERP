[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [ValidatePattern('^v[0-9]{4}\.[0-9]{2}\.[0-9]{2}-rc\.[1-9][0-9]*$')]
    [string]$Version,
    [string]$EvidenceDirectory,
    [switch]$Push
)

$ErrorActionPreference = 'Stop'
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path

function Get-TreeHash {
    param([Parameter(Mandatory = $true)][string]$Directory)

    $resolvedDirectory = (Resolve-Path -LiteralPath $Directory).Path
    $prefix = $resolvedDirectory.TrimEnd('\', '/') + [System.IO.Path]::DirectorySeparatorChar
    $files = @(
        Get-ChildItem -LiteralPath $resolvedDirectory -Recurse -File |
            Sort-Object FullName |
            ForEach-Object {
                [pscustomobject][ordered]@{
                    path = $_.FullName.Substring($prefix.Length).Replace('\', '/')
                    sha256 = (Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash.ToLowerInvariant()
                    size = $_.Length
                }
            }
    )
    if ($files.Count -eq 0) {
        throw "Artifact directory has no files: $resolvedDirectory"
    }

    $lines = @($files | ForEach-Object { '{0}  {1}' -f $_.sha256, $_.path })
    $payload = [System.Text.UTF8Encoding]::new($false).GetBytes(($lines -join [Environment]::NewLine))
    $sha256 = [System.Security.Cryptography.SHA256]::Create()
    try {
        $treeHash = ([System.BitConverter]::ToString($sha256.ComputeHash($payload))).Replace('-', '').ToLowerInvariant()
    }
    finally {
        $sha256.Dispose()
    }

    return [pscustomobject][ordered]@{
        sha256 = $treeHash
        fileCount = $files.Count
        totalBytes = [long]($files | Measure-Object -Property size -Sum).Sum
    }
}

if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
    throw 'git is required to create a release candidate.'
}
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker is required to create a release candidate image.'
}

$branch = (& git -C $repoRoot branch --show-current).Trim()
if ($branch -ne 'main') {
    throw "Release candidate creation is restricted to main, found '$branch'."
}
$existingTag = (& git -C $repoRoot tag -l $Version).Trim()
if ($existingTag) {
    throw "Tag '$Version' already exists locally."
}

$verifyAll = Join-Path $repoRoot 'scripts\verify-all.ps1'
& $verifyAll -Full -Install -Candidate
if ($LASTEXITCODE -ne 0) {
    throw "Full candidate verification failed with exit code $LASTEXITCODE."
}

$restoreScript = Join-Path $repoRoot 'erp-backend\scripts\verify-release-restore.ps1'
$sourceCommit = (& git -C $repoRoot rev-parse HEAD).Trim()
$shortCommit = $sourceCommit.Substring(0, 12)
if ([string]::IsNullOrWhiteSpace($EvidenceDirectory)) {
    $EvidenceDirectory = Join-Path ([System.IO.Path]::GetTempPath()) 'shkb-release-candidates'
}
New-Item -ItemType Directory -Force -Path $EvidenceDirectory | Out-Null

$restoreEvidencePath = Join-Path $EvidenceDirectory "$Version-$shortCommit-restore.json"
& $restoreScript -EvidencePath $restoreEvidencePath -AsJson | Out-Null
if ($LASTEXITCODE -ne 0) {
    throw "Local restore rehearsal failed with exit code $LASTEXITCODE."
}
$restoreEvidence = Get-Content -LiteralPath $restoreEvidencePath -Raw -Encoding UTF8 | ConvertFrom-Json
if ($restoreEvidence.passed -ne $true) {
    throw 'Local restore rehearsal evidence is not successful.'
}

$jarPath = Join-Path $repoRoot 'erp-backend\xingyun-api\target\xingyun-api.jar'
$frontendDist = Join-Path $repoRoot 'erp-frontend\dist'
if (-not (Test-Path -LiteralPath $jarPath) -or -not (Test-Path -LiteralPath $frontendDist)) {
    throw 'Full verification did not produce both backend jar and frontend dist artifacts.'
}

$imageTag = "shkb-erp-api:$Version"
& docker build --tag $imageTag (Join-Path $repoRoot 'erp-backend\xingyun-api\target')
if ($LASTEXITCODE -ne 0) {
    throw "Candidate Docker image build failed with exit code $LASTEXITCODE."
}
$imageId = (& docker image inspect --format '{{.Id}}' $imageTag).Trim()
if ($imageId -notmatch '^sha256:[a-f0-9]{64}$') {
    throw "Cannot determine immutable image ID for '$imageTag'."
}

$catalogPath = Join-Path $repoRoot 'docs\governance\migration-catalog.json'
$catalog = Get-Content -LiteralPath $catalogPath -Raw -Encoding UTF8 | ConvertFrom-Json
$catalogHash = (Get-FileHash -LiteralPath $catalogPath -Algorithm SHA256).Hash.ToLowerInvariant()
$planPayload = [System.Text.UTF8Encoding]::new($false).GetBytes((
    @($catalog.existingDatabasePlan | ForEach-Object { [string]$_ }) -join [Environment]::NewLine
))
$planHasher = [System.Security.Cryptography.SHA256]::Create()
try {
    $planHash = ([System.BitConverter]::ToString($planHasher.ComputeHash($planPayload))).Replace('-', '').ToLowerInvariant()
}
finally {
    $planHasher.Dispose()
}

$jar = Get-Item -LiteralPath $jarPath
$frontend = Get-TreeHash -Directory $frontendDist
$manifestPath = Join-Path $EvidenceDirectory "$Version-$shortCommit.json"
$manifest = [ordered]@{
    schemaVersion = 1
    candidateVersion = $Version
    createdAt = (Get-Date).ToUniversalTime().ToString('o')
    source = [ordered]@{
        repository = 'git@github.com:Jy027234/shkbERP.git'
        branch = $branch
        commit = $sourceCommit
    }
    verification = [ordered]@{
        fullGate = 'scripts/verify-all.ps1 -Full -Install -Candidate'
        restoreEvidenceSha256 = (Get-FileHash -LiteralPath $restoreEvidencePath -Algorithm SHA256).Hash.ToLowerInvariant()
        restoreEvidencePath = $restoreEvidencePath
    }
    artifacts = [ordered]@{
        backendJar = [ordered]@{
            path = 'erp-backend/xingyun-api/target/xingyun-api.jar'
            sha256 = (Get-FileHash -LiteralPath $jarPath -Algorithm SHA256).Hash.ToLowerInvariant()
            size = $jar.Length
        }
        frontendDist = $frontend
        localDockerImage = [ordered]@{
            tag = $imageTag
            imageId = $imageId
        }
    }
    migrations = [ordered]@{
        catalogSha256 = $catalogHash
        existingDatabasePlanSha256 = $planHash
        plan = @($catalog.existingDatabasePlan | ForEach-Object { [string]$_ })
    }
    deployment = [ordered]@{
        productionAllowed = $false
        reason = 'A restored production-data copy, business confirmation for V1.21, and explicit deployment approval are still required.'
    }
}

$manifest | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath $manifestPath -Encoding UTF8
$manifestHash = (Get-FileHash -LiteralPath $manifestPath -Algorithm SHA256).Hash.ToLowerInvariant()

$tagMessage = @(
    "SHKB ERP release candidate $Version",
    '',
    "sourceCommit: $sourceCommit",
    "backendJarSha256: $($manifest.artifacts.backendJar.sha256)",
    "frontendDistTreeSha256: $($manifest.artifacts.frontendDist.sha256)",
    "migrationCatalogSha256: $catalogHash",
    "existingDatabasePlanSha256: $planHash",
    "localDockerImageId: $imageId",
    "restoreEvidenceSha256: $($manifest.verification.restoreEvidenceSha256)",
    "candidateManifestSha256: $manifestHash",
    'productionDeployment: LOCKED'
) -join [Environment]::NewLine

& git -C $repoRoot tag -a $Version -m $tagMessage $sourceCommit
if ($LASTEXITCODE -ne 0) {
    throw "Failed to create annotated tag '$Version'."
}
$tagCommit = (& git -C $repoRoot rev-parse "$Version^{commit}").Trim()
if ($tagCommit -ne $sourceCommit) {
    throw "Tag '$Version' does not point to the verified source commit."
}
if (@(& git -C $repoRoot status --porcelain).Count -ne 0) {
    throw 'Candidate creation unexpectedly changed the Git worktree.'
}

if ($Push) {
    & git -C $repoRoot push origin $Version
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to push candidate tag '$Version'."
    }
}

Write-Host "Release candidate $Version created for $sourceCommit." -ForegroundColor Green
Write-Host "Candidate manifest: $manifestPath"
if (-not $Push) {
    Write-Host "Tag is local only. Push it after review with: git push origin $Version"
}
