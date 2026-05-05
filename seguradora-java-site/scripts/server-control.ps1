param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('start', 'stop', 'status', 'restart', 'logs')]
    [string]$Action
)

$ErrorActionPreference = 'Stop'

$ProjectDir = Split-Path -Parent $PSScriptRoot
$JarPath = Join-Path $ProjectDir 'target\seguradora-java-site-0.0.1-SNAPSHOT.jar'
$RunDir = Join-Path $ProjectDir 'run'
$LogsDir = Join-Path $ProjectDir 'logs'
$PidFile = Join-Path $RunDir 'server.pid'
$OutLog = Join-Path $LogsDir 'server.out.log'
$ErrLog = Join-Path $LogsDir 'server.err.log'

function Ensure-Dirs {
    if (-not (Test-Path $RunDir)) {
        New-Item -ItemType Directory -Path $RunDir | Out-Null
    }
    if (-not (Test-Path $LogsDir)) {
        New-Item -ItemType Directory -Path $LogsDir | Out-Null
    }
}

function Get-JavaExe {
    if ($env:JAVA_HOME) {
        $candidate = Join-Path $env:JAVA_HOME 'bin\java.exe'
        if (Test-Path $candidate) {
            return $candidate
        }
    }

    $cmd = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($cmd) {
        return $cmd.Source
    }

    throw 'java.exe nao encontrado. Configure JAVA_HOME ou adicione Java ao PATH.'
}

function Get-PidFromFile {
    if (-not (Test-Path $PidFile)) {
        return $null
    }

    $content = Get-Content $PidFile -Raw
    if ($content.Trim() -notmatch '^[0-9]+$') {
        Remove-Item $PidFile -Force
        return $null
    }

    return [int]$content.Trim()
}

function Find-ServerProcess {
    $pidFromFile = Get-PidFromFile
    if ($pidFromFile) {
        $proc = Get-Process -Id $pidFromFile -ErrorAction SilentlyContinue
        if ($proc) {
            return $proc
        }
        Remove-Item $PidFile -Force -ErrorAction SilentlyContinue
    }

    $jarName = [System.IO.Path]::GetFileName($JarPath)
    $procCandidates = Get-CimInstance Win32_Process -Filter "Name='java.exe'" | Where-Object {
        $_.CommandLine -and $_.CommandLine -like "*${jarName}*"
    }

    if ($procCandidates) {
        $detectedPid = [int]$procCandidates[0].ProcessId
        Set-Content -Path $PidFile -Value $detectedPid
        return Get-Process -Id $detectedPid -ErrorAction SilentlyContinue
    }

    return $null
}

function Test-ServerUp {
    try {
        $response = Invoke-WebRequest -Uri 'http://localhost:8081/' -UseBasicParsing -TimeoutSec 3
        return $response.StatusCode -eq 200
    }
    catch {
        return $false
    }
}

function Start-Server {
    Ensure-Dirs

    $existing = Find-ServerProcess
    if ($existing) {
        Write-Output "Servidor ja esta em execucao (PID=$($existing.Id))."
        return
    }

    if (-not (Test-Path $JarPath)) {
        throw "Jar nao encontrado em: $JarPath. Rode o build antes (mvn -DskipTests package)."
    }

    $javaExe = Get-JavaExe

    $process = Start-Process -FilePath $javaExe -ArgumentList @('-jar', $JarPath) -WorkingDirectory $ProjectDir -RedirectStandardOutput $OutLog -RedirectStandardError $ErrLog -PassThru
    Set-Content -Path $PidFile -Value $process.Id

    for ($i = 0; $i -lt 30; $i++) {
        Start-Sleep -Milliseconds 500
        if (Test-ServerUp) {
            Write-Output "Servidor iniciado com sucesso em http://localhost:8081 (PID=$($process.Id))."
            return
        }

        $stillAlive = Get-Process -Id $process.Id -ErrorAction SilentlyContinue
        if (-not $stillAlive) {
            throw "Processo encerrou durante a inicializacao. Veja logs em: $ErrLog"
        }
    }

    throw "Servidor nao respondeu em tempo esperado. Veja logs em: $ErrLog"
}

function Stop-Server {
    $proc = Find-ServerProcess
    if (-not $proc) {
        Write-Output 'Servidor nao esta em execucao.'
        return
    }

    Stop-Process -Id $proc.Id -Force
    Remove-Item $PidFile -Force -ErrorAction SilentlyContinue
    Write-Output "Servidor finalizado (PID=$($proc.Id))."
}

function Show-Status {
    $proc = Find-ServerProcess
    if (-not $proc) {
        Write-Output 'STATUS=DOWN'
        return
    }

    $port = Get-NetTCPConnection -LocalPort 8081 -ErrorAction SilentlyContinue | Where-Object { $_.OwningProcess -eq $proc.Id }
    if ($port) {
        Write-Output "STATUS=UP PID=$($proc.Id) PORT=8081"
    }
    else {
        Write-Output "STATUS=PROCESS_UP_PORT_DOWN PID=$($proc.Id)"
    }
}

function Show-Logs {
    Ensure-Dirs
    Write-Output '--- server.err.log (tail 80) ---'
    if (Test-Path $ErrLog) {
        Get-Content $ErrLog -Tail 80
    }
    else {
        Write-Output 'Sem erros registrados.'
    }

    Write-Output ''
    Write-Output '--- server.out.log (tail 80) ---'
    if (Test-Path $OutLog) {
        Get-Content $OutLog -Tail 80
    }
    else {
        Write-Output 'Sem saida registrada.'
    }
}

switch ($Action) {
    'start' { Start-Server }
    'stop' { Stop-Server }
    'status' { Show-Status }
    'restart' {
        Stop-Server
        Start-Server
    }
    'logs' { Show-Logs }
}
