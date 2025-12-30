@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package at.tatami.training.presentation.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.PersonAvatar
import at.tatami.training.presentation.detail.TrainingDetailViewModel

/**
 * Bottom sheet content displaying group members with attendance checkboxes.
 * Supports complex permission logic for toggling attendance.
 *
 * @param members List of group members
 * @param attendedPersonIds Set of person IDs who attended
 * @param onToggleAttendance Callback when a checkbox is toggled
 * @param canToggleAttendance Function to determine if a specific person's checkbox can be toggled
 */
@Composable
fun AttendanceBottomSheetContent(
    members: List<TrainingDetailViewModel.MemberInfo>,
    attendedPersonIds: Set<String>,
    onToggleAttendance: (String) -> Unit,
    canToggleAttendance: (String) -> Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(members) { member ->
            AttendeeListItem(
                member = member,
                isAttended = attendedPersonIds.contains(member.personId),
                canToggle = canToggleAttendance(member.personId),
                onToggle = { onToggleAttendance(member.personId) }
            )
        }
    }
}

/**
 * List item for a single member's attendance.
 * Displays avatar, name, trainer badge, and checkbox.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AttendeeListItem(
    member: TrainingDetailViewModel.MemberInfo,
    isAttended: Boolean,
    canToggle: Boolean,
    onToggle: () -> Unit
) {
    ListItem(
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = member.personName,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = if (member.isTrainer) {
            {
                Text(
                    text = "Trainer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else null,
        leadingContent = {
            PersonAvatar(
                name = member.personName,
                profileImageUrl = member.profileImageUrl
            )
        },
        trailingContent = {
            Checkbox(
                checked = isAttended,
                onCheckedChange = { onToggle() },
                enabled = canToggle
            )
        }
    )
}
