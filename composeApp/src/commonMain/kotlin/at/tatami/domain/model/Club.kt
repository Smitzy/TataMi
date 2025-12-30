@file:OptIn(kotlin.time.ExperimentalTime::class)

package at.tatami.domain.model

import kotlin.time.Instant
import kotlinx.datetime.TimeZone

data class Club(
    val id: String,
    val name: String,
    val clubImgUrl: String? = null,
    val ownerId: String,
    val adminIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList(),
    val inviteCode: String = "",
    val inviteCodeExpiresAt: Instant? = null
)

enum class MemberRole {
    ADMIN
}