package at.tatami.auth.domain.usecase

import at.tatami.domain.model.auth.AuthState
import at.tatami.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * Use case for observing authentication state changes
 */
class ObserveAuthStateUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): StateFlow<AuthState> = authRepository.authState
}