package at.tatami.club.presentation.timezone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import at.tatami.club.presentation.components.TimezoneData
import at.tatami.club.presentation.components.TimezoneItem
import at.tatami.common.ui.components.CustomSearchField
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun TimezonePickerScreen(
    navController: NavController,
    onTimezoneSelected: (TimezoneItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val currentTimezone = remember { TimeZone.currentSystemDefault() }
    
    // Filter timezones based on search query
    val filteredTimezones = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            // Show current timezone first, then all others
            val current = TimezoneData.allTimezones.find { it.id == currentTimezone.id }
            val others = TimezoneData.allTimezones.filter { it.id != currentTimezone.id }
            listOfNotNull(current) + others
        } else {
            TimezoneData.search(searchQuery)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.timezone_picker_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
        ) {
            // Custom search field
            CustomSearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = stringResource(Res.string.timezone_search_placeholder),
                modifier = Modifier.padding(top = 8.dp)
            )
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                // Show current timezone section when not searching
                if (searchQuery.isBlank()) {
                    item {
                        Text(
                            text = stringResource(Res.string.timezone_current_section),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    val currentTimezoneItem = TimezoneData.allTimezones.find { it.id == currentTimezone.id }
                    if (currentTimezoneItem != null) {
                        item {
                            TimezoneListItem(
                                timezone = currentTimezoneItem,
                                isCurrent = true,
                                onClick = {
                                    onTimezoneSelected(currentTimezoneItem)
                                    navController.navigateUp()
                                }
                            )
                        }
                    }
                    
                    item {
                        Text(
                            text = stringResource(Res.string.timezone_all_section),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp).padding(top = 16.dp)
                        )
                    }
                }
                
                items(
                    items = if (searchQuery.isBlank()) {
                        filteredTimezones.filter { it.id != currentTimezone.id }
                    } else {
                        filteredTimezones
                    },
                    key = { it.id }
                ) { timezone ->
                    TimezoneListItem(
                        timezone = timezone,
                        isCurrent = timezone.id == currentTimezone.id,
                        onClick = {
                            onTimezoneSelected(timezone)
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun TimezoneListItem(
    timezone: TimezoneItem,
    isCurrent: Boolean = false,
    onClick: () -> Unit
) {
    val now = kotlin.time.Clock.System.now()
    
    // Handle invalid timezone IDs gracefully
    val timeInfo = try {
        val tz = TimeZone.of(timezone.id)
        val localDateTime = now.toLocalDateTime(tz)
        val timeString = "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
        val offsetString = tz.offsetAt(now).toString()
        Triple(timeString, offsetString, true)
    } catch (e: Exception) {
        Triple("--:--", "UTC+0", false)
    }
    
    val (timeString, offsetString, isValid) = timeInfo
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = if (isCurrent) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = if (isCurrent) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timezone.displayName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isCurrent) FontWeight.Medium else FontWeight.Normal
                        ),
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    
                    if (isCurrent) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(Res.string.timezone_current_label),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Text(
                    text = "$timeString • $offsetString • ${timezone.abbreviation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isCurrent) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}