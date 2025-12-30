package at.tatami.debug

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import at.tatami.common.ui.theme.slacksideOneFamily
import kotlinx.coroutines.delay
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalTextApi::class)
@Preview
@Composable
fun PlaygroundScreen() {
    val text = "TataMi"
    val animationStates = remember { mutableStateListOf<Boolean>() }
    var horizontalAnimationStarted by remember { mutableStateOf(false) }
    var animationKey by remember { mutableStateOf(0) }

    LaunchedEffect(animationKey) {
        if (animationKey > 0) {
            // Reset states instantly for button click
            horizontalAnimationStarted = false
            animationStates.clear()
            // Force recomposition to apply instant reset
            delay(10)
        }

        // Small initial delay
        delay(100)

        // Start horizontal animation immediately with first letter
        horizontalAnimationStarted = true

        // Animate letters in from bottom
        text.forEachIndexed { index, _ ->
            delay(70) // Delay between each letter
            animationStates.add(true)
        }

        // Animation complete - ready for next trigger
    }

    // Horizontal offset for the entire text - sliding from right to left
    val globalOffsetX by animateFloatAsState(
        targetValue = if (horizontalAnimationStarted) 0f else 120f,
        animationSpec = if (animationKey == 0 || horizontalAnimationStarted) {
            tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            )
        } else {
            snap() // Instant reset when animation restarts
        },
        label = "global_slide_x"
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),
                        Color(0xFF3949AB),
                        Color(0xFF5C6BC0)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.graphicsLayer {
                    translationX = globalOffsetX.dp.toPx()
                }
            ) {
                text.forEachIndexed { index, char ->
                    val isVisible = animationStates.getOrNull(index) ?: false

                    val offsetY by animateFloatAsState(
                        targetValue = if (isVisible) 0f else 50f,
                        animationSpec = if (isVisible || animationKey == 0) {
                            tween(
                                durationMillis = 600,
                                easing = FastOutSlowInEasing
                            )
                        } else {
                            snap() // Instant reset
                        },
                        label = "letter_slide_y_$index"
                    )

                    val alpha by animateFloatAsState(
                        targetValue = if (isVisible) 1f else 0f,
                        animationSpec = if (isVisible || animationKey == 0) {
                            tween(500)
                        } else {
                            snap() // Instant reset
                        },
                        label = "letter_fade_$index"
                    )

                    Text(
                        text = char.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = slacksideOneFamily(),
                        color = Color.White,
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = offsetY.dp.toPx()
                            }
                            .alpha(alpha)
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = { animationKey++ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                )
            ) {
                Text(
                    "Play Animation",
                    color = Color.White
                )
            }
        }
    }
}