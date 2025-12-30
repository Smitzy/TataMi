package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.domain.model.Group
import at.tatami.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Use case for observing a single group by its ID.
 * Returns a reactive Flow that updates when the group changes.
 * Automatically uses the currently selected club as context.
 */
class ObserveGroupByIdUseCase(
    private val groupRepository: GroupRepository,
    private val observeSelectedClubUseCase: ObserveSelectedClubUseCase
) {
    /**
     * Observes a group by its ID.
     * @param groupId The ID of the group to observe
     * @return Flow of the group, or null if not found or no club selected
     */
    operator fun invoke(groupId: String): Flow<Group?> {
        return observeSelectedClubUseCase()
            .flatMapLatest { club ->
                if (club == null) return@flatMapLatest flowOf(null)

                // Observe all groups and filter for the specific one
                groupRepository.observeGroups(club.id)
                    .map { groups -> groups.firstOrNull { it.id == groupId } }
            }
    }
}