package at.tatami.auth.domain.usecase

import at.tatami.domain.repository.AuthRepository
import at.tatami.common.domain.AuthError
import at.tatami.common.domain.ValidationError
import at.tatami.domain.repository.UserRepository
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidCredentialsException
import dev.gitlive.firebase.auth.FirebaseAuthInvalidUserException

/**
 * Use case for signing in an existing user
 * Validates input and handles authentication errors
 */
class SignInUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<String> {
        // Validate email
        if (email.isBlank()) {
            return Result.failure(ValidationError.EmptyField)
        }

        // Validate password - only check if not empty for sign in
        if (password.isBlank()) {
            return Result.failure(ValidationError.EmptyField)
        }

        // Attempt sign in
        return try {
            val userId = userRepository.signIn(email.trim(), password)
            // Get email verified status from Firebase Auth
            val emailVerified = userRepository.checkEmailVerification()
            // Update auth state on successful sign in
            authRepository.onSignIn(userId, emailVerified)
            // Update lastLoginAt via Cloud Function (after auth state is set)
            // This is a fire-and-forget operation that won't block the login flow
            userRepository.updateLastLoginAt()
            Result.success(userId)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(AuthError.InvalidCredentials)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(AuthError.UserNotFound)
        } catch (e: FirebaseNetworkException) {
            Result.failure(AuthError.NetworkError)
        } catch (e: Exception) {
            Result.failure(AuthError.UnknownError)
        }
    }
}