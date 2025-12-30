package at.tatami.club.domain.usecase

import at.tatami.common.domain.service.SelectedEntityService
import at.tatami.domain.repository.SelectedClubRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

/**
 * Use case for setting the currently selected club.
 * Automatically prefetches club data to warm the cache.
 */
class SetSelectedClubUseCase(
    private val selectedClubRepository: SelectedClubRepository,
    private val selectedEntityService: SelectedEntityService
) {
    private val prefetchScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    suspend operator fun invoke(clubId: String?) {
        val currentSelectedId = selectedClubRepository.getSelectedClubId()
        val currentCachedClub = selectedEntityService.getCurrentSelectedClub()
        
        // Case 1: Same club already selected and cached - skip everything
        if (currentSelectedId == clubId && currentCachedClub != null) {
            println("[SetSelectedClub] Club $clubId is already selected and cached, skipping")
            return
        }
        
        // Case 2: Same club selected but not in cache (e.g., after app restart) - prefetch only
        if (currentSelectedId == clubId && currentCachedClub == null && clubId != null) {
            println("[SetSelectedClub] Club $clubId is selected but not cached, prefetching...")
            prefetchScope.launch {
                selectedEntityService.prefetchClub(clubId)
            }
            return
        }
        
        // Case 3: Different club or clearing selection - do full change
        println("[SetSelectedClub] Changing selection from $currentSelectedId to $clubId")
        selectedClubRepository.setSelectedClubId(clubId)
        
        // Prefetch club data asynchronously to warm the cache
        if (clubId != null) {
            println("[SetSelectedClub] Triggering prefetch for new club $clubId")
            prefetchScope.launch {
                selectedEntityService.prefetchClub(clubId)
            }
        } else {
            println("[SetSelectedClub] Clearing club selection (null)")
        }
    }
}