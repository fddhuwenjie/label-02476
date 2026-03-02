# 本地 MySQL 测试数据初始化：执行 schema.sql
# 用于非 Docker 场景（DB_HOST=localhost），运行测试前需先初始化
# 用法：cd backend; .\init-db.ps1

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$MYSQL_HOST = if ($env:DB_HOST) { $env:DB_HOST } else { "localhost" }
$MYSQL_USER = if ($env:MYSQL_USER) { $env:MYSQL_USER } else { "root" }
$MYSQL_PWD = if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { "root123456" }
$SCHEMA = "src\main\resources\schema.sql"

if (-not (Test-Path $SCHEMA)) {
    Write-Host "错误：$SCHEMA 不存在" -ForegroundColor Red
    exit 1
}

Write-Host ">>> 初始化数据库 schema（host=$MYSQL_HOST）..." -ForegroundColor Cyan
Get-Content $SCHEMA -Raw -Encoding UTF8 | mysql -h $MYSQL_HOST -u $MYSQL_USER "--password=$MYSQL_PWD" 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host ">>> 若失败，请手动执行: mysql -h $MYSQL_HOST -u $MYSQL_USER -p < $SCHEMA" -ForegroundColor Yellow
    exit 1
}
Write-Host ">>> 初始化完成" -ForegroundColor Green
