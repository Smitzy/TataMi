package at.tatami.common.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat

@Composable
actual fun ThemeHandler(isDarkTheme: Boolean) {
    val context = LocalContext.current
    
    SideEffect {
        val activity = context as? Activity ?: return@SideEffect
        val window = activity.window
        
        // Control the appearance of system bar icons (light/dark)
        // This is the modern approach for Android 15+ (API 35+)
        // The system bars themselves are now transparent and handled by edge-to-edge
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = !isDarkTheme
        windowInsetsController.isAppearanceLightNavigationBars = !isDarkTheme
    }
}