@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package at.tatami.group.presentation.create.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.PersonAvatar
import at.tatami.group.presentation.create.GroupCreateViewModel

/**
 * Bottom sheet content for selecting members and trainers.
 * Unique pattern: Single sheet for both selections with conditional trainer switch.
 *
 * UI Pattern:
 * - List items have highlighting when selected as member (secondaryContainer background)
 * - Trainer switch only visible when selected as member
 * - Supporting text shows "Trainer" label when trainer toggle is on
 * - Clicking item toggles member selection
 * - Switch toggles trainer status (only works if member selected)
 */
@Composable
fun MemberTrainerSelectionBottomSheetContent(
    initialSelectedMemberIds: List<String>,
    initialSelectedTrainerIds: List<String>,
    viewModel: GroupCreateViewModel
) {
    val members = viewModel.members.collectAsState()
    val isLoading = viewModel.isMembersLoading.collectAsState()

    // Initialize selection when sheet opens (only once, not on every selection change)
    LaunchedEffect(Unit) {
        viewModel.loadClubMembers(
            initialSelectedMemberIds = initialSelectedMemberIds,
            initialSelectedTrainerIds = initialSelectedTrainerIds
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        // Header with instructions
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Select Members & Trainers",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Tap to select members. Toggle switch for trainer status.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        HorizontalDivider()

        // Member list
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(members.value) { member ->
                    MemberTrainerSelectionListItem(
                        member = member,
                        onToggleMember = { viewModel.toggleMemberSelection(member.personId) },
                        onToggleTrainer = { viewModel.toggleTrainerStatus(member.personId) }
                    )
                }
            }
        }
    }
}

/**
 * List item for member/trainer selection.
 * Shows highlighting when selected as member, and conditional trainer switch.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MemberTrainerSelectionListItem(
    member: GroupCreateViewModel.MemberTrainerItem,
    onToggleMember: () -> Unit,
    onToggleTrainer: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onToggleMember() },
        colors = ListItemDefaults.colors(
            containerColor = if (member.isSelectedAsMember) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                Color.Transparent
            }
        ),
        headlineContent = {
            Text(
                text = member.name,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        supportingContent = if (member.isSelectedAsTrainer) {
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
            // Trainer Switch - ONLY visible when selected as member
            if (member.isSelectedAsMember) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trainer",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Switch(
                        checked = member.isSelectedAsTrainer,
                        onCheckedChange = { onToggleTrainer() },
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }
        }
    )
}