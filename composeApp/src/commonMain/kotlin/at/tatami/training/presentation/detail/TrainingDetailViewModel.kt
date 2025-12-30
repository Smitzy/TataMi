@file:OptIn(ExperimentalTime::class)

package at.tatami.training.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.common.domain.service.DateTimeFormatterService
import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import at.tatami.domain.model.Training
import at.tatami.group.domain.usecase.GetGroupByIdUseCase
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import at.tatami.training.domain.usecase.DeleteTrainingUseCase
import at.tatami.training.domain.usecase.ObserveCanEditTrainingUseCase
import at.tatami.training.domain.usecase.ObserveTrainingByIdUseCase
import at.tatami.training.domain.usecase.UpdateTrainingAttendanceUseCase
import at.tatami.training.domain.usecase.UpdateTrainingNotesUseCase
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the TrainingDetailScreen.
 * Manages training detail display, notes editing, attendance tracking, and deletion.
 *
 * @param groupId The ID of the group this training belongs to
 * @param trainingId The ID of the training to display
 * @param observeTrainingByIdUseCase Use case for observing training details
 * @param observeCanEditTrainingUseCase Use case for checking edit permissions
 * @param updateTrainingNotesUseCase Use case for updating notes
 * @param updateTrainingAttendanceUseCase Use case for updating attendance
 * @param deleteTrainingUseCase Use case for deleting training
 * @param getGroupByIdUseCase Use case for fetching group details
 * @param getPersonByIdUseCase Use case for fetching person details
 * @param observeSelectedPersonUseCase Use case for observing selected person
 * @param dateTimeFormatter Service for formatting dates and times
 */
class TrainingDetailViewModel(
    private val groupId: String,
    private val trainingId: String,
    private val observeTrainingByIdUseCase: ObserveTrainingByIdUseCase,
    private val observeCanEditTrainingUseCase: ObserveCanEditTrainingUseCase,
    private val updateTrainingNotesUseCase: UpdateTrainingNotesUseCase,
    private val updateTrainingAttendanceUseCase: UpdateTrainingAttendanceUseCase,
    private val deleteTrainingUseCase: DeleteTrainingUseCase,
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val getPersonByIdUseCase: GetPersonByIdUseCase,
    observeSelectedPersonUseCase: ObserveSelectedPersonUseCase,
    private val dateTimeFormatter: DateTimeFormatterService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    private val _members = MutableStateFlow<List<MemberInfo>>(emptyList())
    val members: StateFlow<List<MemberInfo>> = _members

    /**
     * StateFlow of the current person's ID.
     * Uses Eagerly so the value is available immediately for permission checks.
     */
    val currentPersonId: StateFlow<String?> = observeSelectedPersonUseCase()
        .map { it?.id }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    /**
     * StateFlow indicating whether the current person can edit this training.
     * True if the person is an admin or a trainer of this group.
     */
    val canEdit: StateFlow<Boolean> = observeCanEditTrainingUseCase(groupId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * StateFlow of the training with formatted datetime for display.
     */
    val trainingDisplay: StateFlow<TrainingDisplay?> = observeTrainingByIdUseCase(groupId, trainingId)
        .map { training ->
            if (training == null) {
                _isLoading.value = false
                return@map null
            }

            _isLoading.value = false
            TrainingDisplay(
                training = training,
                formattedDateTime = dateTimeFormatter.formatDateTime(training.startDateTime)
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * StateFlow indicating whether the training is in the past.
     * Used for permission checks (regular members can't toggle past training attendance).
     * Uses Eagerly so the value is available immediately for permission checks.
     */
    val isPast: StateFlow<Boolean> = trainingDisplay
        .map { display ->
            if (display == null) return@map false
            val now = Clock.System.now().toLocalDateTimeInSystemTimeZone()
            display.training.startDateTime < now
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        loadMembers()
    }

    /**
     * Loads group members and their details.
     * Members are sorted with trainers first.
     */
    private fun loadMembers() {
        viewModelScope.launch {
            try {
                val group = getGroupByIdUseCase(groupId)
                if (group == null) {
                    _error.value = "Group not found"
                    return@launch
                }

                val memberInfoList = group.memberIds.mapNotNull { personId ->
                    val person = getPersonByIdUseCase(personId)
                    if (person == null) {
                        null
                    } else {
                        MemberInfo(
                            personId = person.id,
                            personName = "${person.firstName} ${person.lastName}",
                            profileImageUrl = person.personImgUrl,
                            isTrainer = group.isPersonTrainer(person.id)
                        )
                    }
                }

                // Sort: trainers first, then by name
                _members.value = memberInfoList.sortedWith(
                    compareByDescending<MemberInfo> { it.isTrainer }
                        .thenBy { it.personName }
                )
            } catch (e: Exception) {
                _error.value = "Failed to load members: ${e.message}"
            }
        }
    }

    /**
     * Updates the training notes.
     * Only admins and trainers should be able to call this.
     */
    fun updateNotes(newNotes: String) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            val result = updateTrainingNotesUseCase(groupId, trainingId, newNotes)

            result.onFailure { error ->
                _error.value = error.message ?: "Failed to update notes"
            }

            _isSaving.value = false
        }
    }

    /**
     * Updates the training attendance list.
     * This is called when the attendance bottom sheet is dismissed with changes.
     */
    fun updateAttendance(attendedPersonIds: List<String>) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            val result = updateTrainingAttendanceUseCase(groupId, trainingId, attendedPersonIds)

            result.onFailure { error ->
                _error.value = error.message ?: "Failed to update attendance"
            }

            _isSaving.value = false
        }
    }

    /**
     * Determines whether the current user can toggle a specific person's attendance checkbox.
     *
     * Permission rules:
     * - Admins and trainers: Can toggle all checkboxes (upcoming and past)
     * - Regular members (upcoming training): Can toggle only their own checkbox
     * - Regular members (past training): Cannot toggle anything
     *
     * @param personId The ID of the person whose checkbox is being toggled
     * @return True if the current user can toggle this checkbox
     */
    fun canToggleAttendance(personId: String): Boolean {
        // Admins and trainers can always toggle
        if (canEdit.value) return true

        // Past trainings: only admins/trainers (already checked above)
        if (isPast.value) return false

        // Upcoming trainings: regular members can toggle their own checkbox
        return personId == currentPersonId.value
    }

    /**
     * Deletes the training.
     * Only admins and trainers should be able to call this.
     * Returns true if deletion was successful, false otherwise.
     */
    suspend fun deleteTraining(): Boolean {
        _isSaving.value = true
        _error.value = null

        val result = deleteTrainingUseCase(groupId, trainingId)

        result.onFailure { error ->
            _error.value = error.message ?: "Failed to delete training"
        }

        _isSaving.value = false
        return result.isSuccess
    }

    /**
     * Clears the error message.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Display item for a training with formatted datetime.
     */
    data class TrainingDisplay(
        val training: Training,
        val formattedDateTime: String
    )

    /**
     * Information about a group member for attendance tracking.
     */
    data class MemberInfo(
        val personId: String,
        val personName: String,
        val profileImageUrl: String?,
        val isTrainer: Boolean
    )
}
