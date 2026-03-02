@echo off
chcp 65001 >nul 2>nul
REM One-click Docker startup

cd /d "%~dp0\.."

echo [*] Building and starting Docker...
docker-compose up --build -d
if errorlevel 1 (
    echo [ERROR] Startup failed. Check Docker and Docker Compose.
    pause
    exit /b 1
)

echo [*] Waiting for backend...
set attempts=0
:wait_loop
docker exec library-backend test -f /app/app.jar 2>nul
if %errorlevel% equ 0 goto ready
set /a attempts+=1
if %attempts% geq 30 goto timeout
timeout /t 2 /nobreak >nul
goto wait_loop

:ready
echo [*] Backend ready. Starting CLI...
docker exec -it library-backend java -jar /app/app.jar
exit /b 0

:timeout
echo [ERROR] Timeout. Check: docker logs library-backend
pause
exit /b 1
