#!/bin/bash

echo "Fixing deprecated LinearProgressIndicator usages..."

# Find files with deprecated LinearProgressIndicator usage
find app/src -name "*.kt" -exec grep -l "LinearProgressIndicator(.*Float" {} \; | while read -r file; do
    echo "Processing $file"
    
    # This is a complex replacement that needs to be done manually
    # The deprecated LinearProgressIndicator(Float, ...) needs to become
    # LinearProgressIndicator(progress = { Float }, ...)
    
    # Look for the pattern and show the lines
    grep -n "LinearProgressIndicator(" "$file"
done

echo "Progress indicator analysis completed!"
