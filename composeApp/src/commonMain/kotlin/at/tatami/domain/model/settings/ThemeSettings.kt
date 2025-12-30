package at.tatami.domain.model.settings

/**
 * Theme mode options for the app
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM // Follow system theme
}

/**
 * Settings related to app theme
 */
data class ThemeSettings(
    val mode: ThemeMode = ThemeMode.SYSTEM
)