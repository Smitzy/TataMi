@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package at.tatami.group.presentation.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.PersonAvatar
import at.tatami.common.ui.components.TrainerToggleSwitch

/**
 * Data class representing a group member for the manage trainers bottom sheet.
 */
data class GroupMemberItem(
    val personId: String,
    val name: String,
    val profileImageUrl: String?,
    val isTrainer: Boolean
)

/**
 * Bottom sheet content for managing group trainers.
 * Shows all group members with toggle switch to set trainer status.
 *
 * @param groupMembers List of all group members with their trainer status
 * @param onToggleTrainer Callback when a member's trainer status is toggled
 */
@Composable
fun ManageTrainersBottomSheetContent(
    groupMembers: List<GroupMemberItem>,
    onToggleTrainer: (String, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Manage Trainers",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Toggle switch to grant or revoke trainer status for group members.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        HorizontalDivider()

        // Member list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(groupMembers) { member ->
                ManageTrainerListItem(
                    member = member,
                    onToggleTrainer = { isTrainer -> onToggleTrainer(member.personId, isTrainer) }
                )
            }
        }
    }
}

/**
 * List item for a group member in the manage trainers sheet.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ManageTrainerListItem(
    member: GroupMemberItem,
    onToggleTrainer: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingContent = {
            PersonAvatar(
                name = member.name,
                profileImageUrl = member.profileImageUrl
            )
        },
        trailingContent = {
            TrainerToggleSwitch(
                checked = member.isTrainer,
                onCheckedChange = onToggleTrainer
            )
        }
    )
}