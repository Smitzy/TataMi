package at.tatami.common.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

/**
 * Color schemes for the app theme
 */
object ColorSchemes {
    
    // Light color scheme
    val lightScheme = lightColorScheme(

        // Primary roles are for important actions and elements
        // needing the most emphasis, like a FAB to start a new message.
        primary = primaryLight, // High-emphasis fills, texts, and icons against surface
        onPrimary = onPrimaryLight, // Text and icons against primary
        primaryContainer = primaryContainerLight, // Standout fill color against surface, for key components like FAB
        onPrimaryContainer = onPrimaryContainerLight, // Text and icons against primary container

        // Secondary roles are for elements that don’t need immediate attention and don’t
        // need emphasis, like the selected state of a navigation icon or a dismissive button.
        secondary = secondaryLight, // Less prominent fills, text, and icons against surface
        onSecondary = onSecondaryLight, // Text and icons against secondary
        secondaryContainer = secondaryContainerLight, // Less prominent fill color against surface, for recessive components like tonal buttons
        onSecondaryContainer = onSecondaryContainerLight, // Text and icons against secondary container

        // Tertiary roles are for smaller elements that need special emphasis but don't
        // require immediate attention, such as a badge or notification.
        tertiary = tertiaryLight, // Complementary fills, text, and icons against surface
        onTertiary = onTertiaryLight, // Text and icons against tertiary
        tertiaryContainer = tertiaryContainerLight, // Complementary container color against surface, for components like input fields
        onTertiaryContainer = onTertiaryContainerLight, // Text and icons against tertiary container

        error = errorLight, // Attention-grabbing color against surface for fills, icons, and text, indicating urgency
        onError = onErrorLight, // Text and icons against error
        errorContainer = errorContainerLight, // Attention-grabbing fill color against surface
        onErrorContainer = onErrorContainerLight, // Text and icons against error container

        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight, // Default color for backgrounds
        onSurface = onSurfaceLight, // Text and icons against any surface or surface container color
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight, // Lower-emphasis color for text and icons against any surface or surface container color
        outline = outlineLight, // Important boundaries, such as a text field outline
        outlineVariant = outlineVariantLight, // Decorative elements, such as dividers, and when other elements provide 4.5:1 contrast
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight, // Background fills for elements which contrast against surface
        inverseOnSurface = inverseOnSurfaceLight, // Text and icons against inverse surface
        inversePrimary = inversePrimaryLight, // Actionable elements, such as text buttons, against inverse surface
        surfaceDim = surfaceDimLight, // Dimmest surface color in light and dark themes
        surfaceBright = surfaceBrightLight, // Brightest surface color in light and dark themes
        surfaceContainerLowest = surfaceContainerLowestLight, // Lowest-emphasis container color
        surfaceContainerLow = surfaceContainerLowLight, // Low-emphasis container color
        surfaceContainer = surfaceContainerLight, // Default container color
        surfaceContainerHigh = surfaceContainerHighLight, // High-emphasis container color
        surfaceContainerHighest = surfaceContainerHighestLight, // Highest-emphasis container color
    )
    
    // Dark color scheme
    val darkScheme = darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )
}