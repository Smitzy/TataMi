package at.tatami.main.presentation.event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.tatami.domain.model.EventStatus
import at.tatami.event.presentation.EventViewModel
import at.tatami.main.presentation.event.components.EventTimeFilterButtonGroup
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EventScreen(
    onNavigateToCreateEvent: () -> Unit,
    onNavigateToEventDetail: (eventId: String) -> Unit,
    viewModel: EventViewModel = koinViewModel()
) {
    val events = viewModel.events.collectAsState()
    val isAdmin = viewModel.isAdmin.collectAsState()
    val selectedTimeFilter = viewModel.selectedTimeFilter.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Time Filter Button Group at the top
            EventTimeFilterButtonGroup(
                selectedFilter = selectedTimeFilter.value,
                onFilterSelected = { filter -> viewModel.onTimeFilterSelected(filter) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Content area
            if (events.value.isEmpty()) {
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
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "No Events",
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "There are no events in this category",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(events.value) { displayItem ->
                        EventListItem(
                            displayItem = displayItem,
                            onEventClick = { onNavigateToEventDetail(displayItem.event.id) }
                        )
                    }
                }
            }
        }

        // FAB positioned in the bottom-right corner
        if (isAdmin.value) {
            FloatingActionButton(
                onClick = onNavigateToCreateEvent,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Event")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EventListItem(
    displayItem: EventViewModel.EventDisplayItem,
    onEventClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onEventClick),
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = displayItem.event.title,
                    style = MaterialTheme.typography.titleMedium
                )
                // Show badge if viewing as admin but not invited
                if (!displayItem.isInvited) {
                    SuggestionChip(
                        onClick = { /* No action, just informational */ },
                        label = {
                            Text(
                                text = "Admin View",
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.height(24.dp),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }
        },
        supportingContent = {
            Text(
                text = displayItem.formattedDateTime,
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            EventStatusIndicator(
                status = displayItem.userStatus,
                isInvited = displayItem.isInvited
            )
        }
    )
}

@Composable
private fun EventStatusIndicator(
    status: EventStatus,
    isInvited: Boolean,
    modifier: Modifier = Modifier
) {
    // If not invited, show "viewing only" indicator regardless of status
    val (containerColor, icon) = if (!isInvited) {
        MaterialTheme.colorScheme.surfaceVariant to Icons.Default.Visibility
    } else {
        when (status) {
            EventStatus.YES -> MaterialTheme.colorScheme.tertiary to Icons.Default.Check
            EventStatus.NO -> MaterialTheme.colorScheme.error to Icons.Default.Close
            EventStatus.MAYBE -> MaterialTheme.colorScheme.primary to Icons.Default.QuestionMark
            EventStatus.NO_RESPONSE -> MaterialTheme.colorScheme.onSurfaceVariant to Icons.Default.Remove
        }
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = if (!isInvited) "Viewing as admin" else null,
            tint = if (!isInvited) MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}
