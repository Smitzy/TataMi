package at.tatami.auth.domain.usecase

import at.tatami.domain.repository.AuthRepository
import at.tatami.common.domain.manager.UserDataCleanupManager
import at.tatami.domain.repository.UserRepository

/**
 * Use case for signing out the current user
 * Clears session, user data, and authentication state
 */
class SignOutUseCase(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val userDataCleanupManager: UserDataCleanupManager
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            // Clear all user-specific data before signing out
            userDataCleanupManager.clearAllUserData()
            
            userRepository.signOut()
            // Update auth state on successful sign out
            authRepository.onSignOut()
            Result.success(Unit)
        } catch (e: Exception) {
            // Sign out should rarely fail, but handle it gracefully
            Result.failure(e)
        }
    }
}