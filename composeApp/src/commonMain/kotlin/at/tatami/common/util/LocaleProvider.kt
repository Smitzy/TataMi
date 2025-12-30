package at.tatami.common.util

/**
 * Provides the current locale/language code for the platform
 */
expect class LocaleProvider() {
    /**
     * Gets the current language code (e.g., "en", "de")
     * This will be used to set Firebase Auth email language
     */
    fun getCurrentLanguageCode(): String
}