@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package at.tatami.group.presentation.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.PersonAvatar

/**
 * Data class representing a club member for the manage members bottom sheet.
 */
data class ClubMemberItem(
    val personId: String,
    val name: String,
    val profileImageUrl: String?,
    val isGroupMember: Boolean,
    val isGroupTrainer: Boolean
)

/**
 * Bottom sheet content for managing group members.
 * Shows all club members with checkbox to toggle group membership.
 *
 * @param clubMembers List of all club members with their group membership status
 * @param onToggleMembership Callback when a member's group membership is toggled
 */
@Composable
fun ManageMembersBottomSheetContent(
    clubMembers: List<ClubMemberItem>,
    onToggleMembership: (String) -> Unit
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
                text = "Manage Members",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Toggle checkbox to add or remove members from this group.",
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
            items(clubMembers) { member ->
                ManageMemberListItem(
                    member = member,
                    onToggleMembership = { onToggleMembership(member.personId) }
                )
            }
        }
    }
}

/**
 * List item for a club member in the manage members sheet.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ManageMemberListItem(
    member: ClubMemberItem,
    onToggleMembership: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = if (member.isGroupTrainer) {
            {
                Text(
                    text = "Trainer",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else null,
        leadingContent = {
            PersonAvatar(
                name = member.name,
                profileImageUrl = member.profileImageUrl
            )
        },
        trailingContent = {
            Checkbox(
                checked = member.isGroupMember,
                onCheckedChange = { onToggleMembership() }
            )
        }
    )
}