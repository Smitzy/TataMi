package at.tatami.settings.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.TatamiSettingsCard
import at.tatami.domain.model.settings.DateFormat
import at.tatami.domain.model.settings.TimeFormat
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*

@Composable
fun DateTimeFormatSettingsCard(
    timeFormat: TimeFormat,
    dateFormat: DateFormat,
    onTimeFormatChange: (TimeFormat) -> Unit,
    onDateFormatChange: (DateFormat) -> Unit
) {
    TatamiSettingsCard(
        title = stringResource(Res.string.date_time_format),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        // Time Format Section
        Text(
            text = stringResource(Res.string.time_format),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FormatOption(
            text = stringResource(Res.string.time_format_12_hour),
            example = "1:30 PM",
            selected = timeFormat == TimeFormat.TWELVE_HOUR,
            onClick = { onTimeFormatChange(TimeFormat.TWELVE_HOUR) }
        )

        FormatOption(
            text = stringResource(Res.string.time_format_24_hour),
            example = "13:30",
            selected = timeFormat == TimeFormat.TWENTY_FOUR_HOUR,
            onClick = { onTimeFormatChange(TimeFormat.TWENTY_FOUR_HOUR) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Date Format Section
        Text(
            text = stringResource(Res.string.date_format),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FormatOption(
            text = stringResource(Res.string.date_format_mm_dd_yyyy),
            example = "07/31/2025",
            selected = dateFormat == DateFormat.MM_DD_YYYY,
            onClick = { onDateFormatChange(DateFormat.MM_DD_YYYY) }
        )

        FormatOption(
            text = stringResource(Res.string.date_format_dd_mm_yyyy),
            example = "31/07/2025",
            selected = dateFormat == DateFormat.DD_MM_YYYY,
            onClick = { onDateFormatChange(DateFormat.DD_MM_YYYY) }
        )

        FormatOption(
            text = stringResource(Res.string.date_format_yyyy_mm_dd),
            example = "2025-07-31",
            selected = dateFormat == DateFormat.YYYY_MM_DD,
            onClick = { onDateFormatChange(DateFormat.YYYY_MM_DD) }
        )

        FormatOption(
            text = stringResource(Res.string.date_format_dd_month_yyyy),
            example = "31 July 2025",
            selected = dateFormat == DateFormat.DD_MONTH_YYYY,
            onClick = { onDateFormatChange(DateFormat.DD_MONTH_YYYY) }
        )

        FormatOption(
            text = stringResource(Res.string.date_format_month_dd_yyyy),
            example = "July 31, 2025",
            selected = dateFormat == DateFormat.MONTH_DD_YYYY,
            onClick = { onDateFormatChange(DateFormat.MONTH_DD_YYYY) }
        )
    }
}

@Composable
private fun FormatOption(
    text: String,
    example: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = example,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}