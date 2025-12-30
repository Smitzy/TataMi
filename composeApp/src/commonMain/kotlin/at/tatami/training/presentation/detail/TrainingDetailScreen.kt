@file:OptIn(ExperimentalMaterial3Api::class)

package at.tatami.training.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.ConfirmationDialog
import at.tatami.training.presentation.detail.components.AttendanceBottomSheetContent
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Screen displaying training details with notes and attendance tracking.
 * Allows editing notes (for trainers/admins) and managing attendance with complex permissions.
 *
 * @param groupId The ID of the group this training belongs to
 * @param trainingId The ID of the training to display
 * @param onNavigateBack Callback to navigate back to the previous screen
 * @param viewModel ViewModel managing training detail state (injected with parameters)
 */
@Composable
fun TrainingDetailScreen(
    groupId: String,
    trainingId: String,
    onNavigateBack: () -> Unit,
    viewModel: TrainingDetailViewModel = koinViewModel { parametersOf(groupId, trainingId) }
) {
    val trainingDisplay = viewModel.trainingDisplay.collectAsState()
    val members = viewModel.members.collectAsState()
    val canEdit = viewModel.canEdit.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val error = viewModel.error.collectAsState()
    val isSaving = viewModel.isSaving.collectAsState()

    var localNotes by remember { mutableStateOf("") }
    var showSaveNotesButton by remember { mutableStateOf(false) }
    var showAttendanceSheet by remember { mutableStateOf(false) }
    var localAttendance by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Sync localNotes with training.notes when training loads or updates
    LaunchedEffect(trainingDisplay.value?.training?.notes) {
        trainingDisplay.value?.training?.notes?.let { notes ->
            localNotes = notes
            showSaveNotesButton = false
        }
    }

    // Sync localAttendance with training.attendedPersonIds when training loads or updates
    LaunchedEffect(trainingDisplay.value?.training?.attendedPersonIds) {
        trainingDisplay.value?.training?.attendedPersonIds?.let { ids ->
            localAttendance = ids.toSet()
        }
    }

    // Update showSaveNotesButton when localNotes changes
    LaunchedEffect(localNotes, trainingDisplay.value?.training?.notes) {
        showSaveNotesButton = localNotes != (trainingDisplay.value?.training?.notes ?: "")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trainingDisplay.value?.formattedDateTime ?: "Training Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Delete button (visible only for admins/trainers)
                    if (canEdit.value && trainingDisplay.value != null) {
                        IconButton(
                            onClick = { showDeleteConfirmation = true },
                            enabled = !isSaving.value
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Training",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading.value -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            trainingDisplay.value == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Training Not Found",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = "This training does not exist or has been deleted.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Notes Section
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = localNotes,
                        onValueChange = { localNotes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        enabled = canEdit.value && !isSaving.value,
                        placeholder = { Text("Add training notes...") },
                        maxLines = 10
                    )

                    // Save Notes Button (conditional)
                    if (showSaveNotesButton && canEdit.value) {
                        Button(
                            onClick = { viewModel.updateNotes(localNotes) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving.value
                        ) {
                            if (isSaving.value) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text("Save Notes")
                        }
                    }

                    // Attendance Section
                    Text(
                        text = "Attendance",
                        style = MaterialTheme.typography.titleMedium
                    )

                    val attendanceCount = trainingDisplay.value?.training?.attendedPersonIds?.size ?: 0
                    val totalMembers = members.value.size

                    Button(
                        onClick = { showAttendanceSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isSaving.value
                    ) {
                        Text("Show Attendance ($attendanceCount/$totalMembers)")
                    }

                    // Error Card
                    error.value?.let { errorMessage ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { viewModel.clearError() }) {
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Attendance Bottom Sheet
    if (showAttendanceSheet && trainingDisplay.value != null) {
        ModalBottomSheet(
            onDismissRequest = {
                // Save attendance if changed
                val currentAttendedIds = trainingDisplay.value?.training?.attendedPersonIds?.toSet() ?: emptySet()
                if (localAttendance != currentAttendedIds) {
                    viewModel.updateAttendance(localAttendance.toList())
                }
                showAttendanceSheet = false
            },
            sheetState = sheetState
        ) {
            AttendanceBottomSheetContent(
                members = members.value,
                attendedPersonIds = localAttendance,
                onToggleAttendance = { personId ->
                    localAttendance = if (localAttendance.contains(personId)) {
                        localAttendance - personId
                    } else {
                        localAttendance + personId
                    }
                },
                canToggleAttendance = { personId ->
                    viewModel.canToggleAttendance(personId)
                }
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        ConfirmationDialog(
            title = "Delete Training",
            message = "Are you sure you want to delete this training? This action cannot be undone.",
            confirmText = "Delete",
            dismissText = "Cancel",
            onConfirm = {
                coroutineScope.launch {
                    val success = viewModel.deleteTraining()
                    if (success) {
                        onNavigateBack()
                    }
                    showDeleteConfirmation = false
                }
            },
            onDismiss = {
                showDeleteConfirmation = false
            },
            isDestructive = true
        )
    }
}
