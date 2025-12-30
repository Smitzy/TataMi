package at.tatami.club.presentation.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import at.tatami.common.ui.components.BottomBarNavigationFabs
import at.tatami.common.ui.components.TatamiTextField
import at.tatami.common.ui.components.TatamiLoadingOverlay
import at.tatami.common.util.InputFilters
import at.tatami.navigation.TatamiRoute
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*

/**
 * Screen for creating a club - collects only the club name.
 * After successful creation, navigates to photo upload screen.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateClubScreen(
    navController: NavController
) {
    val viewModel: CreateClubViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.navigationBarsPadding().imePadding(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomBarNavigationFabs(
                onLeftClick = {
                    focusManager.clearFocus()
                    navController.navigateUp()
                },
                rightText = stringResource(Res.string.save),
                rightIcon = Icons.Default.Save,
                onRightClick = {
                    focusManager.clearFocus()
                    viewModel.saveClub()
                },
                rightEnabled = state.canSave && !state.isLoading
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            // Title
            Text(
                text = stringResource(Res.string.create_club_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = stringResource(Res.string.basic_information),
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )

            Spacer(modifier = Modifier.height(38.dp))

            // Club Name Field
            TatamiTextField(
                value = state.clubName,
                onValueChange = viewModel::updateClubName,
                label = stringResource(Res.string.club_name_label),
                placeholder = "",
                errorMessage = state.validation.nameError,
                hideErrorIfEmpty = true,
                inputFilter = InputFilters.clubNameInputFilter(),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // General error message (from server)
            state.validation.generalError?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Loading overlay
        TatamiLoadingOverlay(visible = state.isLoading)
    }

    // Navigate to photo upload after successful creation
    LaunchedEffect(state.createdClubId) {
        state.createdClubId?.let { clubId ->
            viewModel.clearCreatedClubId()
            // Pop CreateClub from back stack so user can't go back to it
            navController.navigate(TatamiRoute.Main.ClubPhotoUpload(clubId)) {
                popUpTo(TatamiRoute.Main.CreateClub) { inclusive = true }
            }
        }
    }
}