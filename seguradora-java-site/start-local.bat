@echo off
setlocal

set "ACTION=%~1"
if "%ACTION%"=="" set "ACTION=start"

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start-local.ps1" -Action %ACTION%
set "EXITCODE=%ERRORLEVEL%"

if not "%EXITCODE%"=="0" (
  echo.
  echo Falha ao executar a acao "%ACTION%".
)

exit /b %EXITCODE%
