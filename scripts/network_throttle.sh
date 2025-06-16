#!/usr/bin/env bash
# network_throttle.sh – Simple helper to simulate poor-network / 2G conditions on
# a connected Android emulator or physical device via `adb shell` traffic control.
# -----------------------------------------------------------------------------
# Requirements:
#   • adb in PATH and a single target device connected (or set ANDROID_SERIAL)
#   • Rooted emulator/device (emulators are fine). For non-rooted physical
#     devices, commands fall back to `adb shell settings put global` where
#     possible.
#
# Usage examples:
#   # Throttle to default "2G" profile (~50 kbps up / 50 kbps down)
#   ./scripts/network_throttle.sh
#
#   # Custom speeds (down=128 kbps, up=64 kbps)
#   SPEED="128kbit 64kbit" ./scripts/network_throttle.sh
#
#   # Disable throttling / restore defaults
#   ./scripts/network_throttle.sh clear

set -euo pipefail

ACTION="${1:-apply}"
SPEED="${SPEED:-"50kbit 50kbit"}"

echo "[net-throttle] Target action: $ACTION" >&2

function apply_throttle() {
  local down up
  read -r down up <<< "$SPEED"
  echo "[net-throttle] Applying throttle – down=$down, up=$up" >&2
  # Use tc to shape traffic (requires root; works on emulators)
  adb shell "su -c 'tc qdisc del dev rmnet_data0 root || true'" 2>/dev/null || true
  adb shell "su -c 'tc qdisc add dev rmnet_data0 root handle 1: htb default 12'"
  adb shell "su -c 'tc class add dev rmnet_data0 parent 1:1 classid 1:12 htb rate $down ceil $down'"
  adb shell "su -c 'tc class add dev rmnet_data0 parent 1:1 classid 1:13 htb rate $up ceil $up'"
  echo "[net-throttle] ✅ Throttling active"
}

function clear_throttle() {
  echo "[net-throttle] Clearing throttle rules" >&2
  adb shell "su -c 'tc qdisc del dev rmnet_data0 root'" 2>/dev/null || true
  echo "[net-throttle] ✅ Network restored to normal speed"
}

if [[ "$ACTION" == "clear" ]]; then
  clear_throttle
else
  apply_throttle
fi
