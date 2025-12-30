package at.tatami.club.domain.usecase

import at.tatami.common.domain.ClubError
import at.tatami.domain.repository.ClubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class JoinClubUseCase(
    private val clubRepository: ClubRepository
) {
    suspend operator fun invoke(inviteCode: String, personId: String): Flow<JoinClubResult> = flow {
        emit(JoinClubResult.Loading)

        try {
            val joinResult = clubRepository.joinClubWithCode(inviteCode, personId)
            emit(JoinClubResult.JoinedSuccessfully(
                clubId = joinResult.clubId,
                clubName = joinResult.clubName
            ))
        } catch (e: ClubError) {
            emit(JoinClubResult.Error(e))
        } catch (e: Exception) {
            emit(JoinClubResult.Error(ClubError.UnknownError))
        }
    }
}

sealed class JoinClubResult {
    data object Loading : JoinClubResult()
    data class JoinedSuccessfully(
        val clubId: String,
        val clubName: String
    ) : JoinClubResult()
    data class Error(val error: ClubError) : JoinClubResult()
}