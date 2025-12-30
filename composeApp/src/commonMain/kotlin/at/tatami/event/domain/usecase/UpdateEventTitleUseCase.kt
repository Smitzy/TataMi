package at.tatami.event.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.EventRepository

/**
 * Use case for updating the title of an event.
 * Only admins can update events.
 */
class UpdateEventTitleUseCase(
    private val eventRepository: EventRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(eventId: String, title: String): Result<Unit> {
        if (title.isBlank()) {
            return Result.failure(Exception("Title cannot be empty"))
        }

        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return eventRepository.updateEventTitle(
            clubId = selectedClub.id,
            eventId = eventId,
            title = title.trim()
        )
    }
}
