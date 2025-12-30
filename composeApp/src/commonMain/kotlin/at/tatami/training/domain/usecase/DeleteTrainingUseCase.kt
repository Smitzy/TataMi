package at.tatami.training.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.TrainingRepository

/**
 * Deletes a training session.
 * Only trainers of the group and club admins should be allowed to call this.
 *
 * @param trainingRepository Repository for training data access
 * @param getSelectedClubUseCase Use case to get the currently selected club
 */
class DeleteTrainingUseCase(
    private val trainingRepository: TrainingRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    /**
     * Deletes a training session.
     * @param groupId The ID of the group the training belongs to
     * @param trainingId The ID of the training to delete
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        groupId: String,
        trainingId: String
    ): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return trainingRepository.deleteTraining(
            clubId = selectedClub.id,
            groupId = groupId,
            trainingId = trainingId
        )
    }
}