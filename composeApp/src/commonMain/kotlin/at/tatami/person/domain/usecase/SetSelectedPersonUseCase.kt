package at.tatami.person.domain.usecase

import at.tatami.common.domain.service.SelectedEntityService
import at.tatami.domain.repository.SelectedPersonRepository
import at.tatami.domain.repository.SelectedClubRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

/**
 * Use case for setting the currently selected person.
 * Automatically prefetches person data to warm the cache.
 * When changing to a different person, clears the selected club since
 * clubs are person-specific.
 */
class SetSelectedPersonUseCase(
    private val selectedPersonRepository: SelectedPersonRepository,
    private val selectedEntityService: SelectedEntityService,
    private val selectedClubRepository: SelectedClubRepository
) {
    private val prefetchScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    suspend operator fun invoke(personId: String?) {
        val currentSelectedId = selectedPersonRepository.getSelectedPersonId()
        val currentCachedPerson = selectedEntityService.getCurrentSelectedPerson()
        
        // Case 1: Same person already selected and cached - skip everything
        if (currentSelectedId == personId && currentCachedPerson != null) {
            println("[SetSelectedPerson] Person $personId is already selected and cached, skipping")
            return
        }
        
        // Case 2: Same person selected but not in cache (e.g., after app restart) - prefetch only
        if (currentSelectedId == personId && currentCachedPerson == null && personId != null) {
            println("[SetSelectedPerson] Person $personId is selected but not cached, prefetching...")
            prefetchScope.launch {
                selectedEntityService.prefetchPerson(personId)
            }
            return
        }
        
        // Case 3: Different person or clearing selection - do full change
        println("[SetSelectedPerson] Changing selection from $currentSelectedId to $personId")
        selectedPersonRepository.setSelectedPersonId(personId)
        
        // Clear the selected club when changing person (clubs are person-specific)
        if (currentSelectedId != personId) {
            println("[SetSelectedPerson] Clearing club selection (person changed)")
            selectedClubRepository.clearSelectedClub()
        }
        
        // Prefetch person data asynchronously to warm the cache
        if (personId != null) {
            println("[SetSelectedPerson] Triggering prefetch for new person $personId")
            prefetchScope.launch {
                selectedEntityService.prefetchPerson(personId)
            }
        } else {
            println("[SetSelectedPerson] Clearing person selection (null)")
        }
    }
}