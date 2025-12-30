@file:OptIn(kotlin.time.ExperimentalTime::class)

package at.tatami.settings.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.auth.domain.usecase.GetCurrentUserUseCase
import at.tatami.auth.domain.usecase.SignOutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for Account Settings screen.
 *
 * Manages sign out functionality with dialog states.
 * Extracted from the main SettingsViewModel for better separation of concerns.
 */
class AccountSettingsViewModel(
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUser: GetCurrentUserUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AccountSettingsState())
    val state: StateFlow<AccountSettingsState> = _state.asStateFlow()

    fun showSignOutDialog() {
        _state.update { it.copy(showSignOutDialog = true) }
    }

    fun hideSignOutDialog() {
        _state.update { it.copy(showSignOutDialog = false) }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            // Navigation will be handled by AuthStateManager
            _state.update { it.copy(showSignOutDialog = false) }
        }
    }
}
