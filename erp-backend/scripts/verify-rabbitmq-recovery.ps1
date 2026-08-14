[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$RabbitApiUrl = 'http://127.0.0.1:15672',
    [string]$RabbitUsername = 'admin',
    [string]$RabbitPassword = 'admin123'
)

$ErrorActionPreference = 'Stop'
$baseUri = [Uri]$BaseUrl
$rabbitUri = [Uri]$RabbitApiUrl
if ($baseUri.Host -notin @('127.0.0.1', 'localhost') -or
    $rabbitUri.Host -notin @('127.0.0.1', 'localhost')) {
    throw 'The RabbitMQ recovery probe is restricted to local application and broker endpoints.'
}
$probeId = 'v124-mq-probe'
$failedQueuePath = 'api/queues/%2F/shkb.failed'
$authBytes = [Text.Encoding]::ASCII.GetBytes("${RabbitUsername}:${RabbitPassword}")
$rabbitHeaders = @{ Authorization = 'Basic ' + [Convert]::ToBase64String($authBytes) }

function Invoke-RabbitApi {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [ValidateSet('Get', 'Post', 'Delete')][string]$Method = 'Get',
        [object]$Body
    )

    $arguments = @{
        Uri = ([Uri]::new($rabbitUri, $Path)).AbsoluteUri
        Method = $Method
        Headers = $rabbitHeaders
        TimeoutSec = 15
    }
    if ($null -ne $Body) {
        $arguments.ContentType = 'application/json'
        $arguments.Body = $Body | ConvertTo-Json -Compress -Depth 10
    }
    return Invoke-RestMethod @arguments
}

try {
    $health = Invoke-RestMethod -Uri ([Uri]::new($baseUri, '/readyz')).AbsoluteUri -TimeoutSec 10
    if ($health.status -ne 'UP') { throw 'Application readiness is not UP.' }

    Invoke-RabbitApi -Path $failedQueuePath | Out-Null
    Invoke-RabbitApi -Path "$failedQueuePath/contents" -Method Delete | Out-Null

    $messageJson = @{
        id = $probeId
        totalAmount = $null
        orderType = 'PURCHASE_ORDER'
    } | ConvertTo-Json -Compress -Depth 5
    $publishBody = @{
        properties = @{
            content_type = 'application/json'
            delivery_mode = 2
            headers = @{
                tenantId = 1000
                '__TypeId__' = 'com.lframework.xingyun.core.dto.order.ApprovePassOrderDto'
            }
        }
        routing_key = ''
        payload = $messageJson
        payload_encoding = 'string'
    }

    $timer = [Diagnostics.Stopwatch]::StartNew()
    $published = Invoke-RabbitApi -Path 'api/exchanges/%2F/approve_pass_order.fanout/publish' -Method Post -Body $publishBody
    if (-not $published.routed) { throw 'Probe message was not routed to chart.approve_pass_order.' }

    $failedMessages = @()
    for ($attempt = 0; $attempt -lt 20 -and $failedMessages.Count -eq 0; $attempt++) {
        Start-Sleep -Milliseconds 500
        $failedMessages = @(Invoke-RabbitApi -Path "$failedQueuePath/get" -Method Post -Body @{
            count = 1
            ackmode = 'ack_requeue_false'
            encoding = 'auto'
            truncate = 50000
        } | ForEach-Object { $_ })
    }
    $timer.Stop()

    if ($failedMessages.Count -ne 1) {
        throw 'Failed message did not reach shkb.failed after listener retries.'
    }
    $failed = $failedMessages | Select-Object -First 1
    $payloadText = [string]$failed.payload
    if ($failed.payload_encoding -eq 'base64') {
        $payloadText = [Text.Encoding]::UTF8.GetString([Convert]::FromBase64String($payloadText))
    }
    if ($payloadText -notlike "*$probeId*") {
        throw 'Recovered message payload does not match the injected probe.'
    }
    if ($failed.properties.headers.'x-original-exchange' -ne 'approve_pass_order.fanout') {
        throw 'Recovered message does not preserve the original exchange.'
    }
    if (-not $failed.properties.headers.'x-exception-message') {
        throw 'Recovered message does not include failure diagnostics.'
    }
    if ($timer.Elapsed.TotalSeconds -lt 2.5) {
        throw 'Message reached the failed queue before the configured retry backoff elapsed.'
    }

    Write-Host ("RabbitMQ recovery verification passed: poison message retried, confirmed and stored in shkb.failed after {0:N1}s." -f $timer.Elapsed.TotalSeconds)
}
finally {
    try { Invoke-RabbitApi -Path "$failedQueuePath/contents" -Method Delete | Out-Null } catch {}
}
