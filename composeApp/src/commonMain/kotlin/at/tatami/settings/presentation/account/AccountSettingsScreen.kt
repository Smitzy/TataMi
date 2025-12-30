package at.tatami.settings.presentation.account

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.tatami.common.ui.components.SignOutDialog
import at.tatami.settings.presentation.components.AccountManagementCard
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.*

/**
 * Account Settings screen for managing account and security options.
 *
 * This is a dedicated screen accessed from the main settings screen,
 * providing sign out functionality.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AccountSettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.account_settings)) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Management Card
            AccountManagementCard(
                onSignOut = { viewModel.showSignOutDialog() }
            )
        }

        // Sign Out Confirmation Dialog
        if (state.showSignOutDialog) {
            SignOutDialog(
                onConfirm = viewModel::signOut,
                onDismiss = viewModel::hideSignOutDialog
            )
        }
    }
}
