package at.tatami.training.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.TrainingRepository

/**
 * Updates the attendance list of a training session.
 * Permission checks should be done at the ViewModel level based on user role.
 *
 * @param trainingRepository Repository for training data access
 * @param getSelectedClubUseCase Use case to get the currently selected club
 */
class UpdateTrainingAttendanceUseCase(
    private val trainingRepository: TrainingRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    /**
     * Updates training attendance.
     * @param groupId The ID of the group the training belongs to
     * @param trainingId The ID of the training to update
     * @param attendedPersonIds List of person IDs who attended
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(
        groupId: String,
        trainingId: String,
        attendedPersonIds: List<String>
    ): Result<Unit> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return trainingRepository.updateTrainingAttendance(
            clubId = selectedClub.id,
            groupId = groupId,
            trainingId = trainingId,
            attendedPersonIds = attendedPersonIds
        )
    }
}
