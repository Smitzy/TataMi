package at.tatami.event.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.ConfirmationDialog
import at.tatami.common.ui.components.expressive.FabMenuItem
import at.tatami.common.ui.components.expressive.TatamiFloatingActionButtonMenu
import at.tatami.domain.model.EventStatus
import at.tatami.event.presentation.create.components.CustomDatePickerDialog
import at.tatami.event.presentation.create.components.CustomTimePickerDialog
import at.tatami.event.presentation.detail.components.ParticipantStatusBottomSheetContent
import at.tatami.event.presentation.detail.components.EventStatusSelectionButtonGroup
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import kotlin.collections.get
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class, kotlin.time.ExperimentalTime::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    viewModel: EventDetailViewModel = koinViewModel()
) {
    val eventDisplay = viewModel.event.collectAsState()
    val participants = viewModel.participants.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val currentUserPersonId = viewModel.currentUserPersonId.collectAsState()
    val canCurrentUserRespond = viewModel.canCurrentUserRespond.collectAsState()
    val isAdmin = viewModel.isAdmin.collectAsState()
    val isSaving = viewModel.isSaving.collectAsState()
    val isDeleting = viewModel.isDeleting.collectAsState()
    val error = viewModel.error.collectAsState()

    // Track locally selected status - will be saved when user presses back
    val localSelectedStatus = remember { mutableStateOf<EventStatus?>(null) }

    // Bottom sheet state for participants overlay
    var showParticipantsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Edit dialog states
    var showEditTitleDialog by remember { mutableStateOf(false) }
    var showEditDescriptionDialog by remember { mutableStateOf(false) }
    var showEditLocationDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Edit form values
    var editTitleText by remember { mutableStateOf("") }
    var editDescriptionText by remember { mutableStateOf("") }
    var editLocationText by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }

    val coroutineScope = rememberCoroutineScope()

    // FAB menu items for event editing
    val fabMenuItems = listOf(
        FabMenuItem(
            id = "edit_title",
            icon = Icons.Default.Title,
            label = stringResource(Res.string.event_edit_name_label)
        ),
        FabMenuItem(
            id = "edit_when",
            icon = Icons.Default.AccessTime,
            label = stringResource(Res.string.event_edit_when_label)
        ),
        FabMenuItem(
            id = "edit_where",
            icon = Icons.Default.LocationOn,
            label = stringResource(Res.string.event_edit_where_label)
        ),
        FabMenuItem(
            id = "edit_details",
            icon = Icons.Default.Description,
            label = stringResource(Res.string.event_edit_details_label)
        ),
        FabMenuItem(
            id = "delete",
            icon = Icons.Default.Delete,
            label = stringResource(Res.string.delete)
        )
    )

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    fun handleNavigateBack() {
        // Save status change if user made a selection
        localSelectedStatus.value?.let {
            viewModel.updateStatus(eventId, it)
        }
        onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(eventDisplay.value?.event?.title ?: "Event Details") },
                navigationIcon = {
                    IconButton(onClick = { handleNavigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            // FAB menu for editing event (visible only for admins)
            if (isAdmin.value && eventDisplay.value != null) {
                TatamiFloatingActionButtonMenu(
                    visible = true,
                    icon = Icons.Default.Edit,
                    items = fabMenuItems,
                    onItemClick = { item ->
                        val currentEvent = eventDisplay.value?.event
                        when (item.id) {
                            "edit_title" -> {
                                editTitleText = currentEvent?.title ?: ""
                                showEditTitleDialog = true
                            }
                            "edit_when" -> {
                                // Initialize with current event time
                                currentEvent?.startDateTime?.let { dt ->
                                    selectedHour = dt.hour
                                    selectedMinute = dt.minute
                                }
                                showDatePicker = true
                            }
                            "edit_where" -> {
                                editLocationText = currentEvent?.location ?: ""
                                showEditLocationDialog = true
                            }
                            "edit_details" -> {
                                editDescriptionText = currentEvent?.description ?: ""
                                showEditDescriptionDialog = true
                            }
                            "delete" -> {
                                showDeleteConfirmation = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        if (eventDisplay.value != null) {
            val display = eventDisplay.value!!
            val currentEvent = display.event

            // Initialize local status with current event status on first load
            LaunchedEffect(currentEvent.status, currentUserPersonId.value) {
                if (localSelectedStatus.value == null && currentUserPersonId.value != null) {
                    localSelectedStatus.value = currentEvent.status[
                        currentUserPersonId.value
                    ] ?: EventStatus.NO_RESPONSE
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Admin viewing notice (if not invited)
                if (!canCurrentUserRespond.value) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = stringResource(Res.string.event_admin_view_title),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = stringResource(Res.string.event_admin_view_description),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                // Date and Time
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
                            text = stringResource(Res.string.event_when_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = display.formattedDateTime,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Location
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
                            text = stringResource(Res.string.event_where_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currentEvent.location,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Description
                if (currentEvent.description.isNotEmpty()) {
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
                                text = stringResource(Res.string.event_details_label),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = currentEvent.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Your Response - Only show if user is invited
                if (canCurrentUserRespond.value) {
                    Text(
                        text = stringResource(Res.string.event_your_response_label),
                        style = MaterialTheme.typography.titleSmall
                    )

                    EventStatusSelectionButtonGroup(
                        selectedStatus = localSelectedStatus.value ?: EventStatus.NO_RESPONSE,
                        onStatusSelected = { status ->
                            // Update local state only - will be persisted when user presses back
                            localSelectedStatus.value = status
                        },
                        enabled = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // View Participants
                Button(
                    onClick = { showParticipantsSheet = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.event_view_responses_button, currentEvent.invitedPersonIds.size))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(Res.string.event_not_found))
            }
        }
    }

    // Participant Status Bottom Sheet Overlay
    if (showParticipantsSheet && eventDisplay.value != null) {
        ModalBottomSheet(
            onDismissRequest = { showParticipantsSheet = false },
            sheetState = sheetState
        ) {
            ParticipantStatusBottomSheetContent(
                participants = participants.value.values.toList()
            )
        }
    }

    // Edit Title Dialog
    if (showEditTitleDialog) {
        AlertDialog(
            onDismissRequest = { showEditTitleDialog = false },
            title = { Text(stringResource(Res.string.event_edit_name_label)) },
            text = {
                OutlinedTextField(
                    value = editTitleText,
                    onValueChange = { editTitleText = it },
                    label = { Text(stringResource(Res.string.event_name_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editTitleText.isNotBlank()) {
                            viewModel.updateTitle(eventId, editTitleText)
                            showEditTitleDialog = false
                        }
                    },
                    enabled = editTitleText.isNotBlank() && editTitleText != eventDisplay.value?.event?.title
                ) {
                    Text(stringResource(Res.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditTitleDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    // Edit Description Dialog
    if (showEditDescriptionDialog) {
        AlertDialog(
            onDismissRequest = { showEditDescriptionDialog = false },
            title = { Text(stringResource(Res.string.event_edit_details_label)) },
            text = {
                OutlinedTextField(
                    value = editDescriptionText,
                    onValueChange = { editDescriptionText = it },
                    label = { Text(stringResource(Res.string.event_description_label)) },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateDescription(eventId, editDescriptionText)
                        showEditDescriptionDialog = false
                    },
                    enabled = editDescriptionText != eventDisplay.value?.event?.description
                ) {
                    Text(stringResource(Res.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDescriptionDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    // Edit Location Dialog
    if (showEditLocationDialog) {
        AlertDialog(
            onDismissRequest = { showEditLocationDialog = false },
            title = { Text(stringResource(Res.string.event_edit_where_label)) },
            text = {
                OutlinedTextField(
                    value = editLocationText,
                    onValueChange = { editLocationText = it },
                    label = { Text(stringResource(Res.string.event_location_label)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editLocationText.isNotBlank()) {
                            viewModel.updateLocation(eventId, editLocationText)
                            showEditLocationDialog = false
                        }
                    },
                    enabled = editLocationText.isNotBlank() && editLocationText != eventDisplay.value?.event?.location
                ) {
                    Text(stringResource(Res.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditLocationDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

    // Date Picker Dialog
    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { millis ->
                selectedDate = millis
                showDatePicker = false
                showTimePicker = true
            }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        CustomTimePickerDialog(
            onDismiss = {
                showTimePicker = false
                selectedDate = null
            },
            onTimeSelected = { hour, minute ->
                selectedDate?.let { dateMillis ->
                    // Convert selected date and time to LocalDateTime
                    val instant = Instant.fromEpochMilliseconds(dateMillis)
                    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val newDateTime = LocalDateTime(
                        year = localDate.year,
                        monthNumber = localDate.monthNumber,
                        dayOfMonth = localDate.dayOfMonth,
                        hour = hour,
                        minute = minute
                    )
                    viewModel.updateStartDateTime(eventId, newDateTime)
                }
                showTimePicker = false
                selectedDate = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmation) {
        val eventTitle = eventDisplay.value?.event?.title ?: "this event"
        ConfirmationDialog(
            title = stringResource(Res.string.event_delete_title),
            message = stringResource(Res.string.event_delete_confirmation, eventTitle),
            confirmText = stringResource(Res.string.delete),
            dismissText = stringResource(Res.string.cancel),
            onConfirm = {
                coroutineScope.launch {
                    val success = viewModel.deleteEvent(eventId)
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