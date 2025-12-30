@file:OptIn(ExperimentalTime::class)

package at.tatami.data.mapper

import at.tatami.data.model.FirebaseEvent
import at.tatami.domain.model.Event
import at.tatami.domain.model.EventStatus
import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import dev.gitlive.firebase.firestore.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Mappers for Event-related domain models
 */

fun FirebaseEvent.toDomain(id: String) = Event(
    id = id,
    clubId = clubId,
    title = title,
    description = description,
    startDateTime = Instant.fromEpochSeconds(startDateTime.seconds, startDateTime.nanoseconds.toLong())
        .toLocalDateTimeInSystemTimeZone(),
    location = location,
    creatorId = creatorId,
    invitedPersonIds = invitedPersonIds,
    status = status.mapValues { (_, value) ->
        EventStatus.valueOf(value)
    }
)

fun Event.toFirebase() = FirebaseEvent(
    clubId = clubId,
    title = title,
    description = description,
    startDateTime = run {
        val instant = startDateTime.toInstant(TimeZone.currentSystemDefault())
        Timestamp(instant.epochSeconds, instant.nanosecondsOfSecond)
    },
    location = location,
    creatorId = creatorId,
    invitedPersonIds = invitedPersonIds,
    status = status.mapValues { (_, eventStatus) ->
        eventStatus.name
    }
)