#!/bin/bash

echo "Fixing ChatBubbleOutline icon usage..."

# ChatBubbleOutline doesn't have AutoMirrored version, revert to Icons.Default
find app/src -name "*.kt" -exec grep -l "Icons.AutoMirrored.Filled.ChatBubbleOutline" {} \; | while read -r file; do
    echo "Reverting ChatBubbleOutline to Icons.Default in $file"
    
    # Remove the incorrect AutoMirrored import
    sed -i '/import androidx.compose.material.icons.automirrored.filled.ChatBubbleOutline/d' "$file"
    
    # Replace the usage back to Default
    sed -i 's/Icons.AutoMirrored.Filled.ChatBubbleOutline/Icons.Default.ChatBubbleOutline/g' "$file"
done

echo "ChatBubbleOutline fixes completed!"
