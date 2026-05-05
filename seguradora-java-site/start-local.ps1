param(
    [ValidateSet('start', 'stop', 'status', 'restart', 'logs')]
    [string]$Action = 'start'
)

$ErrorActionPreference = 'Stop'

$projectScript = Join-Path $PSScriptRoot 'seguradora-java-site\scripts\server-control.ps1'

if (-not (Test-Path $projectScript)) {
    throw "Script de controle nao encontrado em: $projectScript"
}

& powershell -NoProfile -ExecutionPolicy Bypass -File $projectScript -Action $Action
