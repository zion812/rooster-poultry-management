#!/usr/bin/env bash
# fl_matrix.sh – Run Firebase Test Lab test matrix for Rooster Android app
# -----------------------------------------------------------------------
# Preconditions:
#   • gcloud CLI installed and authenticated with access to Firebase project.
#   • FIREBASE_PROJECT env var set to your Google Cloud project ID.
#   • The Gradle wrapper (./gradlew) at project root builds the APK & test APK.
#   • A test instrumentation runner is configured in build.gradle (e.g., android.defaultConfig.testInstrumentationRunner)
# Usage examples:
#   FIREBASE_PROJECT=my-rooster-project ./scripts/fl_matrix.sh
#   FIREBASE_PROJECT=my-rooster-project DEVICE_MATRIX=default ./scripts/fl_matrix.sh

set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
FIREBASE_PROJECT="${FIREBASE_PROJECT:-}"
DEVICE_MATRIX="${DEVICE_MATRIX:-default}"

if [[ -z "${FIREBASE_PROJECT}" ]]; then
  echo "[fl_matrix] ERROR: Set FIREBASE_PROJECT environment variable to your Firebase/Google Cloud project ID" >&2
  exit 1
fi

cd "$PROJECT_ROOT"

echo "[fl_matrix] 🔄 Building APK and AndroidTest APK…"
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest --quiet

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
TEST_APK_PATH="app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk"

if [[ ! -f "$APK_PATH" || ! -f "$TEST_APK_PATH" ]]; then
  echo "[fl_matrix] ERROR: APKs not found after build" >&2
  exit 1
fi

echo "[fl_matrix] ✅ APKs built: $APK_PATH , $TEST_APK_PATH"

echo "[fl_matrix] 🚀 Submitting tests to Firebase Test Lab (project: $FIREBASE_PROJECT)…"

declare -a DEFAULT_DEVICE_MATRIX=(
  "model=Pixel2,version=34,locale=en,orientation=portrait"
  "model=Nexus5X,version=29,locale=en,orientation=portrait"
  "model=Pixel4,version=30,locale=en,orientation=portrait"
  "model=Pixel6,version=33,locale=en,orientation=portrait"
)

if [[ "$DEVICE_MATRIX" == "default" ]]; then
  DEVICE_ARGS=( )
  for spec in "${DEFAULT_DEVICE_MATRIX[@]}"; do
    DEVICE_ARGS+=( "--device" "$spec" )
  done
else
  # Expect DEVICE_MATRIX as comma-separated string list of specs
  IFS=',' read -ra SPECS <<< "$DEVICE_MATRIX"
  DEVICE_ARGS=( )
  for spec in "${SPECS[@]}"; do
    DEVICE_ARGS+=( "--device" "$spec" )
  done
fi

# Run the tests
set +e  # Allow gcloud to fail but still capture exit status
GCLOUD_OUTPUT=$(mktemp)
if gcloud firebase test android run \
  --type instrumentation \
  --project "$FIREBASE_PROJECT" \
  --app "$APK_PATH" \
  --test "$TEST_APK_PATH" \
  "${DEVICE_ARGS[@]}" \
  --timeout 30m \
  --results-bucket "test-lab-$FIREBASE_PROJECT" \
  --results-dir "rooster/fl_matrix_$(date +%Y%m%d_%H%M%S)" \
  --format json | tee "$GCLOUD_OUTPUT"; then
  TEST_EXIT_CODE=0
else
  TEST_EXIT_CODE=$?
fi
set -e

if [[ $TEST_EXIT_CODE -ne 0 ]]; then
  echo "[fl_matrix] ❌ Firebase Test Lab run failed (exit code $TEST_EXIT_CODE)" >&2
  exit $TEST_EXIT_CODE
fi

echo "[fl_matrix] ✅ Test matrix completed. See Firebase Console for detailed results."
