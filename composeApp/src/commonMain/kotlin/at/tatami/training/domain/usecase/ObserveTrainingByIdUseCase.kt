package at.tatami.training.domain.usecase

import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.domain.model.Training
import at.tatami.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Observes a single training by ID with real-time updates.
 *
 * @param trainingRepository Repository for training data access
 * @param observeSelectedClubUseCase Use case to get the currently selected club
 */
class ObserveTrainingByIdUseCase(
    private val trainingRepository: TrainingRepository,
    private val observeSelectedClubUseCase: ObserveSelectedClubUseCase
) {
    /**
     * Observes a single training in the selected club.
     * @param groupId The ID of the group the training belongs to
     * @param trainingId The ID of the training to observe
     * @return Flow of training or null if not found or no club selected
     */
    operator fun invoke(groupId: String, trainingId: String): Flow<Training?> {
        return observeSelectedClubUseCase()
            .flatMapLatest { club ->
                if (club == null) {
                    return@flatMapLatest flowOf(null)
                }
                trainingRepository.observeTrainingById(club.id, groupId, trainingId)
            }
    }
}
