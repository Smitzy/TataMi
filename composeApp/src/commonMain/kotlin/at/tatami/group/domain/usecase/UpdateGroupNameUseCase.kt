package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.GroupRepository

/**
 * Use case for updating the name of a group.
 * Only trainers of the group and club admins can update the name.
 *
 * @param groupRepository Repository for group data access
 * @param getSelectedClubUseCase Use case to get the currently selected club
 */
class UpdateGroupNameUseCase(
    private val groupRepository: GroupRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    /**
     * Updates the name of a group.
     * @param groupId The ID of the group to update
     * @param name The new name for the group
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(groupId: String, name: String): Result<Unit> {
        if (name.isBlank()) {
            return Result.failure(Exception("Group name cannot be empty"))
        }

        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return groupRepository.updateGroupName(
            clubId = selectedClub.id,
            groupId = groupId,
            name = name.trim()
        )
    }
}
