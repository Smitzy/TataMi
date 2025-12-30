package at.tatami.common.util

import java.util.Locale

/**
 * Android implementation of LocaleProvider
 */
actual class LocaleProvider {
    actual fun getCurrentLanguageCode(): String {
        // Get the current system locale
        val locale = Locale.getDefault()
        // Return the language code (e.g., "en", "de")
        return locale.language
    }
}