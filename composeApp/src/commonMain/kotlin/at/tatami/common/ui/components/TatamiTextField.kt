package at.tatami.common.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun TatamiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    errorMessage: String? = null,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    hideErrorIfEmpty: Boolean = false,
    inputFilter: ((String) -> String)? = null
) {
    // Apply input filter if provided
    val handleValueChange: (String) -> Unit = if (inputFilter != null) {
        { newValue -> onValueChange(inputFilter(newValue)) }
    } else {
        onValueChange
    }

    // Determine if error should be shown
    val shouldShowError = errorMessage != null && (!hideErrorIfEmpty || value.isNotEmpty())

    OutlinedTextField(
        value = value,
        onValueChange = handleValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = shouldShowError,
        supportingText = {
            when {
                shouldShowError -> Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error
                )
                supportingText != null -> Text(supportingText)
            }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines
    )
}