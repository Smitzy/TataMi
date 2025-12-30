package at.tatami.common.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.back

enum class FabType {
    NORMAL,    // Standard FAB
    EXTENDED,  // Extended FAB with text
    SMALL      // Small FAB
}

data class NavigationFabConfig(
    val onClick: () -> Unit,
    val icon: ImageVector? = null,
    val text: String? = null,
    val enabled: Boolean = true,
    val containerColor: Color? = null,
    val contentColor: Color? = null,
    val showLoadingIndicator: Boolean = false,
    val contentDescription: String? = null,
    val fabType: FabType = FabType.NORMAL
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NavigationFabs(
    modifier: Modifier = Modifier,
    leftButton: NavigationFabConfig? = null,
    rightButton: NavigationFabConfig? = null,
    isLoading: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left FAB
        if (leftButton != null) {
            when (leftButton.fabType) {
                FabType.NORMAL -> {
                    FloatingActionButton(
                        onClick = { if (leftButton.enabled && !isLoading) leftButton.onClick() },
                        containerColor = leftButton.containerColor ?: MaterialTheme.colorScheme.surface,
                        contentColor = leftButton.contentColor ?: MaterialTheme.colorScheme.onSurface
                    ) {
                        if (leftButton.showLoadingIndicator && isLoading) {
                            LoadingIndicator()
                        } else if (leftButton.icon != null) {
                            Icon(
                                imageVector = leftButton.icon,
                                contentDescription = leftButton.contentDescription
                            )
                        }
                    }
                }
                FabType.EXTENDED -> {
                    ExtendedFloatingActionButton(
                        onClick = { if (leftButton.enabled && !isLoading) leftButton.onClick() },
                        containerColor = leftButton.containerColor ?: MaterialTheme.colorScheme.surface,
                        contentColor = leftButton.contentColor ?: MaterialTheme.colorScheme.onSurface
                    ) {
                        if (leftButton.text != null) {
                            Text(
                                text = leftButton.text,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        if (leftButton.icon != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = leftButton.icon,
                                contentDescription = leftButton.contentDescription
                            )
                        }
                    }
                }
                FabType.SMALL -> {
                    SmallFloatingActionButton(
                        onClick = { if (leftButton.enabled && !isLoading) leftButton.onClick() },
                        containerColor = leftButton.containerColor ?: MaterialTheme.colorScheme.surface,
                        contentColor = leftButton.contentColor ?: MaterialTheme.colorScheme.onSurface
                    ) {
                        if (leftButton.icon != null) {
                            Icon(
                                imageVector = leftButton.icon,
                                contentDescription = leftButton.contentDescription
                            )
                        }
                    }
                }
            }
        } else {
            // Placeholder to maintain spacing
            Spacer(modifier = Modifier.size(56.dp))
        }

        // Right FAB
        if (rightButton != null) {
            when (rightButton.fabType) {
                FabType.NORMAL -> {
                    FloatingActionButton(
                        onClick = { if (rightButton.enabled && !isLoading) rightButton.onClick() },
                        containerColor = rightButton.containerColor ?: MaterialTheme.colorScheme.primary,
                        contentColor = rightButton.contentColor ?: MaterialTheme.colorScheme.onPrimary
                    ) {
                        if (rightButton.showLoadingIndicator && isLoading) {
                            LoadingIndicator()
                        } else if (rightButton.icon != null) {
                            Icon(
                                imageVector = rightButton.icon,
                                contentDescription = rightButton.contentDescription
                            )
                        }
                    }
                }
                FabType.EXTENDED -> {
                    ExtendedFloatingActionButton(
                        onClick = { if (rightButton.enabled && !isLoading) rightButton.onClick() },
                        containerColor = rightButton.containerColor ?: MaterialTheme.colorScheme.primary,
                        contentColor = rightButton.contentColor ?: MaterialTheme.colorScheme.onPrimary
                    ) {
                        if (rightButton.text != null) {
                            Text(
                                text = rightButton.text,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        if (rightButton.icon != null || (rightButton.showLoadingIndicator && isLoading)) {
                            Spacer(modifier = Modifier.width(16.dp))
                            if (rightButton.showLoadingIndicator && isLoading) {
                                LoadingIndicator()
                            } else if (rightButton.icon != null) {
                                Icon(
                                    imageVector = rightButton.icon,
                                    contentDescription = rightButton.contentDescription
                                )
                            }
                        }
                    }
                }
                FabType.SMALL -> {
                    SmallFloatingActionButton(
                        onClick = { if (rightButton.enabled && !isLoading) rightButton.onClick() },
                        containerColor = rightButton.containerColor ?: MaterialTheme.colorScheme.primary,
                        contentColor = rightButton.contentColor ?: MaterialTheme.colorScheme.onPrimary
                    ) {
                        if (rightButton.icon != null) {
                            Icon(
                                imageVector = rightButton.icon,
                                contentDescription = rightButton.contentDescription
                            )
                        }
                    }
                }
            }
        } else {
            // Placeholder to maintain spacing
            Spacer(modifier = Modifier.size(56.dp))
        }
    }
}

// Convenience builders for common patterns

/**
 * Bottom bar navigation with proper padding for Scaffold bottomBar.
 * Includes horizontal and vertical padding to prevent FABs from hugging screen edges
 * and navigation bar.
 *
 * @param leftIcon Icon for the left FAB (typically back/cancel)
 * @param leftContentDescription Content description for the left FAB
 * @param onLeftClick Click handler for the left FAB
 * @param rightIcon Icon for the right button (typically next/forward)
 * @param rightText Text for the right button
 * @param rightContentDescription Content description for the right button
 * @param onRightClick Click handler for the right button
 * @param rightEnabled Whether the right button is enabled (default: true)
 * @param modifier Optional modifier for the container
 */
@Composable
fun BottomBarNavigationFabs(
    leftIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
    leftContentDescription: String? = stringResource(Res.string.back),
    onLeftClick: () -> Unit,
    rightIcon: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
    rightText: String,
    rightContentDescription: String? = rightText,
    onRightClick: () -> Unit,
    rightEnabled: Boolean = true,
    topPadding: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = topPadding, start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        FloatingActionButton(
            onClick = onLeftClick,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ) {
            Icon(
                imageVector = leftIcon,
                contentDescription = leftContentDescription
            )
        }
        Button(
            onClick = onRightClick,
            enabled = rightEnabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FloatingActionButtonDefaults.containerColor
            ),
            elevation = ButtonDefaults.buttonElevation(8.dp),
            modifier = Modifier
                .height(56.dp)
                .defaultMinSize(56.dp, 56.dp)
        ) {
            Text(text = rightText)
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Icon(
                imageVector = rightIcon,
                contentDescription = rightContentDescription,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}