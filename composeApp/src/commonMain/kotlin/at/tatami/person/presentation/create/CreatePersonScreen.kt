package at.tatami.person.presentation.create

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import at.tatami.common.ui.components.BottomBarNavigationFabs
import at.tatami.common.ui.components.TatamiTextField
import at.tatami.common.ui.components.TatamiLoadingOverlay
import at.tatami.common.util.InputFilters
import at.tatami.navigation.TatamiRoute
import at.tatami.person.presentation.create.components.SexSelectionButtonGroup
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*

/**
 * Screen for creating a person - collects all 4 required fields:
 * - First Name
 * - Last Name
 * - Year of Birth
 * - Sex (MALE, FEMALE, OTHER)
 *
 * After successful creation, navigates to photo upload screen.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreatePersonScreen(
    navController: NavController
) {
    val viewModel: CreatePersonViewModel = koinViewModel()
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
                    viewModel.savePerson()
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
                text = stringResource(Res.string.person_create_title),
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

            // First Name Field
            TatamiTextField(
                value = state.firstName,
                onValueChange = viewModel::updateFirstName,
                label = stringResource(Res.string.first_name),
                errorMessage = state.validation.firstNameError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                hideErrorIfEmpty = true,
                inputFilter = InputFilters.personFirstNameInputFilter(),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Last Name Field
            TatamiTextField(
                value = state.lastName,
                onValueChange = viewModel::updateLastName,
                label = stringResource(Res.string.last_name),
                errorMessage = state.validation.lastNameError,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                hideErrorIfEmpty = true,
                inputFilter = InputFilters.personLastNameInputFilter(),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Year of Birth Field
            TatamiTextField(
                value = state.yearOfBirthText,
                onValueChange = viewModel::updateYearOfBirthText,
                label = stringResource(Res.string.year_of_birth),
                errorMessage = state.validation.yearOfBirthError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                hideErrorIfEmpty = true,
                inputFilter = InputFilters.digitOnlyFilter(4),
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sex Selection ButtonGroup
            SexSelectionButtonGroup(
                selectedSex = state.sex,
                onSexSelected = { sex ->
                    if (!state.isLoading) {
                        viewModel.updateSex(sex)
                    }
                },
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Show sex validation error if present
            state.validation.sexError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            }
        }

        // Loading overlay
        TatamiLoadingOverlay(
            visible = state.isLoading,
            message = stringResource(Res.string.creating_person),
            paddingValues = paddingValues
        )
    }

    // Handle navigation to photo upload screen when person is created
    LaunchedEffect(state.createdPersonId) {
        state.createdPersonId?.let { personId ->
            viewModel.clearCreatedPersonId()
            // Pop CreatePerson from back stack so user can't go back to it
            navController.navigate(TatamiRoute.Main.PersonPhotoUpload(personId)) {
                popUpTo(TatamiRoute.Main.CreatePerson) { inclusive = true }
            }
        }
    }
}