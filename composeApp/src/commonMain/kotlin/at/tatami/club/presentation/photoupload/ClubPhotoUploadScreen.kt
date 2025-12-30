package at.tatami.club.presentation.photoupload

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.tatami.common.ui.components.ProfileImageDisplay
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import at.tatami.common.ui.components.ImagePickerHandler
import at.tatami.common.ui.components.TatamiButton
import at.tatami.common.ui.components.ErrorMessage
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*

/**
 * Photo upload screen for uploading a club's profile picture.
 * Can be used for both creating and editing a club.
 * Receives clubId as a parameter and loads the club independently.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ClubPhotoUploadScreen(
    navController: NavController,
    clubId: String
) {
    val viewModel: ClubPhotoUploadViewModel = koinViewModel(
        parameters = { parametersOf(clubId) }
    )
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showImagePicker by remember { mutableStateOf(false) }

    // Handle back button press - skip photo upload
    BackHandler(enabled = true) {
        viewModel.skipPhotoUpload()
    }

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
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
                text = stringResource(Res.string.add_profile_picture_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle with club's name
            state.club?.let { club ->
                Text(
                    text = "Helps members recognize ${club.name}.",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
            }

            Spacer(modifier = Modifier.height(38.dp))

            // Profile image preview
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ProfileImageDisplay(
                    imageData = state.selectedProfileImage,
                    onClick = { if (!state.isLoading) showImagePicker = true },
                    size = 200.dp,
                    showCameraOverlay = state.selectedProfileImage == null,
                    showLabel = false
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Show error message if present
            state.errorMessage?.let { error ->
                ErrorMessage(
                    message = error,
                    onRetry = if (state.selectedProfileImage != null) {
                        { viewModel.saveProfileImage() }
                    } else null
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Action buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.selectedProfileImage != null) {
                    // Show Save and Change buttons when image is selected
                    TatamiButton(
                        onClick = {
                            viewModel.saveProfileImage()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        loading = state.isLoading
                    ) {
                        Text(stringResource(Res.string.save_profile_picture))
                    }

                    TextButton(
                        onClick = { showImagePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(Res.string.change_photo))
                    }

                    TextButton(
                        onClick = viewModel::skipPhotoUpload,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(Res.string.skip_for_now))
                    }
                } else {
                    // Show Add Photo button when no image is selected
                    TatamiButton(
                        onClick = { showImagePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(Res.string.add_photo))
                    }

                    TextButton(
                        onClick = viewModel::skipPhotoUpload,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    ) {
                        Text(stringResource(Res.string.skip_for_now))
                    }
                }
            }
        }
    }

    // Image picker
    if (showImagePicker) {
        ImagePickerHandler(
            currentImage = state.selectedProfileImage,
            onImageSelected = { imageData ->
                viewModel.onProfileImageSelected(imageData)
                showImagePicker = false
            },
            onDismiss = { showImagePicker = false }
        )
    }

    // Navigate back after successful upload or skip
    LaunchedEffect(state.uploadSuccess) {
        if (state.uploadSuccess) {
            navController.popBackStack()
        }
    }
}