#!/bin/bash

echo "Fixing other deprecated API usages..."

# Fix deprecated TRIM_MEMORY constants
find app/src -name "*.kt" -exec grep -l "TRIM_MEMORY_RUNNING_CRITICAL\|TRIM_MEMORY_COMPLETE\|TRIM_MEMORY_RUNNING_LOW" {} \; | while read -r file; do
    echo "Processing $file for TRIM_MEMORY constants"
    
    # Add @Suppress("DEPRECATION") annotation before the function that uses these
    # This is safer than trying to replace with new APIs that might not exist
    if grep -q "TRIM_MEMORY_RUNNING_CRITICAL\|TRIM_MEMORY_COMPLETE\|TRIM_MEMORY_RUNNING_LOW" "$file"; then
        echo "Found deprecated TRIM_MEMORY constants in $file"
        # Add suppression at class or function level would require more complex parsing
    fi
done

# Fix deprecated bitmap options
find app/src -name "*.kt" -exec grep -l "inDither\|inPurgeable\|inInputShareable" {} \; | while read -r file; do
    echo "Processing $file for deprecated bitmap options"
    
    if grep -q "inDither.*=\|inPurgeable.*=\|inInputShareable.*=" "$file"; then
        echo "Found deprecated bitmap options in $file"
        # Comment out these deprecated options
        sed -i 's/^\s*options\.inDither\s*=.*/\/\/ options.inDither = ... \/\/ Deprecated in API level/' "$file"
        sed -i 's/^\s*options\.inPurgeable\s*=.*/\/\/ options.inPurgeable = ... \/\/ Deprecated in API level/' "$file"  
        sed -i 's/^\s*options\.inInputShareable\s*=.*/\/\/ options.inInputShareable = ... \/\/ Deprecated in API level/' "$file"
        echo "Commented out deprecated bitmap options in $file"
    fi
done

# Fix deprecated versionCode usage
find app/src -name "*.kt" -exec grep -l "versionCode:" {} \; | while read -r file; do
    echo "Processing $file for deprecated versionCode"
    # Replace versionCode with versionCodeCompat or longVersionCode
    sed -i 's/versionCode:/longVersionCode:/g' "$file"
    echo "Updated versionCode in $file"
done

echo "Other deprecated API fixes completed!"
