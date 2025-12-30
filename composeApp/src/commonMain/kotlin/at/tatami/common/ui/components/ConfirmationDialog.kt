package at.tatami.common.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

/**
 * Reusable confirmation dialog component.
 * Displays a title, message, and confirm/cancel buttons.
 *
 * @param title The dialog title
 * @param message The confirmation message
 * @param confirmText Text for the confirm button (default: "Confirm" from string resources)
 * @param dismissText Text for the dismiss button (default: "Cancel" from string resources)
 * @param onConfirm Callback when user confirms
 * @param onDismiss Callback when user dismisses
 * @param isDestructive If true, the confirm button will use error color scheme
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String? = null,
    dismissText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false
) {
    val actualConfirmText = confirmText ?: stringResource(Res.string.confirm)
    val actualDismissText = dismissText ?: stringResource(Res.string.cancel)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = actualConfirmText,
                    color = if (isDestructive) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(actualDismissText)
            }
        }
    )
}