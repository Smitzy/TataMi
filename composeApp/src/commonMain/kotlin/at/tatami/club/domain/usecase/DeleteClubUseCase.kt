package at.tatami.club.domain.usecase

import at.tatami.domain.repository.ClubRepository
import at.tatami.domain.repository.SelectedClubRepository
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase

/**
 * Use case for deleting a club and all its subcollections.
 * Calls a Cloud Function that performs recursive deletion of:
 * - All events in the club
 * - All groups and their trainings
 * - The invite code (if exists)
 * - The club document itself
 * - Updates all members' clubIds arrays
 *
 * Only the club OWNER can delete the club.
 */
class DeleteClubUseCase(
    private val clubRepository: ClubRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase,
    private val selectedClubRepository: SelectedClubRepository
) {
    /**
     * Deletes the currently selected club.
     * After successful deletion, clears the selected club.
     *
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        val selectedPerson = getSelectedPersonUseCase()
            ?: return Result.failure(Exception("No person selected"))

        // Verify the person is the OWNER (not just admin)
        if (selectedClub.ownerId != selectedPerson.id) {
            return Result.failure(Exception("Only the club owner can delete the club"))
        }

        val result = clubRepository.deleteClub(
            clubId = selectedClub.id,
            personId = selectedPerson.id
        )

        // Clear selected club on successful deletion
        if (result.isSuccess) {
            selectedClubRepository.clearSelectedClub()
        }

        return result
    }
}