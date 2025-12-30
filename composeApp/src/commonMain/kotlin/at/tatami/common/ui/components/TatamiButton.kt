package at.tatami.common.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TatamiButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingText: String? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(40.dp).then(modifier),
        enabled = enabled && !loading,
        colors = colors,
        content = {
            if (loading) {
                LoadingIndicator(modifier = Modifier.size(20.dp))
                if (loadingText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(loadingText)
                }
            } else {
                content()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TatamiOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    loadingText: String? = null,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(40.dp).then(modifier),
        enabled = enabled && !loading,
        content = {
            if (loading) {
                LoadingIndicator(modifier = Modifier.size(20.dp))
                if (loadingText != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(loadingText)
                }
            } else {
                content()
            }
        }
    )
}

@Composable
fun TatamiIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}