package at.tatami.domain.model

/**
 * Represents a group within a club.
 * Groups organize members and trainers for training sessions.
 *
 * Invariant: All trainers must also be members (trainerIds ⊆ memberIds)
 */
data class Group(
    val id: String,
    val clubId: String,
    val name: String,
    val memberIds: List<String>, // Person IDs who are members of this group
    val trainerIds: List<String> // Person IDs who are trainers (must be subset of memberIds)
) {
    /**
     * Checks if a person is a member of this group.
     */
    fun isPersonMember(personId: String): Boolean {
        return memberIds.contains(personId)
    }

    /**
     * Checks if a person is a trainer of this group.
     */
    fun isPersonTrainer(personId: String): Boolean {
        return trainerIds.contains(personId)
    }

    /**
     * Validates that all trainers are also members.
     * This invariant should always be maintained.
     */
    fun isValid(): Boolean {
        return trainerIds.all { memberIds.contains(it) }
    }
}