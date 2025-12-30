@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package at.tatami.event.presentation.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import at.tatami.event.presentation.create.components.CustomDatePickerDialog
import at.tatami.event.presentation.create.components.CustomTimePickerDialog
import at.tatami.event.presentation.create.components.MemberSelectionBottomSheetContent
import kotlin.time.Clock
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCreateScreen(
    onNavigateBack: () -> Unit,
    viewModel: EventCreateViewModel = koinViewModel()
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMemberSelectionSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val title = viewModel.title.collectAsState()
    val description = viewModel.description.collectAsState()
    val location = viewModel.location.collectAsState()
    val startDateTime = viewModel.startDateTime.collectAsState()
    val invitedPersonIds = viewModel.invitedPersonIds.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val error = viewModel.error.collectAsState()
    val success = viewModel.success.collectAsState()
    val selectedMemberIds = viewModel.selectedMemberIds.collectAsState()

    LaunchedEffect(success.value) {
        if (success.value) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.event_create_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back))
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
            // Title
            OutlinedTextField(
                value = title.value,
                onValueChange = { viewModel.setTitle(it) },
                label = { Text(stringResource(Res.string.event_title_label)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value,
                singleLine = true
            )

            // Description
            OutlinedTextField(
                value = description.value,
                onValueChange = { viewModel.setDescription(it) },
                label = { Text(stringResource(Res.string.event_description_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                enabled = !isLoading.value,
                maxLines = 4
            )

            // Location
            OutlinedTextField(
                value = location.value,
                onValueChange = { viewModel.setLocation(it) },
                label = { Text(stringResource(Res.string.event_location_label)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value,
                singleLine = true
            )

            // Date and Time
            if (startDateTime.value != null) {
                Text(
                    "Start: ${startDateTime.value}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading.value
                ) {
                    Text(stringResource(Res.string.event_pick_date_button))
                }

                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    enabled = !isLoading.value && startDateTime.value != null
                ) {
                    Text(stringResource(Res.string.event_pick_time_button))
                }
            }

            // Invited Members
            Button(
                onClick = { showMemberSelectionSheet = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value
            ) {
                Text(stringResource(Res.string.event_select_members_button, invitedPersonIds.value.size))
            }

            // Error message
            error.value?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Create button
            Button(
                onClick = { viewModel.createEvent() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(stringResource(Res.string.event_create_title))
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        CustomDatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelected = { dateMillis ->
                val selectedDate = Instant.fromEpochMilliseconds(dateMillis)
                    .toLocalDateTimeInSystemTimeZone()
                val currentTime = startDateTime.value?.let {
                    LocalDateTime(
                        it.year, it.month.number, it.day,
                        it.hour, it.minute, it.second, it.nanosecond
                    )
                } ?: selectedDate
                val combined = LocalDateTime(
                    selectedDate.year, selectedDate.month.number, selectedDate.day,
                    currentTime.hour, currentTime.minute, currentTime.second, currentTime.nanosecond
                )
                viewModel.setStartDateTime(combined)
                showDatePicker = false
            }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        CustomTimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelected = { hour, minute ->
                val current = startDateTime.value ?: Clock.System.now()
                    .toLocalDateTimeInSystemTimeZone()
                val combined = LocalDateTime(
                    current.year, current.month.number, current.day,
                    hour, minute, 0, 0
                )
                viewModel.setStartDateTime(combined)
                showTimePicker = false
            }
        )
    }

    // Member Selection Bottom Sheet Overlay
    if (showMemberSelectionSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                // Save selection when sheet is dismissed (swipe/outside click)
                viewModel.setInvitedPersonIds(selectedMemberIds.value.toList())
                showMemberSelectionSheet = false
            },
            sheetState = sheetState
        ) {
            MemberSelectionBottomSheetContent(
                initialSelectedIds = invitedPersonIds.value.toList(),
                viewModel = viewModel,
            )
        }
    }
}