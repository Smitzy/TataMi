package at.tatami.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TatamiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        ColorSchemes.darkScheme
    } else {
        ColorSchemes.lightScheme
    }
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        typography = TatamiTypography,
        content = content
    )
}