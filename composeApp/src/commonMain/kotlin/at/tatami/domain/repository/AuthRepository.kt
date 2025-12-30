package at.tatami.domain.repository

import at.tatami.domain.model.auth.AuthState
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for managing authentication state observation.
 *
 * Note: Authentication operations (signIn, signOut, etc.) are handled by UserRepository.
 * This repository focuses on observing and updating the reactive auth state.
 */
interface AuthRepository {
    /**
     * Observable authentication state
     */
    val authState: StateFlow<AuthState>

    /**
     * Update auth state after successful sign in
     */
    suspend fun onSignIn(userId: String, emailVerified: Boolean)

    /**
     * Update auth state after sign out
     */
    fun onSignOut()

    /**
     * Update auth state after email verification
     */
    fun onEmailVerified(userId: String)
}