package at.tatami.settings.presentation.system

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.tatami.settings.presentation.components.DateTimeFormatSettingsCard
import at.tatami.settings.presentation.components.ThemeSettingsCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.*

/**
 * System Settings screen displaying theme and date/time format preferences.
 *
 * This is a dedicated screen accessed from the main settings screen,
 * providing a focused view for system-level customization options.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SystemSettingsViewModel = koinViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val timeFormat by viewModel.timeFormat.collectAsStateWithLifecycle()
    val dateFormat by viewModel.dateFormat.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.system_settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Settings
            ThemeSettingsCard(
                themeMode = themeMode,
                onThemeModeChange = viewModel::setThemeMode
            )

            // Date & Time Format Settings
            DateTimeFormatSettingsCard(
                timeFormat = timeFormat,
                dateFormat = dateFormat,
                onTimeFormatChange = viewModel::setTimeFormat,
                onDateFormatChange = viewModel::setDateFormat
            )
        }
    }
}
