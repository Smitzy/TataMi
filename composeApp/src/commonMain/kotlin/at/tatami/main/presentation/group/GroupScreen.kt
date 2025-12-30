@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package at.tatami.main.presentation.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.tatami.group.presentation.GroupViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen displaying list of groups.
 * Shows groups with member/trainer counts and a FAB for admins to create new groups.
 *
 * Visibility:
 * - Admins see all groups in the club
 * - Members see only groups they belong to
 */
@Composable
fun GroupScreen(
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToGroupDetail: (groupId: String) -> Unit,
    viewModel: GroupViewModel = koinViewModel()
) {
    val groups = viewModel.groups.collectAsState()
    val isAdmin = viewModel.isAdmin.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (groups.value.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "No Groups",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isAdmin.value) {
                            "Create your first group to get started"
                        } else {
                            "You are not a member of any groups yet"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Groups list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(groups.value) { displayItem ->
                    GroupListItem(
                        displayItem = displayItem,
                        onGroupClick = { onNavigateToGroupDetail(displayItem.group.id) }
                    )
                }
            }
        }

        // FAB positioned in the bottom-right corner (only for admins)
        if (isAdmin.value) {
            FloatingActionButton(
                onClick = onNavigateToCreateGroup,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Group")
            }
        }
    }
}

/**
 * List item for a group.
 * Simpler than EventListItem: no status indicators, no invitation badges.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun GroupListItem(
    displayItem: GroupViewModel.GroupDisplayItem,
    onGroupClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onGroupClick),
        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent),
        headlineContent = {
            Text(
                text = displayItem.group.name,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = "${displayItem.memberCount} members • ${displayItem.trainerCount} trainers",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    )
}