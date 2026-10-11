$ErrorActionPreference = 'Stop'
$envFile = Join-Path $PSScriptRoot '.env'

if (Test-Path -LiteralPath $envFile) {
    foreach ($line in Get-Content -LiteralPath $envFile) {
        if ($line -match '^\s*(?:#|$)') { continue }
        if ($line -notmatch '^\s*(?:export\s+)?([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(.*?)\s*$') {
            throw "Invalid setting in backend/.env. Expected KEY=value."
        }
        $name = $Matches[1]
        $value = $Matches[2]
        if ($value.Length -ge 2 -and (($value[0] -eq '"' -and $value[-1] -eq '"') -or ($value[0] -eq "'" -and $value[-1] -eq "'"))) {
            $value = $value.Substring(1, $value.Length - 2)
        }
        if ([string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($name, 'Process'))) {
            [Environment]::SetEnvironmentVariable($name, $value, 'Process')
        }
    }
}

$required = @('DB_URL', 'DB_USERNAME', 'DB_PASSWORD', 'JWT_SECRET')
$missing = @($required | Where-Object { [string]::IsNullOrWhiteSpace([Environment]::GetEnvironmentVariable($_, 'Process')) })
if ($missing.Count -gt 0) {
    throw "Backend configuration is incomplete. Set $($missing -join ', ') in backend/.env (copy backend/.env.example and edit it). No values were printed."
}
if ($env:DB_PASSWORD -eq 'replace-me' -or $env:JWT_SECRET -eq 'replace-with-random-secret-at-least-32-bytes-long') {
    throw 'Replace the example database password and JWT secret in backend/.env with values you choose before starting.'
}
if ([System.Text.Encoding]::UTF8.GetByteCount($env:JWT_SECRET) -lt 32) {
    throw 'JWT_SECRET must contain at least 32 UTF-8 bytes.'
}

Set-Location -LiteralPath $PSScriptRoot
& mvn spring-boot:run
exit $LASTEXITCODE
