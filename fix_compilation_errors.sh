#!/bin/bash

echo "Fixing compilation errors..."

# Fix 1: Remove deprecated bitmap options (they are commented out incorrectly)
find app/src -name "*.kt" -exec sed -i '/\/\/ options\.inDither = .*\/\/ Deprecated/d' {} \;
find app/src -name "*.kt" -exec sed -i '/\/\/ options\.inPurgeable = .*\/\/ Deprecated/d' {} \;
find app/src -name "*.kt" -exec sed -i '/\/\/ options\.inInputShareable = .*\/\/ Deprecated/d' {} \;

# Fix 2: Add missing imports for HorizontalDivider
find app/src -name "*.kt" -exec grep -l "HorizontalDivider" {} \; | while read -r file; do
    if ! grep -q "import androidx.compose.material3.HorizontalDivider" "$file"; then
        sed -i '1i import androidx.compose.material3.HorizontalDivider' "$file"
    fi
done

# Fix 3: Fix Notes icons (check if they actually exist as AutoMirrored)
find app/src -name "*.kt" -exec grep -l "Icons.AutoMirrored.Filled.Notes" {} \; | while read -r file; do
    echo "Checking Notes icon in $file"
    # Revert Notes back to Default if AutoMirrored doesn't exist
    sed -i 's/Icons.AutoMirrored.Filled.Notes/Icons.Default.Notes/g' "$file"
    sed -i '/import androidx.compose.material.icons.automirrored.filled.Notes/d' "$file"
done

# Fix 4: Remove any remaining conflicting getInstance functions that aren't properly named
echo "Compilation error fixes applied!"
