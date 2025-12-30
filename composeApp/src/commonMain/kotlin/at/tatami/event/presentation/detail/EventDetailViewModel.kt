package at.tatami.event.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.common.domain.service.DateTimeFormatterService
import at.tatami.domain.model.Event
import at.tatami.domain.model.EventStatus
import at.tatami.event.domain.usecase.DeleteEventUseCase
import at.tatami.event.domain.usecase.GetEventByIdUseCase
import at.tatami.event.domain.usecase.UpdateEventDescriptionUseCase
import at.tatami.event.domain.usecase.UpdateEventLocationUseCase
import at.tatami.event.domain.usecase.UpdateEventStartDateTimeUseCase
import at.tatami.event.domain.usecase.UpdateEventStatusUseCase
import at.tatami.event.domain.usecase.UpdateEventTitleUseCase
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

class EventDetailViewModel(
    private val getEventByIdUseCase: GetEventByIdUseCase,
    private val updateEventStatusUseCase: UpdateEventStatusUseCase,
    private val getPersonByIdUseCase: GetPersonByIdUseCase,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase,
    private val dateTimeFormatter: DateTimeFormatterService,
    private val observeSelectedClubUseCase: ObserveSelectedClubUseCase,
    private val observeSelectedPersonUseCase: ObserveSelectedPersonUseCase,
    private val updateEventTitleUseCase: UpdateEventTitleUseCase,
    private val updateEventDescriptionUseCase: UpdateEventDescriptionUseCase,
    private val updateEventLocationUseCase: UpdateEventLocationUseCase,
    private val updateEventStartDateTimeUseCase: UpdateEventStartDateTimeUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {

    private val _event = MutableStateFlow<EventDisplay?>(null)
    val event: StateFlow<EventDisplay?> = _event.asStateFlow()

    private val _participants = MutableStateFlow<Map<String, ParticipantInfo>>(emptyMap())
    val participants: StateFlow<Map<String, ParticipantInfo>> = _participants.asStateFlow()

    private val _currentUserPersonId = MutableStateFlow<String?>(null)
    val currentUserPersonId: StateFlow<String?> = _currentUserPersonId.asStateFlow()

    private val _canCurrentUserRespond = MutableStateFlow(false)
    val canCurrentUserRespond: StateFlow<Boolean> = _canCurrentUserRespond.asStateFlow()

    // Admin check: current person is in club's adminIds
    val isAdmin: StateFlow<Boolean> = combine(
        observeSelectedClubUseCase(),
        observeSelectedPersonUseCase()
    ) { club, person ->
        club != null && person != null && club.adminIds.contains(person.id)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // Load current user
            val currentUser = getSelectedPersonUseCase()
            _currentUserPersonId.value = currentUser?.id

            val loadedEvent = getEventByIdUseCase(eventId)
            if (loadedEvent != null) {
                val formattedDateTime = dateTimeFormatter.formatDateTime(loadedEvent.startDateTime)
                _event.value = EventDisplay(
                    event = loadedEvent,
                    formattedDateTime = formattedDateTime
                )

                // Check if current user can respond (must be invited)
                _canCurrentUserRespond.value = currentUser?.id?.let { personId ->
                    loadedEvent.canPersonRespond(personId)
                } ?: false

                loadParticipants(loadedEvent)
            }
            _isLoading.value = false
        }
    }

    private suspend fun loadParticipants(event: Event) {
        val participantMap = mutableMapOf<String, ParticipantInfo>()

        for (personId in event.invitedPersonIds) {
            val person = getPersonByIdUseCase(personId)
            val status = event.status[personId] ?: EventStatus.NO_RESPONSE

            participantMap[personId] = ParticipantInfo(
                personId = personId,
                personName = person?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown",
                profileImageUrl = person?.personImgUrl,
                status = status
            )
        }

        _participants.value = participantMap
    }

    fun updateStatus(eventId: String, status: EventStatus) {
        viewModelScope.launch {
            // Only allow status updates if user can respond (is invited)
            if (!_canCurrentUserRespond.value) return@launch

            val personId = _currentUserPersonId.value ?: return@launch
            val currentEvent = _event.value?.event ?: return@launch

            // Optimistically update local state immediately
            val updatedStatusMap = currentEvent.status.toMutableMap().apply {
                this[personId] = status
            }
            val updatedEvent = currentEvent.copy(status = updatedStatusMap)
            _event.value = _event.value?.copy(event = updatedEvent)

            // Update participant info in local state
            _participants.value = _participants.value.toMutableMap().apply {
                this[personId]?.let { participant ->
                    this[personId] = participant.copy(status = status)
                }
            }

            // Send to Firebase asynchronously
            val result = updateEventStatusUseCase(eventId, status)
            if (result.isFailure) {
                // Reload event on failure to restore correct state
                loadEvent(eventId)
            }
        }
    }

    /**
     * Updates the event title.
     */
    fun updateTitle(eventId: String, title: String) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            val result = updateEventTitleUseCase(eventId, title)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update title"
            } else {
                // Update local state
                _event.value?.let { display ->
                    val formattedDateTime = dateTimeFormatter.formatDateTime(display.event.startDateTime)
                    _event.value = display.copy(
                        event = display.event.copy(title = title.trim())
                    )
                }
            }
            _isSaving.value = false
        }
    }

    /**
     * Updates the event description.
     */
    fun updateDescription(eventId: String, description: String) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            val result = updateEventDescriptionUseCase(eventId, description)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update description"
            } else {
                // Update local state
                _event.value?.let { display ->
                    _event.value = display.copy(
                        event = display.event.copy(description = description.trim())
                    )
                }
            }
            _isSaving.value = false
        }
    }

    /**
     * Updates the event location.
     */
    fun updateLocation(eventId: String, location: String) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            val result = updateEventLocationUseCase(eventId, location)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update location"
            } else {
                // Update local state
                _event.value?.let { display ->
                    _event.value = display.copy(
                        event = display.event.copy(location = location.trim())
                    )
                }
            }
            _isSaving.value = false
        }
    }

    /**
     * Updates the event start date/time.
     */
    fun updateStartDateTime(eventId: String, startDateTime: LocalDateTime) {
        viewModelScope.launch {
            _isSaving.value = true
            _error.value = null

            val result = updateEventStartDateTimeUseCase(eventId, startDateTime)
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to update date/time"
            } else {
                // Update local state with new formatted date
                _event.value?.let { display ->
                    val formattedDateTime = dateTimeFormatter.formatDateTime(startDateTime)
                    _event.value = display.copy(
                        event = display.event.copy(startDateTime = startDateTime),
                        formattedDateTime = formattedDateTime
                    )
                }
            }
            _isSaving.value = false
        }
    }

    /**
     * Deletes the event.
     * @return true if deletion was successful
     */
    suspend fun deleteEvent(eventId: String): Boolean {
        _isDeleting.value = true
        _error.value = null

        val result = deleteEventUseCase(eventId)
        if (result.isFailure) {
            _error.value = result.exceptionOrNull()?.message ?: "Failed to delete event"
        }
        _isDeleting.value = false
        return result.isSuccess
    }

    /**
     * Clears the error message.
     */
    fun clearError() {
        _error.value = null
    }

    data class EventDisplay(
        val event: Event,
        val formattedDateTime: String
    )

    data class ParticipantInfo(
        val personId: String,
        val personName: String,
        val profileImageUrl: String?,
        val status: EventStatus
    )
}