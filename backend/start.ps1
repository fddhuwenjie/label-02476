# 一键启动：构建、启动 Docker 服务，并进入命令行交互界面

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
Set-Location $ProjectRoot

Write-Host ">>> 构建并启动 Docker 服务..." -ForegroundColor Cyan
docker-compose up --build -d

Write-Host ">>> 等待后端容器就绪..." -ForegroundColor Cyan
$maxAttempts = 30
for ($i = 1; $i -le $maxAttempts; $i++) {
    docker exec library-backend test -f /app/app.jar 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host ">>> 后端已就绪，进入命令行界面..." -ForegroundColor Green
        docker exec -it library-backend java -jar /app/app.jar
        exit
    }
    Start-Sleep -Seconds 2
}

Write-Host ">>> 超时：后端未就绪，请检查 docker logs library-backend" -ForegroundColor Red
exit 1
