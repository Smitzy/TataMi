package at.tatami.data.repository

import at.tatami.data.mapper.toDomain
import at.tatami.data.mapper.toFirebase
import at.tatami.data.model.FirebaseGroup
import at.tatami.domain.model.Group
import at.tatami.domain.repository.GroupRepository
import at.tatami.common.util.DataSourceLogger
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.functions.FirebaseFunctions
import dev.gitlive.firebase.functions.FirebaseFunctionsException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

/**
 * Firestore implementation of GroupRepository.
 * Manages group data in the /clubs/{clubId}/groups/{groupId} collection.
 */
class GroupRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions
) : GroupRepository {

    private fun groupsCollection(clubId: String) =
        firestore.collection("clubs").document(clubId).collection("groups")

    override suspend fun createGroup(group: Group): Group {
        val documentRef = groupsCollection(group.clubId).add(group.toFirebase())
        val groupWithId = group.copy(id = documentRef.id)
        DataSourceLogger.logFirestoreFetch("Group", "created: ${groupWithId.id}")
        return groupWithId
    }

    override suspend fun getGroupById(clubId: String, groupId: String): Group? {
        return try {
            val document = groupsCollection(clubId).document(groupId).get()
            val data = document.data(FirebaseGroup.serializer())
            val group = data.toDomain(groupId)
            DataSourceLogger.logFirestoreFetch("Group", groupId)
            group
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Group", "fetch failed: ${e.message}")
            null
        }
    }

    override fun observeGroups(clubId: String): Flow<List<Group>> {
        return groupsCollection(clubId)
            .snapshots
            .map { snapshot ->
                try {
                    val groups = snapshot.documents
                        .mapNotNull { document ->
                            try {
                                val data = document.data(FirebaseGroup.serializer())
                                val group = data.toDomain(document.id)
                                group
                            } catch (e: Exception) {
                                DataSourceLogger.logNoData("Group", "document parse failed: ${e.message}")
                                null
                            }
                        }
                        .sortedBy { it.name } // Sort alphabetically by name

                    if (groups.isNotEmpty()) {
                        DataSourceLogger.logFirestoreFetch("Groups", "clubId: $clubId (${groups.size} items)")
                    }
                    groups
                } catch (e: Exception) {
                    DataSourceLogger.logNoData("Groups", "observe failed: ${e.message}")
                    emptyList()
                }
            }
    }

    override suspend fun deleteGroup(clubId: String, groupId: String, personId: String): Result<Unit> {
        @Serializable
        data class DeleteGroupRequest(
            val clubId: String,
            val groupId: String,
            val personId: String
        )

        @Serializable
        data class DeleteGroupResponse(
            val success: Boolean,
            val message: String
        )

        return try {
            val request = DeleteGroupRequest(
                clubId = clubId,
                groupId = groupId,
                personId = personId
            )

            val result = functions.httpsCallable("deleteGroup").invoke(request)
            val response = result.data<DeleteGroupResponse>()

            if (response.success) {
                DataSourceLogger.logFirestoreFetch("Group", "deleted: $groupId")
                Result.success(Unit)
            } else {
                DataSourceLogger.logNoData("Group", "delete failed: ${response.message}")
                Result.failure(Exception(response.message))
            }
        } catch (e: FirebaseFunctionsException) {
            DataSourceLogger.logNoData("Group", "delete failed: ${e.message}")
            Result.failure(Exception(e.message ?: "Failed to delete group"))
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Group", "delete failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateGroupMembers(
        clubId: String,
        groupId: String,
        memberIds: List<String>
    ): Result<Unit> {
        return try {
            groupsCollection(clubId)
                .document(groupId)
                .updateFields { "memberIds" to memberIds }
            DataSourceLogger.logFirestoreFetch("Group", "members updated: $groupId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Group", "update members failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateGroupTrainers(
        clubId: String,
        groupId: String,
        trainerIds: List<String>
    ): Result<Unit> {
        return try {
            groupsCollection(clubId)
                .document(groupId)
                .updateFields { "trainerIds" to trainerIds }
            DataSourceLogger.logFirestoreFetch("Group", "trainers updated: $groupId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Group", "update trainers failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun updateGroupName(
        clubId: String,
        groupId: String,
        name: String
    ): Result<Unit> {
        return try {
            groupsCollection(clubId)
                .document(groupId)
                .updateFields { "name" to name }
            DataSourceLogger.logFirestoreFetch("Group", "name updated: $groupId")
            Result.success(Unit)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Group", "update name failed: ${e.message}")
            Result.failure(e)
        }
    }
}