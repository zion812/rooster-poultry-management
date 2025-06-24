#!/bin/bash

echo "Fixing deprecated menuAnchor() usages..."

# Replace .menuAnchor() with .menuAnchor(MenuAnchorType.PrimaryNotEditable)
find app/src -name "*.kt" -exec grep -l "\.menuAnchor()" {} \; | while read -r file; do
    echo "Processing $file"
    
    # Replace .menuAnchor() with .menuAnchor(MenuAnchorType.PrimaryNotEditable)
    sed -i 's/\.menuAnchor()/\.menuAnchor(MenuAnchorType.PrimaryNotEditable)/g' "$file"
    
    # Add the import for MenuAnchorType if not already present
    if ! grep -q "import androidx.compose.material3.MenuAnchorType" "$file"; then
        # Find the line with material3 imports and add the import
        if grep -q "import androidx.compose.material3" "$file"; then
            sed -i "/import androidx.compose.material3/a\\import androidx.compose.material3.MenuAnchorType" "$file"
        fi
    fi
    
    echo "Updated $file"
done

echo "menuAnchor fixes completed!"
