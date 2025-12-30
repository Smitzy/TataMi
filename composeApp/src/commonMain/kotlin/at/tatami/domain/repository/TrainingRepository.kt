package at.tatami.domain.repository

import at.tatami.domain.model.Training
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing training sessions.
 * Trainings are scoped to specific groups within clubs.
 */
interface TrainingRepository {
    /**
     * Creates a new training session.
     * @param training The training to create (id will be generated)
     * @return The created training with generated ID
     */
    suspend fun createTraining(training: Training): Training

    /**
     * Observes upcoming trainings for a specific group.
     * Upcoming trainings are those with startDateTime >= current time.
     * @param clubId The club ID
     * @param groupId The group ID
     * @return Flow of upcoming trainings, sorted by startDateTime ascending
     */
    fun observeUpcomingTrainings(clubId: String, groupId: String): Flow<List<Training>>

    /**
     * Observes past trainings for a specific group.
     * Past trainings are those with startDateTime < current time.
     * @param clubId The club ID
     * @param groupId The group ID
     * @return Flow of past trainings, sorted by startDateTime descending
     */
    fun observePastTrainings(clubId: String, groupId: String): Flow<List<Training>>

    /**
     * Observes a single training by ID with real-time updates.
     * @param clubId The club ID
     * @param groupId The group ID
     * @param trainingId The training ID
     * @return Flow of training or null if not found
     */
    fun observeTrainingById(clubId: String, groupId: String, trainingId: String): Flow<Training?>

    /**
     * Updates the notes field of a training.
     * @param clubId The club ID
     * @param groupId The group ID
     * @param trainingId The training ID
     * @param notes The new notes content
     * @return Result indicating success or failure
     */
    suspend fun updateTrainingNotes(clubId: String, groupId: String, trainingId: String, notes: String): Result<Unit>

    /**
     * Updates the attendance list of a training.
     * @param clubId The club ID
     * @param groupId The group ID
     * @param trainingId The training ID
     * @param attendedPersonIds List of person IDs who attended
     * @return Result indicating success or failure
     */
    suspend fun updateTrainingAttendance(clubId: String, groupId: String, trainingId: String, attendedPersonIds: List<String>): Result<Unit>

    /**
     * Deletes a training session.
     * @param clubId The club ID
     * @param groupId The group ID
     * @param trainingId The training ID
     * @return Result indicating success or failure
     */
    suspend fun deleteTraining(clubId: String, groupId: String, trainingId: String): Result<Unit>
}