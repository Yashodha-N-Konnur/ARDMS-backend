#!/usr/bin/env bash
# =====================================================
# ARDMS - Docker Rollback Script
# =====================================================
set -euo pipefail

ROLLBACK_TAG="${1:-}"
[[ -z "$ROLLBACK_TAG" ]] && { echo "Usage: $0 <version>"; exit 1; }

echo "[ROLLBACK] Rolling back to ardms/ardms-backend:${ROLLBACK_TAG}"

cd "$(dirname "${BASH_SOURCE[0]}")/../docker"
docker-compose stop ardms-backend
docker-compose rm -f ardms-backend

sed -i "s|image: ardms/ardms-backend:.*|image: ardms/ardms-backend:${ROLLBACK_TAG}|g" docker-compose.yml
docker-compose up -d ardms-backend

echo "[ROLLBACK] Waiting for health check..."
for i in $(seq 1 15); do
    curl -sf http://localhost:8080/api/actuator/health &>/dev/null && \
        echo "[OK] Rollback to ${ROLLBACK_TAG} successful!" && exit 0
    sleep 10
done
echo "[FAIL] Rollback health check failed"; exit 1
