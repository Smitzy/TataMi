@file:OptIn(kotlin.time.ExperimentalTime::class)

package at.tatami.data.repository

import at.tatami.core.ImageType
import at.tatami.core.StorageService
import at.tatami.core.createDataFromBytes
import at.tatami.data.mapper.*
import at.tatami.data.model.*
import at.tatami.domain.model.*
import at.tatami.domain.repository.ClubRepository
import at.tatami.domain.repository.GenerateInviteCodeResponse
import at.tatami.domain.repository.JoinClubResponse
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import at.tatami.common.util.DataSourceLogger
import at.tatami.common.domain.ClubError
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.functions.FirebaseFunctionsException
import dev.gitlive.firebase.functions.FunctionsExceptionCode
import dev.gitlive.firebase.functions.code

class ClubRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val functions: FirebaseFunctions,
    private val storageService: StorageService
) : ClubRepository {
    
    private val clubsCollection = firestore.collection("clubs")
    
    override suspend fun createClub(club: Club): Club {
        val documentRef = clubsCollection.add(club.toFirebase())
        val clubWithId = club.copy(id = documentRef.id)
        
        return clubWithId
    }
    
    override suspend fun updateClub(club: Club): Club {
        clubsCollection.document(club.id).set(club.toFirebase())

        return club
    }

    override suspend fun getClubById(clubId: String): Club? {
        return try {
            val document = clubsCollection.document(clubId).get()
            val data = document.data(FirebaseClub.serializer())
            val club = data.toDomain(clubId)
            DataSourceLogger.logFirestoreFetch("Club", clubId)
            club
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Club", "fetch failed: ${e.message}")
            null
        }
    }
    
    override suspend fun uploadClubProfilePicture(clubId: String, imageData: ByteArray, imageType: ImageType): String {
        val path = "clubs/$clubId/profile.${imageType.name.lowercase()}"
        return storageService.uploadImage(path, imageData, imageType)
    }
    
    override fun observeClub(clubId: String): Flow<Club?> {
        return clubsCollection
            .document(clubId)
            .snapshots
            .map { snapshot ->
                try {
                    val data = snapshot.data(FirebaseClub.serializer())
                    val club = data.toDomain(clubId)
                    // Only log on actual data changes, not every emission
                    if (snapshot.metadata.isFromCache) {
                        DataSourceLogger.logCacheHit("Club", "$clubId (from Firestore cache)")
                    } else {
                        DataSourceLogger.logFirestoreFetch("Club", clubId)
                    }
                    club
                } catch (e: Exception) {
                    DataSourceLogger.logNoData("Club", "observe failed: ${e.message}")
                    null
                }
            }
    }
    
    override suspend fun joinClubWithCode(inviteCode: String, personId: String): JoinClubResponse {
        @Serializable
        data class JoinClubRequest(
            val inviteCode: String,
            val personId: String
        )

        @Serializable
        data class JoinClubCloudResponse(
            val success: Boolean,
            val clubId: String,
            val clubName: String,
            val message: String
        )

        val request = JoinClubRequest(
            inviteCode = inviteCode,
            personId = personId
        )

        try {
            val result = functions.httpsCallable("joinClubWithCode").invoke(request)
            val responseData = result.data<JoinClubCloudResponse>()

            return JoinClubResponse(
                success = responseData.success,
                clubId = responseData.clubId,
                clubName = responseData.clubName,
                message = responseData.message
            )
        } catch (e: FirebaseNetworkException) {
            throw ClubError.NetworkError
        } catch (e: FirebaseFunctionsException) {
            throw when (e.code) {
                FunctionsExceptionCode.ALREADY_EXISTS -> ClubError.AlreadyMember
                FunctionsExceptionCode.FAILED_PRECONDITION -> ClubError.InviteCodeExpired
                FunctionsExceptionCode.NOT_FOUND -> ClubError.InvalidInviteCode
                FunctionsExceptionCode.INVALID_ARGUMENT -> ClubError.InvalidInviteCode
                FunctionsExceptionCode.UNAUTHENTICATED -> ClubError.Unauthenticated
                FunctionsExceptionCode.PERMISSION_DENIED -> ClubError.PermissionDenied
                FunctionsExceptionCode.UNAVAILABLE -> ClubError.NetworkError
                FunctionsExceptionCode.INTERNAL -> ClubError.NetworkError // Network errors often appear as INTERNAL
                else -> ClubError.UnknownError
            }
        } catch (e: Exception) {
            println("DEBUG JoinClub Exception: ${e::class.simpleName} - ${e.message}")
            throw ClubError.UnknownError
        }
    }

    override suspend fun generateInviteCode(clubId: String, personId: String): GenerateInviteCodeResponse {
        @Serializable
        data class GenerateCodeRequest(
            val clubId: String,
            val personId: String
        )

        @Serializable
        data class GenerateCodeCloudResponse(
            val inviteCode: String,
            val expiresAt: Long
        )

        val request = GenerateCodeRequest(
            clubId = clubId,
            personId = personId
        )
        val result = functions.httpsCallable("generateClubInviteCode").invoke(request)
        val responseData = result.data<GenerateCodeCloudResponse>()

        return GenerateInviteCodeResponse(
            inviteCode = responseData.inviteCode,
            expiresAt = responseData.expiresAt
        )
    }

    override suspend fun disableInviteCode(clubId: String, personId: String) {
        @Serializable
        data class DisableCodeRequest(
            val clubId: String,
            val personId: String
        )

        val request = DisableCodeRequest(
            clubId = clubId,
            personId = personId
        )
        functions.httpsCallable("disableClubInviteCode").invoke(request)
    }

    override suspend fun deleteClub(clubId: String, personId: String): Result<Unit> {
        @Serializable
        data class DeleteClubRequest(
            val clubId: String,
            val personId: String
        )

        @Serializable
        data class DeleteClubResponse(
            val success: Boolean,
            val message: String
        )

        return try {
            val request = DeleteClubRequest(
                clubId = clubId,
                personId = personId
            )

            val result = functions.httpsCallable("deleteClub").invoke(request)
            val response = result.data<DeleteClubResponse>()

            if (response.success) {
                DataSourceLogger.logFirestoreFetch("Club", "deleted: $clubId")
                Result.success(Unit)
            } else {
                DataSourceLogger.logNoData("Club", "delete failed: ${response.message}")
                Result.failure(Exception(response.message))
            }
        } catch (e: FirebaseFunctionsException) {
            DataSourceLogger.logNoData("Club", "delete failed: ${e.message}")
            when (e.code) {
                FunctionsExceptionCode.PERMISSION_DENIED -> Result.failure(ClubError.PermissionDenied)
                FunctionsExceptionCode.NOT_FOUND -> Result.failure(ClubError.ClubNotFound)
                FunctionsExceptionCode.UNAUTHENTICATED -> Result.failure(ClubError.Unauthenticated)
                else -> Result.failure(Exception(e.message ?: "Failed to delete club"))
            }
        } catch (e: FirebaseNetworkException) {
            DataSourceLogger.logNoData("Club", "delete failed (network): ${e.message}")
            Result.failure(ClubError.NetworkError)
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Club", "delete failed: ${e.message}")
            Result.failure(e)
        }
    }
}