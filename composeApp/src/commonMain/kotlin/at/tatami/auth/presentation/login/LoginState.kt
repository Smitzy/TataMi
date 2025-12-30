package at.tatami.auth.presentation.login

/**
 * Represents the UI state for the login screen
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordVisible: Boolean = false
)