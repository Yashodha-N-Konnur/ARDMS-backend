#!/usr/bin/env bash
# =====================================================
# ARDMS - Docker Full Deployment Script
# Builds image and starts all containers
# =====================================================
set -euo pipefail

RED='\033[0;31m'; GREEN='\033[0;32m'; CYAN='\033[0;36m'; NC='\033[0m'
log()  { echo -e "${CYAN}[ARDMS]${NC} $1"; }
ok()   { echo -e "${GREEN}[OK]${NC} $1"; }
fail() { echo -e "${RED}[FAIL]${NC} $1"; exit 1; }

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TAG="${1:-latest}"

log "Deploying ARDMS with tag: $TAG"

# Build
log "Building Docker image..."
cd "$PROJECT_ROOT/backend"
docker build -t "ardms/ardms-backend:${TAG}" -t "ardms/ardms-backend:latest" .
ok "Image built: ardms/ardms-backend:${TAG}"

# Deploy
log "Starting containers..."
cd "$PROJECT_ROOT/docker"
[[ -f .env ]] || { cp .env.example .env; echo "Created .env from example"; }
docker-compose up -d

log "Waiting for health check..."
for i in $(seq 1 20); do
    if curl -sf http://localhost:8080/api/actuator/health &>/dev/null; then
        ok "ARDMS is running!"
        echo ""
        ok "Swagger UI: http://localhost:8080/api/swagger-ui.html"
        ok "API Base:   http://localhost:8080/api"
        exit 0
    fi
    sleep 10
done
fail "Health check failed after 3 minutes"
