package at.tatami.auth.presentation.emailverification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.auth.domain.usecase.CheckEmailVerificationUseCase
import at.tatami.auth.domain.usecase.ResendVerificationEmailUseCase
import at.tatami.auth.domain.usecase.SignOutUseCase
import at.tatami.auth.domain.usecase.GetCurrentUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

class EmailVerificationViewModel(
    private val checkEmailVerificationUseCase: CheckEmailVerificationUseCase,
    private val resendVerificationEmailUseCase: ResendVerificationEmailUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(EmailVerificationState())
    val state: StateFlow<EmailVerificationState> = _state.asStateFlow()
    
    init {
        loadUserEmail()
        // Check verification status on init
        checkVerificationStatus()
        // Start cooldown timer since email was just sent during registration
        _state.update { it.copy(canResendEmail = false) }
        startResendCooldown()
    }
    
    private fun loadUserEmail() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            if (user != null) {
                _state.update { it.copy(userEmail = user.email) }
            }
        }
    }
    
    fun checkVerificationStatus() {
        viewModelScope.launch {
            _state.update { it.copy(isCheckingVerification = true, errorMessage = null) }

            try {
                checkEmailVerificationUseCase().fold(
                    onSuccess = { isVerified ->
                        println("DEBUG: Email verification check result - isVerified=$isVerified")
                        _state.update {
                            it.copy(
                                isCheckingVerification = false,
                                isEmailVerified = isVerified
                            )
                        }
                    },
                    onFailure = { error ->
                        println("DEBUG: Email verification check failed - ${error.message}")
                        val errorMessage = error.message ?: "Failed to check email verification"
                        _state.update {
                            it.copy(
                                isCheckingVerification = false,
                                errorMessage = errorMessage
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Email verification check error - ${e.message}")
                _state.update {
                    it.copy(
                        isCheckingVerification = false,
                        errorMessage = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }
    
    fun resendVerificationEmail() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isResendingEmail = true,
                    errorMessage = null,
                    showSuccessMessage = false
                )
            }

            try {
                resendVerificationEmailUseCase().fold(
                    onSuccess = {
                        println("DEBUG: Resend verification email success")
                        _state.update {
                            it.copy(
                                isResendingEmail = false,
                                showSuccessMessage = true,
                                canResendEmail = false
                            )
                        }

                        // Start cooldown timer
                        startResendCooldown()
                    },
                    onFailure = { error ->
                        println("DEBUG: Resend verification email failed - ${error.message}")
                        val errorMessage = error.message ?: "Failed to send verification email"
                        _state.update {
                            it.copy(
                                isResendingEmail = false,
                                errorMessage = errorMessage
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                println("DEBUG: Resend verification email error - ${e.message}")
                _state.update {
                    it.copy(
                        isResendingEmail = false,
                        errorMessage = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }
    
    private fun startResendCooldown() {
        viewModelScope.launch {
            for (seconds in 60 downTo 1) {
                _state.update { it.copy(resendCooldownSeconds = seconds) }
                delay(1000)
            }
            _state.update { 
                it.copy(
                    canResendEmail = true,
                    resendCooldownSeconds = 0,
                    showSuccessMessage = false
                )
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
        }
    }
}

data class EmailVerificationState(
    val userEmail: String = "",
    val isCheckingVerification: Boolean = false,
    val isResendingEmail: Boolean = false,
    val isEmailVerified: Boolean = false,
    val canResendEmail: Boolean = true,
    val resendCooldownSeconds: Int = 0,
    val showSuccessMessage: Boolean = false,
    val errorMessage: String? = null
)