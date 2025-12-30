package at.tatami.person.domain.usecase

import at.tatami.common.domain.service.SelectedEntityService
import at.tatami.domain.model.Person
import at.tatami.common.util.DataSourceLogger

/**
 * Gets the currently selected person from the cached state.
 * This provides immediate access to the cached value without creating
 * a new Firestore listener. Use this when you need a one-time read
 * of the selected person.
 * 
 * For observing changes, use ObserveSelectedPersonUseCase instead.
 */
class GetSelectedPersonUseCase(
    private val selectedEntityService: SelectedEntityService
) {
    /**
     * Returns the currently selected Person, or null if no person is selected
     * or the person data hasn't been loaded yet.
     * This is an immediate read from the cached state.
     */
    operator fun invoke(): Person? {
        return selectedEntityService.getCurrentSelectedPerson()
    }
    
    /**
     * Suspends until a selected person is available and returns it.
     * This ensures the person data is fully loaded from Firestore before returning.
     * Use this when you need to ensure a person is definitely selected and loaded.
     */
    suspend fun awaitSelectedPerson(): Person? {
        val person = selectedEntityService.awaitSelectedPerson()
        if (person != null) {
            DataSourceLogger.logCacheHit("Person", "${person.id} (via awaitSelectedPerson)")
        } else {
            DataSourceLogger.logNoData("Person", "awaitSelectedPerson returned null")
        }
        return person
    }
}