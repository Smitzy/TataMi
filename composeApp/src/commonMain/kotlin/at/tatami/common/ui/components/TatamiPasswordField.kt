package at.tatami.common.ui.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A reusable password text field with built-in visibility toggle
 * 
 * @param value The current password value
 * @param onValueChange Callback when password changes
 * @param label The label for the field
 * @param modifier Optional modifier
 * @param enabled Whether the field is enabled
 * @param isError Whether the field is in error state
 * @param errorMessage Optional error message to display
 * @param placeholder Optional placeholder text
 * @param supportingText Optional supporting text
 * @param imeAction The IME action for the keyboard
 * @param keyboardActions Keyboard actions to handle
 * @param isPasswordVisible Optional external control of password visibility
 * @param onPasswordVisibilityChange Optional callback for visibility changes
 */
@Composable
fun TatamiPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    errorMessage: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isPasswordVisible: Boolean? = null,
    onPasswordVisibilityChange: ((Boolean) -> Unit)? = null
) {
    // Use internal state if not controlled externally
    var internalPasswordVisible by remember { mutableStateOf(false) }
    val passwordVisible = isPasswordVisible ?: internalPasswordVisible
    
    TatamiTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        enabled = enabled,
        errorMessage = errorMessage,
        placeholder = placeholder,
        supportingText = supportingText,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            TatamiIconButton(
                onClick = {
                    if (onPasswordVisibilityChange != null) {
                        onPasswordVisibilityChange(!passwordVisible)
                    } else {
                        internalPasswordVisible = !internalPasswordVisible
                    }
                },
                enabled = enabled
            ) {
                Icon(
                    imageVector = if (passwordVisible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = if (passwordVisible) {
                        "Hide password"
                    } else {
                        "Show password"
                    }
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        singleLine = true
    )
}