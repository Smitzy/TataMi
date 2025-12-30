package at.tatami.club.domain.usecase

import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Observes whether the currently selected person is an admin of the currently selected club.
 * This is a convenience use case that combines checking both the selected person and club.
 * 
 * Returns true if:
 * - Both a person and club are selected
 * - The person's ID is in the club's adminIds array
 * 
 * Returns false if:
 * - No person is selected
 * - No club is selected
 * - The person is not an admin of the club
 */
class ObserveIsCurrentPersonAdminUseCase(
    private val observeSelectedPerson: ObserveSelectedPersonUseCase,
    private val observeSelectedClub: ObserveSelectedClubUseCase
) {
    /**
     * Returns a Flow that emits true if the current person is an admin of the current club
     */
    operator fun invoke(): Flow<Boolean> = 
        combine(
            observeSelectedClub(),
            observeSelectedPerson()
        ) { club, person ->
            club != null && person != null && club.adminIds.contains(person.id)
        }
}