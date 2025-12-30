package at.tatami.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import at.tatami.domain.model.settings.DateFormat
import at.tatami.domain.model.settings.DateTimeFormatSettings
import at.tatami.domain.model.settings.TimeFormat
import at.tatami.settings.domain.repository.DateTimeFormatRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore implementation of DateTimeFormatRepository
 */
class DateTimeFormatRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : DateTimeFormatRepository {
    
    companion object {
        private val TIME_FORMAT_KEY = stringPreferencesKey("time_format")
        private val DATE_FORMAT_KEY = stringPreferencesKey("date_format")
    }
    
    override suspend fun saveDateTimeFormatSettings(settings: DateTimeFormatSettings) {
        dataStore.edit { preferences ->
            preferences[TIME_FORMAT_KEY] = settings.timeFormat.name
            preferences[DATE_FORMAT_KEY] = settings.dateFormat.name
        }
    }
    
    override suspend fun getDateTimeFormatSettings(): DateTimeFormatSettings {
        return dataStore.data.map { preferences ->
            val timeFormatString = preferences[TIME_FORMAT_KEY] ?: TimeFormat.TWELVE_HOUR.name
            val timeFormat = try {
                TimeFormat.valueOf(timeFormatString)
            } catch (e: IllegalArgumentException) {
                TimeFormat.TWELVE_HOUR
            }
            
            val dateFormatString = preferences[DATE_FORMAT_KEY] ?: DateFormat.MM_DD_YYYY.name
            val dateFormat = try {
                DateFormat.valueOf(dateFormatString)
            } catch (e: IllegalArgumentException) {
                DateFormat.MM_DD_YYYY
            }
            
            DateTimeFormatSettings(
                timeFormat = timeFormat,
                dateFormat = dateFormat
            )
        }.first()
    }
}