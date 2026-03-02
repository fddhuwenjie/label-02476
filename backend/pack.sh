#!/bin/bash
# 项目打包脚本 - 将项目压缩打包以便提交
# 用法: ./pack.sh 或 bash pack.sh（在 backend 目录下运行）

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

PROJECT_NAME="library-management-system"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
ARCHIVE_NAME="${PROJECT_NAME}_${TIMESTAMP}.zip"

echo "正在打包项目..."
echo "排除: target/, .git/, *.zip, __pycache__, .idea/, *.iml"

# 创建压缩包（排除不必要的文件）
zip -r "$ARCHIVE_NAME" . \
  -x "*.git*" \
  -x "*target*" \
  -x "*.zip" \
  -x "*__pycache__*" \
  -x "*.idea*" \
  -x "*.iml" \
  -x "*.class" \
  -x "*node_modules*" \
  -x ".DS_Store"

echo "✓ 打包完成: $ARCHIVE_NAME"
echo "文件大小: $(du -h "$ARCHIVE_NAME" | cut -f1)"
echo ""
echo "提交说明: 请将 $ARCHIVE_NAME 压缩包提交至指定平台"
