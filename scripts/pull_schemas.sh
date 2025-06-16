#!/usr/bin/env bash
# pull_schemas.sh – Fetch all Parse class schemas once and cache locally.
# Usage: export APP_ID=xxx MASTER_KEY=yyy && ./scripts/pull_schemas.sh
# Cached file: tmp/schemas_$(date +%F).json
set -euo pipefail

if [[ -z "${APP_ID:-}" || -z "${MASTER_KEY:-}" ]]; then
  echo "APP_ID and MASTER_KEY env vars are required" >&2
  exit 1
fi

mkdir -p tmp
out="tmp/schemas_$(date +%F).json"

if [[ -f "$out" ]]; then
  echo "[pull_schemas] Using cached $out"
  exit 0
fi

echo "[pull_schemas] Fetching schemas from Back4App API …"
curl -s https://api.back4app.com/schemas \
  -H "X-Parse-Application-Id: $APP_ID" \
  -H "X-Parse-Master-Key: $MASTER_KEY" \
  -o "$out"

echo "[pull_schemas] Saved -> $out (size $(du -h "$out" | cut -f1))"