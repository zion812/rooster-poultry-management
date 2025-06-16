#!/usr/bin/env python3
"""Generate UAT test-matrix CSV.

Add or adjust devices/networks/scenarios below, then run:
    python tools/device_matrix.py  > docs/UAT_Matrix.csv
"""
import csv
import itertools
from datetime import date

# ---- CONFIG -----------------------------------------------------------------
DEVICES = [
    ("Android Go 1 GB", "and-go"),
    ("Redmi 7", "redmi7"),
    ("Pixel 3", "pixel3"),
]

NETWORKS = [
    ("2G", "2g"),
    ("3G intermittent", "3g-drop"),
    ("Offline→Online flap", "flap"),
]

SCENARIOS = [
    "Login",
    "Marketplace Bid",
    "Photo Upload",
    "Transfer Verify",
    "Background Sync",
]
# ----------------------------------------------------------------------------

def main():
    writer = csv.writer(open("/dev/stdout", "w", newline=""))
    writer.writerow(["Scenario", "Device", "Network", "Planned", "Actual", "Result"])
    for scenario, (device_name, _), (net_name, _) in itertools.product(
        SCENARIOS, DEVICES, NETWORKS
    ):
        writer.writerow([scenario, device_name, net_name, "", "", ""])  # placeholders

    print(f"# Generated on {date.today()} – {len(SCENARIOS)*len(DEVICES)*len(NETWORKS)} rows", file=sys.stderr)

if __name__ == "__main__":
    import sys
    main()
