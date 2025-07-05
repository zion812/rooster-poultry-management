#!/bin/bash

echo "Fixing Notes icon usage..."

# Notes might not have AutoMirrored version, revert to Icons.Default
find app/src -name "*.kt" -exec grep -l "Icons.AutoMirrored.Filled.Notes" {} \; | while read -r file; do
    echo "Reverting Notes to Icons.Default in $file"
    
    # Remove the incorrect AutoMirrored import
    sed -i '/import androidx.compose.material.icons.automirrored.filled.Notes/d' "$file"
    
    # Replace the usage back to Default
    sed -i 's/Icons.AutoMirrored.Filled.Notes/Icons.Default.Notes/g' "$file"
done

echo "Notes icon fixes completed!"
