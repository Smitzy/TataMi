package at.tatami.common.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A standardized settings card component for consistent styling across settings screens.
 *
 * Provides a Card with a title and content area, following the established patterns
 * in the TataMi app for settings sections.
 *
 * @param title The title text displayed at the top of the card
 * @param modifier Optional modifier for the card
 * @param colors Custom card colors (defaults to standard card colors)
 * @param contentSpacing Vertical spacing between content items (default 8.dp)
 * @param content The content lambda for the card body
 */
@Composable
fun TatamiSettingsCard(
    title: String,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(),
    contentSpacing: Dp = 8.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = colors
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(contentSpacing)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            content()
        }
    }
}