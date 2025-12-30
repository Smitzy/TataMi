package at.tatami.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.UIKit.UIApplication
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIWindow

@Composable
actual fun ThemeHandler(isDarkTheme: Boolean) {
    SideEffect {
        // Get the key window from UIApplication
        // Using windows.firstOrNull() as keyWindow is deprecated in iOS 13+
        val window = UIApplication.sharedApplication.windows.firstOrNull() as? UIWindow

        if (window != null) {
            // Set the interface style for the entire window
            // This affects status bar, safe areas, and all system UI
            window.overrideUserInterfaceStyle = when {
                isDarkTheme -> UIUserInterfaceStyle.UIUserInterfaceStyleDark
                else -> UIUserInterfaceStyle.UIUserInterfaceStyleLight
            }
        }
    }
}