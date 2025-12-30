package at.tatami.settings.domain.usecase

import at.tatami.domain.model.settings.ThemeMode
import at.tatami.settings.domain.repository.ThemeRepository
import at.tatami.common.domain.manager.ThemeManager

/**
 * Use case for saving theme settings to storage
 */
class SaveThemeSettingsUseCase(
    private val themeRepository: ThemeRepository,
    private val themeManager: ThemeManager
) {
    suspend fun setThemeMode(mode: ThemeMode) {
        themeManager.setThemeMode(mode)
        themeRepository.saveThemeSettings(themeManager.themeSettings.value)
    }
}