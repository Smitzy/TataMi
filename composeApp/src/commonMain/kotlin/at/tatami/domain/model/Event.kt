@file:OptIn(ExperimentalTime::class)

package at.tatami.domain.model

import kotlin.time.ExperimentalTime
import kotlinx.datetime.LocalDateTime

data class Event(
    val id: String,
    val clubId: String,
    val title: String,
    val description: String,
    val startDateTime: LocalDateTime,
    val location: String,
    val creatorId: String, // Person ID of creator
    val invitedPersonIds: List<String>, // Explicitly invited members
    val status: Map<String, EventStatus> = emptyMap() // personId -> status
) {
    /**
     * Checks if a person is explicitly invited to this event.
     * Only invited persons can respond with YES/NO/MAYBE.
     */
    fun isPersonInvited(personId: String): Boolean {
        return invitedPersonIds.contains(personId)
    }

    /**
     * Checks if a person can respond to this event.
     * Only explicitly invited persons can respond.
     */
    fun canPersonRespond(personId: String): Boolean {
        return isPersonInvited(personId)
    }

    /**
     * Gets the response status for a specific person.
     * Returns NO_RESPONSE if person hasn't responded or isn't invited.
     */
    fun getPersonStatus(personId: String): EventStatus {
        return status[personId] ?: EventStatus.NO_RESPONSE
    }
}

enum class EventStatus {
    YES,
    NO,
    MAYBE,
    NO_RESPONSE
}