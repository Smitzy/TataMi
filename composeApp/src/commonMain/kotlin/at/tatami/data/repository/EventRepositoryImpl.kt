@file:OptIn(ExperimentalTime::class)

package at.tatami.data.repository

import at.tatami.data.mapper.toDomain
import at.tatami.data.mapper.toFirebase
import at.tatami.data.model.FirebaseEvent
import at.tatami.domain.model.Event
import at.tatami.domain.model.EventStatus
import at.tatami.domain.repository.EventRepository
import at.tatami.common.util.DataSourceLogger
import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.ExperimentalTime
import kotlin.time.Clock

class EventRepositoryImpl(
    private val firestore: FirebaseFirestore
) : EventRepository {

    private fun eventsCollection(clubId: String) = firestore.collection("clubs").document(clubId).collection("events")

    override suspend fun createEvent(event: Event): Event {
        val documentRef = eventsCollection(event.clubId).add(event.toFirebase())
        val eventWithId = event.copy(id = documentRef.id)
        return eventWithId
    }

    override suspend fun getEventById(clubId: String, eventId: String): Event? {
        return try {
            val document = eventsCollection(clubId).document(eventId).get()
            val data = document.data(FirebaseEvent.serializer())
            val event = data.toDomain(eventId)
            DataSourceLogger.logFirestoreFetch("Event", eventId)
            event
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Event", "fetch failed: ${e.message}")
            null
        }
    }

    override fun observeUpcomingEvents(clubId: String, personId: String): Flow<List<Event>> {
        val now = Clock.System.now().toLocalDateTimeInSystemTimeZone()

        return eventsCollection(clubId)
            .snapshots
            .map { snapshot ->
                try {
                    val events = snapshot.documents
                        .mapNotNull { document ->
                            try {
                                val data = document.data(FirebaseEvent.serializer())
                                val event = data.toDomain(document.id)
                                event
                            } catch (e: Exception) {
                                null
                            }
                        }
                        .filter { event ->
                            // Only show upcoming events
                            event.startDateTime >= now
                        }
                        .sortedBy { it.startDateTime }

                    if (events.isNotEmpty()) {
                        DataSourceLogger.logFirestoreFetch("Events", "clubId: $clubId (${events.size} items)")
                    }
                    events
                } catch (e: Exception) {
                    DataSourceLogger.logNoData("Events", "observe failed: ${e.message}")
                    emptyList()
                }
            }
    }

    override fun observePastEvents(clubId: String, personId: String): Flow<List<Event>> {
        val now = Clock.System.now().toLocalDateTimeInSystemTimeZone()

        return eventsCollection(clubId)
            .snapshots
            .map { snapshot ->
                try {
                    val events = snapshot.documents
                        .mapNotNull { document ->
                            try {
                                val data = document.data(FirebaseEvent.serializer())
                                val event = data.toDomain(document.id)
                                event
                            } catch (e: Exception) {
                                null
                            }
                        }
                        .filter { event ->
                            // Only show past events
                            event.startDateTime < now
                        }
                        .sortedByDescending { it.startDateTime }

                    if (events.isNotEmpty()) {
                        DataSourceLogger.logFirestoreFetch("Past Events", "clubId: $clubId (${events.size} items)")
                    }
                    events
                } catch (e: Exception) {
                    DataSourceLogger.logNoData("Past Events", "observe failed: ${e.message}")
                    emptyList()
                }
            }
    }

    override suspend fun updateEventStatus(clubId: String, eventId: String, personId: String, status: EventStatus) {
        val eventDoc = eventsCollection(clubId).document(eventId)
        val updatedStatus = mapOf("status.$personId" to status.name)
        eventDoc.update(updatedStatus)
    }

    override suspend fun updateEventTitle(clubId: String, eventId: String, title: String): Result<Unit> {
        return try {
            eventsCollection(clubId)
                .document(eventId)
                .updateFields { "title" to title }
            DataSourceLogger.logFirestoreFetch("Event", "title updated: $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Event", "update title failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateEventDescription(clubId: String, eventId: String, description: String): Result<Unit> {
        return try {
            eventsCollection(clubId)
                .document(eventId)
                .updateFields { "description" to description }
            DataSourceLogger.logFirestoreFetch("Event", "description updated: $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Event", "update description failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateEventLocation(clubId: String, eventId: String, location: String): Result<Unit> {
        return try {
            eventsCollection(clubId)
                .document(eventId)
                .updateFields { "location" to location }
            DataSourceLogger.logFirestoreFetch("Event", "location updated: $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Event", "update location failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateEventStartDateTime(clubId: String, eventId: String, startDateTime: LocalDateTime): Result<Unit> {
        return try {
            val instant = startDateTime.toInstant(TimeZone.currentSystemDefault())
            val timestamp = Timestamp(instant.epochSeconds, instant.nanosecondsOfSecond)
            eventsCollection(clubId)
                .document(eventId)
                .updateFields { "startDateTime" to timestamp }
            DataSourceLogger.logFirestoreFetch("Event", "startDateTime updated: $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Event", "update startDateTime failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(clubId: String, eventId: String): Result<Unit> {
        return try {
            eventsCollection(clubId)
                .document(eventId)
                .delete()
            DataSourceLogger.logFirestoreFetch("Event", "deleted: $eventId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Event", "delete failed: ${e.message}")
            Result.failure(e)
        }
    }
}