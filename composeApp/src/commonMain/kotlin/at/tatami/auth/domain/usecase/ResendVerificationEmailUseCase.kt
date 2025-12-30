package at.tatami.auth.domain.usecase

import at.tatami.common.domain.AuthError
import at.tatami.domain.repository.UserRepository
import dev.gitlive.firebase.FirebaseNetworkException

/**
 * Use case for resending email verification
 */
class ResendVerificationEmailUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            userRepository.sendEmailVerification()
            Result.success(Unit)
        } catch (e: FirebaseNetworkException) {
            Result.failure(AuthError.NetworkError)
        } catch (e: Exception) {
            Result.failure(AuthError.UnknownError)
        }
    }
}