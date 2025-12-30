package at.tatami.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * Reusable text input dialog component.
 * Displays a title, text input field, and confirm/cancel buttons.
 *
 * @param title The dialog title
 * @param label The label for the text field
 * @param initialValue Initial value for the text field
 * @param placeholder Placeholder text when field is empty
 * @param confirmText Text for the confirm button (default: "Save")
 * @param dismissText Text for the dismiss button (default: "Cancel")
 * @param onConfirm Callback when user confirms with the entered text
 * @param onDismiss Callback when user dismisses
 * @param inputFilter Optional filter function to apply to input
 * @param errorMessage Error message to display below the text field
 * @param isLoading If true, shows loading indicator and disables buttons
 */
@Composable
fun TextInputDialog(
    title: String,
    label: String,
    initialValue: String = "",
    placeholder: String = "",
    confirmText: String = "Save",
    dismissText: String = "Cancel",
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    inputFilter: ((String) -> String)? = null,
    errorMessage: String? = null,
    isLoading: Boolean = false
) {
    var textValue by remember { mutableStateOf(initialValue) }
    val focusRequester = remember { FocusRequester() }

    // Apply input filter if provided
    val handleValueChange: (String) -> Unit = if (inputFilter != null) {
        { newValue -> textValue = inputFilter(newValue) }
    } else {
        { newValue -> textValue = newValue }
    }

    // Auto-focus the text field when dialog opens
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = handleValueChange,
                    label = { Text(label) },
                    placeholder = if (placeholder.isNotEmpty()) {
                        { Text(placeholder) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    enabled = !isLoading,
                    isError = errorMessage != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!isLoading && errorMessage == null && textValue.isNotBlank()) {
                                onConfirm(textValue)
                            }
                        }
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(textValue) },
                enabled = !isLoading && textValue.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = confirmText,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text(dismissText)
            }
        }
    )
}
