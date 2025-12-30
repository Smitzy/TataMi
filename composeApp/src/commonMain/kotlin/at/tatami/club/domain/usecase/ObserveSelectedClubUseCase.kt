package at.tatami.club.domain.usecase

import at.tatami.common.domain.service.SelectedEntityService
import at.tatami.domain.model.Club
import kotlinx.coroutines.flow.Flow

/**
 * Observes the currently selected club and returns the full Club object.
 * Uses the shared SelectedEntityService to provide a cached, real-time view
 * of the selected club with a single Firestore listener shared across
 * all observers in the application.
 */
class ObserveSelectedClubUseCase(
    private val selectedEntityService: SelectedEntityService
) {
    /**
     * Returns a Flow that emits the currently selected Club object,
     * or null if no club is selected or the club doesn't exist.
     * This flow is shared across all observers, meaning only one
     * Firestore listener is maintained regardless of how many ViewModels
     * observe this data.
     */
    operator fun invoke(): Flow<Club?> = selectedEntityService.observeSelectedClub()
}