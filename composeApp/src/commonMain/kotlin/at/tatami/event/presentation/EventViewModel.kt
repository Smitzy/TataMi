package at.tatami.event.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.tatami.club.domain.usecase.ObserveIsCurrentPersonAdminUseCase
import at.tatami.domain.model.Event
import at.tatami.domain.model.EventStatus
import at.tatami.domain.model.EventTimeFilter
import at.tatami.event.domain.usecase.ObservePastEventsUseCase
import at.tatami.event.domain.usecase.ObserveUpcomingEventsUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import at.tatami.common.domain.service.DateTimeFormatterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class EventViewModel(
    private val observeUpcomingEventsUseCase: ObserveUpcomingEventsUseCase,
    private val observePastEventsUseCase: ObservePastEventsUseCase,
    private val observeIsCurrentPersonAdminUseCase: ObserveIsCurrentPersonAdminUseCase,
    private val observeSelectedPersonUseCase: ObserveSelectedPersonUseCase,
    private val dateTimeFormatter: DateTimeFormatterService
) : ViewModel() {

    private val _selectedTimeFilter = MutableStateFlow(EventTimeFilter.UPCOMING)
    val selectedTimeFilter: StateFlow<EventTimeFilter> = _selectedTimeFilter

    val events: StateFlow<List<EventDisplayItem>> = _selectedTimeFilter
        .flatMapLatest { filter ->
            val eventsFlow = when (filter) {
                EventTimeFilter.UPCOMING -> observeUpcomingEventsUseCase()
                EventTimeFilter.PAST -> observePastEventsUseCase()
            }

            combine(
                eventsFlow,
                observeSelectedPersonUseCase()
            ) { events, person ->
                events.map { event ->
                    val personId = person?.id
                    val userStatus = personId?.let { event.status[it] } ?: EventStatus.NO_RESPONSE
                    val isInvited = personId?.let { event.isPersonInvited(it) } ?: false
                    EventDisplayItem(
                        event = event,
                        formattedDateTime = dateTimeFormatter.formatDateTime(event.startDateTime),
                        userStatus = userStatus,
                        isInvited = isInvited
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isAdmin: StateFlow<Boolean> = observeIsCurrentPersonAdminUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onTimeFilterSelected(filter: EventTimeFilter) {
        _selectedTimeFilter.value = filter
    }

    data class EventDisplayItem(
        val event: Event,
        val formattedDateTime: String,
        val userStatus: EventStatus,
        val isInvited: Boolean
    )
}