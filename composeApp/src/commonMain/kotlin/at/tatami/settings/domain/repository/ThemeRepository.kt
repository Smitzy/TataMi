package at.tatami.settings.domain.repository

import at.tatami.domain.model.settings.ThemeSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing theme preferences
 */
interface ThemeRepository {
    /**
     * Save theme settings to local storage
     */
    suspend fun saveThemeSettings(settings: ThemeSettings)

    /**
     * Get theme settings from local storage
     */
    suspend fun getThemeSettings(): ThemeSettings

    /**
     * Observe theme settings changes
     */
    fun observeThemeSettings(): Flow<ThemeSettings>
}