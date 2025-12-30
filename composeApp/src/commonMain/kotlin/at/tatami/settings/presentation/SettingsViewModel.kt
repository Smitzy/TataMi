package at.tatami.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.ObserveIsCurrentPersonAdminUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for main Settings screen.
 *
 * Simplified to only manage admin status check for conditional club card visibility.
 * Theme, date/time format, and account management logic moved to dedicated ViewModels:
 * - SystemSettingsViewModel: Theme and date/time formats
 * - AccountSettingsViewModel: Sign out and delete account
 */
class SettingsViewModel(
    observeIsCurrentPersonAdmin: ObserveIsCurrentPersonAdminUseCase
) : ViewModel() {

    val isClubAdmin: StateFlow<Boolean> = observeIsCurrentPersonAdmin()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
}