package at.tatami.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.tatami.common.ui.components.SettingsCategoryCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.*

/**
 * Main Settings screen showing category cards for navigation.
 *
 * Provides access to:
 * - Club Administration (admin-only)
 * - System Settings (theme, date/time formats)
 * - Account Settings (sign out, delete account)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToClubSettings: () -> Unit,
    onNavigateToSystemSettings: () -> Unit,
    onNavigateToAccountSettings: () -> Unit,
    onNavigateToComponentPlayground: () -> Unit = {},
    viewModel: SettingsViewModel = koinViewModel()
) {
    val isClubAdmin by viewModel.isClubAdmin.collectAsStateWithLifecycle()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings)) },
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
            // Club Administration (admin-only, priority first)
            if (isClubAdmin) {
                SettingsCategoryCard(
                    title = stringResource(Res.string.club_administration),
                    subtitle = stringResource(Res.string.club_administration_description),
                    icon = Icons.Default.Groups,
                    onClick = onNavigateToClubSettings
                )
            }

            // System Settings
            SettingsCategoryCard(
                title = stringResource(Res.string.system_settings),
                subtitle = stringResource(Res.string.system_settings_description),
                icon = Icons.Default.Settings,
                onClick = onNavigateToSystemSettings
            )

            // Account Settings
            SettingsCategoryCard(
                title = stringResource(Res.string.account_settings),
                subtitle = stringResource(Res.string.account_settings_description),
                icon = Icons.Default.AccountCircle,
                onClick = onNavigateToAccountSettings
            )

            // Component Playground (Debug/Developer section)
            //SettingsCategoryCard(
            //    title = "Component Playground",
            //    subtitle = "Test Material 3 Expressive UI components",
            //    icon = Icons.Default.Palette,
            //    onClick = onNavigateToComponentPlayground
            //)
        }
    }
}