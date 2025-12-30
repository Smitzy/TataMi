package at.tatami.event.domain.usecase

import at.tatami.domain.model.Event
import at.tatami.domain.repository.EventRepository
import at.tatami.club.domain.usecase.GetSelectedClubUseCase

class GetEventByIdUseCase(
    private val eventRepository: EventRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(eventId: String): Event? {
        val clubId = getSelectedClubUseCase()?.id ?: return null
        return eventRepository.getEventById(clubId, eventId)
    }
}