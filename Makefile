# Convenience tasks for Rooster security + UAT automation
# -------------------------------------------------------
# Usage examples:
#   make audit APP_ID=xxx MASTER_KEY=yyy
#   make uat
#   make stress ANDROID_DB=/data/data/com.example.rooster/databases/bid_db

# Default help
.DEFAULT_GOAL := help

DATE := $(shell date +%F)

help:
	@echo "Available targets:"
	@echo "  audit         Pull schemas, scan CLP, write docs/audit/clp_report_<date>.csv";
	@echo "  uat           Generate docs/UAT_Matrix.csv via Python helper";
	@echo "  stress        Bid-storm Room DB; requires ANDROID_DB env var";
	@echo "  clean-cache   Remove tmp/schemas cache files";
	@echo "  fl_testlab    Build debug APK + run default Firebase Test Lab matrix (requires FIREBASE_PROJECT)";
	@echo "  net-throttle  Throttle connected emulator/device to 2G (or custom) network speeds";

# ------------- SECURITY ------------------------------------------------------

audt-deps:
	@command -v jq >/dev/null || (echo "jq not installed" && exit 1)
	@command -v curl >/dev/null || (echo "curl not installed" && exit 1)

.PHONY: audit
audit: audt-deps
	@if [ -z "$(APP_ID)" ] || [ -z "$(MASTER_KEY)" ]; then \
	  echo "Set APP_ID and MASTER_KEY env vars"; exit 1; \
	exit 0; fi
	@echo "[make] Pulling schemas and scanning CLP…"
	APP_ID=$(APP_ID) MASTER_KEY=$(MASTER_KEY) scripts/pull_schemas.sh
	APP_ID=$(APP_ID) MASTER_KEY=$(MASTER_KEY) scripts/scan_clp.sh > docs/audit/clp_report_$(DATE).csv
	@echo "Report written to docs/audit/clp_report_$(DATE).csv"

# ------------- UAT -----------------------------------------------------------
.PHONY: uat
uat:
	@python tools/device_matrix.py > docs/UAT_Matrix.csv
	@echo "Generated docs/UAT_Matrix.csv"

# ------------- STRESS --------------------------------------------------------
.PHONY: stress
stress:
	@if [ -z "$(ANDROID_DB)" ]; then echo "Set ANDROID_DB=/path/to/db"; exit 1; fi
	@ITERATIONS=$(ITERATIONS) ANDROID_DB=$(ANDROID_DB) scripts/conflict_test.sh

# ------------- CLEAN ---------------------------------------------------------
.PHONY: clean-cache
clean-cache:
	rm -f tmp/schemas_*.json
	@echo "Cache cleared."

# ------------- TEST LAB ------------------------------------------------------
.PHONY: fl_testlab
fl_testlab:
	@if [ -z "$(FIREBASE_PROJECT)" ]; then echo "Set FIREBASE_PROJECT=your-gcp-project"; exit 1; fi
	@FIREBASE_PROJECT=$(FIREBASE_PROJECT) DEVICE_MATRIX=$(DEVICE_MATRIX) scripts/fl_matrix.sh

# ------------- NETWORK THROTTLE ---------------------------------------------
.PHONY: net-throttle
net-throttle:
	@SPEED=$(SPEED) scripts/network_throttle.sh
