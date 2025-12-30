@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, ExperimentalTime::class)

package at.tatami.group.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.ConfirmationDialog
import at.tatami.common.ui.components.expressive.FabMenuItem
import at.tatami.common.ui.components.expressive.TatamiFloatingActionButtonMenu
import at.tatami.group.presentation.detail.components.AttendanceStatisticsBottomSheetContent
import at.tatami.group.presentation.detail.components.ManageMembersBottomSheetContent
import at.tatami.group.presentation.detail.components.ManageTrainersBottomSheetContent
import at.tatami.group.presentation.detail.components.MemberListBottomSheetContent
import kotlinx.coroutines.launch
import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.koin.compose.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

@Composable
fun GroupDetailScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    onNavigateToTrainingList: (String) -> Unit,
    viewModel: GroupDetailViewModel = koinViewModel()
) {
    val group = viewModel.group.collectAsState()
    val members = viewModel.members.collectAsState()
    val clubMembers = viewModel.clubMembers.collectAsState()
    val groupMembersForTrainers = viewModel.groupMembersForTrainers.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val isClubMembersLoading = viewModel.isClubMembersLoading.collectAsState()
    val canViewGroup = viewModel.canViewGroup.collectAsState()
    val canDeleteGroup = viewModel.canDeleteGroup.collectAsState()
    val canManageGroup = viewModel.canManageGroup.collectAsState()
    val isDeleting = viewModel.isDeleting.collectAsState()
    val isSaving = viewModel.isSaving.collectAsState()
    val error = viewModel.error.collectAsState()

    var showMembersSheet by remember { mutableStateOf(false) }
    var showManageMembersSheet by remember { mutableStateOf(false) }
    var showManageTrainersSheet by remember { mutableStateOf(false) }
    var showStatisticsSheet by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var renameText by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val manageMembersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val manageTrainersSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val statisticsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val attendanceStats = viewModel.attendanceStats.collectAsState()

    // FAB menu items for group management
    val fabMenuItems = listOf(
        FabMenuItem(
            id = "statistics",
            icon = Icons.Default.Analytics,
            label = stringResource(Res.string.group_statistics_label)
        ),
        FabMenuItem(
            id = "change_name",
            icon = Icons.Default.Edit,
            label = stringResource(Res.string.group_change_name_label)
        ),
        FabMenuItem(
            id = "manage_members",
            icon = Icons.Default.People,
            label = stringResource(Res.string.group_manage_members_label)
        ),
        FabMenuItem(
            id = "manage_trainers",
            icon = Icons.Default.School,
            label = stringResource(Res.string.group_manage_trainers_label)
        )
    )

    LaunchedEffect(groupId) {
        viewModel.loadGroup(groupId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group.value?.name ?: "Group Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Delete button (visible only for admins)
                    if (canDeleteGroup.value && group.value != null) {
                        IconButton(
                            onClick = { showDeleteConfirmation = true },
                            enabled = !isDeleting.value
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Group",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            // FAB menu for managing members/trainers (visible only for admins and trainers)
            if (canManageGroup.value && group.value != null) {
                TatamiFloatingActionButtonMenu(
                    visible = true,
                    icon = Icons.Default.Edit,
                    items = fabMenuItems,
                    onItemClick = { item ->
                        when (item.id) {
                            "statistics" -> {
                                viewModel.loadAttendanceStatistics()
                                showStatisticsSheet = true
                            }
                            "change_name" -> {
                                renameText = group.value?.name ?: ""
                                showRenameDialog = true
                            }
                            "manage_members" -> {
                                viewModel.loadClubMembers()
                                showManageMembersSheet = true
                            }
                            "manage_trainers" -> {
                                viewModel.loadGroupMembersForTrainers()
                                showManageTrainersSheet = true
                            }
                        }
                    }
                )
            }
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
            !canViewGroup.value -> {
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
                            text = stringResource(Res.string.group_access_denied_title),
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = stringResource(Res.string.group_access_denied_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            group.value != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Member count card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.group_members_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(Res.string.group_member_trainer_count, group.value!!.memberIds.size, group.value!!.trainerIds.size),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Show Members button
                    Button(
                        onClick = { showMembersSheet = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.group_show_members_button, group.value!!.memberIds.size))
                    }

                    // Show Trainings button
                    OutlinedButton(
                        onClick = { onNavigateToTrainingList(groupId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.group_show_trainings_button))
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(Res.string.group_not_found))
                }
            }
        }
    }

    // Members Bottom Sheet
    if (showMembersSheet && group.value != null) {
        ModalBottomSheet(
            onDismissRequest = { showMembersSheet = false },
            sheetState = sheetState
        ) {
            MemberListBottomSheetContent(
                members = members.value
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        val groupName = group.value?.name ?: "this group"
        ConfirmationDialog(
            title = stringResource(Res.string.group_delete_title),
            message = stringResource(Res.string.group_delete_confirmation, groupName),
            confirmText = stringResource(Res.string.delete),
            dismissText = stringResource(Res.string.cancel),
            onConfirm = {
                coroutineScope.launch {
                    val success = viewModel.deleteGroup()
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

    // Manage Members Bottom Sheet
    if (showManageMembersSheet && group.value != null) {
        ModalBottomSheet(
            onDismissRequest = { showManageMembersSheet = false },
            sheetState = manageMembersSheetState
        ) {
            if (isClubMembersLoading.value) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ManageMembersBottomSheetContent(
                    clubMembers = clubMembers.value,
                    onToggleMembership = { personId ->
                        viewModel.toggleMembership(personId)
                    }
                )
            }
        }
    }

    // Manage Trainers Bottom Sheet
    if (showManageTrainersSheet && group.value != null) {
        ModalBottomSheet(
            onDismissRequest = { showManageTrainersSheet = false },
            sheetState = manageTrainersSheetState
        ) {
            ManageTrainersBottomSheetContent(
                groupMembers = groupMembersForTrainers.value,
                onToggleTrainer = { personId, isTrainer ->
                    viewModel.toggleTrainerStatus(personId, isTrainer)
                }
            )
        }
    }

    // Rename Group Dialog
    if (showRenameDialog) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text(stringResource(Res.string.group_change_name_title)) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text(stringResource(Res.string.group_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (renameText.isNotBlank()) {
                            viewModel.updateGroupName(renameText)
                            showRenameDialog = false
                        }
                    },
                    enabled = renameText.isNotBlank() && renameText != group.value?.name
                ) {
                    Text(stringResource(Res.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    // Statistics Bottom Sheet
    if (showStatisticsSheet && group.value != null) {
        ModalBottomSheet(
            onDismissRequest = { showStatisticsSheet = false },
            sheetState = statisticsSheetState
        ) {
            AttendanceStatisticsBottomSheetContent(
                stats = attendanceStats.value.memberStats,
                totalTrainings = attendanceStats.value.totalTrainings,
                formattedCutoffDate = attendanceStats.value.formattedCutoffDate,
                isLoading = attendanceStats.value.isLoading,
                onSelectDate = { showDatePickerDialog = true }
            )
        }
    }

    // Date Picker Dialog for Statistics
    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val localDate = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTimeInSystemTimeZone()
                                .date
                            viewModel.updateStatisticsCutoffDate(localDate)
                        }
                        showDatePickerDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}