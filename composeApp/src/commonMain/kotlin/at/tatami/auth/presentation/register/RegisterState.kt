package at.tatami.auth.presentation.register

/**
 * Represents the UI state for the register screen
 */
data class RegisterState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,
    val acceptedTerms: Boolean = false,
    val showTermsDialog: Boolean = false,
    val showPrivacyDialog: Boolean = false
)