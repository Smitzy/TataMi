package at.tatami.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

/**
 * Reusable avatar component for displaying person profile pictures with initials fallback.
 *
 * @param name Full name for initials extraction (e.g., "John Doe")
 * @param profileImageUrl Optional URL to profile image
 * @param shape Shape of the avatar (default: CircleShape)
 * @param modifier Modifier for customization
 */
@Composable
fun PersonAvatar(
    name: String,
    profileImageUrl: String? = null,
    shape: Shape = CircleShape,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (profileImageUrl != null) {
            AsyncImage(
                model = profileImageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Extract initials from name (first letter of each word, max 2)
            val initials = name.split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull() }
                .joinToString("")
                .uppercase()

            Text(
                text = initials.ifEmpty { "?" },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}