package at.tatami.event.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import at.tatami.domain.model.EventStatus

/**
 * Single-Select Connected ButtonGroup for event status selection (YES, NO, MAYBE).
 * Uses Material 3 Expressive API with connected button shapes and radio button semantics.
 * Shows icons representing each status:
 * - YES: Check icon
 * - NO: Close icon
 * - MAYBE: Help icon
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EventStatusSelectionButtonGroup(
    selectedStatus: EventStatus,
    onStatusSelected: (EventStatus) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val statusOptions = listOf(
        EventStatus.YES,
        EventStatus.NO,
        EventStatus.MAYBE
    )
    val statusIcons = listOf(
        Icons.Default.Check,                    // YES
        Icons.Default.Close,                    // NO
        Icons.Default.QuestionMark          // MAYBE
    )
    val selectedIndex = statusOptions.indexOf(selectedStatus)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        statusOptions.forEachIndexed { index, status ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { onStatusSelected(status) },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    statusOptions.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                enabled = enabled
            ) {
                Icon(
                    imageVector = statusIcons[index],
                    contentDescription = status.name
                )
            }
        }
    }
}