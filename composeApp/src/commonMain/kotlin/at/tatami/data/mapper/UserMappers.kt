@file:OptIn(ExperimentalTime::class)

package at.tatami.data.mapper

import at.tatami.data.model.FirebaseUser
import at.tatami.domain.model.User
import dev.gitlive.firebase.firestore.Timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Mappers for User-related domain models
 */

fun FirebaseUser.toDomain(id: String) = User(
    id = id,
    email = email,
    emailVerified = emailVerified,
    createdAt = Instant.fromEpochSeconds(createdAt.seconds, createdAt.nanoseconds.toLong()),
    lastLoginAt = Instant.fromEpochSeconds(lastLoginAt.seconds, lastLoginAt.nanoseconds.toLong()),
    fcmToken = fcmToken
)

fun User.toFirebase() = FirebaseUser(
    email = email,
    emailVerified = emailVerified,
    createdAt = Timestamp(createdAt.epochSeconds, createdAt.nanosecondsOfSecond),
    lastLoginAt = Timestamp(lastLoginAt.epochSeconds, lastLoginAt.nanosecondsOfSecond),
    fcmToken = fcmToken
)