@file:OptIn(kotlin.time.ExperimentalTime::class)

package at.tatami.settings.presentation.club

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import at.tatami.common.ui.components.ConfirmationDialog
import at.tatami.common.ui.components.TextInputDialog
import at.tatami.common.util.InputFilters
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.tatami.domain.model.Club
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ClubSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToClubList: () -> Unit = {},
    viewModel: ClubSettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Handle successful deletion - navigate to club list
    LaunchedEffect(state.deleteSuccess) {
        if (state.deleteSuccess) {
            onNavigateToClubList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.club_settings_title)) },
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
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            !state.isAdmin -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.not_admin_message),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            state.currentClub == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_club_selected),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    state.currentClub?.let { club ->
                        // Club Info Card
                        ClubInfoCard(
                            club = club,
                            isOwner = state.isOwner,
                            onEditClick = { viewModel.startEditClubName() }
                        )

                        // Invite Code Card
                        ClubInviteCodeCard(
                            club = club,
                            formattedExpirationDate = state.formattedExpirationDate,
                            isGeneratingCode = state.isGeneratingCode,
                            isRemovingCode = state.isRemovingCode,
                            onGenerateCode = { viewModel.generateInviteCode() },
                            onRemoveCode = { viewModel.removeInviteCode() },
                            onCopyCode = { code ->
                                clipboardManager.setText(AnnotatedString(code))
                            }
                        )

                        // Error message
                        state.codeActionError?.let { error ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = { viewModel.clearCodeError() }) {
                                        Text(stringResource(Res.string.dismiss))
                                    }
                                }
                            }
                        }

                        // Delete Club Card (Owner only)
                        if (state.isOwner) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.danger_zone),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )

                                    Text(
                                        text = stringResource(Res.string.delete_club_warning),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = { showDeleteConfirmation = true },
                                        enabled = !state.isDeleting,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        )
                                    ) {
                                        if (state.isDeleting) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp,
                                                color = MaterialTheme.colorScheme.onError
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                        }
                                        Text(stringResource(Res.string.delete_club))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Club Confirmation Dialog
    if (showDeleteConfirmation) {
        val clubName = state.currentClub?.name ?: ""
        ConfirmationDialog(
            title = stringResource(Res.string.delete_club_title),
            message = stringResource(Res.string.delete_club_confirmation, clubName),
            confirmText = stringResource(Res.string.delete),
            dismissText = stringResource(Res.string.cancel),
            onConfirm = {
                viewModel.deleteClub()
                showDeleteConfirmation = false
            },
            onDismiss = {
                showDeleteConfirmation = false
            },
            isDestructive = true
        )
    }

    // Edit Club Name Dialog
    if (state.isEditingClubName) {
        TextInputDialog(
            title = stringResource(Res.string.edit_club_name_title),
            label = stringResource(Res.string.club_name_label),
            initialValue = state.editingClubNameValue,
            placeholder = stringResource(Res.string.club_name_placeholder),
            confirmText = stringResource(Res.string.save),
            dismissText = stringResource(Res.string.cancel),
            onConfirm = { newName ->
                viewModel.updateEditingClubName(newName)
                viewModel.saveClubName()
            },
            onDismiss = { viewModel.cancelEditClubName() },
            inputFilter = InputFilters.clubNameInputFilter(),
            errorMessage = state.editingClubNameError,
            isLoading = state.isUpdatingClubName
        )
    }
}

@Composable
private fun ClubInfoCard(
    club: Club,
    isOwner: Boolean = false,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = club.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (isOwner) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(Res.string.edit_club_name),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ClubInviteCodeCard(
    club: Club,
    formattedExpirationDate: String?,
    isGeneratingCode: Boolean,
    isRemovingCode: Boolean,
    onGenerateCode: () -> Unit,
    onRemoveCode: () -> Unit,
    onCopyCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = Clock.System.now()
    val hasActiveCode = club.inviteCode.isNotEmpty() &&
                       club.inviteCodeExpiresAt?.let { it > now } ?: false

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.invite_code_title),
                style = MaterialTheme.typography.titleMedium
            )

            if (hasActiveCode) {
                // Active code display
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Code display with copy button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = club.inviteCode,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = { onCopyCode(club.inviteCode) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(Res.string.copy_code),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Formatted expiration date
                    formattedExpirationDate?.let { formattedDate ->
                        Text(
                            text = stringResource(Res.string.valid_until, formattedDate),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                // Action buttons for active code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onGenerateCode,
                        enabled = !isGeneratingCode && !isRemovingCode,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isGeneratingCode) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(Res.string.generate_new_code))
                        }
                    }

                    OutlinedButton(
                        onClick = onRemoveCode,
                        enabled = !isGeneratingCode && !isRemovingCode,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        if (isRemovingCode) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(stringResource(Res.string.remove_code))
                        }
                    }
                }
            } else {
                // No active code
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.no_active_code),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = stringResource(Res.string.no_active_code_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = onGenerateCode,
                        enabled = !isGeneratingCode,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isGeneratingCode) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(stringResource(Res.string.generate_invite_code))
                    }
                }
            }
        }
    }
}