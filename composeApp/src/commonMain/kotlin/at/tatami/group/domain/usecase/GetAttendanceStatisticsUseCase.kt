package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.repository.TrainingRepository
import at.tatami.person.domain.usecase.GetPersonByIdUseCase
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate

/**
 * Calculates attendance statistics for all members of a group.
 * Filters trainings from a cutoff date (optional) to now.
 *
 * @param trainingRepository Repository for training data access
 * @param getSelectedClubUseCase Use case to get the currently selected club
 * @param getPersonByIdUseCase Use case to fetch person details
 */
class GetAttendanceStatisticsUseCase(
    private val trainingRepository: TrainingRepository,
    private val getSelectedClubUseCase: GetSelectedClubUseCase,
    private val getPersonByIdUseCase: GetPersonByIdUseCase
) {
    /**
     * Calculates attendance statistics for group members.
     *
     * @param groupId The ID of the group
     * @param memberIds List of all member IDs in the group
     * @param cutoffDate Optional start date filter (null = all past trainings)
     * @return List of MemberAttendanceStats sorted by attendance rate descending
     */
    suspend operator fun invoke(
        groupId: String,
        memberIds: List<String>,
        cutoffDate: LocalDate? = null
    ): Result<List<MemberAttendanceStats>> {
        val selectedClub = getSelectedClubUseCase()
            ?: return Result.failure(Exception("No club selected"))

        return try {
            // Get all past trainings (reactive flow, take first emission)
            val allPastTrainings = trainingRepository
                .observePastTrainings(selectedClub.id, groupId)
                .first()

            // Filter by cutoff date if provided
            val filteredTrainings = if (cutoffDate != null) {
                allPastTrainings.filter { training ->
                    training.startDateTime.date >= cutoffDate
                }
            } else {
                allPastTrainings
            }

            val totalTrainings = filteredTrainings.size

            // Calculate attendance for each member
            val memberStats = memberIds.map { personId ->
                val person = getPersonByIdUseCase(personId)
                val attendedCount = filteredTrainings.count { training ->
                    training.attendedPersonIds.contains(personId)
                }
                val attendanceRate = if (totalTrainings > 0) {
                    attendedCount.toFloat() / totalTrainings.toFloat()
                } else {
                    0f
                }

                MemberAttendanceStats(
                    personId = personId,
                    personName = person?.let { "${it.firstName} ${it.lastName}" } ?: "Unknown",
                    profileImageUrl = person?.personImgUrl,
                    attendedCount = attendedCount,
                    totalTrainings = totalTrainings,
                    attendanceRate = attendanceRate
                )
            }.sortedByDescending { it.attendanceRate }

            Result.success(memberStats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Represents attendance statistics for a single member.
     */
    data class MemberAttendanceStats(
        val personId: String,
        val personName: String,
        val profileImageUrl: String?,
        val attendedCount: Int,
        val totalTrainings: Int,
        val attendanceRate: Float  // 0.0 to 1.0
    )
}
