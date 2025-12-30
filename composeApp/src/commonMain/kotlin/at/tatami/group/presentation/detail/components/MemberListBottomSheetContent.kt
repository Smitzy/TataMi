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
import at.tatami.group.presentation.detail.GroupDetailViewModel

@Composable
fun MemberListBottomSheetContent(
    members: List<GroupDetailViewModel.MemberInfo>
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(members) { member ->
            MemberListItem(member = member)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MemberListItem(
    member: GroupDetailViewModel.MemberInfo
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
        }
    )
}