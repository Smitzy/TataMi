package at.tatami.common.domain.manager

import at.tatami.domain.model.settings.ThemeMode
import at.tatami.domain.model.settings.ThemeSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manages the app theme state
 */
class ThemeManager {
    private val _themeSettings = MutableStateFlow(ThemeSettings())
    val themeSettings: StateFlow<ThemeSettings> = _themeSettings.asStateFlow()
    
    /**
     * Update the theme mode
     */
    fun setThemeMode(mode: ThemeMode) {
        _themeSettings.value = _themeSettings.value.copy(mode = mode)
    }
    
    
    /**
     * Load saved theme preferences
     */
    suspend fun loadThemeSettings(settings: ThemeSettings) {
        _themeSettings.value = settings
    }
}