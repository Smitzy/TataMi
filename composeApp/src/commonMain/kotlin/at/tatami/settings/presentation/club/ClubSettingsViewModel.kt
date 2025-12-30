@file:OptIn(kotlin.time.ExperimentalTime::class)

package at.tatami.settings.presentation.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.common.domain.service.DateTimeFormatterService
import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import at.tatami.domain.model.Club
import at.tatami.domain.repository.ClubRepository
import at.tatami.club.domain.usecase.DeleteClubUseCase
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ClubSettingsState(
    val isLoading: Boolean = true,
    val currentClub: Club? = null,
    val isAdmin: Boolean = false,
    val isOwner: Boolean = false,
    val errorMessage: String? = null,
    val isGeneratingCode: Boolean = false,
    val isRemovingCode: Boolean = false,
    val isDeleting: Boolean = false,
    val codeActionError: String? = null,
    val formattedExpirationDate: String? = null,
    val deleteSuccess: Boolean = false,
    // Edit club name state
    val isEditingClubName: Boolean = false,
    val editingClubNameValue: String = "",
    val editingClubNameError: String? = null,
    val isUpdatingClubName: Boolean = false
)

class ClubSettingsViewModel(
    private val observeSelectedClub: ObserveSelectedClubUseCase,
    private val observeSelectedPerson: ObserveSelectedPersonUseCase,
    private val getSelectedPerson: GetSelectedPersonUseCase,
    private val clubRepository: ClubRepository,
    private val dateTimeFormatter: DateTimeFormatterService,
    private val deleteClubUseCase: DeleteClubUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ClubSettingsState())
    val state: StateFlow<ClubSettingsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                observeSelectedClub(),
                observeSelectedPerson()
            ) { club, person ->
                if (club != null && person != null) {
                    val isAdmin = club.adminIds.contains(person.id)
                    val isOwner = club.ownerId == person.id

                    // Format expiration date
                    val formattedDate = club.inviteCodeExpiresAt?.let { instant ->
                        val localDateTime = instant.toLocalDateTimeInSystemTimeZone()
                        dateTimeFormatter.formatDateTime(localDateTime)
                    }

                    _state.update { it.copy(
                        isLoading = false,
                        currentClub = club,
                        isAdmin = isAdmin,
                        isOwner = isOwner,
                        formattedExpirationDate = formattedDate
                    ) }
                } else {
                    _state.update { it.copy(
                        isLoading = false,
                        errorMessage = when {
                            person == null -> "No person selected"
                            club == null -> "No club selected"
                            else -> "Unable to load settings"
                        }
                    ) }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun generateInviteCode() {
        val club = _state.value.currentClub ?: return
        val person = getSelectedPerson() ?: return

        viewModelScope.launch {
            _state.update { it.copy(isGeneratingCode = true, codeActionError = null) }
            try {
                clubRepository.generateInviteCode(club.id, person.id)
                _state.update { it.copy(isGeneratingCode = false) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isGeneratingCode = false,
                    codeActionError = e.message ?: "Failed to generate invite code"
                ) }
            }
        }
    }

    fun removeInviteCode() {
        val club = _state.value.currentClub ?: return
        val person = getSelectedPerson() ?: return

        viewModelScope.launch {
            _state.update { it.copy(isRemovingCode = true, codeActionError = null) }
            try {
                clubRepository.disableInviteCode(club.id, person.id)
                _state.update { it.copy(isRemovingCode = false) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isRemovingCode = false,
                    codeActionError = e.message ?: "Failed to remove invite code"
                ) }
            }
        }
    }

    fun clearCodeError() {
        _state.update { it.copy(codeActionError = null) }
    }

    fun deleteClub() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true, codeActionError = null) }

            deleteClubUseCase().onSuccess {
                _state.update { it.copy(
                    isDeleting = false,
                    deleteSuccess = true
                ) }
            }.onFailure { error ->
                _state.update { it.copy(
                    isDeleting = false,
                    codeActionError = error.message ?: "Failed to delete club"
                ) }
            }
        }
    }

    // Edit club name functions
    fun startEditClubName() {
        val currentName = _state.value.currentClub?.name ?: ""
        _state.update { it.copy(
            isEditingClubName = true,
            editingClubNameValue = currentName,
            editingClubNameError = null
        ) }
    }

    fun updateEditingClubName(newName: String) {
        _state.update { it.copy(editingClubNameValue = newName) }
        validateClubName(newName)
    }

    private fun validateClubName(name: String) {
        val error = when {
            name.isBlank() -> "Club name is required"
            name.length < 3 -> "Club name must be at least 3 characters"
            name.length > 40 -> "Club name must be less than 40 characters"
            else -> null
        }
        _state.update { it.copy(editingClubNameError = error) }
    }

    fun saveClubName() {
        val club = _state.value.currentClub ?: return
        val newName = _state.value.editingClubNameValue.trim()

        // Validate before saving
        validateClubName(newName)
        if (_state.value.editingClubNameError != null) return

        // Don't save if name hasn't changed
        if (newName == club.name) {
            cancelEditClubName()
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isUpdatingClubName = true, editingClubNameError = null) }
            try {
                clubRepository.updateClub(club.copy(name = newName))
                _state.update { it.copy(
                    isUpdatingClubName = false,
                    isEditingClubName = false,
                    editingClubNameValue = ""
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    isUpdatingClubName = false,
                    editingClubNameError = e.message ?: "Failed to update club name"
                ) }
            }
        }
    }

    fun cancelEditClubName() {
        _state.update { it.copy(
            isEditingClubName = false,
            editingClubNameValue = "",
            editingClubNameError = null
        ) }
    }
}