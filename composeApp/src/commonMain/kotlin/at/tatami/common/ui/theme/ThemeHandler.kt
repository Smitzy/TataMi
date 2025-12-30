package at.tatami.common.ui.theme

import androidx.compose.runtime.Composable

/**
 * Platform-specific theme handler for setting window/system UI colors
 */
@Composable
expect fun ThemeHandler(isDarkTheme: Boolean)