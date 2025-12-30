package at.tatami.club.domain.usecase

import at.tatami.common.domain.service.SelectedEntityService
import at.tatami.domain.model.Club
import at.tatami.common.util.DataSourceLogger
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

/**
 * Gets the currently selected club from the cached state.
 * This provides immediate access to the cached value without creating
 * a new Firestore listener. Use this when you need a one-time read
 * of the selected club.
 * 
 * For observing changes, use ObserveSelectedClubUseCase instead.
 */
class GetSelectedClubUseCase(
    private val selectedEntityService: SelectedEntityService
) {
    /**
     * Returns the currently selected Club, or null if no club is selected
     * or the club data hasn't been loaded yet.
     * This is an immediate read from the cached state.
     */
    operator fun invoke(): Club? {
        return selectedEntityService.getCurrentSelectedClub()
    }
    
    /**
     * Suspends until a selected club is available and returns it.
     * This ensures the club data is fully loaded from Firestore before returning.
     * Use this when you need to ensure a club is definitely selected and loaded.
     */
    suspend fun awaitSelectedClub(): Club? {
        val cachedClub = selectedEntityService.selectedClub.value
        return if (cachedClub != null) {
            DataSourceLogger.logCacheHit("Club", cachedClub.id)
            cachedClub
        } else {
            try {
                DataSourceLogger.logAwaitingFirestore("Club")
                val club = selectedEntityService.selectedClub
                    .filterNotNull()
                    .first()
                DataSourceLogger.logFirestoreFetch("Club", club.id)
                club
            } catch (_: Exception) {
                DataSourceLogger.logNoData("Club", "timeout or no selection")
                selectedEntityService.selectedClub.value
            }
        }
    }
}