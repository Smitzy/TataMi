@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package at.tatami.event.presentation.create.components

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
import at.tatami.event.presentation.create.EventCreateViewModel

@Composable
fun MemberSelectionBottomSheetContent(
    initialSelectedIds: List<String>,
    viewModel: EventCreateViewModel
) {
    val members = viewModel.members.collectAsState()
    val isLoading = viewModel.isMembersLoading.collectAsState()

    // Initialize selection when sheet opens
    LaunchedEffect(initialSelectedIds) {
        viewModel.setInitialSelection(initialSelectedIds)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
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
                    MemberSelectionListItem(
                        member = member,
                        onToggle = { viewModel.toggleMemberSelection(member.personId) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MemberSelectionListItem(
    member: EventCreateViewModel.MemberItem,
    onToggle: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onToggle() },
        colors = ListItemDefaults.colors(
            containerColor = if (member.isSelected) {
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
        leadingContent = {
            PersonAvatar(
                name = member.name,
                profileImageUrl = member.profileImageUrl
            )
        },
        trailingContent = {
            Checkbox(
                checked = member.isSelected,
                onCheckedChange = { onToggle() },
                enabled = true
            )
        }
    )
}