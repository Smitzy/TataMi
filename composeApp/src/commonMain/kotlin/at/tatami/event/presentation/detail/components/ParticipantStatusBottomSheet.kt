@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package at.tatami.event.presentation.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.PersonAvatar
import at.tatami.domain.model.EventStatus
import at.tatami.event.presentation.detail.EventDetailViewModel

@Composable
fun ParticipantStatusBottomSheetContent(
    participants: List<EventDetailViewModel.ParticipantInfo>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(participants) { participant ->
            ParticipantListItem(participant = participant)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ParticipantListItem(
    participant: EventDetailViewModel.ParticipantInfo
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = participant.personName,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = {
            Text(
                text = getStatusDisplayText(participant.status),
                style = MaterialTheme.typography.bodySmall,
                color = getStatusColor(participant.status)
            )
        },
        leadingContent = {
            PersonAvatar(
                name = participant.personName,
                profileImageUrl = participant.profileImageUrl
            )
        }
    )
}

@Composable
private fun getStatusColor(status: EventStatus): Color {
    return when (status) {
        EventStatus.YES -> MaterialTheme.colorScheme.tertiary
        EventStatus.NO -> MaterialTheme.colorScheme.error
        EventStatus.MAYBE -> MaterialTheme.colorScheme.primary
        EventStatus.NO_RESPONSE -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

private fun getStatusDisplayText(status: EventStatus): String {
    return when (status) {
        EventStatus.YES -> "Yes"
        EventStatus.NO -> "No"
        EventStatus.MAYBE -> "Maybe"
        EventStatus.NO_RESPONSE -> "No Response"
    }
}