package at.tatami.common.domain.validation.states

data class ClubValidationState(
    val nameError: String? = null,
    val generalError: String? = null
) {
    val hasErrors: Boolean
        get() = nameError != null || generalError != null

    val isValid: Boolean
        get() = !hasErrors
}