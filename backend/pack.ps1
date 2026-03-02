# 项目打包脚本 - 将项目压缩打包以便提交 (Windows PowerShell)
# 用法: .\pack.ps1（在 backend 目录下运行）

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir
Set-Location $ProjectRoot

$ProjectName = "library-management-system"
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$ArchiveName = "$ProjectName`_$Timestamp.zip"

Write-Host "正在打包项目..." -ForegroundColor Cyan
Write-Host "排除: target/, .git/, *.zip"

# 创建临时目录并复制需要的文件（排除 target）
$TempDir = "$env:TEMP\pack_temp_$Timestamp"
New-Item -ItemType Directory -Path $TempDir -Force | Out-Null

# 复制 backend（排除 target）
$BackendDest = Join-Path $TempDir "backend"
New-Item -ItemType Directory -Path $BackendDest -Force | Out-Null
Copy-Item -Path "backend\*" -Destination $BackendDest -Recurse -Force
if (Test-Path "$BackendDest\target") { Remove-Item "$BackendDest\target" -Recurse -Force -ErrorAction SilentlyContinue }

# 复制根目录文件（pack.sh/pack.ps1 已随 backend 复制）
Copy-Item -Path "docker-compose.yml", ".gitignore", "README.md" -Destination $TempDir -Force -ErrorAction SilentlyContinue
Get-ChildItem -Path . -File | Where-Object { $_.Extension -match "\.(md|yml|yaml|sql)$" -or $_.Name -eq ".gitignore" } | ForEach-Object {
    Copy-Item $_.FullName -Destination $TempDir -Force -ErrorAction SilentlyContinue
}

Compress-Archive -Path "$TempDir\*" -DestinationPath $ArchiveName -Force
Remove-Item -Path $TempDir -Recurse -Force -ErrorAction SilentlyContinue

$Size = (Get-Item $ArchiveName).Length / 1KB
Write-Host "`n✓ 打包完成: $ArchiveName" -ForegroundColor Green
Write-Host "文件大小: $([math]::Round($Size, 2)) KB"
Write-Host "`n提交说明: 请将 $ArchiveName 压缩包提交至指定平台"
