package at.tatami.common.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number

/**
 * Simple date formatter for consistent date/time display
 */
object DateFormatter {
    /**
     * Formats a LocalDateTime based on the current locale
     * @param dateTime The date and time to format
     * @param languageCode The language code (e.g., "en", "de")
     * @return Formatted string appropriate for the locale
     */
    fun formatDateTime(dateTime: LocalDateTime, languageCode: String): String {
        val day = dateTime.day
        val month = dateTime.month.number
        val year = dateTime.year
        val hour = dateTime.hour
        val minute = dateTime.minute.toString().padStart(2, '0')
        
        return when (languageCode) {
            "de" -> {
                // German format: "22.01.2025, 19:30"
                "${day.toString().padStart(2, '0')}.${month.toString().padStart(2, '0')}.$year, $hour:$minute"
            }
            else -> {
                // English format: "Jan 22, 2025, 7:30 PM"
                val monthName = getMonthName(month, languageCode)
                val (hour12, period) = to12HourFormat(hour)
                "$monthName $day, $year, $hour12:$minute $period"
            }
        }
    }
    
    private fun getMonthName(month: Int, languageCode: String): String {
        val englishMonths = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        
        return when (languageCode) {
            "de" -> {
                // For German, we'll use numeric format
                month.toString().padStart(2, '0')
            }
            else -> englishMonths[month - 1]
        }
    }
    
    private fun to12HourFormat(hour: Int): Pair<Int, String> {
        return when {
            hour == 0 -> 12 to "AM"
            hour < 12 -> hour to "AM"
            hour == 12 -> 12 to "PM"
            else -> (hour - 12) to "PM"
        }
    }
}