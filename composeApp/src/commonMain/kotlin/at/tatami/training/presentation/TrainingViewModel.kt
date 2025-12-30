package at.tatami.training.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.common.domain.service.DateTimeFormatterService
import at.tatami.domain.model.EventTimeFilter
import at.tatami.domain.model.Training
import at.tatami.training.domain.usecase.CreateTrainingUseCase
import at.tatami.training.domain.usecase.ObserveCanCreateTrainingUseCase
import at.tatami.training.domain.usecase.ObservePastTrainingsUseCase
import at.tatami.training.domain.usecase.ObserveUpcomingTrainingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

/**
 * ViewModel for the TrainingListScreen.
 * Manages training list display, filtering, and creation.
 *
 * @param groupId The ID of the group whose trainings are being displayed
 * @param observeUpcomingTrainingsUseCase Use case for observing upcoming trainings
 * @param observePastTrainingsUseCase Use case for observing past trainings
 * @param observeCanCreateTrainingUseCase Use case for checking creation permissions
 * @param createTrainingUseCase Use case for creating new trainings
 * @param dateTimeFormatter Service for formatting dates and times
 */
class TrainingViewModel(
    private val groupId: String,
    private val observeUpcomingTrainingsUseCase: ObserveUpcomingTrainingsUseCase,
    private val observePastTrainingsUseCase: ObservePastTrainingsUseCase,
    private val observeCanCreateTrainingUseCase: ObserveCanCreateTrainingUseCase,
    private val createTrainingUseCase: CreateTrainingUseCase,
    private val dateTimeFormatter: DateTimeFormatterService
) : ViewModel() {

    private val _selectedTimeFilter = MutableStateFlow(EventTimeFilter.UPCOMING)
    val selectedTimeFilter: StateFlow<EventTimeFilter> = _selectedTimeFilter

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating

    private val _creationError = MutableStateFlow<String?>(null)
    val creationError: StateFlow<String?> = _creationError

    /**
     * StateFlow of trainings to display, filtered by time and formatted for UI.
     * Switches between upcoming and past trainings based on selected filter.
     */
    val trainings: StateFlow<List<TrainingDisplayItem>> = _selectedTimeFilter
        .flatMapLatest { filter ->
            val trainingsFlow = when (filter) {
                EventTimeFilter.UPCOMING -> observeUpcomingTrainingsUseCase(groupId)
                EventTimeFilter.PAST -> observePastTrainingsUseCase(groupId)
            }

            trainingsFlow.map { trainings ->
                trainings.map { training ->
                    TrainingDisplayItem(
                        training = training,
                        formattedDateTime = dateTimeFormatter.formatDateTime(training.startDateTime)
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * StateFlow indicating whether the current person can create trainings.
     * True if the person is an admin or a trainer of this group.
     */
    val canCreateTraining: StateFlow<Boolean> = observeCanCreateTrainingUseCase(groupId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * Updates the time filter selection (UPCOMING or PAST).
     */
    fun onTimeFilterSelected(filter: EventTimeFilter) {
        _selectedTimeFilter.value = filter
    }

    /**
     * Creates a new training with the specified start date/time.
     * Notes and attendance are initially empty.
     */
    fun createTraining(startDateTime: LocalDateTime) {
        viewModelScope.launch {
            _isCreating.value = true
            _creationError.value = null

            val result = createTrainingUseCase(groupId, startDateTime)

            result.onFailure { error ->
                _creationError.value = error.message ?: "Failed to create training"
            }

            _isCreating.value = false
        }
    }

    /**
     * Clears the creation error message.
     */
    fun clearCreationError() {
        _creationError.value = null
    }

    /**
     * Display item for a training in the list.
     * Contains the raw training data and pre-formatted datetime string.
     */
    data class TrainingDisplayItem(
        val training: Training,
        val formattedDateTime: String
    )
}