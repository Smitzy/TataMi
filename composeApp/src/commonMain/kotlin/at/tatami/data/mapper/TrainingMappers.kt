@file:OptIn(ExperimentalTime::class)

package at.tatami.data.mapper

import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import at.tatami.data.model.FirebaseTraining
import at.tatami.domain.model.Training
import dev.gitlive.firebase.firestore.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Converts a FirebaseTraining to a domain Training model.
 * @param id The document ID from Firestore
 * @return Domain Training model with LocalDateTime in system timezone
 */
fun FirebaseTraining.toDomain(id: String) = Training(
    id = id,
    clubId = clubId,
    groupId = groupId,
    startDateTime = Instant.fromEpochSeconds(startDateTime.seconds, startDateTime.nanoseconds.toLong())
        .toLocalDateTimeInSystemTimeZone(),
    notes = notes,
    attendedPersonIds = attendedPersonIds
)

/**
 * Converts a domain Training model to FirebaseTraining.
 * @return FirebaseTraining with Timestamp in UTC
 */
fun Training.toFirebase() = FirebaseTraining(
    clubId = clubId,
    groupId = groupId,
    startDateTime = run {
        val instant = startDateTime.toInstant(TimeZone.currentSystemDefault())
        Timestamp(instant.epochSeconds, instant.nanosecondsOfSecond)
    },
    notes = notes,
    attendedPersonIds = attendedPersonIds
)
