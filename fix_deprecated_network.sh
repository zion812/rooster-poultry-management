#!/bin/bash

echo "Fixing deprecated network API usages..."

# Fix deprecated networking APIs
find app/src -name "*.kt" -exec grep -l "activeNetworkInfo\|isConnected\|type.*TYPE_WIFI\|TYPE_MOBILE\|subtype\|isRoaming" {} \; | while read -r file; do
    echo "Processing $file for deprecated network APIs"
    
    # For now, just log these - fixing networking APIs requires more complex changes
    grep -n "activeNetworkInfo\|isConnected\|type.*TYPE_WIFI\|TYPE_MOBILE\|subtype\|isRoaming" "$file" | head -5
    echo "Note: Network API fixes require manual updates to use ConnectivityManager.NetworkCallback"
    echo ""
done

echo "Network API analysis completed!"
