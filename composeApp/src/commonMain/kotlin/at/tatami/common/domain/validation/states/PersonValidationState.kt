package at.tatami.common.domain.validation.states

data class PersonValidationState(
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val yearOfBirthError: String? = null,
    val sexError: String? = null,
    val generalError: String? = null
) {
    val hasErrors: Boolean
        get() = firstNameError != null ||
                lastNameError != null ||
                yearOfBirthError != null ||
                sexError != null ||
                generalError != null

    val isValid: Boolean
        get() = !hasErrors
}