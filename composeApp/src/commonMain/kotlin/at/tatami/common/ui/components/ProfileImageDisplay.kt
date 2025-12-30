package at.tatami.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.theme.TatamiTheme
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tatami.composeapp.generated.resources.*

/**
 * A reusable component for displaying profile images.
 * This component only handles the visual display - no selection logic.
 *
 * @param imageData The image data as ByteArray, or null for placeholder
 * @param imageUrl Optional URL to load image from (used when imageData is null)
 * @param onClick Optional click handler
 * @param size The size of the profile image (default 120.dp)
 * @param showCameraOverlay Whether to show the camera icon overlay (default true)
 * @param showLabel Whether to show the "Tap to change photo" label (default true)
 * @param enabled Whether the component is enabled for interaction (default true)
 * @param modifier Modifier for the root composable
 */
@Composable
fun ProfileImageDisplay(
    imageData: ByteArray?,
    imageUrl: String? = null,
    onClick: (() -> Unit)? = null,
    size: Dp = 120.dp,
    showCameraOverlay: Boolean = true,
    showLabel: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image with Camera Overlay
        Box(
            modifier = Modifier.size(size),
            contentAlignment = Alignment.Center
        ) {
            // Circular image container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                when {
                    imageData != null && imageData.isNotEmpty() -> {
                        AsyncImage(
                            model = imageData,
                            contentDescription = stringResource(Res.string.profile_image),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    !imageUrl.isNullOrEmpty() -> {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = stringResource(Res.string.profile_image),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(Res.string.profile_image),
                            modifier = Modifier.size(size * 0.5f), // Icon is 50% of container size
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Camera overlay icon - positioned outside the clip
            if (showCameraOverlay && onClick != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(size * 0.3f) // Overlay is 30% of container size
                        .clip(CircleShape)
                        .background(
                            if (enabled) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.outline
                        )
                        .then(
                            if (enabled) Modifier.clickable { onClick() }
                            else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = stringResource(Res.string.change_photo),
                        modifier = Modifier.size(size * 0.167f), // Icon is ~17% of container size
                        tint = if (enabled) MaterialTheme.colorScheme.onPrimary 
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
            }
        }
        
        // Label text
        if (showLabel && onClick != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.tap_to_change_photo),
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant 
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}

@Preview
@Composable
private fun ProfileImageDisplayPreview_NoImage() {
    TatamiTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                ProfileImageDisplay(
                    imageData = null,
                    onClick = { },
                    showLabel = true
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileImageDisplayPreview_NoImageWithCamera() {
    TatamiTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                ProfileImageDisplay(
                    imageData = null,
                    onClick = { },
                    showCameraOverlay = true,
                    showLabel = true
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileImageDisplayPreview_NoImageNoLabel() {
    TatamiTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                ProfileImageDisplay(
                    imageData = null,
                    onClick = { },
                    showCameraOverlay = true,
                    showLabel = false
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileImageDisplayPreview_LargeSize() {
    TatamiTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                ProfileImageDisplay(
                    imageData = null,
                    onClick = { },
                    size = 120.dp,
                    showCameraOverlay = true,
                    showLabel = false
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileImageDisplayPreview_ExtraLargeSize() {
    TatamiTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                ProfileImageDisplay(
                    imageData = null,
                    onClick = { },
                    size = 200.dp,
                    showCameraOverlay = true,
                    showLabel = false
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProfileImageDisplayPreview_SmallSize() {
    TatamiTheme {
        Surface {
            Box(modifier = Modifier.padding(16.dp)) {
                ProfileImageDisplay(
                    imageData = null,
                    onClick = { },
                    size = 48.dp,
                    showCameraOverlay = false,
                    showLabel = false
                )
            }
        }
    }
}