package at.tatami.group.domain.usecase

import at.tatami.club.domain.usecase.GetSelectedClubUseCase
import at.tatami.domain.model.Group
import at.tatami.domain.repository.GroupRepository
import at.tatami.person.domain.usecase.GetSelectedPersonUseCase

/**
 * Use case for creating a new group within the currently selected club.
 *
 * Validates that:
 * - A club and person are selected
 * - All trainers are also members (trainerIds ⊆ memberIds)
 * - Group name is not empty
 *
 * Note: The creator is NOT automatically added to the group.
 */
class CreateGroupUseCase(
    private val groupRepository: GroupRepository,
    private val getSelectedPersonUseCase: GetSelectedPersonUseCase,
    private val getSelectedClubUseCase: GetSelectedClubUseCase
) {
    suspend operator fun invoke(
        name: String,
        memberIds: List<String>,
        trainerIds: List<String>
    ): Result<Group> {
        return try {
            val selectedClub = getSelectedClubUseCase()
                ?: return Result.failure(Exception("No club selected"))

            val selectedPerson = getSelectedPersonUseCase()
                ?: return Result.failure(Exception("No person selected"))

            // Validate group name
            if (name.trim().isEmpty()) {
                return Result.failure(Exception("Group name cannot be empty"))
            }

            // De-duplicate IDs
            val uniqueMemberIds = memberIds.distinct()
            val uniqueTrainerIds = trainerIds.distinct()

            // Validate that all trainers are also members (critical constraint)
            if (!uniqueTrainerIds.all { uniqueMemberIds.contains(it) }) {
                return Result.failure(Exception("All trainers must also be members of the group"))
            }

            // Create group (creator is NOT auto-added)
            val group = Group(
                id = "", // Firestore will assign the ID
                clubId = selectedClub.id,
                name = name.trim(),
                memberIds = uniqueMemberIds,
                trainerIds = uniqueTrainerIds
            )

            // Double-check invariant
            if (!group.isValid()) {
                return Result.failure(Exception("Invalid group: trainers must be members"))
            }

            val createdGroup = groupRepository.createGroup(group)
            Result.success(createdGroup)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}