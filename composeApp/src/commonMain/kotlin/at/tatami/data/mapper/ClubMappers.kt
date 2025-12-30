@file:OptIn(ExperimentalTime::class)

package at.tatami.data.mapper

import at.tatami.data.model.FirebaseClub
import at.tatami.domain.model.Club
import dev.gitlive.firebase.firestore.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Mappers for Club-related domain models
 */

fun FirebaseClub.toDomain(id: String) = Club(
    id = id,
    name = name,
    clubImgUrl = clubImgUrl,
    ownerId = ownerId,
    adminIds = adminIds,
    memberIds = memberIds,
    inviteCode = inviteCode,
    inviteCodeExpiresAt = inviteCodeExpiresAt?.let { ts ->
        Instant.fromEpochSeconds(ts.seconds, ts.nanoseconds.toLong())
    }
)

fun Club.toFirebase() = FirebaseClub(
    name = name,
    clubImgUrl = clubImgUrl,
    ownerId = ownerId,
    adminIds = adminIds,
    memberIds = memberIds,
    inviteCode = inviteCode,
    inviteCodeExpiresAt = inviteCodeExpiresAt?.let { instant ->
        Timestamp(instant.epochSeconds, instant.nanosecondsOfSecond)
    }
)