package at.tatami.common.util

import at.tatami.common.domain.validation.config.AllowedCharacters
import at.tatami.common.domain.validation.config.PersonValidationConfig
import at.tatami.common.domain.validation.config.ClubValidationConfig

/**
 * Utility functions for input filtering in text fields.
 * These functions provide consistent input filtering across the app.
 */
object InputFilters {
    
    /**
     * Creates a person name input filter that enforces the exact same constraints as validation.
     * Uses ValidationConfig as the single source of truth for allowed characters and length limits.
     * 
     * @param config The validation configuration to use (defaults to PersonValidationConfig.DEFAULT)
     * @param isFirstName Whether this is for first name (true) or last name (false)
     * @return A filter function that enforces person name constraints
     */
    fun personNameInputFilter(
        config: PersonValidationConfig = PersonValidationConfig.DEFAULT,
        isFirstName: Boolean = true
    ): (String) -> String = { text ->
        // Filter characters using the character set (O(1) lookup)
        val filtered = text.filter { char ->
            char in config.allowedNameChars
        }
        
        // Apply length limit based on whether it's first or last name
        val maxLength = if (isFirstName) config.maxFirstNameLength else config.maxLastNameLength
        filtered.take(maxLength)
    }
    
    /**
     * Creates a person first name input filter using PersonValidationConfig constraints.
     */
    fun personFirstNameInputFilter(config: PersonValidationConfig = PersonValidationConfig.DEFAULT): (String) -> String = 
        personNameInputFilter(config, isFirstName = true)
    
    /**
     * Creates a person last name input filter using PersonValidationConfig constraints.
     */
    fun personLastNameInputFilter(config: PersonValidationConfig = PersonValidationConfig.DEFAULT): (String) -> String = 
        personNameInputFilter(config, isFirstName = false)

    fun decimalInputFilter(
        maxIntegerDigits: Int = 3,
        maxDecimalDigits: Int = 1,
        displaySeparator: Char = ','
    ): (String) -> String = { text ->
        // Allow digits, comma, and dot
        val filtered = text.filter { it.isDigit() || it == ',' || it == '.' }
        
        // Normalize separators - convert all to the display separator
        val otherSeparator = if (displaySeparator == ',') '.' else ','
        val normalized = filtered.replace(otherSeparator, displaySeparator)
        
        // Split by separator (will have 1 part if no separator)
        val parts = normalized.split(displaySeparator)
        
        // Get integer part (max digits) and decimal part (max digits)
        val integerPart = parts[0].take(maxIntegerDigits)
        val decimalPart = if (parts.size > 1) parts[1].take(maxDecimalDigits) else ""
        
        // Build the result
        when {
            // No separator in input - just return the digits
            !normalized.contains(displaySeparator) -> integerPart
            // Has separator - format appropriately
            integerPart.isEmpty() && decimalPart.isNotEmpty() -> "0$displaySeparator$decimalPart"
            integerPart.isNotEmpty() && decimalPart.isEmpty() -> "$integerPart$displaySeparator"
            integerPart.isNotEmpty() && decimalPart.isNotEmpty() -> "$integerPart$displaySeparator$decimalPart"
            else -> ""
        }
    }
    
    /**
     * Creates a simple digit-only input filter.
     * 
     * @param maxDigits Maximum number of digits allowed
     * @return A filter function that allows only digits up to the specified limit
     */
    fun digitOnlyFilter(maxDigits: Int): (String) -> String = { text ->
        text.filter { it.isDigit() }.take(maxDigits)
    }
    
    /**
     * Creates a club name input filter that enforces the same constraints as ClubValidator.
     * Uses ClubValidationConfig as the single source of truth for allowed characters and length limits.
     * 
     * @param config The validation configuration to use (defaults to ClubValidationConfig.DEFAULT)
     * @return A filter function that enforces club name constraints
     */
    fun clubNameInputFilter(
        config: ClubValidationConfig = ClubValidationConfig.DEFAULT
    ): (String) -> String = { text ->
        // Remove newlines and carriage returns (not allowed in club names)
        val noNewlines = text.replace('\n', ' ').replace('\r', ' ')
        
        // Filter characters to match the allowed club name characters
        val filtered = noNewlines.filter { char ->
            char in config.allowedNameChars
        }
        
        // Apply length limit
        filtered.take(config.maxNameLength)
    }
    
    /**
     * Creates a club description input filter that enforces the same constraints as ClubValidator.
     * Uses ClubValidationConfig as the single source of truth for allowed characters and length limits.
     * 
     * @param config The validation configuration to use (defaults to ClubValidationConfig.DEFAULT)
     * @return A filter function that enforces club description constraints
     */
    fun clubDescriptionInputFilter(
        config: ClubValidationConfig = ClubValidationConfig.DEFAULT
    ): (String) -> String = { text ->
        // Remove newlines and carriage returns (not allowed in club descriptions)
        val noNewlines = text.replace('\n', ' ').replace('\r', ' ')
        
        // Filter characters to match the allowed club description characters
        val filtered = noNewlines.filter { char ->
            char in config.allowedDescriptionChars
        }
        
        // Apply length limit
        filtered.take(config.maxDescriptionLength)
    }
}