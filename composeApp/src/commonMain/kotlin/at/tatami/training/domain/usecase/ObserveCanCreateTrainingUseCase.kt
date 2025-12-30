package at.tatami.training.domain.usecase

import at.tatami.club.domain.usecase.ObserveIsCurrentPersonAdminUseCase
import at.tatami.group.domain.usecase.GetGroupByIdUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Observes whether the current person can create trainings for a specific group.
 * Returns true if the person is either:
 * - An admin of the club (admins have full access to all groups)
 * - A trainer of the specific group
 *
 * @param observeIsCurrentPersonAdminUseCase Use case to check admin status
 * @param getGroupByIdUseCase Use case to fetch group details
 * @param observeSelectedPersonUseCase Use case to get the currently selected person
 */
class ObserveCanCreateTrainingUseCase(
    private val observeIsCurrentPersonAdminUseCase: ObserveIsCurrentPersonAdminUseCase,
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val observeSelectedPersonUseCase: ObserveSelectedPersonUseCase
) {
    /**
     * Observes whether the current person can create trainings for the given group.
     * @param groupId The ID of the group to check permissions for
     * @return Flow of boolean indicating whether the person can create trainings
     */
    operator fun invoke(groupId: String): Flow<Boolean> {
        return combine(
            observeIsCurrentPersonAdminUseCase(),
            observeSelectedPersonUseCase()
        ) { isAdmin, person ->
            // Admin can create trainings in any group
            if (isAdmin) return@combine true

            // Check if person is a trainer of this specific group
            if (person == null) return@combine false

            // Fetch group to check trainer status
            val group = getGroupByIdUseCase(groupId)
            group?.isPersonTrainer(person.id) ?: false
        }
    }
}