package at.tatami.club.domain.usecase

import at.tatami.domain.model.Club
import at.tatami.domain.repository.ClubRepository
import at.tatami.person.domain.usecase.ObserveSelectedPersonUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

data class PersonClub(
    val club: Club,
    val role: ClubRole
)

enum class ClubRole {
    OWNER,
    ADMIN,
    MEMBER
}

class ObservePersonClubsUseCase(
    private val clubRepository: ClubRepository,
    private val observeSelectedPerson: ObserveSelectedPersonUseCase
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<PersonClub>> =
        observeSelectedPerson().flatMapLatest { person ->
            if (person == null) {
                flowOf(emptyList())
            } else {
                // Create flows for each club and combine them
                val clubFlows = person.clubIds.map { clubId ->
                    clubRepository.observeClub(clubId).map { club ->
                        if (club != null) {
                            // Determine the person's role in this club
                            val role = when {
                                club.ownerId == person.id -> ClubRole.OWNER
                                person.id in club.adminIds -> ClubRole.ADMIN
                                else -> ClubRole.MEMBER
                            }
                            PersonClub(club, role)
                        } else {
                            null
                        }
                    }
                }
                
                // Combine all club flows and emit when any club changes
                if (clubFlows.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(clubFlows) { clubs ->
                        // Filter out null clubs and sort by role
                        clubs.filterNotNull().sortedBy { it.role.ordinal }
                    }
                }
            }
        }.catch { exception ->
            // In case of error, emit empty list
            // You could also throw the exception if you want error handling in the UI
            emit(emptyList())
        }
}