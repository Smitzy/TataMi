package at.tatami.training.domain.usecase

import at.tatami.club.domain.usecase.ObserveSelectedClubUseCase
import at.tatami.domain.model.Training
import at.tatami.domain.repository.TrainingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Observes upcoming trainings for a specific group.
 * Upcoming trainings are those with startDateTime >= current time.
 *
 * @param trainingRepository Repository for training data access
 * @param observeSelectedClubUseCase Use case to get the currently selected club
 */
class ObserveUpcomingTrainingsUseCase(
    private val trainingRepository: TrainingRepository,
    private val observeSelectedClubUseCase: ObserveSelectedClubUseCase
) {
    /**
     * Observes upcoming trainings for the given group in the selected club.
     * @param groupId The ID of the group to observe trainings for
     * @return Flow of upcoming trainings, sorted by startDateTime ascending
     */
    operator fun invoke(groupId: String): Flow<List<Training>> {
        return observeSelectedClubUseCase()
            .flatMapLatest { club ->
                if (club == null) {
                    return@flatMapLatest flowOf(emptyList())
                }
                trainingRepository.observeUpcomingTrainings(club.id, groupId)
            }
    }
}