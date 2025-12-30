package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.GroupRepository

/**
 * Use case for updating the trainer list of a group.
 * Only trainers of the group and club admins can update trainers.
 * Note: All trainers must also be members (trainerIds ⊆ memberIds)
 *
 * @param groupRepository Repository for group data access
 * @param getSelectedClubUseCase Use case to get the currently selected club
 */
class UpdateGroupTrainersUseCase(
    private val groupRepository: GroupRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    /**
     * Updates the trainer list of a group.
     * @param groupId The ID of the group to update
     * @param trainerIds The new list of trainer IDs
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(groupId: String, trainerIds: List<String>): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return groupRepository.updateGroupTrainers(
            clubId = selectedClub.id,
            groupId = groupId,
            trainerIds = trainerIds
        )
    }
}