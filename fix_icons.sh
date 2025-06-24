#!/bin/bash

# Define the mappings of deprecated icons to their AutoMirrored versions
declare -A ICON_MAPPINGS=(
    ["Icons.Default.ArrowBack"]="Icons.AutoMirrored.Filled.ArrowBack"
    ["Icons.Default.ArrowForward"]="Icons.AutoMirrored.Filled.ArrowForward"
    ["Icons.Default.Send"]="Icons.AutoMirrored.Filled.Send"
    ["Icons.Default.Comment"]="Icons.AutoMirrored.Filled.Comment"
    ["Icons.Default.Chat"]="Icons.AutoMirrored.Filled.Chat"
    ["Icons.Default.Help"]="Icons.AutoMirrored.Filled.Help"
    ["Icons.Default.ExitToApp"]="Icons.AutoMirrored.Filled.ExitToApp"
    ["Icons.Default.TrendingUp"]="Icons.AutoMirrored.Filled.TrendingUp"
    ["Icons.Default.TrendingDown"]="Icons.AutoMirrored.Filled.TrendingDown"
    ["Icons.Default.Message"]="Icons.AutoMirrored.Filled.Message"
    ["Icons.Default.Note"]="Icons.AutoMirrored.Filled.Note"
    ["Icons.Default.Notes"]="Icons.AutoMirrored.Filled.Notes"
    ["Icons.Default.Label"]="Icons.AutoMirrored.Filled.Label"
    ["Icons.Default.HelpOutline"]="Icons.AutoMirrored.Filled.HelpOutline"
    ["Icons.Default.DirectionsRun"]="Icons.AutoMirrored.Filled.DirectionsRun"
    ["Icons.Default.VolumeUp"]="Icons.AutoMirrored.Filled.VolumeUp"
    ["Icons.Default.ShowChart"]="Icons.AutoMirrored.Filled.ShowChart"
    ["Icons.Default.TextSnippet"]="Icons.AutoMirrored.Filled.TextSnippet"
)

# Define required imports for each AutoMirrored icon
declare -A REQUIRED_IMPORTS=(
    ["Icons.AutoMirrored.Filled.ArrowBack"]="import androidx.compose.material.icons.automirrored.filled.ArrowBack"
    ["Icons.AutoMirrored.Filled.ArrowForward"]="import androidx.compose.material.icons.automirrored.filled.ArrowForward"
    ["Icons.AutoMirrored.Filled.Send"]="import androidx.compose.material.icons.automirrored.filled.Send"
    ["Icons.AutoMirrored.Filled.Comment"]="import androidx.compose.material.icons.automirrored.filled.Comment"
    ["Icons.AutoMirrored.Filled.Chat"]="import androidx.compose.material.icons.automirrored.filled.Chat"
    ["Icons.AutoMirrored.Filled.Help"]="import androidx.compose.material.icons.automirrored.filled.Help"
    ["Icons.AutoMirrored.Filled.ExitToApp"]="import androidx.compose.material.icons.automirrored.filled.ExitToApp"
    ["Icons.AutoMirrored.Filled.TrendingUp"]="import androidx.compose.material.icons.automirrored.filled.TrendingUp"
    ["Icons.AutoMirrored.Filled.TrendingDown"]="import androidx.compose.material.icons.automirrored.filled.TrendingDown"
    ["Icons.AutoMirrored.Filled.Message"]="import androidx.compose.material.icons.automirrored.filled.Message"
    ["Icons.AutoMirrored.Filled.Note"]="import androidx.compose.material.icons.automirrored.filled.Note"
    ["Icons.AutoMirrored.Filled.Notes"]="import androidx.compose.material.icons.automirrored.filled.Notes"
    ["Icons.AutoMirrored.Filled.Label"]="import androidx.compose.material.icons.automirrored.filled.Label"
    ["Icons.AutoMirrored.Filled.HelpOutline"]="import androidx.compose.material.icons.automirrored.filled.HelpOutline"
    ["Icons.AutoMirrored.Filled.DirectionsRun"]="import androidx.compose.material.icons.automirrored.filled.DirectionsRun"
    ["Icons.AutoMirrored.Filled.VolumeUp"]="import androidx.compose.material.icons.automirrored.filled.VolumeUp"
    ["Icons.AutoMirrored.Filled.ShowChart"]="import androidx.compose.material.icons.automirrored.filled.ShowChart"
    ["Icons.AutoMirrored.Filled.TextSnippet"]="import androidx.compose.material.icons.automirrored.filled.TextSnippet"
)

echo "Fixing deprecated icon usages..."

# Find all Kotlin files
find app/src -name "*.kt" | while read -r file; do
    modified=false
    
    # Check if file contains any deprecated icons
    for old_icon in "${!ICON_MAPPINGS[@]}"; do
        if grep -q "$old_icon" "$file"; then
            echo "Processing $file for $old_icon"
            new_icon="${ICON_MAPPINGS[$old_icon]}"
            required_import="${REQUIRED_IMPORTS[$new_icon]}"
            
            # Add import if needed and not already present
            if ! grep -q "$required_import" "$file"; then
                # Find the line with "import androidx.compose.material.icons.Icons"
                if grep -q "import androidx.compose.material.icons.Icons" "$file"; then
                    sed -i "/import androidx.compose.material.icons.Icons/a\\$required_import" "$file"
                fi
            fi
            
            # Replace the icon usage
            sed -i "s|$old_icon|$new_icon|g" "$file"
            modified=true
        fi
    done
    
    if [ "$modified" = true ]; then
        echo "Updated $file"
    fi
done

echo "Icon fixes completed!"
