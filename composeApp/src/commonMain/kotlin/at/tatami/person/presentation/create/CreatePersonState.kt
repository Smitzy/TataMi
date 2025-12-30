package at.tatami.person.presentation.create

import at.tatami.domain.model.Sex
import at.tatami.common.domain.validation.states.PersonValidationState

data class CreatePersonState(
    val firstName: String = "",
    val lastName: String = "",
    val yearOfBirth: Int? = null,
    val yearOfBirthText: String = "",
    val sex: Sex? = null,
    val validation: PersonValidationState = PersonValidationState(),
    val isLoading: Boolean = false,
    val createdPersonId: String? = null
) {
    val canSave: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                yearOfBirth != null &&
                sex != null &&
                !isLoading &&
                validation.isValid
}