package at.tatami.common.util

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

/**
 * iOS implementation of LocaleProvider
 */
actual class LocaleProvider {
    actual fun getCurrentLanguageCode(): String {
        // Get the current system locale
        val locale = NSLocale.currentLocale
        // Return the language code (e.g., "en", "de")
        return locale.languageCode ?: "en"
    }
}