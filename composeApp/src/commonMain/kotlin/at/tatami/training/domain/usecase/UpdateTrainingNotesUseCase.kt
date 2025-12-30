package at.tatami.training.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.TrainingRepository

/**
 * Updates the notes field of a training session.
 * Only trainers of the group and club admins should be allowed to call this.
 *
 * @param trainingRepository Repository for training data access
 * @param getSelectedClubUseCase Use case to get the currently selected club
 */
class UpdateTrainingNotesUseCase(
    private val trainingRepository: TrainingRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    /**
     * Updates training notes.
     * @param groupId The ID of the group the training belongs to
     * @param trainingId The ID of the training to update
     * @param notes The new notes content
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        groupId: String,
        trainingId: String,
        notes: String
    ): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return trainingRepository.updateTrainingNotes(
            clubId = selectedClub.id,
            groupId = groupId,
            trainingId = trainingId,
            notes = notes
        )
    }
}
