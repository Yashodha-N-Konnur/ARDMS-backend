#!/usr/bin/env bash
# =====================================================
# ARDMS - Local Development Setup Script
# Starts MySQL via Docker and runs the Spring Boot app
# =====================================================
set -euo pipefail

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
log()  { echo -e "${CYAN}[ARDMS]${NC} $1"; }
ok()   { echo -e "${GREEN}[OK]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
fail() { echo -e "${RED}[FAIL]${NC} $1"; exit 1; }

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

log "Starting ARDMS local development environment..."
log "Project root: $PROJECT_ROOT"

# --- Validate tools ---
for tool in docker docker-compose mvn java; do
    command -v $tool &>/dev/null || fail "$tool is not installed"
done

JAVA_VER=$(java -version 2>&1 | head -1 | awk -F'"' '{print $2}' | cut -d'.' -f1)
[[ "$JAVA_VER" -ge 17 ]] || fail "Java 17+ required (found: $JAVA_VER)"
ok "Java $JAVA_VER found"

# --- Copy env if missing ---
ENV_FILE="$PROJECT_ROOT/docker/.env"
if [[ ! -f "$ENV_FILE" ]]; then
    cp "$PROJECT_ROOT/docker/.env.example" "$ENV_FILE"
    warn ".env file created from example. Review $ENV_FILE before continuing."
fi

# --- Start MySQL ---
log "Starting MySQL container..."
cd "$PROJECT_ROOT/docker"
docker-compose up -d mysql
log "Waiting for MySQL to be ready..."
for i in $(seq 1 30); do
    if docker-compose exec -T mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
        ok "MySQL is ready"; break
    fi
    [[ $i -eq 30 ]] && fail "MySQL failed to start within 5 minutes"
    sleep 10
done

# --- Build backend ---
log "Building ARDMS backend (this may take a few minutes)..."
cd "$PROJECT_ROOT/backend"
mvn clean package -DskipTests -q && ok "Build successful"

# --- Run application ---
log "Starting ARDMS backend on port 8080..."
log "Swagger UI: http://localhost:8080/api/swagger-ui.html"
log "Press Ctrl+C to stop"
echo ""
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
