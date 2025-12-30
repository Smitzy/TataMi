package at.tatami.event.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.EventRepository

/**
 * Use case for updating the description of an event.
 * Only admins can update events.
 */
class UpdateEventDescriptionUseCase(
    private val eventRepository: EventRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(eventId: String, description: String): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return eventRepository.updateEventDescription(
            clubId = selectedClub.id,
            eventId = eventId,
            description = description.trim()
        )
    }
}
