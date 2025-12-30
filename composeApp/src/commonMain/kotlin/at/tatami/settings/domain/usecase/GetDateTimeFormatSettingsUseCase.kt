package at.tatami.settings.domain.usecase

import at.tatami.domain.model.settings.DateTimeFormatSettings
import at.tatami.settings.domain.repository.DateTimeFormatRepository

/**
 * Use case for getting date/time format settings
 */
class GetDateTimeFormatSettingsUseCase(
    private val repository: DateTimeFormatRepository
) {
    suspend operator fun invoke(): DateTimeFormatSettings {
        return repository.getDateTimeFormatSettings()
    }
}