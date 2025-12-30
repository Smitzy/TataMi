package at.tatami.event.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.EventRepository

/**
 * Use case for deleting an event.
 * Only admins can delete events.
 */
class DeleteEventUseCase(
    private val eventRepository: EventRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(eventId: String): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return eventRepository.deleteEvent(
            clubId = selectedClub.id,
            eventId = eventId
        )
    }
}
