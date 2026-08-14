[CmdletBinding()]
param(
    [string]$BaseUrl = 'http://127.0.0.1:8088',
    [string]$TenantName,
    [string]$Username = 'admin',
    [string]$Password = 'admin',
    [string]$DbContainer = 'xingyun-smoke-mysql',
    [string]$Database = 'shkb_platform',
    [string]$DbUsername = 'root',
    [string]$DbPassword = '335577',
    [ValidateRange(1, 20)][int]$Iterations = 5
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Net.Http
$baseUri = $BaseUrl.TrimEnd('/')
$parsedBaseUri = [Uri]$baseUri
if ($parsedBaseUri.Host -notin @('127.0.0.1', 'localhost')) {
    throw 'The concurrency probe is restricted to a local API endpoint.'
}
if (-not $TenantName) {
    # Keep the source ASCII-only for Windows PowerShell 5 UTF-8 compatibility.
    $TenantName = -join [char[]](0x4E0A, 0x6D77, 0x51EF, 0x5954, 0x822A, 0x7A7A, 0x6280, 0x672F, 0x6709, 0x9650, 0x516C, 0x53F8)
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

function Invoke-ErpJson {
    param(
        [Parameter(Mandatory = $true)][string]$Uri,
        [ValidateSet('Get', 'Post', 'Patch')][string]$Method = 'Get',
        [hashtable]$Headers,
        [string]$JsonBody,
        [hashtable]$FormBody
    )

    $arguments = @{
        Uri = $Uri
        Method = $Method
        TimeoutSec = 30
    }
    if ($Headers) { $arguments.Headers = $Headers }
    if ($JsonBody) {
        $arguments.Body = $JsonBody
        $arguments.ContentType = 'application/json'
    } elseif ($FormBody) {
        $arguments.Body = $FormBody
        $arguments.ContentType = 'application/x-www-form-urlencoded'
    }

    return Invoke-RestMethod @arguments
}

function Invoke-ConcurrentApproval {
    param(
        [Parameter(Mandatory = $true)][string[]]$SheetIds,
        [Parameter(Mandatory = $true)][string]$Token
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $client.DefaultRequestHeaders.Add('X-Auth-Token', $Token)
    $requests = New-Object System.Collections.Generic.List[System.Net.Http.HttpRequestMessage]
    $tasks = New-Object System.Collections.Generic.List[System.Threading.Tasks.Task[System.Net.Http.HttpResponseMessage]]
    try {
        foreach ($sheetId in $SheetIds) {
            $request = [System.Net.Http.HttpRequestMessage]::new(
                [System.Net.Http.HttpMethod]::new('PATCH'),
                "$baseUri/material/out/sheet/approve/pass"
            )
            $body = @{ id = $sheetId; description = 'V1.18 concurrency smoke' } | ConvertTo-Json -Compress
            $request.Content = [System.Net.Http.StringContent]::new(
                $body,
                [System.Text.Encoding]::UTF8,
                'application/json'
            )
            $requests.Add($request)
            $tasks.Add($client.SendAsync($request))
        }

        [System.Threading.Tasks.Task]::WaitAll([System.Threading.Tasks.Task[]]$tasks.ToArray())
        $results = @()
        foreach ($task in $tasks) {
            $response = $task.GetAwaiter().GetResult()
            $content = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
            $results += $content | ConvertFrom-Json
        }
        return @($results)
    } finally {
        foreach ($request in $requests) { $request.Dispose() }
        $client.Dispose()
    }
}

function Invoke-ConcurrentSheetAction {
    param(
        [Parameter(Mandatory = $true)][string]$SheetId,
        [Parameter(Mandatory = $true)][string]$Token,
        [Parameter(Mandatory = $true)][ValidateSet('MarkPickable', 'Delete')][string]$SecondAction
    )

    $client = [System.Net.Http.HttpClient]::new()
    $client.Timeout = [TimeSpan]::FromSeconds(30)
    $client.DefaultRequestHeaders.Add('X-Auth-Token', $Token)
    $requests = New-Object System.Collections.Generic.List[System.Net.Http.HttpRequestMessage]
    $tasks = New-Object System.Collections.Generic.List[System.Threading.Tasks.Task[System.Net.Http.HttpResponseMessage]]
    try {
        $approveRequest = [System.Net.Http.HttpRequestMessage]::new(
            [System.Net.Http.HttpMethod]::new('PATCH'),
            "$baseUri/material/out/sheet/approve/pass"
        )
        $approveBody = @{ id = $SheetId; description = 'V1.18 state race smoke' } | ConvertTo-Json -Compress
        $approveRequest.Content = [System.Net.Http.StringContent]::new(
            $approveBody,
            [System.Text.Encoding]::UTF8,
            'application/json'
        )
        $requests.Add($approveRequest)

        if ($SecondAction -eq 'MarkPickable') {
            $secondRequest = [System.Net.Http.HttpRequestMessage]::new(
                [System.Net.Http.HttpMethod]::new('PATCH'),
                "$baseUri/material/out/sheet/mark/pickable"
            )
            $secondBody = @{ id = $SheetId; refuseReason = 'V1.18 state race smoke' } | ConvertTo-Json -Compress
            $secondRequest.Content = [System.Net.Http.StringContent]::new(
                $secondBody,
                [System.Text.Encoding]::UTF8,
                'application/json'
            )
        } else {
            $escapedId = [Uri]::EscapeDataString($SheetId)
            $secondRequest = [System.Net.Http.HttpRequestMessage]::new(
                [System.Net.Http.HttpMethod]::Delete,
                "$baseUri/material/out/sheet?id=$escapedId"
            )
        }
        $requests.Add($secondRequest)

        foreach ($request in $requests) {
            $tasks.Add($client.SendAsync($request))
        }
        [System.Threading.Tasks.Task]::WaitAll([System.Threading.Tasks.Task[]]$tasks.ToArray())

        $results = @()
        for ($index = 0; $index -lt $tasks.Count; $index++) {
            $response = $tasks[$index].GetAwaiter().GetResult()
            $content = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult() | ConvertFrom-Json
            $results += [pscustomobject]@{
                Action = if ($index -eq 0) { 'Approve' } else { $SecondAction }
                Code = $content.code
                Message = $content.msg
            }
        }
        return @($results)
    } finally {
        foreach ($request in $requests) { $request.Dispose() }
        $client.Dispose()
    }
}

function Test-ApprovalRejected {
    param(
        [Parameter(Mandatory = $true)][string]$SheetId,
        [Parameter(Mandatory = $true)][string]$Token,
        [Parameter(Mandatory = $true)][string]$Description
    )

    try {
        $response = Invoke-ErpJson -Uri "$baseUri/material/out/sheet/approve/pass" -Method Patch `
            -Headers @{ 'X-Auth-Token' = $Token } `
            -JsonBody (@{ id = $SheetId; description = $Description } | ConvertTo-Json -Compress)
        return $response.code -ne 200
    } catch {
        # The global exception handler returns business failures as HTTP 500 in this runtime.
        if ($_.Exception.Response -and [int]$_.Exception.Response.StatusCode -ge 400) {
            return $true
        }
        throw
    }
}

$prefix = 'v118-'
$cleanupSql = @"
DELETE FROM op_logs WHERE extra LIKE '%v118-%' OR extra LIKE '%V1.18 concurrency smoke%';
DELETE FROM tbl_order_time_line WHERE order_id IN ('$($prefix)sheet-a','$($prefix)sheet-b','$($prefix)sheet-c','$($prefix)sheet-d','$($prefix)sheet-e','$($prefix)sheet-f','$($prefix)sheet-g','$($prefix)sheet-h','$($prefix)sheet-i');
DELETE FROM tbl_product_stock_log WHERE biz_id IN ('$($prefix)sheet-a','$($prefix)sheet-b','$($prefix)sheet-c','$($prefix)sheet-d','$($prefix)sheet-e','$($prefix)sheet-f','$($prefix)sheet-g','$($prefix)sheet-h','$($prefix)sheet-i');
DELETE FROM tbl_material_out_sheet_detail_serial WHERE sheet_id IN ('$($prefix)sheet-a','$($prefix)sheet-b','$($prefix)sheet-c','$($prefix)sheet-d','$($prefix)sheet-e','$($prefix)sheet-f','$($prefix)sheet-g','$($prefix)sheet-h','$($prefix)sheet-i');
DELETE FROM tbl_material_out_sheet_detail WHERE sheet_id IN ('$($prefix)sheet-a','$($prefix)sheet-b','$($prefix)sheet-c','$($prefix)sheet-d','$($prefix)sheet-e','$($prefix)sheet-f','$($prefix)sheet-g','$($prefix)sheet-h','$($prefix)sheet-i');
DELETE FROM tbl_material_out_sheet WHERE id IN ('$($prefix)sheet-a','$($prefix)sheet-b','$($prefix)sheet-c','$($prefix)sheet-d','$($prefix)sheet-e','$($prefix)sheet-f','$($prefix)sheet-g','$($prefix)sheet-h','$($prefix)sheet-i');
DELETE FROM shkb_material_order_detail WHERE order_id='$($prefix)order';
DELETE FROM shkb_material_order WHERE id='$($prefix)order';
DELETE FROM tbl_product_stock_serial WHERE product_id IN ('$($prefix)product-a','$($prefix)product-b','$($prefix)product-serial');
DELETE FROM tbl_product_stock_batch WHERE product_id IN ('$($prefix)product-a','$($prefix)product-b','$($prefix)product-serial');
DELETE FROM tbl_product_stock WHERE product_id IN ('$($prefix)product-a','$($prefix)product-b','$($prefix)product-serial');
DELETE FROM base_data_product WHERE id IN ('$($prefix)product-a','$($prefix)product-b','$($prefix)product-serial');
DELETE FROM base_data_store_center WHERE id IN ('$($prefix)sc','$($prefix)sc-other');
"@

$resetSql = @"
DELETE FROM op_logs WHERE extra LIKE '%v118-%' OR extra LIKE '%V1.18 concurrency smoke%';
DELETE FROM tbl_order_time_line WHERE order_id IN ('$($prefix)sheet-a','$($prefix)sheet-b');
DELETE FROM tbl_product_stock_log WHERE biz_id IN ('$($prefix)sheet-a','$($prefix)sheet-b');
UPDATE tbl_material_out_sheet SET status=0, approve_by=NULL, approve_time=NULL, description='V1.18 concurrency smoke', update_time=NOW() WHERE id IN ('$($prefix)sheet-a','$($prefix)sheet-b');
UPDATE shkb_material_order_detail SET out_num=0 WHERE order_id='$($prefix)order';
UPDATE shkb_material_order SET total_out_num=0, is_out_finish=0 WHERE id='$($prefix)order';
UPDATE tbl_product_stock SET stock_num=10, tax_price=5.000000, tax_amount=50.00 WHERE product_id IN ('$($prefix)product-a','$($prefix)product-b') AND sc_id='$($prefix)sc';
UPDATE tbl_product_stock_batch SET quantity=10 WHERE product_id IN ('$($prefix)product-a','$($prefix)product-b') AND sc_id='$($prefix)sc';
"@

try {
    Invoke-SmokeSql -Sql $cleanupSql
    Invoke-SmokeSql -Sql @"
INSERT INTO base_data_store_center (id,code,name,available,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$($prefix)sc','V118-SC','V118 warehouse',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$($prefix)sc-other','V118-SC-OTHER','V118 other warehouse',1,'smoke','smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO base_data_product (id,code,name,short_name,sku_code,external_code,category_id,brand_id,part_number_id,machine_type_id,product_type,tax_rate,sale_tax_rate,spec,unit,weight,volume,available,is_batch,is_serial,create_by,create_by_id,create_time,update_by,update_by_id,update_time) VALUES
('$($prefix)product-a','V118-PA','V1.18 material A',NULL,'V118-SKU-A',NULL,'1','1',NULL,NULL,1,13,13,'V118-A','EA',NULL,NULL,1,1,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$($prefix)product-b','V118-PB','V1.18 material B',NULL,'V118-SKU-B',NULL,'1','1',NULL,NULL,1,13,13,'V118-B','EA',NULL,NULL,1,1,0,'smoke','smoke',NOW(),'smoke','smoke',NOW()),
('$($prefix)product-serial','V118-PS','V1.18 serial material',NULL,'V118-SKU-S',NULL,'1','1',NULL,NULL,1,13,13,'V118-S','EA',NULL,NULL,1,1,1,'smoke','smoke',NOW(),'smoke','smoke',NOW());
INSERT INTO tbl_product_stock (id,sc_id,product_id,stock_num,tax_price,tax_amount) VALUES
('$($prefix)stock-a','$($prefix)sc','$($prefix)product-a',10,5.000000,50.00),
('$($prefix)stock-b','$($prefix)sc','$($prefix)product-b',10,5.000000,50.00),
('$($prefix)stock-serial','$($prefix)sc','$($prefix)product-serial',10,5.000000,50.00);
INSERT INTO tbl_product_stock_batch (id,sc_id,product_id,quantity,batch_number,create_time) VALUES
('$($prefix)batch-a','$($prefix)sc','$($prefix)product-a',10,'V118-BATCH-A',NOW()),
('$($prefix)batch-b','$($prefix)sc','$($prefix)product-b',10,'V118-BATCH-B',NOW()),
('$($prefix)batch-serial','$($prefix)sc','$($prefix)product-serial',10,'V118-BATCH-S',NOW()),
('$($prefix)batch-serial-other','$($prefix)sc-other','$($prefix)product-serial',1,'V118-BATCH-S-OTHER',NOW());
INSERT INTO tbl_product_stock_serial (id,product_id,serial_number,stock_status,batch_id,create_time) VALUES
('$($prefix)serial','$($prefix)product-serial','V118-SERIAL',1,'$($prefix)batch-serial',NOW()),
('$($prefix)serial-other','$($prefix)product-serial','V118-SERIAL-OTHER',1,'$($prefix)batch-serial-other',NOW());
INSERT INTO shkb_material_order (id,code,sc_id,total_num,total_out_num,total_amount,description,create_by,create_by_id,create_time,material_apply_id,is_out_finish) VALUES
('$($prefix)order','V118-ORDER','$($prefix)sc',2,0,10.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),NULL,0);
INSERT INTO shkb_material_order_detail (id,order_id,product_id,tax_price,tax_amount,description,out_num,order_num) VALUES
('$($prefix)order-detail-a','$($prefix)order','$($prefix)product-a',5.000000,5.00,'A',0,1),
('$($prefix)order-detail-b','$($prefix)order','$($prefix)product-b',5.000000,5.00,'B',0,1);
INSERT INTO tbl_material_out_sheet (id,code,sc_id,material_order_id,total_num,total_amount,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time,status) VALUES
('$($prefix)sheet-a','V118-SHEET-A','$($prefix)sc','$($prefix)order',1,5.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0),
('$($prefix)sheet-b','V118-SHEET-B','$($prefix)sc','$($prefix)order',1,5.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0),
('$($prefix)sheet-c','V118-SHEET-C','$($prefix)sc',NULL,6,30.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0),
('$($prefix)sheet-d','V118-SHEET-D','$($prefix)sc',NULL,6,30.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0),
('$($prefix)sheet-e','V118-SHEET-E','$($prefix)sc',NULL,1,5.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0),
('$($prefix)sheet-f','V118-SHEET-F','$($prefix)sc',NULL,1,5.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0),
('$($prefix)sheet-g','V118-SHEET-G','$($prefix)sc',NULL,1,5.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0),
('$($prefix)sheet-h','V118-SHEET-H','$($prefix)sc',NULL,1,5.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0),
('$($prefix)sheet-i','V118-SHEET-I','$($prefix)sc',NULL,1,5.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0);
INSERT INTO tbl_material_out_sheet_detail (id,sheet_id,product_id,order_num,tax_price,description,order_no,out_num,stock_batch_id,tax_amount,material_order_detail_id) VALUES
('$($prefix)sheet-detail-a','$($prefix)sheet-a','$($prefix)product-a',1,5.000000,'A',1,1,'$($prefix)batch-a',5.00,'$($prefix)order-detail-a'),
('$($prefix)sheet-detail-b','$($prefix)sheet-b','$($prefix)product-b',1,5.000000,'B',1,1,'$($prefix)batch-b',5.00,'$($prefix)order-detail-b'),
('$($prefix)sheet-detail-c','$($prefix)sheet-c','$($prefix)product-a',6,5.000000,'batch race C',1,6,'$($prefix)batch-a',30.00,NULL),
('$($prefix)sheet-detail-d','$($prefix)sheet-d','$($prefix)product-a',6,5.000000,'batch race D',1,6,'$($prefix)batch-a',30.00,NULL),
('$($prefix)sheet-detail-e','$($prefix)sheet-e','$($prefix)product-serial',1,5.000000,'serial race E',1,1,'$($prefix)batch-serial',5.00,NULL),
('$($prefix)sheet-detail-f','$($prefix)sheet-f','$($prefix)product-serial',1,5.000000,'serial race F',1,1,'$($prefix)batch-serial',5.00,NULL),
('$($prefix)sheet-detail-g','$($prefix)sheet-g','$($prefix)product-serial',1,5.000000,'cross warehouse serial',1,1,'$($prefix)batch-serial',5.00,NULL),
('$($prefix)sheet-detail-h','$($prefix)sheet-h','$($prefix)product-b',1,5.000000,'mark race',1,1,'$($prefix)batch-b',5.00,NULL),
('$($prefix)sheet-detail-i','$($prefix)sheet-i','$($prefix)product-b',1,5.000000,'delete race',1,1,'$($prefix)batch-b',5.00,NULL);
INSERT INTO tbl_material_out_sheet_detail_serial (id,sheet_id,product_id,stock_serial_id) VALUES
('$($prefix)sheet-serial-e','$($prefix)sheet-e','$($prefix)product-serial','$($prefix)serial'),
('$($prefix)sheet-serial-f','$($prefix)sheet-f','$($prefix)product-serial','$($prefix)serial'),
('$($prefix)sheet-serial-g','$($prefix)sheet-g','$($prefix)product-serial','$($prefix)serial-other');
"@

    $login = Invoke-ErpJson -Uri "$baseUri/auth/login" -Method Post -FormBody @{
        tenantName = $TenantName
        username = $Username
        password = $Password
    }
    if ($login.code -ne 200 -or -not $login.data.token) {
        throw "Login failed: $($login.msg)"
    }
    $token = [string]$login.data.token
    $headers = @{ 'X-Auth-Token' = $token }

    for ($iteration = 1; $iteration -le $Iterations; $iteration++) {
        Invoke-SmokeSql -Sql $resetSql
        $results = Invoke-ConcurrentApproval -SheetIds @("$($prefix)sheet-a", "$($prefix)sheet-b") -Token $token
        if (@($results | Where-Object { $_.code -eq 200 }).Count -ne 2) {
            throw "Iteration $iteration did not approve both independent sheets: $($results | ConvertTo-Json -Compress -Depth 5)"
        }

        $state = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT CONCAT(total_out_num,':',is_out_finish) FROM shkb_material_order WHERE id='$($prefix)order';
SELECT GROUP_CONCAT(CONCAT(product_id,':',out_num) ORDER BY product_id SEPARATOR ',') FROM shkb_material_order_detail WHERE order_id='$($prefix)order';
SELECT GROUP_CONCAT(CONCAT(product_id,':',stock_num) ORDER BY product_id SEPARATOR ',') FROM tbl_product_stock WHERE sc_id='$($prefix)sc' AND product_id IN ('$($prefix)product-a','$($prefix)product-b');
SELECT GROUP_CONCAT(CONCAT(product_id,':',quantity) ORDER BY product_id SEPARATOR ',') FROM tbl_product_stock_batch WHERE sc_id='$($prefix)sc' AND product_id IN ('$($prefix)product-a','$($prefix)product-b');
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id IN ('$($prefix)sheet-a','$($prefix)sheet-b');
"@
        $expectedDetails = "$($prefix)product-a:1,$($prefix)product-b:1"
        $expectedStock = "$($prefix)product-a:9,$($prefix)product-b:9"
        if ($state.Count -ne 5 -or $state[0] -ne '2:1' -or $state[1] -ne $expectedDetails -or
                $state[2] -ne $expectedStock -or $state[3] -ne $expectedStock -or [int]$state[4] -ne 2) {
            throw "Iteration $iteration exposed a lost update: $($state -join ' | ')"
        }
    }

    Invoke-SmokeSql -Sql $resetSql
    $duplicateResults = Invoke-ConcurrentApproval -SheetIds @("$($prefix)sheet-a", "$($prefix)sheet-a") -Token $token
    if (@($duplicateResults | Where-Object { $_.code -eq 200 }).Count -ne 1) {
        throw "Concurrent duplicate approval must succeed exactly once: $($duplicateResults | ConvertTo-Json -Compress -Depth 5)"
    }
    $duplicateState = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT CONCAT(status,':',total_num) FROM tbl_material_out_sheet WHERE id='$($prefix)sheet-a';
SELECT CONCAT(total_out_num,':',is_out_finish) FROM shkb_material_order WHERE id='$($prefix)order';
SELECT CONCAT(stock_num,':',tax_amount) FROM tbl_product_stock WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-a';
SELECT quantity FROM tbl_product_stock_batch WHERE id='$($prefix)batch-a';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id='$($prefix)sheet-a';
"@
    if ($duplicateState.Count -ne 5 -or $duplicateState[0] -ne '1:1' -or $duplicateState[1] -ne '1:0' -or
            $duplicateState[2] -ne '9:45.00' -or $duplicateState[3] -ne '9' -or [int]$duplicateState[4] -ne 1) {
        throw "Concurrent duplicate approval changed inventory more than once: $($duplicateState -join ' | ')"
    }

    Invoke-SmokeSql -Sql $resetSql
    Invoke-SmokeSql -Sql "UPDATE tbl_product_stock SET stock_num=0, tax_amount=0 WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-a'; UPDATE tbl_product_stock_batch SET quantity=5 WHERE id='$($prefix)batch-a';"
    $rollbackRejected = Test-ApprovalRejected -SheetId "$($prefix)sheet-a" -Token $token `
        -Description 'V1.18 rollback smoke'
    if (-not $rollbackRejected) {
        throw 'The rollback probe unexpectedly approved an out-of-stock sheet.'
    }
    $rollbackState = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_material_out_sheet WHERE id='$($prefix)sheet-a';
SELECT out_num FROM shkb_material_order_detail WHERE id='$($prefix)order-detail-a';
SELECT quantity FROM tbl_product_stock_batch WHERE id='$($prefix)batch-a';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id='$($prefix)sheet-a';
"@
    if ($rollbackState.Count -ne 4 -or $rollbackState[0] -ne '0' -or $rollbackState[1] -ne '0' -or
            $rollbackState[2] -ne '5' -or [int]$rollbackState[3] -ne 0) {
        throw "Inventory failure did not roll back the entire approval: $($rollbackState -join ' | ')"
    }

    # Two independent sheets compete for six units each from one ten-unit batch.
    # Exactly one may commit; the rejected transaction must not leave a partial batch/log mutation.
    Invoke-SmokeSql -Sql @"
DELETE FROM tbl_product_stock_log WHERE biz_id IN ('$($prefix)sheet-c','$($prefix)sheet-d');
UPDATE tbl_material_out_sheet SET status=0, approve_by=NULL, approve_time=NULL, update_time=NOW() WHERE id IN ('$($prefix)sheet-c','$($prefix)sheet-d');
UPDATE tbl_product_stock SET stock_num=10, tax_price=5.000000, tax_amount=50.00 WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-a';
UPDATE tbl_product_stock_batch SET quantity=10 WHERE id='$($prefix)batch-a';
"@
    $batchRaceResults = Invoke-ConcurrentApproval -SheetIds @("$($prefix)sheet-c", "$($prefix)sheet-d") -Token $token
    if (@($batchRaceResults | Where-Object { $_.code -eq 200 }).Count -ne 1) {
        throw "Oversubscribed batch race must commit exactly one sheet: $($batchRaceResults | ConvertTo-Json -Compress -Depth 5)"
    }
    $batchRaceState = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT CONCAT(SUM(status=1),':',SUM(status=0)) FROM tbl_material_out_sheet WHERE id IN ('$($prefix)sheet-c','$($prefix)sheet-d');
SELECT CONCAT(stock_num,':',tax_amount) FROM tbl_product_stock WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-a';
SELECT quantity FROM tbl_product_stock_batch WHERE id='$($prefix)batch-a';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id IN ('$($prefix)sheet-c','$($prefix)sheet-d');
"@
    if ($batchRaceState.Count -ne 4 -or $batchRaceState[0] -ne '1:1' -or
            $batchRaceState[1] -ne '4:20.00' -or $batchRaceState[2] -ne '4' -or
            [int]$batchRaceState[3] -ne 1) {
        throw "Oversubscribed batch race left inconsistent inventory: $($batchRaceState -join ' | ')"
    }

    # The same serial number is intentionally referenced by two independent sheets.
    # The conditional serial transition must allow one commit and roll back the other transaction.
    Invoke-SmokeSql -Sql @"
DELETE FROM tbl_product_stock_log WHERE biz_id IN ('$($prefix)sheet-e','$($prefix)sheet-f');
UPDATE tbl_material_out_sheet SET status=0, approve_by=NULL, approve_time=NULL, update_time=NOW() WHERE id IN ('$($prefix)sheet-e','$($prefix)sheet-f');
UPDATE tbl_product_stock SET stock_num=10, tax_price=5.000000, tax_amount=50.00 WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-serial';
UPDATE tbl_product_stock_batch SET quantity=10 WHERE id='$($prefix)batch-serial';
UPDATE tbl_product_stock_serial SET stock_status=1 WHERE id='$($prefix)serial';
"@
    $serialRaceResults = Invoke-ConcurrentApproval -SheetIds @("$($prefix)sheet-e", "$($prefix)sheet-f") -Token $token
    if (@($serialRaceResults | Where-Object { $_.code -eq 200 }).Count -ne 1) {
        throw "Duplicate serial race must commit exactly one sheet: $($serialRaceResults | ConvertTo-Json -Compress -Depth 5)"
    }
    $serialRaceState = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT CONCAT(SUM(status=1),':',SUM(status=0)) FROM tbl_material_out_sheet WHERE id IN ('$($prefix)sheet-e','$($prefix)sheet-f');
SELECT CONCAT(stock_num,':',tax_amount) FROM tbl_product_stock WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-serial';
SELECT quantity FROM tbl_product_stock_batch WHERE id='$($prefix)batch-serial';
SELECT stock_status FROM tbl_product_stock_serial WHERE id='$($prefix)serial';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id IN ('$($prefix)sheet-e','$($prefix)sheet-f');
"@
    if ($serialRaceState.Count -ne 5 -or $serialRaceState[0] -ne '1:1' -or
            $serialRaceState[1] -ne '9:45.00' -or $serialRaceState[2] -ne '9' -or
            $serialRaceState[3] -ne '0' -or [int]$serialRaceState[4] -ne 1) {
        throw "Duplicate serial race left inconsistent inventory: $($serialRaceState -join ' | ')"
    }

    # A serial physically held by another warehouse must be rejected before any stock mutation.
    Invoke-SmokeSql -Sql @"
DELETE FROM tbl_product_stock_log WHERE biz_id='$($prefix)sheet-g';
UPDATE tbl_material_out_sheet SET status=0, approve_by=NULL, approve_time=NULL, update_time=NOW() WHERE id='$($prefix)sheet-g';
UPDATE tbl_product_stock SET stock_num=10, tax_price=5.000000, tax_amount=50.00 WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-serial';
UPDATE tbl_product_stock_batch SET quantity=10 WHERE id='$($prefix)batch-serial';
UPDATE tbl_product_stock_serial SET stock_status=1 WHERE id='$($prefix)serial-other';
"@
    $crossWarehouseRejected = Test-ApprovalRejected -SheetId "$($prefix)sheet-g" -Token $token `
        -Description 'V1.18 cross warehouse serial smoke'
    if (-not $crossWarehouseRejected) {
        throw 'A serial number from another warehouse was unexpectedly approved.'
    }
    $crossWarehouseState = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_material_out_sheet WHERE id='$($prefix)sheet-g';
SELECT CONCAT(stock_num,':',tax_amount) FROM tbl_product_stock WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-serial';
SELECT quantity FROM tbl_product_stock_batch WHERE id='$($prefix)batch-serial';
SELECT stock_status FROM tbl_product_stock_serial WHERE id='$($prefix)serial-other';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id='$($prefix)sheet-g';
"@
    if ($crossWarehouseState.Count -ne 5 -or $crossWarehouseState[0] -ne '0' -or
            $crossWarehouseState[1] -ne '10:50.00' -or $crossWarehouseState[2] -ne '10' -or
            $crossWarehouseState[3] -ne '1' -or [int]$crossWarehouseState[4] -ne 0) {
        throw "Cross-warehouse serial rejection mutated inventory: $($crossWarehouseState -join ' | ')"
    }

    # Mark-pickable may win first, but approval accepts that state and must always be the final transition.
    for ($iteration = 1; $iteration -le $Iterations; $iteration++) {
        Invoke-SmokeSql -Sql @"
DELETE FROM tbl_product_stock_log WHERE biz_id='$($prefix)sheet-h';
UPDATE tbl_material_out_sheet SET status=0, approve_by=NULL, approve_time=NULL, refuse_reason=NULL, update_time=NOW() WHERE id='$($prefix)sheet-h';
UPDATE tbl_product_stock SET stock_num=10, tax_price=5.000000, tax_amount=50.00 WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-b';
UPDATE tbl_product_stock_batch SET quantity=10 WHERE id='$($prefix)batch-b';
"@
        $markRace = @(Invoke-ConcurrentSheetAction -SheetId "$($prefix)sheet-h" -Token $token -SecondAction MarkPickable)
        if ($markRace.Count -ne 2 -or $markRace[0].Code -ne 200) {
            throw "Iteration $iteration approval/mark race did not approve the sheet: $($markRace | ConvertTo-Json -Compress -Depth 5)"
        }
        $markRaceState = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT status FROM tbl_material_out_sheet WHERE id='$($prefix)sheet-h';
SELECT CONCAT(stock_num,':',tax_amount) FROM tbl_product_stock WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-b';
SELECT quantity FROM tbl_product_stock_batch WHERE id='$($prefix)batch-b';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id='$($prefix)sheet-h';
"@
        if ($markRaceState.Count -ne 4 -or $markRaceState[0] -ne '1' -or
                $markRaceState[1] -ne '9:45.00' -or $markRaceState[2] -ne '9' -or
                [int]$markRaceState[3] -ne 1) {
            throw "Iteration $iteration approval/mark race overwrote the issued state: $($markRaceState -join ' | ')"
        }
    }

    # Approval and deletion may finish in either order, but the persisted sheet and inventory must describe one outcome.
    for ($iteration = 1; $iteration -le $Iterations; $iteration++) {
        Invoke-SmokeSql -Sql @"
DELETE FROM tbl_product_stock_log WHERE biz_id='$($prefix)sheet-i';
DELETE FROM tbl_material_out_sheet_detail WHERE sheet_id='$($prefix)sheet-i';
DELETE FROM tbl_material_out_sheet WHERE id='$($prefix)sheet-i';
INSERT INTO tbl_material_out_sheet (id,code,sc_id,material_order_id,total_num,total_amount,description,create_by,create_by_id,create_time,update_by,update_by_id,update_time,status) VALUES
('$($prefix)sheet-i','V118-SHEET-I','$($prefix)sc',NULL,1,5.00,'V1.18 concurrency smoke','smoke','smoke',NOW(),'smoke','smoke',NOW(),0);
INSERT INTO tbl_material_out_sheet_detail (id,sheet_id,product_id,order_num,tax_price,description,order_no,out_num,stock_batch_id,tax_amount,material_order_detail_id) VALUES
('$($prefix)sheet-detail-i','$($prefix)sheet-i','$($prefix)product-b',1,5.000000,'delete race',1,1,'$($prefix)batch-b',5.00,NULL);
UPDATE tbl_product_stock SET stock_num=10, tax_price=5.000000, tax_amount=50.00 WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-b';
UPDATE tbl_product_stock_batch SET quantity=10 WHERE id='$($prefix)batch-b';
"@
        $deleteRace = @(Invoke-ConcurrentSheetAction -SheetId "$($prefix)sheet-i" -Token $token -SecondAction Delete)
        $deleteRaceState = Invoke-SmokeSql -ReturnOutput -Sql @"
SELECT COALESCE(MAX(status),-1) FROM tbl_material_out_sheet WHERE id='$($prefix)sheet-i';
SELECT COUNT(*) FROM tbl_material_out_sheet WHERE id='$($prefix)sheet-i';
SELECT COUNT(*) FROM tbl_material_out_sheet_detail WHERE sheet_id='$($prefix)sheet-i';
SELECT CONCAT(stock_num,':',tax_amount) FROM tbl_product_stock WHERE sc_id='$($prefix)sc' AND product_id='$($prefix)product-b';
SELECT quantity FROM tbl_product_stock_batch WHERE id='$($prefix)batch-b';
SELECT COUNT(*) FROM tbl_product_stock_log WHERE biz_id='$($prefix)sheet-i';
"@
        $approvalWon = $deleteRace.Count -eq 2 -and $deleteRace[0].Code -eq 200 -and $deleteRace[1].Code -ne 200
        $deletionWon = $deleteRace.Count -eq 2 -and $deleteRace[0].Code -ne 200 -and $deleteRace[1].Code -eq 200
        if ($approvalWon) {
            if ($deleteRaceState.Count -ne 6 -or $deleteRaceState[0] -ne '1' -or
                    $deleteRaceState[1] -ne '1' -or $deleteRaceState[2] -ne '1' -or
                    $deleteRaceState[3] -ne '9:45.00' -or $deleteRaceState[4] -ne '9' -or
                    [int]$deleteRaceState[5] -ne 1) {
                throw "Iteration $iteration approval won but deletion corrupted state: $($deleteRaceState -join ' | ')"
            }
        } elseif ($deletionWon) {
            if ($deleteRaceState.Count -ne 6 -or $deleteRaceState[0] -ne '-1' -or
                    $deleteRaceState[1] -ne '0' -or $deleteRaceState[2] -ne '0' -or
                    $deleteRaceState[3] -ne '10:50.00' -or $deleteRaceState[4] -ne '10' -or
                    [int]$deleteRaceState[5] -ne 0) {
                throw "Iteration $iteration deletion won but inventory changed: $($deleteRaceState -join ' | ')"
            }
        } else {
            throw "Iteration $iteration approval/delete race had an invalid outcome: $($deleteRace | ConvertTo-Json -Compress -Depth 5)"
        }
    }

    Write-Host "Material concurrency verification passed: $Iterations iterations for parent aggregation and state races, duplicate approval, rollback integrity, batch oversubscription, serial race and warehouse isolation."
} finally {
    Invoke-SmokeSql -Sql $cleanupSql
}
