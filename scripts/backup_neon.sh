#!/usr/bin/env bash
# ==============================================================================
# scripts/backup_neon.sh — Safe Automated Neon PostgreSQL Backup
# Zero hardcoded passwords; reads DATABASE_URL / DB_URL from environment
# ==============================================================================
set -euo pipefail

TARGET_URL="${DATABASE_URL:-${DB_URL:-}}"

if [ -z "$TARGET_URL" ]; then
  echo "[-] ERROR: DATABASE_URL or DB_URL environment variable must be set."
  echo "    Example: export DATABASE_URL=\"postgresql://user:pass@ep-xyz.neon.tech/dbname?sslmode=require\""
  exit 1
fi

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="${BACKUP_DIR:-./backups}"
mkdir -p "$BACKUP_DIR"
BACKUP_FILE="${BACKUP_DIR}/neon_backup_${TIMESTAMP}.dump"

echo "[+] Starting PostgreSQL backup to ${BACKUP_FILE}..."

# Use custom format (-Fc) for compressed, flexible pg_restore capability
pg_dump \
  --dbname="$TARGET_URL" \
  --format=c \
  --no-owner \
  --no-privileges \
  --file="$BACKUP_FILE"

FILE_SIZE=$(du -h "$BACKUP_FILE" | cut -f1)
echo "[+] Backup successfully created: ${BACKUP_FILE} (${FILE_SIZE})"
echo "[+] Verification: pg_restore --list ${BACKUP_FILE} | head -n 10"
