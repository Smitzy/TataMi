package at.tatami.domain.model.auth

/**
 * Represents the authentication state of the application
 */
sealed class AuthState {
    /**
     * Initial state when the app is starting and checking authentication
     */
    data object Loading : AuthState()

    /**
     * User is not authenticated
     */
    data object NotAuthenticated : AuthState()

    /**
     * User is authenticated but email is not verified
     */
    data class EmailNotVerified(val userId: String) : AuthState()

    /**
     * User is authenticated and email is verified
     */
    data class Authenticated(val userId: String) : AuthState()
}