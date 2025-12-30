package at.tatami.person.domain.usecase

import at.tatami.common.domain.service.SelectedEntityService
import at.tatami.domain.model.Person
import kotlinx.coroutines.flow.Flow

/**
 * Observes the currently selected person and returns the full Person object.
 * Uses the shared SelectedEntityService to provide a cached, real-time view
 * of the selected person with a single Firestore listener shared across
 * all observers in the application.
 */
class ObserveSelectedPersonUseCase(
    private val selectedEntityService: SelectedEntityService
) {
    /**
     * Returns a Flow that emits the currently selected Person object,
     * or null if no person is selected or the person doesn't exist.
     * This flow is shared across all observers, meaning only one
     * Firestore listener is maintained regardless of how many ViewModels
     * observe this data.
     */
    operator fun invoke(): Flow<Person?> = selectedEntityService.observeSelectedPerson()
}