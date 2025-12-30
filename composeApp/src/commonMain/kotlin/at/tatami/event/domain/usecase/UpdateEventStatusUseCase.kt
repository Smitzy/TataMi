package at.tatami.event.domain.usecase

import at.tatami.domain.model.EventStatus
import at.tatami.domain.repository.EventRepository
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import at.tatami.club.domain.usecase.GetSelectedClubUseCase

class UpdateEventStatusUseCase(
    private val eventRepository: EventRepository,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(eventId: String, status: EventStatus): Result<Unit> {
        return try {
            val clubId = getSelectedClubUseCase()?.id
                ?: return Result.failure(Exception("No club selected"))

            val personId = getSelectedPersonUseCase()?.id
                ?: return Result.failure(Exception("No person selected"))

            eventRepository.updateEventStatus(clubId, eventId, personId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}