package at.tatami.auth.domain.usecase

import at.tatami.domain.model.auth.AuthState
import at.tatami.domain.repository.AuthRepository
import at.tatami.domain.repository.UserRepository
import kotlin.time.ExperimentalTime

/**
 * Use case for checking email verification status
 */
class CheckEmailVerificationUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(): Result<Boolean> {
        return try {
            val isVerified = userRepository.checkEmailVerification()

            // If verified, update the auth state
            if (isVerified) {
                val currentUser = userRepository.getCurrentUser()
                if (currentUser != null) {
                    authRepository.onEmailVerified(currentUser.id)
                }
            }

            Result.success(isVerified)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}