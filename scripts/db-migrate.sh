#!/usr/bin/env bash
# =====================================================
# ARDMS - Database Migration Script (Flyway)
# =====================================================
set -euo pipefail

DB_URL="${DB_URL:-jdbc:mysql://localhost:3306/ardms_db}"
DB_USER="${DB_USERNAME:-ardms_user}"
DB_PASS="${DB_PASSWORD:-ardms_pass}"

echo "[DB] Running Flyway migrations..."
cd "$(dirname "${BASH_SOURCE[0]}")/../backend"
mvn flyway:migrate \
    -Dflyway.url="$DB_URL" \
    -Dflyway.user="$DB_USER" \
    -Dflyway.password="$DB_PASS" \
    -q
echo "[DB] Migrations completed successfully"
