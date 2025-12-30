package at.tatami.settings.presentation.account

/**
 * State for Account Settings screen.
 *
 * Manages dialog visibility and account action states (sign out).
 * Moved from main SettingsState for better separation of concerns.
 */
data class AccountSettingsState(
    val showSignOutDialog: Boolean = false
)
