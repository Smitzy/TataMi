package at.tatami.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A gradient overlay that fades content to the background color.
 * Typically used to create a smooth fade effect at the top or bottom of scrollable content.
 *
 * @param alignment Where to position the overlay (TopCenter or BottomCenter)
 * @param height Height of the gradient overlay
 * @param backgroundColor The color to fade into (defaults to MaterialTheme background)
 * @param startY Starting Y position for the gradient
 * @param endY Ending Y position for the gradient
 */
@Composable
fun BoxScope.ScrollFadeOverlay(
    alignment: Alignment,
    height: Dp = 46.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    startY: Float = if (alignment == Alignment.TopCenter) 100f else 0f,
    endY: Float = if (alignment == Alignment.TopCenter) 20f else 80f,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .align(alignment)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        backgroundColor
                    ),
                    startY = startY,
                    endY = endY
                )
            )
    )
}