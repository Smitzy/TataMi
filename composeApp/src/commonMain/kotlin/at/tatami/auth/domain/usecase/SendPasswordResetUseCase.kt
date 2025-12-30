package at.tatami.auth.domain.usecase

import at.tatami.common.domain.AuthError
import at.tatami.common.domain.ValidationError
import at.tatami.domain.repository.UserRepository
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException

class SendPasswordResetUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        // Validate email
        if (email.isBlank()) {
            return Result.failure(ValidationError.EmptyField)
        }

        return try {
            userRepository.sendPasswordResetEmail(email)
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            // For security, we don't reveal if user exists or not
            // Return success to prevent email enumeration attacks
            Result.success(Unit)
        } catch (e: FirebaseNetworkException) {
            Result.failure(AuthError.NetworkError)
        } catch (e: Exception) {
            Result.failure(AuthError.UnknownError)
        }
    }
}