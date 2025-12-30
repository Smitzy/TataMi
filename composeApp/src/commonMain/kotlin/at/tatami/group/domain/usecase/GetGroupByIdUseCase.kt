package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.model.Group
import at.tatami.domain.repository.GroupRepository

/**
 * Use case for fetching a single group by its ID.
 * Automatically uses the currently selected club as context.
 *
 * Future use: For group detail and edit screens.
 */
class GetGroupByIdUseCase(
    private val groupRepository: GroupRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(groupId: String): Group? {
        val selectedClub = getSelectedClubUseCase() ?: return null
        return groupRepository.getGroupById(selectedClub.id, groupId)
    }
}