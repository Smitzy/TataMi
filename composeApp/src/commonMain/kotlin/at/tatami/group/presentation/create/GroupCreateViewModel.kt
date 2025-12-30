package at.tatami.group.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.group.domain.usecase.CreateGroupUseCase
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Group Create screen.
 * Manages dual selection state: members AND trainers (with constraint: trainers ⊆ members).
 *
 * Key State Management:
 * - selectedMemberIds: Set of person IDs selected as members
 * - selectedTrainerIds: Set of person IDs selected as trainers (must be subset of members)
 * - members: List of MemberTrainerItem with both selection flags
 */
class GroupCreateViewModel(
    private val createGroupUseCase: CreateGroupUseCase,
    private val getSelectedClubUseCase: GetSelectedClubUseCase,
    private val getPersonByIdUseCase: GetPersonByIdUseCase,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _members = MutableStateFlow<List<MemberTrainerItem>>(emptyList())
    val members: StateFlow<List<MemberTrainerItem>> = _members.asStateFlow()

    private val _selectedMemberIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedMemberIds: StateFlow<Set<String>> = _selectedMemberIds.asStateFlow()

    private val _selectedTrainerIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedTrainerIds: StateFlow<Set<String>> = _selectedTrainerIds.asStateFlow()

    private val _isMembersLoading = MutableStateFlow(false)
    val isMembersLoading: StateFlow<Boolean> = _isMembersLoading.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    fun setName(name: String) {
        _name.value = name
    }

    /**
     * Loads all club members and marks the initial selection state.
     * Called when the member/trainer selection BottomSheet opens.
     *
     * @param initialSelectedMemberIds Previously selected member IDs (for re-opening sheet)
     * @param initialSelectedTrainerIds Previously selected trainer IDs (for re-opening sheet)
     */
    fun loadClubMembers(
        initialSelectedMemberIds: List<String> = emptyList(),
        initialSelectedTrainerIds: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            _isMembersLoading.value = true

            val club = getSelectedClubUseCase()
            val creator = getSelectedPersonUseCase()

            if (club != null) {
                val memberList = mutableListOf<MemberTrainerItem>()
                val initialMemberSet = initialSelectedMemberIds.toMutableSet()
                val initialTrainerSet = initialSelectedTrainerIds.toMutableSet()

                // Fetch all club members
                for (memberId in club.memberIds) {
                    val person = getPersonByIdUseCase(memberId)
                    if (person != null) {
                        val isSelectedAsMember = initialMemberSet.contains(memberId)
                        val isSelectedAsTrainer = initialTrainerSet.contains(memberId)
                        memberList.add(
                            MemberTrainerItem(
                                personId = memberId,
                                name = "${person.firstName} ${person.lastName}",
                                profileImageUrl = person.personImgUrl,
                                isCreator = memberId == creator?.id,
                                isSelectedAsMember = isSelectedAsMember,
                                isSelectedAsTrainer = isSelectedAsTrainer
                            )
                        )
                    }
                }

                _members.value = memberList
                _selectedMemberIds.value = initialMemberSet
                _selectedTrainerIds.value = initialTrainerSet
            }

            _isMembersLoading.value = false
        }
    }

    /**
     * Toggles member selection for a person.
     * CRITICAL: If deselecting a member, also removes them from trainers (enforces constraint).
     *
     * @param personId The person ID to toggle
     */
    fun toggleMemberSelection(personId: String) {
        val currentMembers = _selectedMemberIds.value.toMutableSet()
        val currentTrainers = _selectedTrainerIds.value.toMutableSet()

        if (currentMembers.contains(personId)) {
            // Deselecting as member → MUST remove from trainers too (enforce constraint)
            currentMembers.remove(personId)
            currentTrainers.remove(personId)
        } else {
            // Selecting as member
            currentMembers.add(personId)
        }

        _selectedMemberIds.value = currentMembers
        _selectedTrainerIds.value = currentTrainers

        // Update members list UI to reflect both changes
        val updatedMembers = _members.value.map { member ->
            if (member.personId == personId) {
                member.copy(
                    isSelectedAsMember = currentMembers.contains(personId),
                    isSelectedAsTrainer = currentTrainers.contains(personId)
                )
            } else {
                member
            }
        }
        _members.value = updatedMembers
    }

    /**
     * Toggles trainer status for a person.
     * Can only toggle if the person is already selected as a member.
     *
     * @param personId The person ID to toggle trainer status for
     */
    fun toggleTrainerStatus(personId: String) {
        // Can only toggle trainer if already selected as member
        if (!_selectedMemberIds.value.contains(personId)) {
            return
        }

        val currentTrainers = _selectedTrainerIds.value.toMutableSet()

        if (currentTrainers.contains(personId)) {
            currentTrainers.remove(personId)
        } else {
            currentTrainers.add(personId)
        }

        _selectedTrainerIds.value = currentTrainers

        // Update members list UI
        val updatedMembers = _members.value.map { member ->
            if (member.personId == personId) {
                member.copy(isSelectedAsTrainer = currentTrainers.contains(personId))
            } else {
                member
            }
        }
        _members.value = updatedMembers
    }

    /**
     * Creates the group with validation.
     * Validates:
     * - Name is not empty
     * - At least one member is selected
     */
    fun createGroup() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            if (_name.value.isEmpty()) {
                _error.value = "Please enter a group name"
                _isLoading.value = false
                return@launch
            }

            if (_selectedMemberIds.value.isEmpty()) {
                _error.value = "Please select at least one member"
                _isLoading.value = false
                return@launch
            }

            val result = createGroupUseCase(
                name = _name.value,
                memberIds = _selectedMemberIds.value.toList(),
                trainerIds = _selectedTrainerIds.value.toList()
            )

            _isLoading.value = false
            if (result.isSuccess) {
                _success.value = true
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to create group"
            }
        }
    }

    /**
     * Display model for a person in the member/trainer selection list.
     * Tracks BOTH member and trainer selection state.
     */
    data class MemberTrainerItem(
        val personId: String,
        val name: String,
        val profileImageUrl: String?,
        val isCreator: Boolean,
        val isSelectedAsMember: Boolean,  // Selected as member
        val isSelectedAsTrainer: Boolean  // Selected as trainer (only valid if isSelectedAsMember)
    )
}