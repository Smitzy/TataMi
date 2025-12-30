package at.tatami.domain.model.settings

/**
 * Time format options for displaying time
 */
enum class TimeFormat {
    TWELVE_HOUR,    // 1:30 PM, 11:45 AM
    TWENTY_FOUR_HOUR // 13:30, 23:45
}

/**
 * Date format options for displaying dates
 */
enum class DateFormat {
    MM_DD_YYYY,     // 07/31/2025 (American)
    DD_MM_YYYY,     // 31/07/2025 (European)
    YYYY_MM_DD,     // 2025-07-31 (ISO 8601)
    DD_MONTH_YYYY,  // 31 July 2025
    MONTH_DD_YYYY   // July 31, 2025 (American long)
}

/**
 * Settings for date and time formatting preferences
 */
data class DateTimeFormatSettings(
    val timeFormat: TimeFormat = TimeFormat.TWELVE_HOUR,
    val dateFormat: DateFormat = DateFormat.MM_DD_YYYY
)