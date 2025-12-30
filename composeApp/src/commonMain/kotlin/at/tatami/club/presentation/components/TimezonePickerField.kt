package at.tatami.club.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.datetime.*
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun TimezonePickerField(
    selectedTimezone: TimeZone?,
    onTimezonePickerClick: () -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    val selectedTimezoneItem = selectedTimezone?.let { tz ->
        TimezoneData.allTimezones.find { it.id == tz.id }
    }
    
    val now = kotlin.time.Clock.System.now()
    val displayText = selectedTimezoneItem?.let { tz ->
        try {
            val timeZone = TimeZone.of(tz.id)
            val localDateTime = now.toLocalDateTime(timeZone)
            val timeString = "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
            "${tz.displayName} • $timeString"
        } catch (e: Exception) {
            "${tz.displayName} • --:--"
        }
    } ?: ""
    
    OutlinedTextField(
        value = displayText,
        onValueChange = { },
        label = { Text(stringResource(Res.string.club_timezone_label)) },
        placeholder = { Text(stringResource(Res.string.club_timezone_placeholder)) },
        readOnly = true,
        enabled = enabled,
        isError = isError,
        supportingText = errorMessage?.let { { Text(it) } },
        trailingIcon = {
            IconButton(
                onClick = onTimezonePickerClick,
                enabled = enabled
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.club_timezone_label)
                )
            }
        },
        singleLine = true,
        modifier = modifier
    )
}