package com.example.rooster.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Rooster App Theme System
 * Implements Material 3 theming with earthy tones suitable for rural agricultural app
 * Supports light/dark themes with accessibility-first color choices
 */

// Light Theme ColorScheme - Warm and Natural
private val RoosterLightColorScheme =
    lightColorScheme(
        // Primary colors - Warm brown theme
        primary = RoosterBrown40,
        onPrimary = WarmNeutral95,
        primaryContainer = RoosterBrown80,
        onPrimaryContainer = RoosterBrown20,
        // Secondary colors - Natural green theme
        secondary = NaturalGreen40,
        onSecondary = WarmNeutral95,
        secondaryContainer = NaturalGreen80,
        onSecondaryContainer = NaturalGreen20,
        // Tertiary colors - Rooster red accent
        tertiary = RoosterRed40,
        onTertiary = WarmNeutral95,
        tertiaryContainer = RoosterRed80,
        onTertiaryContainer = RoosterRed20,
        // Error colors
        error = ErrorLight,
        onError = WarmNeutral95,
        errorContainer = ErrorDark,
        onErrorContainer = OnErrorLight,
        // Background and surface colors
        background = WarmNeutral95,
        onBackground = WarmNeutral10,
        surface = SurfaceLight,
        onSurface = WarmNeutral10,
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = WarmNeutral40,
        // Additional surface colors
        inverseSurface = WarmNeutral20,
        inverseOnSurface = WarmNeutral90,
        inversePrimary = RoosterBrown80,
        // Outline colors for borders and dividers
        outline = WarmNeutral60,
        outlineVariant = WarmNeutral80,
        // Surface container colors for elevated surfaces
        surfaceContainer = WarmNeutral90,
        surfaceContainerHigh = WarmNeutral80,
        surfaceContainerHighest = WarmNeutral80,
        surfaceContainerLow = WarmNeutral95,
        surfaceContainerLowest = WarmNeutral95,
        // Scrim for modal overlays
        scrim = WarmNeutral10.copy(alpha = 0.32f),
    )

// Dark Theme ColorScheme - Maintaining warmth in dark mode
private val RoosterDarkColorScheme =
    darkColorScheme(
        // Primary colors - Lighter browns for dark theme
        primary = RoosterBrown80,
        onPrimary = RoosterBrown20,
        primaryContainer = RoosterBrown20,
        onPrimaryContainer = RoosterBrown80,
        // Secondary colors - Lighter greens for dark theme
        secondary = NaturalGreen80,
        onSecondary = NaturalGreen20,
        secondaryContainer = NaturalGreen20,
        onSecondaryContainer = NaturalGreen80,
        // Tertiary colors - Lighter reds for dark theme
        tertiary = RoosterRed80,
        onTertiary = RoosterRed20,
        tertiaryContainer = RoosterRed20,
        onTertiaryContainer = RoosterRed80,
        // Error colors for dark theme
        error = ErrorDark,
        onError = OnErrorDark,
        errorContainer = OnErrorDark,
        onErrorContainer = ErrorDark,
        // Background and surface colors for dark theme
        background = WarmNeutral10,
        onBackground = WarmNeutral90,
        surface = SurfaceDark,
        onSurface = WarmNeutral90,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = WarmNeutral60,
        // Additional surface colors for dark theme
        inverseSurface = WarmNeutral90,
        inverseOnSurface = WarmNeutral20,
        inversePrimary = RoosterBrown40,
        // Outline colors for dark theme
        outline = WarmNeutral40,
        outlineVariant = WarmNeutral20,
        // Surface container colors for dark theme
        surfaceContainer = WarmNeutral20,
        surfaceContainerHigh = WarmNeutral20,
        surfaceContainerHighest = WarmNeutral40,
        surfaceContainerLow = WarmNeutral10,
        surfaceContainerLowest = WarmNeutral10,
        // Scrim for modal overlays in dark theme
        scrim = WarmNeutral10.copy(alpha = 0.8f),
    )

// Legacy color schemes for backward compatibility (deprecated)
@Deprecated(" Use RoosterLightColorScheme instead")
private val LightColorScheme = RoosterLightColorScheme

@Deprecated(" Use RoosterDarkColorScheme instead")
private val DarkColorScheme = RoosterDarkColorScheme

/**
 * Main theme composable for the Rooster App
 * Automatically adapts between light and dark themes
 * Supports dynamic color on Android 12+ devices
 */
@Composable
fun RoosterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+ but we prefer our custom scheme
    dynamicColor: Boolean = false, // Set to false to always use our custom colors
    contents: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            // Use dynamic colors on Android 12+ if enabled (usually disabled for brand consistency)
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            // Use our custom dark theme
            darkTheme -> RoosterDarkColorScheme
            // Use our custom light theme (default)
            else -> RoosterLightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RoosterTypography,
        shapes = RoosterShapes,
        content = contents,
    )
}

/**
 * Alternative theme composable that forces light theme
 * Useful for specific screens or components that should always use light theme
 */
@Composable
fun RoosterLightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RoosterLightColorScheme,
        typography = RoosterTypography,
        shapes = RoosterShapes,
        content = content,
    )
}

/**
 * Alternative theme composable that forces dark theme
 * Useful for specific screens or components that should always use dark theme
 */
@Composable
fun RoosterDarkTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = RoosterDarkColorScheme,
        typography = RoosterTypography,
        shapes = RoosterShapes,
        content = content,
    )
}
