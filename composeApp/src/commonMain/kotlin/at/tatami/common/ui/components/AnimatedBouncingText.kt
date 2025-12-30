package at.tatami.common.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Animated text component where each character bounces up and down sequentially.
 *
 * @param text The text to animate
 * @param modifier Modifier for the entire text row
 * @param style Text style to apply
 * @param color Text color
 * @param bounceHeight Height of the bounce animation in dp (default 12f)
 * @param letterAnimationDurationMs Duration for one letter to bounce up and down (default 400ms)
 * @param letterDelayMs Delay between each letter starting its animation (default 100ms)
 * @param loopPauseMs Pause duration between animation loops (default 5000ms)
 * @param letterSpacing Optional list of spacing values between characters. For N characters, provide N-1 spacing values.
 *                      If null or empty, characters will be placed with default spacing.
 */
@Composable
fun AnimatedBouncingText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified,
    bounceHeight: Float = 12f,
    letterAnimationDurationMs: Int = 800,
    letterDelayMs: Int = 100,
    loopPauseMs: Long = 5000L,
    letterSpacing: List<Dp>? = null
) {
    val characters = remember(text) { text.toList() }
    // Shared state to synchronize all letter animations
    var animationCycle by remember { mutableStateOf(0) }

    // Calculate cumulative offset for each character
    val cumulativeOffsets = remember(letterSpacing) {
        if (letterSpacing != null) {
            var accumulated = 0.dp
            listOf(0.dp) + letterSpacing.map { spacing ->
                accumulated += spacing
                accumulated
            }
        } else {
            List(characters.size) { 0.dp }
        }
    }

    // Calculate total offset to compensate for centering
    val totalOffset = remember(letterSpacing) {
        letterSpacing?.fold(0.dp) { acc, spacing -> acc + spacing } ?: 0.dp
    }

    Row(modifier = modifier.offset(x = -totalOffset / 2)) {
        characters.forEachIndexed { index, char ->
            AnimatedCharacter(
                char = char,
                index = index,
                totalCharacters = characters.size,
                style = style,
                color = color,
                bounceHeight = bounceHeight,
                letterAnimationDurationMs = letterAnimationDurationMs,
                letterDelayMs = letterDelayMs,
                loopPauseMs = loopPauseMs,
                animationCycle = animationCycle,
                onCycleComplete = { animationCycle++ },
                horizontalOffset = cumulativeOffsets[index]
            )
        }
    }
}

@Composable
private fun AnimatedCharacter(
    char: Char,
    index: Int,
    totalCharacters: Int,
    style: TextStyle,
    color: Color,
    bounceHeight: Float,
    letterAnimationDurationMs: Int,
    letterDelayMs: Int,
    loopPauseMs: Long,
    animationCycle: Int,
    onCycleComplete: () -> Unit,
    horizontalOffset: Dp
) {
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(animationCycle) {
        // Wait for this letter's turn in the sequence
        delay((index * letterDelayMs).toLong())

        // Animate up
        offsetY.animateTo(
            targetValue = -bounceHeight,
            animationSpec = tween(
                durationMillis = letterAnimationDurationMs / 2,
                easing = FastOutSlowInEasing
            )
        )

        // Animate down
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = letterAnimationDurationMs / 2,
                easing = FastOutSlowInEasing
            )
        )

        // Only the first letter manages the cycle timing
        if (index == 0) {
            // Calculate total animation time: all letters animate + pause
            val totalAnimationTime = (totalCharacters - 1) * letterDelayMs + letterAnimationDurationMs
            // Wait for any remaining time until loop pause
            val remainingTime = maxOf(0L, totalAnimationTime.toLong() - (letterDelayMs.toLong() * (totalCharacters - 1)))
            delay(remainingTime)

            // Wait for the loop pause
            delay(loopPauseMs)

            // Trigger the next cycle
            onCycleComplete()
        }
    }

    Text(
        text = char.toString(),
        style = style,
        color = color,
        modifier = Modifier
            .offset(x = horizontalOffset)
            .graphicsLayer {
                translationY = offsetY.value
            }
    )
}