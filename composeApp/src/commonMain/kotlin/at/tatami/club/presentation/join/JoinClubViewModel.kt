package at.tatami.club.presentation.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.JoinClubResult
import at.tatami.club.domain.usecase.JoinClubUseCase
import at.tatami.common.domain.ClubError
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.error_already_club_member
import tatami.composeapp.generated.resources.error_invalid_invite_code
import tatami.composeapp.generated.resources.error_network
import tatami.composeapp.generated.resources.error_unknown
import tatami.composeapp.generated.resources.invalid_club_code_length

data class JoinClubState(
    val clubCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val joinedSuccessfully: Boolean = false,
    val clubName: String? = null
)

class JoinClubViewModel(
    private val getSelectedPerson: GetSelectedPersonUseCase,
    private val joinClubUseCase: JoinClubUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(JoinClubState())
    val state: StateFlow<JoinClubState> = _state.asStateFlow()

    fun updateClubCode(code: String) {
        _state.value = _state.value.copy(
            clubCode = code.uppercase().take(8),
            errorMessage = null
        )
    }

    fun joinClub() {
        viewModelScope.launch {
            if (_state.value.clubCode.length != 8) {
                _state.value = _state.value.copy(
                    errorMessage = getString(Res.string.invalid_club_code_length)
                )
                return@launch
            }

            val person = getSelectedPerson()
            if (person != null) {
                joinClubUseCase(_state.value.clubCode, person.id)
                    .onEach { result ->
                        when (result) {
                            is JoinClubResult.Loading -> {
                                _state.value = _state.value.copy(
                                    isLoading = true,
                                    errorMessage = null
                                )
                            }
                            is JoinClubResult.JoinedSuccessfully -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    joinedSuccessfully = true,
                                    clubName = result.clubName
                                )
                            }
                            is JoinClubResult.Error -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    errorMessage = getErrorMessage(result.error)
                                )
                            }
                        }
                    }
                    .launchIn(viewModelScope)
            } else {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = getString(Res.string.error_unknown)
                )
            }
        }
    }

    private suspend fun getErrorMessage(error: ClubError): String {
        return when (error) {
            is ClubError.InvalidInviteCode -> getString(Res.string.error_invalid_invite_code)
            is ClubError.InviteCodeExpired -> getString(Res.string.error_invalid_invite_code)
            is ClubError.AlreadyMember -> getString(Res.string.error_already_club_member)
            is ClubError.ClubNotFound -> getString(Res.string.error_invalid_invite_code)
            is ClubError.NetworkError -> getString(Res.string.error_network)
            is ClubError.Unauthenticated -> getString(Res.string.error_unknown)
            is ClubError.PermissionDenied -> getString(Res.string.error_unknown)
            is ClubError.UnknownError -> getString(Res.string.error_unknown)
        }
    }
}