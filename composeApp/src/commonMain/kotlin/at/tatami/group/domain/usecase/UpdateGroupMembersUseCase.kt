package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.GroupRepository

/**
 * Use case for updating the member list of a group.
 * Only trainers of the group and club admins can update members.
 *
 * @param groupRepository Repository for group data access
 * @param getSelectedClubUseCase Use case to get the currently selected club
 */
class UpdateGroupMembersUseCase(
    private val groupRepository: GroupRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    /**
     * Updates the member list of a group.
     * @param groupId The ID of the group to update
     * @param memberIds The new list of member IDs
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(groupId: String, memberIds: List<String>): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return groupRepository.updateGroupMembers(
            clubId = selectedClub.id,
            groupId = groupId,
            memberIds = memberIds
        )
    }
}