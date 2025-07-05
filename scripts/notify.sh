#!/usr/bin/env bash
# notify.sh – post a message to Slack or Teams via webhook.
# Requires CI_SLACK_HOOK env var.
# Usage: ./scripts/notify.sh "Build succeeded ✅" "#build-alerts"
set -euo pipefail

HOOK="${CI_SLACK_HOOK:-}"
if [[ -z "$HOOK" ]]; then
  echo "CI_SLACK_HOOK not set; skipping notification" >&2
  exit 0
fi

MSG=${1:-"(no message)"}
CHANNEL=${2:-"#general"}

curl -s -X POST -H 'Content-type: application/json' --data "{\"text\": \"$MSG\", \"channel\": \"$CHANNEL\"}" "$HOOK" >/dev/null

echo "[notify] Sent: $MSG -> $CHANNEL"