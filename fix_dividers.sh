#!/bin/bash

echo "Fixing deprecated Divider usages..."

# Replace Divider with HorizontalDivider
find app/src -name "*.kt" -exec grep -l "Divider(" {} \; | while read -r file; do
    echo "Processing $file"
    
    # Replace Divider with HorizontalDivider
    sed -i 's/Divider(/HorizontalDivider(/g' "$file"
    
    echo "Updated $file"
done

echo "Divider fixes completed!"
