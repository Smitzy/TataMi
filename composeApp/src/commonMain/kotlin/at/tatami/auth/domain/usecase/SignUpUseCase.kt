package at.tatami.auth.domain.usecase

import at.tatami.domain.repository.AuthRepository
import at.tatami.common.domain.AuthError
import at.tatami.common.domain.ValidationError
import at.tatami.domain.repository.UserRepository
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.auth.FirebaseAuthUserCollisionException
import dev.gitlive.firebase.auth.FirebaseAuthWeakPasswordException

/**
 * Use case for registering a new user
 * Validates input and handles registration errors
 */
class SignUpUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String
    ): Result<String> {
        // Validate email
        if (email.isBlank()) {
            return Result.failure(ValidationError.EmptyField)
        }

        // Validate password
        if (password.isBlank()) {
            return Result.failure(ValidationError.EmptyField)
        }

        // Validate password confirmation
        if (password != confirmPassword) {
            return Result.failure(ValidationError.PasswordsDoNotMatch)
        }
        
        // Attempt registration
        return try {
            val userId = userRepository.createUser(email.trim(), password)
            // Update auth state on successful registration
            // Email is not verified for new users
            authRepository.onSignIn(userId, emailVerified = false)
            Result.success(userId)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(AuthError.EmailAlreadyInUse)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(ValidationError.WeakPassword)
        } catch (e: FirebaseNetworkException) {
            Result.failure(AuthError.NetworkError)
        } catch (e: Exception) {
            Result.failure(AuthError.UnknownError)
        }
    }
}