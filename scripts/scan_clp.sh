#!/usr/bin/env bash
# scan_clp.sh  – identify classes whose Class-Level Permissions (CLP) are missing or too open.
# Usage: export APP_ID=xxxx MASTER_KEY=yyyy && ./scripts/scan_clp.sh > docs/audit/clp_report_$(date +%F).csv
# Requires: curl, jq

set -euo pipefail

if [[ -z "${APP_ID:-}" || -z "${MASTER_KEY:-}" ]]; then
  echo "APP_ID and MASTER_KEY env vars are required" >&2
  exit 1
fi

SCHEMA_ENDPOINT="https://api.back4app.com/schemas"

# Fetch all schemas in one shot (Back4App supports masterKey auth)
json=$(curl -s "$SCHEMA_ENDPOINT" \
  -H "X-Parse-Application-Id: $APP_ID" \
  -H "X-Parse-Master-Key: $MASTER_KEY" )

# Header for CSV output
printf "className,issue\n"

# 1) Missing CLP entirely OR
# 2) Missing .find block OR
# 3) .find allows wildcard (*)
# 4) .create allows wildcard (*)
# 5) .update allows wildcard (*)
# We treat any of the above as a red-flag row.

echo "$json" | jq -r '
  .results[] | {className, clp: .classLevelPermissions} | 
  select(.clp==null or ( (.clp.find==null) or (.clp.find["*"]==true) or (.clp.create!=null and .clp.create["*"]==true) or (.clp.update!=null and .clp.update["*"]==true) ) ) | 
  "\(.className),OPEN_CLP" '
