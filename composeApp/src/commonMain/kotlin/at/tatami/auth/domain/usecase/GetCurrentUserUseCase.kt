package at.tatami.auth.domain.usecase

import at.tatami.domain.model.User
import at.tatami.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting the current authenticated user
 * Returns null if no user is signed in
 */
class GetCurrentUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? {
        return userRepository.getCurrentUser()
    }
    
    /**
     * Observe the current user for real-time updates
     */
    fun observeCurrentUser(): Flow<User?> {
        return userRepository.observeCurrentUser()
    }
}