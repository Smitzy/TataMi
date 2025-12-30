package at.tatami.auth.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.auth.domain.usecase.SendPasswordResetUseCase
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

class ForgotPasswordViewModel(
    private val sendPasswordResetUseCase: SendPasswordResetUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()
    
    fun onEmailChange(email: String) {
        _state.update { it.copy(
            email = email,
            errorMessage = null
        )}
    }
    
    fun onSendResetEmail() {
        viewModelScope.launch {
            _state.update { it.copy(
                isLoading = true,
                errorMessage = null
            )}
            
            sendPasswordResetUseCase(state.value.email).fold(
                onSuccess = {
                    _state.update { it.copy(
                        isLoading = false,
                        isEmailSent = true
                    )}
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
    
    fun resetState() {
        _state.value = ForgotPasswordState()
    }
    
    private suspend fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is ValidationError.EmptyField -> getString(Res.string.error_empty_email)
            is ValidationError.InvalidEmail -> getString(Res.string.error_invalid_email)
            is AuthError.UserNotFound -> getString(Res.string.error_user_not_found)
            is AuthError.NetworkError -> getString(Res.string.error_network)
            else -> getString(Res.string.error_send_reset_email)
        }
    }
}