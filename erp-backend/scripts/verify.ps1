[CmdletBinding()]
param(
    [string]$JavaHome,
    [switch]$Full
)

$ErrorActionPreference = 'Stop'
$requiredJavaMajor = 25
$repoRoot = (Resolve-Path -LiteralPath (Join-Path $PSScriptRoot '..')).Path
Set-Location -LiteralPath $repoRoot

function Get-JavaMajor {
    param([Parameter(Mandatory = $true)][string]$JavaExecutable)

    $versionText = (& $JavaExecutable --version 2>&1 | Out-String)
    if ($LASTEXITCODE -ne 0) {
        return $null
    }

    if ($versionText -match 'version\s+"(?:1\.)?(\d+)') {
        return [int]$Matches[1]
    }
    if ($versionText -match '(?:openjdk|java)\s+(\d+)(?:\.|\s)') {
        return [int]$Matches[1]
    }
    return $null
}

$candidateHomes = [System.Collections.Generic.List[string]]::new()
if ($JavaHome) { $candidateHomes.Add($JavaHome) }
if ($env:JAVA_HOME) { $candidateHomes.Add($env:JAVA_HOME) }

$searchRoots = @(
    (Join-Path $env:USERPROFILE '.jdks'),
    (Join-Path $env:ProgramFiles 'Java'),
    (Join-Path $env:ProgramFiles 'Eclipse Adoptium'),
    (Join-Path $env:ProgramFiles 'Microsoft')
)
foreach ($searchRoot in $searchRoots) {
    if (Test-Path -LiteralPath $searchRoot) {
        Get-ChildItem -LiteralPath $searchRoot -Directory -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -match '(?:jdk[-_]?|java[-_]?)?25(?:\.|-|$)' } |
            Sort-Object Name -Descending |
            ForEach-Object { $candidateHomes.Add($_.FullName) }
    }
}

$selectedJavaHome = $null
foreach ($candidate in ($candidateHomes | Select-Object -Unique)) {
    $javaExecutable = Join-Path $candidate 'bin\java.exe'
    if (-not (Test-Path -LiteralPath $javaExecutable)) { continue }
    if ((Get-JavaMajor -JavaExecutable $javaExecutable) -eq $requiredJavaMajor) {
        $selectedJavaHome = (Resolve-Path -LiteralPath $candidate).Path
        break
    }
}

if (-not $selectedJavaHome) {
    throw 'JDK 25 is required. Install it, set JAVA_HOME, or pass -JavaHome.'
}

$env:JAVA_HOME = $selectedJavaHome
$env:Path = "$(Join-Path $selectedJavaHome 'bin');$env:Path"

$maven = Get-Command mvn -ErrorAction SilentlyContinue
if (-not $maven) {
    throw 'Maven 3.9.x is required and mvn must be available on PATH.'
}

Write-Host "Using JAVA_HOME=$selectedJavaHome"
& java --version
& mvn --version
if ($Full) {
    & mvn -B verify
}
else {
    & mvn -B -DskipTests compile
}
if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

$dockerfiles = @(
    'xingyun-api\Dockerfile',
    'cloud\xingyun-cloud-api\Dockerfile',
    'cloud\xingyun-cloud-gateway\Dockerfile'
)
foreach ($dockerfile in $dockerfiles) {
    $content = Get-Content -Raw -LiteralPath $dockerfile
    if ($content -notmatch '^FROM eclipse-temurin:25-jre-ubi10-minimal') {
        throw "$dockerfile must use the Java 25 Eclipse Temurin runtime baseline."
    }
    if ($content -match '-jar\s+-server') {
        throw "$dockerfile places -server after -jar. JVM options must precede -jar."
    }
}

Write-Host 'Backend verification passed: Java 25 and all main reactor modules compiled.'
