package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.domain.model.Group
import at.tatami.domain.repository.GroupRepository
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Use case for observing groups with permission-based filtering.
 *
 * Permission Rules:
 * - Admins: See ALL groups in the club
 * - Non-admins: See ONLY groups they are members of
 *
 * Returns a reactive Flow that updates whenever:
 * - Groups are added/modified/deleted in Firestore
 * - Selected club or person changes
 */
class ObserveGroupsUseCase(
    private val groupRepository: GroupRepository,
    private val observeSelectedClubUseCase: ObserveSelectedClubUseCase,
    private val observeSelectedPersonUseCase: ObserveSelectedPersonUseCase
) {
    operator fun invoke(): Flow<List<Group>> {
        return combine(
            observeSelectedClubUseCase(),
            observeSelectedPersonUseCase()
        ) { club, person ->
            club to person
        }.flatMapLatest { (club, person) ->
            if (club == null || person == null) {
                // No club or person selected - return empty list
                return@flatMapLatest flowOf(emptyList())
            }

            // Observe all groups for the club from repository
            groupRepository.observeGroups(club.id)
                .map { groups ->
                    // Apply permission-based filtering
                    val isAdmin = club.adminIds.contains(person.id)

                    if (isAdmin) {
                        // Admins see ALL groups
                        groups
                    } else {
                        // Non-admins only see groups they're a member of
                        groups.filter { group ->
                            group.memberIds.contains(person.id)
                        }
                    }
                }
        }
    }
}