package at.tatami.main.presentation.event.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Upcoming
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import at.tatami.domain.model.EventTimeFilter

/**
 * Single-Select Connected ButtonGroup for event time filtering (UPCOMING, PAST).
 * Uses Material 3 Expressive API with connected button shapes and radio button semantics.
 * Shows icons and labels representing each filter option:
 * - UPCOMING: Upcoming icon with "Upcoming" label
 * - PAST: History icon with "Past" label
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EventTimeFilterButtonGroup(
    selectedFilter: EventTimeFilter,
    onFilterSelected: (EventTimeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filterOptions = listOf(
        EventTimeFilter.UPCOMING,
        EventTimeFilter.PAST
    )
    val filterIcons = listOf(
        Icons.Default.Upcoming,    // UPCOMING
        Icons.Default.History      // PAST
    )
    val filterLabels = listOf(
        "Upcoming",
        "Past"
    )
    val selectedIndex = filterOptions.indexOf(selectedFilter)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        filterOptions.forEachIndexed { index, filter ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { onFilterSelected(filter) },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    filterOptions.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                }
            ) {
                Icon(
                    imageVector = filterIcons[index],
                    contentDescription = filterLabels[index]
                )
                Text(
                    text = filterLabels[index],
                    modifier = Modifier
                )
            }
        }
    }
}