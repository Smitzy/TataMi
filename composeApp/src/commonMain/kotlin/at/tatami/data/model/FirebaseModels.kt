package at.tatami.data.model

import dev.gitlive.firebase.firestore.Timestamp
import kotlinx.serialization.Serializable

@Serializable
data class FirebaseUser(
    val email: String = "",
    val emailVerified: Boolean = false,
    val createdAt: Timestamp = Timestamp(0, 0),
    val lastLoginAt: Timestamp = Timestamp(0, 0),
    val fcmToken: String? = null
)

@Serializable
data class FirebasePerson(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val yearOfBirth: Int = 0,
    val sex: String = "",
    val personImgUrl: String? = null,
    val clubIds: List<String> = emptyList()
)

@Serializable
data class FirebaseClub(
    val name: String = "",
    val clubImgUrl: String? = null,
    val ownerId: String = "",
    val adminIds: List<String> = emptyList(),
    val memberIds: List<String> = emptyList(),
    val inviteCode: String = "",
    val inviteCodeExpiresAt: Timestamp? = null
)

@Serializable
data class FirebaseEvent(
    val clubId: String = "",
    val title: String = "",
    val description: String = "",
    val startDateTime: Timestamp = Timestamp(0, 0),
    val location: String = "",
    val creatorId: String = "",
    val invitedPersonIds: List<String> = emptyList(),
    val status: Map<String, String> = emptyMap() // personId -> "YES" | "NO" | "MAYBE" | "NO_RESPONSE"
)

@Serializable
data class FirebaseGroup(
    val clubId: String = "",
    val name: String = "",
    val memberIds: List<String> = emptyList(),
    val trainerIds: List<String> = emptyList()
)

@Serializable
data class FirebaseTraining(
    val clubId: String = "",
    val groupId: String = "",
    val startDateTime: Timestamp = Timestamp(0, 0),
    val notes: String = "",
    val attendedPersonIds: List<String> = emptyList()
)


