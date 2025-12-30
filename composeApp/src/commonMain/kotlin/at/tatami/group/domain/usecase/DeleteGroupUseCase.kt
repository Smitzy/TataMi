package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.GroupRepository
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase

/**
 * Use case for deleting a group and all its subcollections (trainings).
 * Calls a Cloud Function that performs recursive deletion.
 * Only club admins can delete groups.
 *
 * @param groupRepository Repository for group data access
 * @param getSelectedClubUseCase Use case to get the currently selected club
 * @param getSelectedPersonUseCase Use case to get the currently selected person
 */
class DeleteGroupUseCase(
    private val groupRepository: GroupRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase
) {
    /**
     * Deletes a group and all its trainings.
     * @param groupId The ID of the group to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(groupId: String): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        val selectedPerson = getSelectedPersonUseCase()
            ?: return Result.failure(Exception("No person selected"))

        // Verify the person is an admin
        if (!selectedClub.adminIds.contains(selectedPerson.id)) {
            return Result.failure(Exception("Only club admins can delete groups"))
        }

        return groupRepository.deleteGroup(
            clubId = selectedClub.id,
            groupId = groupId,
            personId = selectedPerson.id
        )
    }
}