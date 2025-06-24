#!/bin/bash

echo "Fixing deprecated LinearProgressIndicator usages..."

# Fix LinearProgressIndicator(progress = Float, ...) to LinearProgressIndicator(progress = { Float }, ...)
find app/src -name "*.kt" -exec grep -l "LinearProgressIndicator(" {} \; | while read -r file; do
    echo "Processing $file"
    
    # Replace LinearProgressIndicator(progress = variable with LinearProgressIndicator(progress = { variable }
    sed -i 's/LinearProgressIndicator(\s*progress\s*=\s*\([^,)]*\)/LinearProgressIndicator(progress = { \1 }/g' "$file"
    
    # Replace LinearProgressIndicator(variable, with LinearProgressIndicator(progress = { variable },
    sed -i 's/LinearProgressIndicator(\([^,)]*\),/LinearProgressIndicator(progress = { \1 },/g' "$file"
    
    echo "Updated $file"
done

echo "LinearProgressIndicator fixes completed!"
