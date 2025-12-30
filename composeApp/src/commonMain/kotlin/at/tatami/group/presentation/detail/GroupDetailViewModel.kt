package at.tatami.group.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.common.domain.service.DateTimeFormatterService
import at.tatami.domain.model.Group
import at.tatami.group.domain.usecase.DeleteGroupUseCase
import at.tatami.group.domain.usecase.GetAttendanceStatisticsUseCase
import at.tatami.group.domain.usecase.GetGroupByIdUseCase
import at.tatami.group.domain.usecase.UpdateGroupMembersUseCase
import at.tatami.group.domain.usecase.UpdateGroupNameUseCase
import at.tatami.group.domain.usecase.UpdateGroupTrainersUseCase
import at.tatami.group.presentation.detail.components.ClubMemberItem
import at.tatami.group.presentation.detail.components.GroupMemberItem
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class GroupDetailViewModel(
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val getPersonByIdUseCase: GetPersonByIdUseCase,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase,
    private val getSelectedClubUseCase: GetSelectedClubUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val updateGroupMembersUseCase: UpdateGroupMembersUseCase,
    private val updateGroupTrainersUseCase: UpdateGroupTrainersUseCase,
    private val updateGroupNameUseCase: UpdateGroupNameUseCase,
    private val getAttendanceStatisticsUseCase: GetAttendanceStatisticsUseCase,
    private val dateTimeFormatterService: DateTimeFormatterService
) : ViewModel() {

    private val _group = MutableStateFlow<Group?>(null)
    val group: StateFlow<Group?> = _group.asStateFlow()

    private val _members = MutableStateFlow<List<MemberInfo>>(emptyList())
    val members: StateFlow<List<MemberInfo>> = _members.asStateFlow()

    // Club members for manage members bottom sheet
    private val _clubMembers = MutableStateFlow<List<ClubMemberItem>>(emptyList())
    val clubMembers: StateFlow<List<ClubMemberItem>> = _clubMembers.asStateFlow()

    // Group members for manage trainers bottom sheet
    private val _groupMembersForTrainers = MutableStateFlow<List<GroupMemberItem>>(emptyList())
    val groupMembersForTrainers: StateFlow<List<GroupMemberItem>> = _groupMembersForTrainers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isClubMembersLoading = MutableStateFlow(false)
    val isClubMembersLoading: StateFlow<Boolean> = _isClubMembersLoading.asStateFlow()

    private val _canViewGroup = MutableStateFlow(false)
    val canViewGroup: StateFlow<Boolean> = _canViewGroup.asStateFlow()

    private val _canDeleteGroup = MutableStateFlow(false)
    val canDeleteGroup: StateFlow<Boolean> = _canDeleteGroup.asStateFlow()

    // Permission: admin OR trainer of this group can manage members/trainers
    private val _canManageGroup = MutableStateFlow(false)
    val canManageGroup: StateFlow<Boolean> = _canManageGroup.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Attendance statistics state
    private val _attendanceStats = MutableStateFlow(AttendanceStatisticsState())
    val attendanceStats: StateFlow<AttendanceStatisticsState> = _attendanceStats.asStateFlow()

    fun loadGroup(groupId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val currentPerson = getSelectedPersonUseCase()
            val currentClub = getSelectedClubUseCase()

            val loadedGroup = getGroupByIdUseCase(groupId)

            if (loadedGroup != null) {
                _group.value = loadedGroup

                val isAdmin = currentPerson != null && currentClub != null &&
                    currentClub.adminIds.contains(currentPerson.id)
                val isTrainer = currentPerson != null &&
                    loadedGroup.trainerIds.contains(currentPerson.id)

                // Permission: admin OR member of this group
                _canViewGroup.value = when {
                    currentPerson == null || currentClub == null -> false
                    isAdmin -> true
                    loadedGroup.memberIds.contains(currentPerson.id) -> true
                    else -> false
                }

                // Only admins can delete groups
                _canDeleteGroup.value = isAdmin

                // Admins OR trainers of this group can manage members/trainers
                _canManageGroup.value = isAdmin || isTrainer

                if (_canViewGroup.value) {
                    loadMembers(loadedGroup)
                }
            } else {
                _canViewGroup.value = false
                _canDeleteGroup.value = false
            }

            _isLoading.value = false
        }
    }

    private suspend fun loadMembers(group: Group) {
        val memberInfoList = mutableListOf<MemberInfo>()

        for (personId in group.memberIds) {
            val person = getPersonByIdUseCase(personId)
            val isTrainer = group.trainerIds.contains(personId)

            memberInfoList.add(
                MemberInfo(
                    personId = personId,
                    personName = person?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown",
                    profileImageUrl = person?.personImgUrl,
                    isTrainer = isTrainer
                )
            )
        }

        // Sort: Trainers first, then other members
        _members.value = memberInfoList.sortedByDescending { it.isTrainer }
    }

    /**
     * Deletes the current group and all its trainings.
     * Only club admins can delete groups.
     * @return true if deletion was successful
     */
    suspend fun deleteGroup(): Boolean {
        val currentGroup = _group.value ?: return false

        _isDeleting.value = true
        _error.value = null

        val result = deleteGroupUseCase(currentGroup.id)

        result.onFailure { error ->
            _error.value = error.message ?: "Failed to delete group"
        }

        _isDeleting.value = false
        return result.isSuccess
    }

    /**
     * Loads all club members for the manage members bottom sheet.
     * Marks each member's group membership and trainer status.
     */
    fun loadClubMembers() {
        viewModelScope.launch {
            _isClubMembersLoading.value = true

            val club = getSelectedClubUseCase()
            val currentGroup = _group.value

            if (club != null && currentGroup != null) {
                val memberList = mutableListOf<ClubMemberItem>()

                for (memberId in club.memberIds) {
                    val person = getPersonByIdUseCase(memberId)
                    if (person != null) {
                        memberList.add(
                            ClubMemberItem(
                                personId = memberId,
                                name = "${person.firstName} ${person.lastName}",
                                profileImageUrl = person.personImgUrl,
                                isGroupMember = currentGroup.memberIds.contains(memberId),
                                isGroupTrainer = currentGroup.trainerIds.contains(memberId)
                            )
                        )
                    }
                }

                // Sort: group members first (with trainers at top), then non-members
                _clubMembers.value = memberList.sortedWith(
                    compareByDescending<ClubMemberItem> { it.isGroupMember }
                        .thenByDescending { it.isGroupTrainer }
                        .thenBy { it.name }
                )
            }

            _isClubMembersLoading.value = false
        }
    }

    /**
     * Loads group members for the manage trainers bottom sheet.
     */
    fun loadGroupMembersForTrainers() {
        viewModelScope.launch {
            val currentGroup = _group.value ?: return@launch

            val memberList = mutableListOf<GroupMemberItem>()

            for (memberId in currentGroup.memberIds) {
                val person = getPersonByIdUseCase(memberId)
                if (person != null) {
                    memberList.add(
                        GroupMemberItem(
                            personId = memberId,
                            name = "${person.firstName} ${person.lastName}",
                            profileImageUrl = person.personImgUrl,
                            isTrainer = currentGroup.trainerIds.contains(memberId)
                        )
                    )
                }
            }

            // Sort: trainers first, then by name
            _groupMembersForTrainers.value = memberList.sortedWith(
                compareByDescending<GroupMemberItem> { it.isTrainer }
                    .thenBy { it.name }
            )
        }
    }

    /**
     * Toggles a person's membership in the group.
     * If removing a member who is a trainer, also removes trainer status.
     */
    fun toggleMembership(personId: String) {
        viewModelScope.launch {
            val currentGroup = _group.value ?: return@launch
            _isSaving.value = true
            _error.value = null

            val currentMemberIds = currentGroup.memberIds.toMutableList()
            val currentTrainerIds = currentGroup.trainerIds.toMutableList()

            if (currentMemberIds.contains(personId)) {
                // Removing member - also remove from trainers if applicable
                currentMemberIds.remove(personId)
                currentTrainerIds.remove(personId)
            } else {
                // Adding member
                currentMemberIds.add(personId)
            }

            // Update members first
            val membersResult = updateGroupMembersUseCase(currentGroup.id, currentMemberIds)
            if (membersResult.isFailure) {
                _error.value = membersResult.exceptionOrNull()?.message ?: "Failed to update members"
                _isSaving.value = false
                return@launch
            }

            // If we removed a trainer, update trainers too
            if (!currentTrainerIds.contains(personId) && currentGroup.trainerIds.contains(personId)) {
                val trainersResult = updateGroupTrainersUseCase(currentGroup.id, currentTrainerIds)
                if (trainersResult.isFailure) {
                    _error.value = trainersResult.exceptionOrNull()?.message ?: "Failed to update trainers"
                    _isSaving.value = false
                    return@launch
                }
            }

            // Update local state
            _group.value = currentGroup.copy(
                memberIds = currentMemberIds,
                trainerIds = currentTrainerIds
            )

            // Update club members list UI
            _clubMembers.value = _clubMembers.value.map { member ->
                if (member.personId == personId) {
                    member.copy(
                        isGroupMember = currentMemberIds.contains(personId),
                        isGroupTrainer = currentTrainerIds.contains(personId)
                    )
                } else {
                    member
                }
            }

            // Update main members list
            loadMembers(_group.value!!)

            _isSaving.value = false
        }
    }

    /**
     * Toggles a person's trainer status in the group.
     * Person must already be a member.
     */
    fun toggleTrainerStatus(personId: String, isTrainer: Boolean) {
        viewModelScope.launch {
            val currentGroup = _group.value ?: return@launch

            // Can only toggle trainer status for existing members
            if (!currentGroup.memberIds.contains(personId)) {
                return@launch
            }

            _isSaving.value = true
            _error.value = null

            val currentTrainerIds = currentGroup.trainerIds.toMutableList()

            if (isTrainer) {
                if (!currentTrainerIds.contains(personId)) {
                    currentTrainerIds.add(personId)
                }
            } else {
                currentTrainerIds.remove(personId)
            }

            val result = updateGroupTrainersUseCase(currentGroup.id, currentTrainerIds)

            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update trainers"
                _isSaving.value = false
                return@launch
            }

            // Update local state
            _group.value = currentGroup.copy(trainerIds = currentTrainerIds)

            // Update group members for trainers list UI
            _groupMembersForTrainers.value = _groupMembersForTrainers.value.map { member ->
                if (member.personId == personId) {
                    member.copy(isTrainer = isTrainer)
                } else {
                    member
                }
            }

            // Update main members list
            loadMembers(_group.value!!)

            _isSaving.value = false
        }
    }

    /**
     * Updates the group name.
     * @param newName The new name for the group
     */
    fun updateGroupName(newName: String) {
        viewModelScope.launch {
            val currentGroup = _group.value ?: return@launch

            _isSaving.value = true
            _error.value = null

            val result = updateGroupNameUseCase(currentGroup.id, newName)

            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update group name"
                _isSaving.value = false
                return@launch
            }

            // Update local state
            _group.value = currentGroup.copy(name = newName.trim())

            _isSaving.value = false
        }
    }

    /**
     * Loads attendance statistics for all group members.
     * @param cutoffDate Optional start date filter (null = all time)
     */
    fun loadAttendanceStatistics(cutoffDate: LocalDate? = null) {
        viewModelScope.launch {
            val currentGroup = _group.value ?: return@launch

            _attendanceStats.value = _attendanceStats.value.copy(isLoading = true)

            val formattedDate = if (cutoffDate != null) {
                dateTimeFormatterService.formatDate(cutoffDate)
            } else {
                "All Time"
            }

            val result = getAttendanceStatisticsUseCase(
                groupId = currentGroup.id,
                memberIds = currentGroup.memberIds,
                cutoffDate = cutoffDate
            )

            result.onSuccess { stats ->
                _attendanceStats.value = AttendanceStatisticsState(
                    isLoading = false,
                    cutoffDate = cutoffDate,
                    memberStats = stats.map { stat ->
                        MemberAttendanceStat(
                            personId = stat.personId,
                            personName = stat.personName,
                            profileImageUrl = stat.profileImageUrl,
                            attendedCount = stat.attendedCount,
                            totalTrainings = stat.totalTrainings,
                            attendanceRate = stat.attendanceRate
                        )
                    },
                    totalTrainings = stats.firstOrNull()?.totalTrainings ?: 0,
                    formattedCutoffDate = formattedDate
                )
            }.onFailure { error ->
                _attendanceStats.value = AttendanceStatisticsState(
                    isLoading = false,
                    cutoffDate = cutoffDate,
                    formattedCutoffDate = formattedDate
                )
                _error.value = error.message ?: "Failed to load attendance statistics"
            }
        }
    }

    /**
     * Updates the cutoff date and reloads statistics.
     */
    fun updateStatisticsCutoffDate(cutoffDate: LocalDate?) {
        loadAttendanceStatistics(cutoffDate)
    }

    /**
     * Clears the error message.
     */
    fun clearError() {
        _error.value = null
    }

    data class MemberInfo(
        val personId: String,
        val personName: String,
        val profileImageUrl: String?,
        val isTrainer: Boolean
    )

    /**
     * State for the attendance statistics bottom sheet.
     */
    data class AttendanceStatisticsState(
        val isLoading: Boolean = false,
        val cutoffDate: LocalDate? = null,
        val memberStats: List<MemberAttendanceStat> = emptyList(),
        val totalTrainings: Int = 0,
        val formattedCutoffDate: String = "All Time"
    )

    /**
     * Attendance statistics for a single member.
     */
    data class MemberAttendanceStat(
        val personId: String,
        val personName: String,
        val profileImageUrl: String?,
        val attendedCount: Int,
        val totalTrainings: Int,
        val attendanceRate: Float  // 0.0 to 1.0
    )
}