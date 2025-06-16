#!/usr/bin/env bash
# conflict_test.sh – hammer local Room DB with bid inserts to simulate concurrency
# Preconditions: ANDROID_DB env var path to the device/emulator BidEntity DB
# Usage: ANDROID_DB=$HOME/Library/…/databases/bid_db sqlite path ./scripts/conflict_test.sh
set -euo pipefail

DB="${ANDROID_DB:-}"
if [[ -z "$DB" || ! -f "$DB" ]]; then
  echo "Set ANDROID_DB to the full path of bid database" >&2
  exit 1
fi

AUCTION_ID=123
ITER=${ITERATIONS:-100}

echo "Inserting $ITER random bids into auction $AUCTION_ID …"
for i in $(seq 1 "$ITER"); do
  amount=$(( RANDOM % 100 + 1 ))
  sqlite3 "$DB" "INSERT INTO BidEntity(id,auctionId,amount,clientVersion,lastModified) \
    VALUES('$i',$AUCTION_ID,$amount,$i,strftime('%s','now'));" &
done
wait

echo "Inserted $(sqlite3 "$DB" "SELECT COUNT(*) FROM BidEntity WHERE auctionId=$AUCTION_ID;") bids."

echo "Triggering WorkManager sync (requires adb) …"
adb shell cmd jobscheduler run -u 0 com.example.rooster 0
