package at.tatami.event.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.event.domain.usecase.CreateEventUseCase
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class EventCreateViewModel(
    private val createEventUseCase: CreateEventUseCase,
    private val getSelectedClubUseCase: GetSelectedClubUseCase,
    private val getPersonByIdUseCase: GetPersonByIdUseCase,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title: StateFlow<String> = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _startDateTime = MutableStateFlow<LocalDateTime?>(null)
    val startDateTime: StateFlow<LocalDateTime?> = _startDateTime.asStateFlow()

    private val _invitedPersonIds = MutableStateFlow<List<String>>(emptyList())
    val invitedPersonIds: StateFlow<List<String>> = _invitedPersonIds.asStateFlow()

    private val _members = MutableStateFlow<List<MemberItem>>(emptyList())
    val members: StateFlow<List<MemberItem>> = _members.asStateFlow()

    private val _selectedMemberIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedMemberIds: StateFlow<Set<String>> = _selectedMemberIds.asStateFlow()

    private val _isMembersLoading = MutableStateFlow(false)
    val isMembersLoading: StateFlow<Boolean> = _isMembersLoading.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setLocation(location: String) {
        _location.value = location
    }

    fun setStartDateTime(dateTime: LocalDateTime) {
        _startDateTime.value = dateTime
    }

    fun setInvitedPersonIds(personIds: List<String>) {
        _invitedPersonIds.value = personIds
    }

    fun loadClubMembers(initialSelectedIds: List<String> = emptyList()) {
        viewModelScope.launch {
            _isMembersLoading.value = true

            val club = getSelectedClubUseCase()
            val creator = getSelectedPersonUseCase()

            if (club != null) {
                val memberList = mutableListOf<MemberItem>()
                val initialSelectedSet = initialSelectedIds.toMutableSet()

                for (memberId in club.memberIds) {
                    val person = getPersonByIdUseCase(memberId)
                    if (person != null) {
                        val isSelected = initialSelectedSet.contains(memberId)
                        memberList.add(
                            MemberItem(
                                personId = memberId,
                                name = "${person.firstName} ${person.lastName}",
                                profileImageUrl = person.personImgUrl,
                                isCreator = memberId == creator?.id,
                                isSelected = isSelected
                            )
                        )
                    }
                }

                _members.value = memberList
                _selectedMemberIds.value = initialSelectedSet
            }

            _isMembersLoading.value = false
        }
    }

    fun setInitialSelection(initialSelectedIds: List<String>) {
        loadClubMembers(initialSelectedIds)
    }

    fun toggleMemberSelection(personId: String) {
        val current = _selectedMemberIds.value.toMutableSet()

        if (current.contains(personId)) {
            current.remove(personId)
        } else {
            current.add(personId)
        }

        _selectedMemberIds.value = current

        // Update members list UI
        val updatedMembers = _members.value.map { member ->
            if (member.personId == personId) {
                member.copy(isSelected = !member.isSelected)
            } else {
                member
            }
        }
        _members.value = updatedMembers
    }

    fun createEvent() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val dateTime = _startDateTime.value
            if (_title.value.isEmpty() || dateTime == null || _location.value.isEmpty()) {
                _error.value = "Please fill in all required fields"
                _isLoading.value = false
                return@launch
            }

            val result = createEventUseCase(
                title = _title.value,
                description = _description.value,
                startDateTime = dateTime,
                location = _location.value,
                invitedPersonIds = _invitedPersonIds.value
            )

            _isLoading.value = false
            if (result.isSuccess) {
                _success.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to create event"
            }
        }
    }

    data class MemberItem(
        val personId: String,
        val name: String,
        val profileImageUrl: String?,
        val isCreator: Boolean,
        val isSelected: Boolean
    )
}