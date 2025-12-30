package at.tatami.data.repository

import at.tatami.domain.model.auth.AuthState
import at.tatami.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Firebase implementation of AuthRepository.
 *
 * Manages the reactive authentication state by listening to Firebase auth changes.
 */
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        // Listen to Firebase auth state changes
        scope.launch {
            firebaseAuth.authStateChanged.collect { firebaseUser ->
                if (firebaseUser != null) {
                    val userId = firebaseUser.uid
                    // Reload user to get the latest email verification status from Firebase
                    // This prevents using cached/stale verification status
                    firebaseUser.reload()
                    // Check if email is verified
                    if (firebaseUser.isEmailVerified) {
                        _authState.value = AuthState.Authenticated(userId)
                    } else {
                        _authState.value = AuthState.EmailNotVerified(userId)
                    }
                } else {
                    _authState.value = AuthState.NotAuthenticated
                }
            }
        }
    }

    override suspend fun onSignIn(userId: String, emailVerified: Boolean) {
        _authState.value = if (emailVerified) {
            AuthState.Authenticated(userId)
        } else {
            AuthState.EmailNotVerified(userId)
        }
    }

    override fun onSignOut() {
        _authState.value = AuthState.NotAuthenticated
    }

    override fun onEmailVerified(userId: String) {
        if (_authState.value is AuthState.EmailNotVerified) {
            _authState.value = AuthState.Authenticated(userId)
        }
    }
}