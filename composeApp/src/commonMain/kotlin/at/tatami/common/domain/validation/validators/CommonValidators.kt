package at.tatami.common.domain.validation.validators

/**
 * Helper function to check if a string has consecutive special characters.
 * @param text The text to check
 * @param specialChars The set of special characters to check for
 * @return true if the text contains two consecutive special characters
 */
fun hasConsecutiveSpecialChars(text: String, specialChars: Set<Char>): Boolean {
    for (i in 0 until text.length - 1) {
        if (text[i] in specialChars && text[i + 1] in specialChars) {
            return true
        }
    }
    return false
}