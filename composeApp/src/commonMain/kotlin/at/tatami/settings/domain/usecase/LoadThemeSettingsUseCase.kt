package at.tatami.settings.domain.usecase

import at.tatami.common.domain.manager.ThemeManager
import at.tatami.settings.domain.repository.ThemeRepository

/**
 * Use case for loading theme settings from storage and applying them
 */
class LoadThemeSettingsUseCase(
    private val themeRepository: ThemeRepository,
    private val themeManager: ThemeManager
) {
    suspend operator fun invoke() {
        val settings = themeRepository.getThemeSettings()
        themeManager.loadThemeSettings(settings)
    }
}