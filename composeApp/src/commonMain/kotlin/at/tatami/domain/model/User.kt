@file:OptIn(kotlin.time.ExperimentalTime::class)

package at.tatami.domain.model

import kotlin.time.Instant

data class User (
    val id: String,
    val email: String,
    val emailVerified: Boolean = false,
    val createdAt: Instant,
    val lastLoginAt: Instant,
    val fcmToken: String? = null
)