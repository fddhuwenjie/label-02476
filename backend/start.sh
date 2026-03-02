#!/bin/bash
# One-click Docker startup

set -e
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

echo "[*] Building and starting Docker..."
docker-compose up --build -d

echo "[*] Waiting for backend..."
for i in $(seq 1 30); do
  if docker exec library-backend test -f /app/app.jar 2>/dev/null; then
    echo "[*] Backend ready. Starting CLI..."
    exec docker exec -it library-backend java -jar /app/app.jar
  fi
  sleep 2
done

echo "[ERROR] Timeout. Check: docker logs library-backend"
exit 1
