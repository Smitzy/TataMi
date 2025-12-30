package at.tatami.settings.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.TatamiSettingsCard
import at.tatami.domain.model.settings.ThemeMode
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*

@Composable
fun ThemeSettingsCard(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit
) {
    TatamiSettingsCard(
        title = stringResource(Res.string.theme),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        // Theme Mode Selection
        ThemeModeOption(
            text = stringResource(Res.string.light_theme),
            selected = themeMode == ThemeMode.LIGHT,
            onClick = { onThemeModeChange(ThemeMode.LIGHT) }
        )

        ThemeModeOption(
            text = stringResource(Res.string.dark_theme),
            selected = themeMode == ThemeMode.DARK,
            onClick = { onThemeModeChange(ThemeMode.DARK) }
        )

        ThemeModeOption(
            text = stringResource(Res.string.system_theme),
            selected = themeMode == ThemeMode.SYSTEM,
            onClick = { onThemeModeChange(ThemeMode.SYSTEM) }
        )
    }
}