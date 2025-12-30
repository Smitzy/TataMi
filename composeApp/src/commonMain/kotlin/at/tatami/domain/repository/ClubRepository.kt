package at.tatami.domain.repository

import at.tatami.core.ImageType
import at.tatami.domain.model.Club
import kotlinx.coroutines.flow.Flow

interface ClubRepository {
    suspend fun createClub(club: Club): Club
    suspend fun updateClub(club: Club): Club
    suspend fun getClubById(clubId: String): Club?

    // Join club with invite code (direct membership)
    suspend fun joinClubWithCode(inviteCode: String, personId: String): JoinClubResponse

    // Invite code management (admin only)
    suspend fun generateInviteCode(clubId: String, personId: String): GenerateInviteCodeResponse
    suspend fun disableInviteCode(clubId: String, personId: String)

    suspend fun uploadClubProfilePicture(clubId: String, imageData: ByteArray, imageType: ImageType): String

    fun observeClub(clubId: String): Flow<Club?>

    /**
     * Deletes a club and all its subcollections (events, groups, trainings).
     * Calls a Cloud Function that performs recursive deletion.
     * Also cleans up invite codes and updates member person documents.
     * Only the club owner can delete the club.
     *
     * @param clubId The club ID to delete
     * @param personId The person ID requesting the deletion (must be owner)
     * @return Result indicating success or failure
     */
    suspend fun deleteClub(clubId: String, personId: String): Result<Unit>
}

data class JoinClubResponse(
    val success: Boolean,
    val clubId: String,
    val clubName: String,
    val message: String
)

data class GenerateInviteCodeResponse(
    val inviteCode: String,
    val expiresAt: Long
)