package at.tatami.club.presentation.create

import at.tatami.common.domain.validation.states.ClubValidationState

data class CreateClubState(
    val clubName: String = "",
    val validation: ClubValidationState = ClubValidationState(),
    val isLoading: Boolean = false,
    val createdClubId: String? = null
) {
    val canSave: Boolean
        get() = clubName.isNotBlank() &&
                !isLoading &&
                validation.isValid
}