@file:OptIn(ExperimentalTime::class)

package at.tatami.data.repository

import at.tatami.common.util.DataSourceLogger
import at.tatami.common.util.toLocalDateTimeInSystemTimeZone
import at.tatami.data.mapper.toDomain
import at.tatami.data.mapper.toFirebase
import at.tatami.data.model.FirebaseTraining
import at.tatami.domain.model.Training
import at.tatami.domain.repository.TrainingRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of TrainingRepository using Firebase Firestore.
 * Trainings are stored at: /clubs/{clubId}/groups/{groupId}/trainings/{trainingId}
 */
class TrainingRepositoryImpl(
    private val firestore: FirebaseFirestore
) : TrainingRepository {

    /**
     * Gets the Firestore collection reference for trainings in a specific group.
     */
    private fun trainingsCollection(clubId: String, groupId: String) =
        firestore.collection("clubs")
            .document(clubId)
            .collection("groups")
            .document(groupId)
            .collection("trainings")

    override suspend fun createTraining(training: Training): Training {
        val documentRef = trainingsCollection(training.clubId, training.groupId)
            .add(training.toFirebase())
        val trainingWithId = training.copy(id = documentRef.id)
        DataSourceLogger.logFirestoreFetch("Training", "created: ${documentRef.id}")
        return trainingWithId
    }

    override fun observeUpcomingTrainings(clubId: String, groupId: String): Flow<List<Training>> {
        val now = Clock.System.now().toLocalDateTimeInSystemTimeZone()

        return trainingsCollection(clubId, groupId)
            .snapshots
            .map { snapshot ->
                try {
                    val trainings = snapshot.documents
                        .mapNotNull { document ->
                            try {
                                val data = document.data(FirebaseTraining.serializer())
                                val training = data.toDomain(document.id)
                                training
                            } catch (e: Exception) {
                                null
                            }
                        }
                        .filter { training ->
                            // Only show upcoming trainings
                            training.startDateTime >= now
                        }
                        .sortedBy { it.startDateTime }

                    if (trainings.isNotEmpty()) {
                        DataSourceLogger.logFirestoreFetch(
                            "Upcoming Trainings",
                            "clubId: $clubId, groupId: $groupId (${trainings.size} items)"
                        )
                    }
                    trainings
                } catch (e: Exception) {
                    DataSourceLogger.logNoData("Upcoming Trainings", "observe failed: ${e.message}")
                    emptyList()
                }
            }
    }

    override fun observePastTrainings(clubId: String, groupId: String): Flow<List<Training>> {
        val now = Clock.System.now().toLocalDateTimeInSystemTimeZone()

        return trainingsCollection(clubId, groupId)
            .snapshots
            .map { snapshot ->
                try {
                    val trainings = snapshot.documents
                        .mapNotNull { document ->
                            try {
                                val data = document.data(FirebaseTraining.serializer())
                                val training = data.toDomain(document.id)
                                training
                            } catch (e: Exception) {
                                null
                            }
                        }
                        .filter { training ->
                            // Only show past trainings
                            training.startDateTime < now
                        }
                        .sortedByDescending { it.startDateTime }

                    if (trainings.isNotEmpty()) {
                        DataSourceLogger.logFirestoreFetch(
                            "Past Trainings",
                            "clubId: $clubId, groupId: $groupId (${trainings.size} items)"
                        )
                    }
                    trainings
                } catch (e: Exception) {
                    DataSourceLogger.logNoData("Past Trainings", "observe failed: ${e.message}")
                    emptyList()
                }
            }
    }

    override fun observeTrainingById(clubId: String, groupId: String, trainingId: String): Flow<Training?> {
        return trainingsCollection(clubId, groupId)
            .document(trainingId)
            .snapshots
            .map { snapshot ->
                try {
                    if (!snapshot.exists) {
                        DataSourceLogger.logNoData("Training", "trainingId: $trainingId not found")
                        return@map null
                    }
                    val data = snapshot.data(FirebaseTraining.serializer())
                    val training = data.toDomain(snapshot.id)
                    DataSourceLogger.logFirestoreFetch("Training", "trainingId: $trainingId")
                    training
                } catch (e: Exception) {
                    DataSourceLogger.logNoData("Training", "observe failed: ${e.message}")
                    null
                }
            }
    }

    override suspend fun updateTrainingNotes(
        clubId: String,
        groupId: String,
        trainingId: String,
        notes: String
    ): Result<Unit> {
        return try {
            trainingsCollection(clubId, groupId)
                .document(trainingId)
                .updateFields {
                    "notes" to notes
                }
            DataSourceLogger.logFirestoreFetch("Training Notes", "updated: $trainingId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Training Notes", "update failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateTrainingAttendance(
        clubId: String,
        groupId: String,
        trainingId: String,
        attendedPersonIds: List<String>
    ): Result<Unit> {
        return try {
            trainingsCollection(clubId, groupId)
                .document(trainingId)
                .updateFields {
                    "attendedPersonIds" to attendedPersonIds
                }
            DataSourceLogger.logFirestoreFetch("Training Attendance", "updated: $trainingId (${attendedPersonIds.size} attendees)")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Training Attendance", "update failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteTraining(
        clubId: String,
        groupId: String,
        trainingId: String
    ): Result<Unit> {
        return try {
            trainingsCollection(clubId, groupId)
                .document(trainingId)
                .delete()
            DataSourceLogger.logFirestoreFetch("Training", "deleted: $trainingId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Training", "delete failed: ${e.message}")
            Result.failure(e)
        }
    }
}