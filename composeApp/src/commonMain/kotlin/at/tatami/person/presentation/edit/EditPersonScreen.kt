package at.tatami.person.presentation.edit

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
import at.tatami.common.ui.components.BottomBarNavigationFabs
import at.tatami.common.ui.components.ErrorMessage
import at.tatami.common.ui.components.ImagePickerHandler
import at.tatami.common.ui.components.ProfileImageDisplay
import at.tatami.common.ui.components.TatamiLoadingIndicator
import at.tatami.common.ui.components.TatamiLoadingOverlay
import at.tatami.common.ui.components.TatamiTextField
import at.tatami.common.util.InputFilters
import at.tatami.person.presentation.create.components.SexSelectionButtonGroup
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import tatami.composeapp.generated.resources.*

/**
 * Screen for editing a person's information.
 * Allows editing: firstName, lastName, yearOfBirth, sex, and profile photo.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditPersonScreen(
    navController: NavController,
    personId: String
) {
    val viewModel: EditPersonViewModel = koinViewModel(
        parameters = { parametersOf(personId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    var showImagePicker by remember { mutableStateOf(false) }

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
                rightEnabled = state.canSave && !state.isSaving
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        TatamiLoadingIndicator()
                    }
                }
                state.person == null && state.errorMessage != null -> {
                    ErrorMessage(
                        message = state.errorMessage ?: stringResource(Res.string.unknown_error),
                        onRetry = null
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                            .padding(top = 32.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start
                    ) {
                        // Title
                        Text(
                            text = stringResource(Res.string.person_edit_title),
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

                        Spacer(modifier = Modifier.height(24.dp))

                        // Profile Image
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            ProfileImageDisplay(
                                imageData = state.selectedProfileImage,
                                imageUrl = if (!state.hasImageChanged) state.person?.personImgUrl else null,
                                onClick = { if (!state.isSaving) showImagePicker = true },
                                size = 120.dp,
                                showCameraOverlay = true,
                                showLabel = false,
                                enabled = !state.isSaving
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

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
                            enabled = !state.isSaving
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
                            enabled = !state.isSaving
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
                            enabled = !state.isSaving
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sex Selection ButtonGroup
                        SexSelectionButtonGroup(
                            selectedSex = state.sex,
                            onSexSelected = { sex ->
                                if (!state.isSaving) {
                                    viewModel.updateSex(sex)
                                }
                            },
                            enabled = !state.isSaving,
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

                        // Show general error if present (while form is visible)
                        state.errorMessage?.let { error ->
                            if (state.person != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                ErrorMessage(
                                    message = error,
                                    onRetry = { viewModel.clearError() }
                                )
                            }
                        }

                        // Bottom spacer for scroll
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            // Loading overlay while saving
            TatamiLoadingOverlay(
                visible = state.isSaving,
                message = stringResource(Res.string.saving),
                paddingValues = paddingValues
            )
        }
    }

    // Image picker dialog
    if (showImagePicker) {
        ImagePickerHandler(
            currentImage = state.selectedProfileImage,
            onImageSelected = { imageData ->
                viewModel.onProfileImageSelected(imageData)
            },
            onDismiss = { showImagePicker = false },
            showRemoveOption = state.selectedProfileImage != null || state.person?.personImgUrl != null
        )
    }

    // Handle save success - navigate back
    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            navController.popBackStack()
        }
    }
}
