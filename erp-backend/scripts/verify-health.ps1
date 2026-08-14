[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088'
)

$ErrorActionPreference = 'Stop'
$baseUri = $BaseUrl.TrimEnd('/')

function Assert-HealthEndpoint {
    param(
        [Parameter(Mandatory = $true)][string]$Path
    )

    $response = Invoke-WebRequest -UseBasicParsing -Uri "$baseUri$Path" -TimeoutSec 15
    if ($response.StatusCode -ne 200) {
        throw "$Path returned HTTP $($response.StatusCode)."
    }
    $content = if ($response.Content -is [byte[]]) {
        [System.Text.Encoding]::UTF8.GetString($response.Content)
    } else {
        [string]$response.Content
    }
    $payload = $content | ConvertFrom-Json
    if ($payload.status -ne 'UP') {
        throw "$Path is not UP: $content"
    }
    $propertyNames = @($payload.PSObject.Properties.Name)
    $unexpectedProperties = @($propertyNames | Where-Object { $_ -notin @('status', 'groups') })
    if ($unexpectedProperties.Count -gt 0) {
        throw "$Path exposed unexpected health details: $content"
    }
    if ($payload.groups) {
        $groups = @($payload.groups | Sort-Object)
        if (($groups -join ',') -ne 'liveness,readiness') {
            throw "$Path exposed unexpected health groups: $content"
        }
    }
    Write-Host "$Path -> UP"
}

Assert-HealthEndpoint -Path '/actuator/health'
Assert-HealthEndpoint -Path '/actuator/health/liveness'
Assert-HealthEndpoint -Path '/actuator/health/readiness'
Assert-HealthEndpoint -Path '/livez'
Assert-HealthEndpoint -Path '/readyz'

Write-Host 'Health, liveness, and dependency-aware readiness probes passed without authentication or component details.' -ForegroundColor Green
