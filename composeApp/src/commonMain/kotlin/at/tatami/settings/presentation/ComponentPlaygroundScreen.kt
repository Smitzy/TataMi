package at.tatami.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.MaterialShapes.Companion.Cookie7Sided
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.expressive.*
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.back

/**
 * Component Playground Screen
 *
 * Debug/showcase screen for testing Material 3 Expressive UI components.
 * Accessible from Settings for developers to reference UI patterns.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ComponentPlaygroundScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Component Playground") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section: Progress Indicators
            SectionHeader("Progress Indicators")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Linear Wavy", style = MaterialTheme.typography.labelMedium)
                LinearWavyProgressIndicator()

                Text("Linear Standard", style = MaterialTheme.typography.labelMedium)
                LinearProgressIndicator()

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Circular Wavy", style = MaterialTheme.typography.labelMedium)
                        CircularWavyProgressIndicator()
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Circular Standard", style = MaterialTheme.typography.labelMedium)
                        CircularProgressIndicator()
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Loading", style = MaterialTheme.typography.labelMedium)
                        LoadingIndicator()
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Contained", style = MaterialTheme.typography.labelMedium)
                        ContainedLoadingIndicator()
                    }
                }
            }

            HorizontalDivider()

            // Section: Button Groups
            SectionHeader("Button Groups")

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Standard Button Group", style = MaterialTheme.typography.labelMedium)
                ButtonGroupSample()

                Spacer(modifier = Modifier.height(8.dp))

                Text("Multi-Select Connected", style = MaterialTheme.typography.labelMedium)
                MultiSelectConnectedButtonGroupSample()

                Spacer(modifier = Modifier.height(8.dp))

                Text("Single-Select Connected", style = MaterialTheme.typography.labelMedium)
                SingleSelectConnectedButtonGroupSample()

                Spacer(modifier = Modifier.height(8.dp))

                Text("Multi-Select Flow Layout", style = MaterialTheme.typography.labelMedium)
                MultiSelectConnectedButtonGroupWithFlowLayoutSample()

                Spacer(modifier = Modifier.height(8.dp))

                Text("Single-Select Flow Layout", style = MaterialTheme.typography.labelMedium)
                SingleSelectConnectedButtonGroupWithFlowLayoutSample()
            }

            HorizontalDivider()

            // Section: Material Shapes
            SectionHeader("Material Shapes")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Cookie 7-Sided", style = MaterialTheme.typography.labelMedium)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(Cookie7Sided.toShape())
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            // Footer Info
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "These components showcase Material 3 Expressive API features",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary
    )
}