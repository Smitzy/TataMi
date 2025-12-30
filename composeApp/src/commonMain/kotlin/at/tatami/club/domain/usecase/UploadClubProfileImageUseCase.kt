@file:OptIn(kotlin.time.ExperimentalTime::class)

package at.tatami.club.domain.usecase

import at.tatami.core.ImageType
import at.tatami.domain.model.Club
import at.tatami.domain.repository.ClubRepository

class UploadClubProfileImageUseCase(
    private val clubRepository: ClubRepository
) {
    suspend operator fun invoke(
        club: Club,
        imageData: ByteArray,
        imageType: ImageType
    ): Result<Club> {
        return try {
            // Upload the image and get the URL
            val imageUrl = clubRepository.uploadClubProfilePicture(club.id, imageData, imageType)

            // Update the club with the image URL
            val updatedClub = club.copy(
                clubImgUrl = imageUrl
            )

            val result = clubRepository.updateClub(updatedClub)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}