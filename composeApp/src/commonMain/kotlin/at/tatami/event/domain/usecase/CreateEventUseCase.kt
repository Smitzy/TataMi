package at.tatami.event.domain.usecase

import at.tatami.domain.model.Event
import at.tatami.domain.model.EventStatus
import at.tatami.domain.repository.EventRepository
import at.tatami.domain.repository.NotificationRepository
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase
import at.tatami.club.domain.usecase.GetSelectedClubUseCase

class CreateEventUseCase(
    private val eventRepository: EventRepository,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase,
    private val getSelectedClubUseCase: GetSelectedClubUseCase,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        startDateTime: kotlinx.datetime.LocalDateTime,
        location: String,
        invitedPersonIds: List<String>
    ): Result<Event> {
        return try {
            val selectedClub = getSelectedClubUseCase()
                ?: return Result.failure(Exception("No club selected"))

            val selectedPerson = getSelectedPersonUseCase()
                ?: return Result.failure(Exception("No person selected"))

            // Creator is not automatically added to invited list
            val finalInvitedIds = invitedPersonIds.distinct()

            val event = Event(
                id = "",
                clubId = selectedClub.id,
                title = title.trim(),
                description = description.trim(),
                startDateTime = startDateTime,
                location = location.trim(),
                creatorId = selectedPerson.id,
                invitedPersonIds = finalInvitedIds,
                status = finalInvitedIds.associateWith { EventStatus.NO_RESPONSE }
            )

            val createdEvent = eventRepository.createEvent(event)

            // Send notifications to all invited persons - best effort
            val recipientIds = finalInvitedIds
            if (recipientIds.isNotEmpty()) {
                try {
                    notificationRepository.sendNotificationToPersons(
                        title = "New Event: ${event.title}",
                        body = "${selectedPerson.firstName} created a new event in ${selectedClub.name}",
                        personIds = recipientIds
                    )
                } catch (e: Exception) {
                    // Log but don't fail - event was created successfully
                    println("Failed to send event notifications: ${e.message}")
                }
            }

            Result.success(createdEvent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}