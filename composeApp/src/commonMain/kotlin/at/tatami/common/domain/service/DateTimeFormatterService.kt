package at.tatami.common.domain.service

import at.tatami.domain.model.settings.DateFormat
import at.tatami.domain.model.settings.DateTimeFormatSettings
import at.tatami.domain.model.settings.TimeFormat
import at.tatami.settings.domain.usecase.GetDateTimeFormatSettingsUseCase
import at.tatami.common.util.LocaleProvider
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.number

/**
 * Service for formatting dates and times according to user preferences
 */
class DateTimeFormatterService(
    private val getDateTimeFormatSettings: GetDateTimeFormatSettingsUseCase,
    private val localeProvider: LocaleProvider
) {
    
    /**
     * Format a LocalDateTime according to user preferences
     */
    suspend fun formatDateTime(dateTime: LocalDateTime): String {
        val settings = getDateTimeFormatSettings()
        val date = formatDate(dateTime.date, settings.dateFormat)
        val time = formatTime(dateTime.hour, dateTime.minute, settings.timeFormat)
        return "$date $time"
    }
    
    /**
     * Format a LocalDate according to user preferences
     */
    suspend fun formatDate(date: LocalDate): String {
        val settings = getDateTimeFormatSettings()
        return formatDate(date, settings.dateFormat)
    }
    
    /**
     * Format time according to user preferences
     */
    suspend fun formatTime(hour: Int, minute: Int): String {
        val settings = getDateTimeFormatSettings()
        return formatTime(hour, minute, settings.timeFormat)
    }
    
    private fun formatDate(date: LocalDate, format: DateFormat): String {
        val day = date.day.toString().padStart(2, '0')
        val month = date.month.number.toString().padStart(2, '0')
        val year = date.year
        val monthName = getLocalizedMonthName(date.month.number)
        
        return when (format) {
            DateFormat.MM_DD_YYYY -> "$month.$day.$year"
            DateFormat.DD_MM_YYYY -> "$day.$month.$year"
            DateFormat.YYYY_MM_DD -> "$year-$month-$day"
            DateFormat.DD_MONTH_YYYY -> "$day $monthName $year"
            DateFormat.MONTH_DD_YYYY -> "$monthName $day, $year"
        }
    }
    
    private fun formatTime(hour: Int, minute: Int, format: TimeFormat): String {
        val minuteStr = minute.toString().padStart(2, '0')
        
        return when (format) {
            TimeFormat.TWELVE_HOUR -> {
                val (hour12, period) = to12HourFormat(hour)
                "$hour12:$minuteStr $period"
            }
            TimeFormat.TWENTY_FOUR_HOUR -> {
                val hourStr = hour.toString().padStart(2, '0')
                "$hourStr:$minuteStr"
            }
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
    
    private fun getLocalizedMonthName(month: Int): String {
        val languageCode = localeProvider.getCurrentLanguageCode()
        
        return when (languageCode) {
            "de" -> listOf(
                "Januar", "Februar", "März", "April", "Mai", "Juni",
                "Juli", "August", "September", "Oktober", "November", "Dezember"
            )[month - 1]
            else -> listOf(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
            )[month - 1]
        }
    }
}