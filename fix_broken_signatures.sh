#!/bin/bash

echo "Fixing broken function signatures..."

# Fix the getInstance pattern that was broken
find app/src -name "*.kt" -exec grep -l "fun _context: Context)" {} \; | while read -r file; do
    echo "Fixing getInstance in $file"
    sed -i 's/fun _context: Context)/fun getInstance(context: Context)/g' "$file"
done

# Fix other broken parameter patterns
find app/src -name "*.kt" -exec grep -l "fun.*_.*:" {} \; | while read -r file; do
    echo "Checking $file for broken function parameters"
    
    # Fix getInstance patterns
    sed -i 's/fun getInstance(_context: Context)/fun getInstance(context: Context)/g' "$file"
    sed -i 's/fun .*(_navController: NavController)/fun getInstance(navController: NavController)/g' "$file"
    
    # Fix function parameters that got mangled
    sed -i 's/fun \([^(]*\)(_\([^:]*\):/fun \1(\2:/g' "$file"
done

echo "Function signature fixes completed!"
