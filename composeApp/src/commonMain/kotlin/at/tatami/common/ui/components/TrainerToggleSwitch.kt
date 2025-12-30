package at.tatami.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable trainer toggle switch component.
 * Displays a "Trainer" label above a switch for toggling trainer status.
 *
 * @param checked Whether the switch is checked (trainer status)
 * @param onCheckedChange Callback when the switch is toggled
 * @param enabled Whether the switch is enabled
 */
@Composable
fun TrainerToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Trainer",
            style = MaterialTheme.typography.labelSmall
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = Modifier.padding(0.dp)
        )
    }
}