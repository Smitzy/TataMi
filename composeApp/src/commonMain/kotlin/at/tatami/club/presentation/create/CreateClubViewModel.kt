package at.tatami.club.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.CreateClubUseCase
import at.tatami.common.domain.validation.validators.ClubValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.error_create_club_failed
import tatami.composeapp.generated.resources.error_invalid_credentials

class CreateClubViewModel(
    private val createClubUseCase: CreateClubUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateClubState())
    val state: StateFlow<CreateClubState> = _state.asStateFlow()

    private fun validateCurrentState() {
        viewModelScope.launch {
            val currentState = _state.value
            val validationState = ClubValidator.validate(
                clubName = currentState.clubName
            )

            _state.update { it.copy(validation = validationState) }
        }
    }

    fun updateClubName(clubName: String) {
        _state.update { it.copy(clubName = clubName) }
        validateCurrentState()
    }

    fun saveClub() {
        viewModelScope.launch {
            val currentState = _state.value

            // Final validation
            val validationState = ClubValidator.validate(
                clubName = currentState.clubName
            )
            _state.update { it.copy(validation = validationState) }

            if (!validationState.isValid) {
                return@launch
            }

            _state.update { it.copy(isLoading = true) }

            createClubUseCase(
                name = currentState.clubName.trim()
            ).fold(
                onSuccess = { createdClub ->
                    _state.update { it.copy(
                        isLoading = false,
                        createdClubId = createdClub.id
                    ) }
                },
                onFailure = { error ->
                    val errorMessage = when {
                        error.message?.contains("PERMISSION_DENIED") == true ->
                            getString(Res.string.error_create_club_failed)
                        error.message?.contains("UNAUTHENTICATED") == true ->
                            getString(Res.string.error_invalid_credentials)
                        else -> error.message ?: getString(Res.string.error_create_club_failed)
                    }

                    _state.update { it.copy(
                        isLoading = false,
                        validation = it.validation.copy(generalError = errorMessage)
                    ) }
                }
            )
        }
    }

    fun clearCreatedClubId() {
        _state.update { it.copy(createdClubId = null) }
    }
}