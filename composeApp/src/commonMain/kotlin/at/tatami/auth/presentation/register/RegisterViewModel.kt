package at.tatami.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.auth.domain.usecase.SignUpUseCase
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

class RegisterViewModel(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()
    
    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, errorMessage = null) }
    }
    
    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, errorMessage = null) }
    }
    
    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }
    
    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }
    
    fun onToggleConfirmPasswordVisibility() {
        _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }
    
    fun onToggleTermsAcceptance() {
        _state.update { it.copy(acceptedTerms = !it.acceptedTerms) }
    }
    
    fun onShowTermsOfService() {
        _state.update { it.copy(showTermsDialog = true) }
    }
    
    fun onDismissTermsOfService() {
        _state.update { it.copy(showTermsDialog = false) }
    }
    
    fun onShowPrivacyPolicy() {
        _state.update { it.copy(showPrivacyDialog = true) }
    }
    
    fun onDismissPrivacyPolicy() {
        _state.update { it.copy(showPrivacyDialog = false) }
    }
    
    fun onSignUp() {
        viewModelScope.launch {
            val currentState = _state.value
            
            // Basic validation for terms acceptance
            if (!currentState.acceptedTerms) {
                _state.update { it.copy(errorMessage = getString(Res.string.error_accept_terms)) }
                return@launch
            }
        
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            
            signUpUseCase(currentState.email, currentState.password, currentState.confirmPassword)
                .fold(
                    onSuccess = {
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                isRegistrationSuccessful = true
                            )
                        }
                    },
                    onFailure = { error ->
                        // Registration-specific errors:
                        // - EmptyField: email, password, or confirm password is blank
                        // - InvalidEmail: email format is invalid
                        // - WeakPassword: password doesn't meet strength requirements
                        // - PasswordsDoNotMatch: password and confirm password differ
                        // - EmailAlreadyInUse: account already exists
                        // - NetworkError: connectivity issue
                        val errorMessage = when (error) {
                            is ValidationError.EmptyField -> getString(Res.string.error_empty_field)
                            is ValidationError.InvalidEmail -> getString(Res.string.error_invalid_email)
                            is ValidationError.WeakPassword -> getString(Res.string.error_weak_password)
                            is ValidationError.PasswordsDoNotMatch -> getString(Res.string.error_passwords_dont_match)
                            is AuthError.EmailAlreadyInUse -> getString(Res.string.error_email_already_in_use)
                            is AuthError.NetworkError -> getString(Res.string.error_network)
                            else -> getString(Res.string.error_unknown)
                        }
                        _state.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = errorMessage
                            )
                        }
                    }
                )
        }
    }
    
}