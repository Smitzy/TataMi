package at.tatami.settings.domain.repository

import at.tatami.domain.model.settings.DateTimeFormatSettings

/**
 * Repository for managing date and time format preferences
 */
interface DateTimeFormatRepository {
    /**
     * Save date/time format settings to local storage
     */
    suspend fun saveDateTimeFormatSettings(settings: DateTimeFormatSettings)

    /**
     * Get date/time format settings from local storage
     */
    suspend fun getDateTimeFormatSettings(): DateTimeFormatSettings
}