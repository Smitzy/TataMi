package at.tatami.domain.repository

import at.tatami.domain.model.Group
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing groups within clubs.
 * Groups organize members and trainers for training sessions.
 */
interface GroupRepository {
    /**
     * Creates a new group and returns it with the assigned ID.
     *
     * @param group The group to create (id will be assigned by Firestore)
     * @return The created group with its assigned ID
     */
    suspend fun createGroup(group: Group): Group

    /**
     * Retrieves a single group by its ID.
     *
     * @param clubId The club ID containing the group
     * @param groupId The group ID to fetch
     * @return The group if found, null otherwise
     */
    suspend fun getGroupById(clubId: String, groupId: String): Group?

    /**
     * Observes all groups for a specific club in real-time.
     * Returns a Flow that emits updated lists whenever groups change.
     *
     * Note: Permission filtering (admin vs member) happens in the use case layer.
     *
     * @param clubId The club ID to observe groups for
     * @return Flow of group lists, sorted alphabetically by name
     */
    fun observeGroups(clubId: String): Flow<List<Group>>

    /**
     * Deletes a group and all its subcollections (trainings).
     * Calls a Cloud Function that performs recursive deletion.
     * Only club admins can delete groups.
     *
     * @param clubId The club ID containing the group
     * @param groupId The group ID to delete
     * @param personId The person ID requesting the deletion (must be admin)
     * @return Result indicating success or failure
     */
    suspend fun deleteGroup(clubId: String, groupId: String, personId: String): Result<Unit>

    /**
     * Updates the member list of a group.
     * Only trainers of the group and club admins can update members.
     *
     * @param clubId The club ID containing the group
     * @param groupId The group ID to update
     * @param memberIds The new list of member IDs
     * @return Result indicating success or failure
     */
    suspend fun updateGroupMembers(clubId: String, groupId: String, memberIds: List<String>): Result<Unit>

    /**
     * Updates the trainer list of a group.
     * Only trainers of the group and club admins can update trainers.
     * Note: All trainers must also be members (trainerIds ⊆ memberIds)
     *
     * @param clubId The club ID containing the group
     * @param groupId The group ID to update
     * @param trainerIds The new list of trainer IDs
     * @return Result indicating success or failure
     */
    suspend fun updateGroupTrainers(clubId: String, groupId: String, trainerIds: List<String>): Result<Unit>

    /**
     * Updates the name of a group.
     * Only trainers of the group and club admins can update the name.
     *
     * @param clubId The club ID containing the group
     * @param groupId The group ID to update
     * @param name The new name for the group
     * @return Result indicating success or failure
     */
    suspend fun updateGroupName(clubId: String, groupId: String, name: String): Result<Unit>
}