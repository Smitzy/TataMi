package at.tatami.common.domain.validation.config

import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Configuration for person validation rules.
 * Allows customization of validation parameters without changing the validation logic.
 */
@OptIn(ExperimentalTime::class)
data class PersonValidationConfig(
    val minFirstNameLength: Int = 2,
    val maxFirstNameLength: Int = 15,
    val minLastNameLength: Int = 2,
    val maxLastNameLength: Int = 15,
    val allowedNameChars: Set<Char> = AllowedCharacters.NAME_CHARS,
    val minYearOfBirth: Int = 1900,
    val maxYearOfBirth: Int = Clock.System.now().toLocalDateTimeInSystemTimeZone().year
) {
    companion object {
        val DEFAULT = PersonValidationConfig()
    }
}

/**
 * Configuration for club validation rules.
 * Allows customization of validation parameters without changing the validation logic.
 */
data class ClubValidationConfig(
    val minNameLength: Int = 3,
    val maxNameLength: Int = 40,
    val minDescriptionLength: Int = 10,
    val maxDescriptionLength: Int = 200,
    val allowedNameChars: Set<Char> = AllowedCharacters.ALL_LETTERS + AllowedCharacters.SPECIAL_CHARS + AllowedCharacters.CLUB_PUNCTUATION,
    val allowedDescriptionChars: Set<Char> = AllowedCharacters.ALL_LETTERS + AllowedCharacters.SPECIAL_CHARS + AllowedCharacters.NUMBERS + AllowedCharacters.CLUB_DESCRIPTION_PUNCTUATION
) {
    companion object {
        val DEFAULT = ClubValidationConfig()
    }
    
    // Create regex patterns from character sets for backward compatibility
    val namePattern: Regex by lazy {
        val escapedChars = allowedNameChars.joinToString("") { char ->
            when (char) {
                '-', '.', '\'', '&' -> "\\$char"
                ' ' -> "\\s"
                else -> char.toString()
            }
        }
        Regex("^[$escapedChars]+$")
    }
    
    val descriptionPattern: Regex by lazy {
        val escapedChars = allowedDescriptionChars.joinToString("") { char ->
            when (char) {
                '-', '.', '\'', '&', '!', '?', '"', '(', ')', ':', ';', '/', ',' -> "\\$char"
                ' ' -> "\\s"
                else -> char.toString()
            }
        }
        Regex("^[$escapedChars]+$")
    }
}