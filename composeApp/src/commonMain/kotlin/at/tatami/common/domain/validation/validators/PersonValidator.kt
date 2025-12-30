package at.tatami.common.domain.validation.validators

import at.tatami.common.domain.validation.config.AllowedCharacters
import at.tatami.common.domain.validation.config.PersonValidationConfig
import at.tatami.common.domain.validation.states.PersonValidationState
import at.tatami.domain.model.Sex
import org.jetbrains.compose.resources.getString
import tatami.composeapp.generated.resources.*

object PersonValidator {
    suspend fun validate(
        firstName: String,
        lastName: String,
        yearOfBirth: Int?,
        sex: Sex?,
        config: PersonValidationConfig = PersonValidationConfig.DEFAULT
    ): PersonValidationState {
        // Basic field validation
        val firstNameError = when {
            firstName.isBlank() -> getString(Res.string.validation_first_name_required)
            firstName.length < config.minFirstNameLength -> getString(Res.string.validation_first_name_min_length, config.minFirstNameLength)
            firstName.length > config.maxFirstNameLength -> getString(Res.string.validation_first_name_max_length, config.maxFirstNameLength)
            !firstName.all { it in config.allowedNameChars } -> getString(Res.string.validation_first_name_invalid_chars)
            hasConsecutiveSpecialChars(firstName, AllowedCharacters.SPECIAL_CHARS) -> getString(Res.string.validation_first_name_invalid_consecutive)
            else -> null
        }

        val lastNameError = when {
            lastName.isBlank() -> getString(Res.string.validation_last_name_required)
            lastName.length < config.minLastNameLength -> getString(Res.string.validation_last_name_min_length, config.minLastNameLength)
            lastName.length > config.maxLastNameLength -> getString(Res.string.validation_last_name_max_length, config.maxLastNameLength)
            !lastName.all { it in config.allowedNameChars } -> getString(Res.string.validation_last_name_invalid_chars)
            hasConsecutiveSpecialChars(lastName, AllowedCharacters.SPECIAL_CHARS) -> getString(Res.string.validation_last_name_invalid_consecutive)
            else -> null
        }

        // Year of birth validation (always required)
        val yearOfBirthError = when {
            yearOfBirth == null -> getString(Res.string.validation_year_of_birth_required)
            yearOfBirth < config.minYearOfBirth -> getString(Res.string.validation_year_of_birth_min)
            yearOfBirth > config.maxYearOfBirth -> getString(Res.string.validation_year_of_birth_future)
            else -> null
        }

        // Sex validation (always required)
        val sexError = if (sex == null) {
            getString(Res.string.validation_sex_required)
        } else null

        return PersonValidationState(
            firstNameError = firstNameError,
            lastNameError = lastNameError,
            yearOfBirthError = yearOfBirthError,
            sexError = sexError
        )
    }
}