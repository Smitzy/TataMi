package at.tatami.event.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.EventRepository

/**
 * Use case for updating the location of an event.
 * Only admins can update events.
 */
class UpdateEventLocationUseCase(
    private val eventRepository: EventRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(eventId: String, location: String): Result<Unit> {
        if (location.isBlank()) {
            return Result.failure(Exception("Location cannot be empty"))
        }

        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return eventRepository.updateEventLocation(
            clubId = selectedClub.id,
            eventId = eventId,
            location = location.trim()
        )
    }
}
