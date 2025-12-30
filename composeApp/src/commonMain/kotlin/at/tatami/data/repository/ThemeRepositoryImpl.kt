package at.tatami.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import at.tatami.domain.model.settings.ThemeMode
import at.tatami.domain.model.settings.ThemeSettings
import at.tatami.settings.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore implementation of ThemeRepository
 */
class ThemeRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : ThemeRepository {
    
    companion object {
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }
    
    override suspend fun saveThemeSettings(settings: ThemeSettings) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = settings.mode.name
        }
    }
    
    override suspend fun getThemeSettings(): ThemeSettings {
        return observeThemeSettings().first()
    }
    
    override fun observeThemeSettings(): Flow<ThemeSettings> {
        return dataStore.data
            .catch { exception ->
                // If there's an error reading preferences, emit default
                emit(emptyPreferences())
            }
            .map { preferences ->
                val themeModeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
                val themeMode = try {
                    ThemeMode.valueOf(themeModeString)
                } catch (e: IllegalArgumentException) {
                    ThemeMode.SYSTEM
                }
                
                ThemeSettings(
                    mode = themeMode
                )
            }
    }
}