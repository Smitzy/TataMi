package at.tatami.event.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.EventRepository
import kotlinx.datetime.LocalDateTime

/**
 * Use case for updating the start date/time of an event.
 * Only admins can update events.
 */
class UpdateEventStartDateTimeUseCase(
    private val eventRepository: EventRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(eventId: String, startDateTime: LocalDateTime): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return eventRepository.updateEventStartDateTime(
            clubId = selectedClub.id,
            eventId = eventId,
            startDateTime = startDateTime
        )
    }
}
