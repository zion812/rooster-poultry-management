package com.example.rooster.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Rooster App Shape System
 * Defines consistent corner radii and shapes throughout the app
 * Optimized for rural-friendly UI with appropriate touch targets
 */

val RoosterShapes =
    Shapes(
        // Extra Small - Used for small buttons, chips, and indicators
        extraSmall = RoundedCornerShape(4.dp),
        // Small - Used for buttons, text fields, and small cards
        small = RoundedCornerShape(8.dp),
        // Medium - Used for cards, dialogs, and main content containers
        medium = RoundedCornerShape(12.dp),
        // Large - Used for bottom sheets, large cards, and prominent containers
        large = RoundedCornerShape(16.dp),
        // Extra Large - Used for screens, full-screen components
        extraLarge = RoundedCornerShape(24.dp),
    )

// Custom shapes for specific app components
object RoosterCustomShapes {
    // Navigation bar with top-only rounding
    val NavigationBarShape =
        RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        )

    // Bottom sheet with top-only rounding
    val BottomSheetShape =
        RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp,
        )

    // Card with subtle rounding for rural-friendly design
    val CardShape = RoundedCornerShape(12.dp)

    // Button with comfortable rounding for touch targets
    val ButtonShape = RoundedCornerShape(8.dp)

    // Text field with minimal rounding for clear boundaries
    val TextFieldShape = RoundedCornerShape(6.dp)

    // Floating action button - circular for clear action indication
    val FabShape = RoundedCornerShape(16.dp)

    // Chip shape for tags and filters
    val ChipShape = RoundedCornerShape(8.dp)

    // Alert dialog with moderate rounding
    val DialogShape = RoundedCornerShape(16.dp)

    // Image container with subtle rounding
    val ImageShape = RoundedCornerShape(8.dp)

    // Profile image - circular or rounded square
    val ProfileImageShape = RoundedCornerShape(50) // Can be overridden for circular
}
