#!/bin/bash
# 本地 MySQL 测试数据初始化：执行 schema.sql
# 用于非 Docker 场景（DB_HOST=localhost），运行测试前需先初始化
# 用法：cd backend && ./init-db.sh

set -e
cd "$(dirname "$0")"

MYSQL_HOST="${DB_HOST:-localhost}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PWD="${MYSQL_PASSWORD:-root123456}"
SCHEMA="src/main/resources/schema.sql"

if [ ! -f "$SCHEMA" ]; then
  echo "错误：$SCHEMA 不存在"
  exit 1
fi

echo ">>> 初始化数据库 schema（host=$MYSQL_HOST）..."
mysql -h "$MYSQL_HOST" -u "$MYSQL_USER" -p"$MYSQL_PWD" < "$SCHEMA"
echo ">>> 初始化完成"
