package at.tatami.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.auth.domain.usecase.SignInUseCase
import at.tatami.common.domain.AuthError
import at.tatami.common.domain.ValidationError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

/**
 * ViewModel for the login screen
 * Handles user input and authentication logic
 */
class LoginViewModel(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()
    
    /**
     * Update the email field
     */
    fun onEmailChange(email: String) {
        _state.update { currentState ->
            currentState.copy(
                email = email,
                errorMessage = null
            )
        }
    }
    
    /**
     * Update the password field
     */
    fun onPasswordChange(password: String) {
        _state.update { currentState ->
            currentState.copy(
                password = password,
                errorMessage = null
            )
        }
    }
    
    /**
     * Toggle password visibility
     */
    fun onTogglePasswordVisibility() {
        _state.update { currentState ->
            currentState.copy(
                isPasswordVisible = !currentState.isPasswordVisible
            )
        }
    }
    
    /**
     * Attempt to sign in the user
     */
    fun onSignIn() {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true,
                errorMessage = null
            )}

            val currentState = _state.value
            signInUseCase(
                email = currentState.email,
                password = currentState.password
            ).fold(
                onSuccess = { user ->
                    _state.update { it.copy(
                        isLoading = false
                    )}
                    // Navigation will be handled by auth state change
                },
                onFailure = { error ->
                    val errorMessage = getErrorMessage(error)
                    _state.update { it.copy(
                        isLoading = false,
                        errorMessage = errorMessage
                    )}
                }
            )
        }
    }
    
    /**
     * Clear any error messages
     */
    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Convert exceptions to user-friendly error messages
     *
     * Login-specific errors:
     * - EmptyField: email or password is blank
     * - InvalidEmail: email format is invalid
     * - InvalidCredentials: wrong password
     * - UserNotFound: no account with this email
     * - NetworkError: connectivity issue
     */
    private suspend fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is ValidationError.EmptyField -> getString(Res.string.error_empty_field)
            is ValidationError.InvalidEmail -> getString(Res.string.error_invalid_email)
            is AuthError.InvalidCredentials -> getString(Res.string.error_invalid_credentials)
            is AuthError.UserNotFound -> getString(Res.string.error_invalid_credentials) // Same message for security
            is AuthError.NetworkError -> getString(Res.string.error_network)
            else -> getString(Res.string.error_unknown)
        }
    }
}