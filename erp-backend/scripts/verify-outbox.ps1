[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$Database = 'shkb_platform',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577'
)

$ErrorActionPreference = 'Stop'
$baseUri = [Uri]$BaseUrl
if ($baseUri.Host -notin @('127.0.0.1', 'localhost')) {
    throw 'The outbox probe is restricted to a local application endpoint.'
}

function Invoke-SmokeSql {
    param(
        [Parameter(Mandatory = $true)][string]$Sql,
        [switch]$ReturnOutput
    )

    $previousErrorActionPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Continue'
    try {
        $output = $Sql | & docker exec -i $DbContainer mysql "-u$DbUsername" "-p$DbPassword" -N -B $Database 2>$null
        $exitCode = $LASTEXITCODE
    } finally {
        $ErrorActionPreference = $previousErrorActionPreference
    }
    if ($exitCode -ne 0) {
        throw "Smoke database command failed: $($output -join [Environment]::NewLine)"
    }
    if ($ReturnOutput) { return @($output) }
}

function Wait-ForOutboxDelivery {
    param([Parameter(Mandatory = $true)][string]$EventId)

    for ($attempt = 0; $attempt -lt 30; $attempt++) {
        $row = @(Invoke-SmokeSql -Sql @"
SELECT CONCAT(o.status, '|',
  (SELECT COUNT(*) FROM sys_mq_inbox i WHERE i.event_id=o.id AND i.consumer_name='chart.approve_pass_order'), '|',
  (SELECT COUNT(*) FROM tbl_order_chart c WHERE c.total_amount=987654.32 AND c.create_time='2026-08-14 12:34:56'))
FROM sys_mq_outbox o WHERE o.id='$EventId';
"@ -ReturnOutput)
        if ($row.Count -eq 1 -and $row[0] -eq '2|1|1') {
            return
        }
        Start-Sleep -Milliseconds 500
    }
    throw "Outbox event $EventId was not confirmed, consumed and deduplicated in time."
}

$eventId = '12600000000000000000000000000001'
$cleanupSql = @"
DELETE FROM sys_mq_inbox WHERE event_id='$eventId';
DELETE FROM sys_mq_outbox WHERE id='$eventId';
DELETE FROM tbl_order_chart WHERE total_amount=987654.32 AND create_time='2026-08-14 12:34:56';
"@

try {
    $health = Invoke-RestMethod -Uri ([Uri]::new($baseUri, '/readyz')).AbsoluteUri -TimeoutSec 10
    if ($health.status -ne 'UP') { throw 'Application readiness is not UP.' }

    $tables = @(Invoke-SmokeSql -Sql "SHOW TABLES LIKE 'sys_mq_%';" -ReturnOutput)
    if ($tables.Count -ne 2) {
        throw 'V1.18__mq_outbox.sql has not been applied to the local smoke database.'
    }

    Invoke-SmokeSql -Sql $cleanupSql
    $payload = '{"eventId":"' + $eventId + '","id":"v126-order","totalAmount":987654.32,"approveTime":"2026-08-14 12:34:56","orderType":"PURCHASE_ORDER"}'
    Invoke-SmokeSql -Sql @"
INSERT INTO sys_mq_outbox
  (id,event_type,payload,tenant_id,status,attempts,next_attempt_time,create_time)
VALUES
  ('$eventId','APPROVE_PASS_ORDER','$payload',1000,0,0,NOW(6),NOW(6));
"@

    Wait-ForOutboxDelivery -EventId $eventId

    # Simulate the unavoidable crash window: broker confirmed, but SENT was not persisted.
    Invoke-SmokeSql -Sql @"
UPDATE sys_mq_outbox
SET status=0, attempts=0, next_attempt_time=NOW(6), locked_until=NULL, sent_time=NULL
WHERE id='$eventId';
"@
    Wait-ForOutboxDelivery -EventId $eventId

    $final = @(Invoke-SmokeSql -Sql @"
SELECT CONCAT(
  (SELECT COUNT(*) FROM sys_mq_inbox WHERE event_id='$eventId' AND consumer_name='chart.approve_pass_order'), '|',
  (SELECT COUNT(*) FROM tbl_order_chart WHERE total_amount=987654.32 AND create_time='2026-08-14 12:34:56'));
"@ -ReturnOutput)
    if ($final.Count -ne 1 -or $final[0] -ne '1|1') {
        throw "Duplicate relay changed business state: $($final -join ',')."
    }

    Write-Host 'Outbox verification passed: broker-confirmed delivery completed and duplicate relay changed business state exactly once.'
}
finally {
    try { Invoke-SmokeSql -Sql $cleanupSql } catch {}
}
