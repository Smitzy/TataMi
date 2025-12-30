package at.tatami.domain.repository

import at.tatami.domain.model.Event
import at.tatami.domain.model.EventStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

interface EventRepository {
    suspend fun createEvent(event: Event): Event
    suspend fun getEventById(clubId: String, eventId: String): Event?
    fun observeUpcomingEvents(clubId: String, personId: String): Flow<List<Event>>
    fun observePastEvents(clubId: String, personId: String): Flow<List<Event>>
    suspend fun updateEventStatus(clubId: String, eventId: String, personId: String, status: EventStatus)

    /**
     * Updates the title of an event.
     */
    suspend fun updateEventTitle(clubId: String, eventId: String, title: String): Result<Unit>

    /**
     * Updates the description of an event.
     */
    suspend fun updateEventDescription(clubId: String, eventId: String, description: String): Result<Unit>

    /**
     * Updates the location of an event.
     */
    suspend fun updateEventLocation(clubId: String, eventId: String, location: String): Result<Unit>

    /**
     * Updates the start date/time of an event.
     */
    suspend fun updateEventStartDateTime(clubId: String, eventId: String, startDateTime: LocalDateTime): Result<Unit>

    /**
     * Deletes an event.
     */
    suspend fun deleteEvent(clubId: String, eventId: String): Result<Unit>
}