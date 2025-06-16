#!/bin/bash

echo "Fixing remaining import issues..."

# Fix ChatBubbleOutline imports
find app/src -name "*.kt" -exec grep -l "Icons.Default.ChatBubbleOutline" {} \; | while read -r file; do
    echo "Fixing ChatBubbleOutline import in $file"
    
    # Add the AutoMirrored import if not present
    if ! grep -q "import androidx.compose.material.icons.automirrored.filled.ChatBubbleOutline" "$file"; then
        if grep -q "import androidx.compose.material.icons.Icons" "$file"; then
            sed -i "/import androidx.compose.material.icons.Icons/a\\import androidx.compose.material.icons.automirrored.filled.ChatBubbleOutline" "$file"
        fi
    fi
    
    # Replace the usage
    sed -i 's/Icons.Default.ChatBubbleOutline/Icons.AutoMirrored.Filled.ChatBubbleOutline/g' "$file"
done

# Fix Notes imports  
find app/src -name "*.kt" -exec grep -l "Icons.Default.Notes" {} \; | while read -r file; do
    echo "Fixing Notes import in $file"
    
    # Add the AutoMirrored import if not present
    if ! grep -q "import androidx.compose.material.icons.automirrored.filled.Notes" "$file"; then
        if grep -q "import androidx.compose.material.icons.Icons" "$file"; then
            sed -i "/import androidx.compose.material.icons.Icons/a\\import androidx.compose.material.icons.automirrored.filled.Notes" "$file"
        fi
    fi
    
    # Replace the usage
    sed -i 's/Icons.Default.Notes/Icons.AutoMirrored.Filled.Notes/g' "$file"
done

# Fix HorizontalDivider imports
find app/src -name "*.kt" -exec grep -l "HorizontalDivider" {} \; | while read -r file; do
    echo "Checking HorizontalDivider import in $file"
    
    # Add the import if not present
    if ! grep -q "import androidx.compose.material3.HorizontalDivider" "$file"; then
        if grep -q "import androidx.compose.material3" "$file"; then
            sed -i "/import androidx.compose.material3/a\\import androidx.compose.material3.HorizontalDivider" "$file"
        fi
    fi
done

echo "Import fixes completed!"
