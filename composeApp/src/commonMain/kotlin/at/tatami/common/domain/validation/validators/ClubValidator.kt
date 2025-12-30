package at.tatami.common.domain.validation.validators

import at.tatami.common.domain.validation.config.ClubValidationConfig
import at.tatami.common.domain.validation.states.ClubValidationState
import org.jetbrains.compose.resources.getString
import tatami.composeapp.generated.resources.*

object ClubValidator {
    suspend fun validate(
        clubName: String,
        config: ClubValidationConfig = ClubValidationConfig.DEFAULT
    ): ClubValidationState {
        val nameError = when {
            clubName.isBlank() -> getString(Res.string.validation_club_name_required)
            clubName.contains('\n') || clubName.contains('\r') -> getString(Res.string.validation_club_name_no_newlines)
            clubName.length < config.minNameLength -> getString(Res.string.validation_club_name_min_length)
            clubName.length > config.maxNameLength -> getString(Res.string.validation_club_name_max_length)
            !clubName.all { it in config.allowedNameChars } -> getString(Res.string.validation_club_name_invalid_chars)
            else -> null
        }

        return ClubValidationState(
            nameError = nameError
        )
    }
}