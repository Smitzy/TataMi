package at.tatami.settings.domain.usecase

import at.tatami.domain.model.settings.DateTimeFormatSettings
import at.tatami.settings.domain.repository.DateTimeFormatRepository

/**
 * Use case for saving date/time format settings
 */
class SaveDateTimeFormatSettingsUseCase(
    private val repository: DateTimeFormatRepository
) {
    suspend operator fun invoke(settings: DateTimeFormatSettings) {
        repository.saveDateTimeFormatSettings(settings)
    }
}