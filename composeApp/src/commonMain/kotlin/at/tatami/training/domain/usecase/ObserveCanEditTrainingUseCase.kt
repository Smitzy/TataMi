package at.tatami.training.domain.usecase

import at.tatami.club.domain.usecase.ObserveIsCurrentPersonAdminUseCase
import at.tatami.group.domain.usecase.ObserveGroupByIdUseCase
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Observes whether the current person can edit trainings for a specific group.
 * Returns true if the person is either:
 * - An admin of the club (admins have full access to all groups)
 * - A trainer of the specific group
 *
 * This determines permission to edit notes and toggle all attendance checkboxes.
 *
 * @param observeIsCurrentPersonAdminUseCase Use case to check admin status
 * @param observeGroupByIdUseCase Use case to observe group details
 * @param observeSelectedPersonUseCase Use case to get the currently selected person
 */
class ObserveCanEditTrainingUseCase(
    private val observeIsCurrentPersonAdminUseCase: ObserveIsCurrentPersonAdminUseCase,
    private val observeGroupByIdUseCase: ObserveGroupByIdUseCase,
    private val observeSelectedPersonUseCase: ObserveSelectedPersonUseCase
) {
    /**
     * Observes whether the current person can edit trainings for the given group.
     * @param groupId The ID of the group to check permissions for
     * @return Flow of boolean indicating whether the person can edit trainings
     */
    operator fun invoke(groupId: String): Flow<Boolean> {
        return combine(
            observeIsCurrentPersonAdminUseCase(),
            observeSelectedPersonUseCase(),
            observeGroupByIdUseCase(groupId)
        ) { isAdmin, person, group ->
            // Admin can edit trainings in any group
            if (isAdmin) return@combine true

            // Check if person is a trainer of this specific group
            if (person == null || group == null) return@combine false

            group.isPersonTrainer(person.id)
        }
    }
}
