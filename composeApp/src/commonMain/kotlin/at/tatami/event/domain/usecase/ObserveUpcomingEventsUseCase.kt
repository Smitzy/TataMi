package at.tatami.event.domain.usecase

import at.tatami.domain.model.Event
import at.tatami.domain.repository.EventRepository
import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class ObserveUpcomingEventsUseCase(
    private val eventRepository: EventRepository,
    private val observeSelectedClubUseCase: ObserveSelectedClubUseCase,
    private val observeSelectedPersonUseCase: ObserveSelectedPersonUseCase
) {
    operator fun invoke(): Flow<List<Event>> {
        return combine(
            observeSelectedClubUseCase(),
            observeSelectedPersonUseCase()
        ) { club, person ->
            club to person
        }.flatMapLatest { (club, person) ->
            if (club == null || person == null) {
                return@flatMapLatest kotlinx.coroutines.flow.flowOf(emptyList())
            }

            eventRepository.observeUpcomingEvents(club.id, person.id)
                .map { events ->
                    // Filter events based on admin status
                    val isAdmin = club.adminIds.contains(person.id)
                    if (isAdmin) {
                        // Admins see all events
                        events
                    } else {
                        // Non-admins only see events they're invited to
                        events.filter { event ->
                            event.invitedPersonIds.contains(person.id)
                        }
                    }
                }
        }
    }
}