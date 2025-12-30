@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package at.tatami.training.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import at.tatami.event.presentation.create.components.CustomDatePickerDialog
import at.tatami.event.presentation.create.components.CustomTimePickerDialog
import at.tatami.main.presentation.event.components.EventTimeFilterButtonGroup
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Screen displaying a list of trainings for a specific group.
 * Shows upcoming or past trainings based on filter selection.
 * Admins and trainers can create new trainings via the FAB.
 *
 * @param groupId The ID of the group to display trainings for
 * @param onNavigateBack Callback to navigate back to the previous screen
 * @param onNavigateToTrainingDetail Callback to navigate to training detail screen
 * @param viewModel ViewModel managing training list state (injected with groupId parameter)
 */
@Composable
fun TrainingListScreen(
    groupId: String,
    onNavigateBack: () -> Unit,
    onNavigateToTrainingDetail: (String, String) -> Unit,
    viewModel: TrainingViewModel = koinViewModel { parametersOf(groupId) }
) {
    val trainings = viewModel.trainings.collectAsState()
    val canCreateTraining = viewModel.canCreateTraining.collectAsState()
    val selectedTimeFilter = viewModel.selectedTimeFilter.collectAsState()
    val isCreating = viewModel.isCreating.collectAsState()
    val creationError = viewModel.creationError.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDateTime by remember { mutableStateOf<LocalDateTime?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trainings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Time Filter Button Group
                EventTimeFilterButtonGroup(
                    selectedFilter = selectedTimeFilter.value,
                    onFilterSelected = { filter -> viewModel.onTimeFilterSelected(filter) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Content area
                if (trainings.value.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "No Trainings",
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "There are no trainings in this category",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // Training list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(trainings.value) { displayItem ->
                            TrainingListItem(
                                displayItem = displayItem,
                                onTrainingClick = { onNavigateToTrainingDetail(groupId, displayItem.training.id) }
                            )
                        }
                    }
                }
            }

            // FAB for creating trainings (only visible to admins and trainers)
            if (canCreateTraining.value && !isCreating.value) {
                FloatingActionButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Training")
                }
            }

            // Error snackbar positioned at bottom
            creationError.value?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearCreationError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
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
                selectedDateTime = selectedDate
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
                selectedDateTime = null
            },
            onTimeSelected = { hour, minute ->
                val current = selectedDateTime ?: Clock.System.now()
                    .toLocalDateTimeInSystemTimeZone()
                val combined = LocalDateTime(
                    current.year, current.month.number, current.day,
                    hour, minute, 0, 0
                )
                viewModel.createTraining(combined)
                showTimePicker = false
                selectedDateTime = null
            }
        )
    }
}

/**
 * List item component for displaying a single training.
 * Shows only the formatted date/time as the headline.
 *
 * @param displayItem Training data with formatted datetime
 * @param onTrainingClick Callback when the training is clicked
 */
@Composable
private fun TrainingListItem(
    displayItem: TrainingViewModel.TrainingDisplayItem,
    onTrainingClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onTrainingClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = displayItem.formattedDateTime,
                style = MaterialTheme.typography.titleMedium
            )
        }
        // No supportingContent or leadingContent as per requirements
    )
}