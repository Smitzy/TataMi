package at.tatami.person.presentation.edit

import at.tatami.common.domain.validation.states.PersonValidationState
import at.tatami.domain.model.Person
import at.tatami.domain.model.Sex

data class EditPersonState(
    val person: Person? = null,
    val firstName: String = "",
    val lastName: String = "",
    val yearOfBirth: Int? = null,
    val yearOfBirthText: String = "",
    val sex: Sex? = null,
    val selectedProfileImage: ByteArray? = null,
    val hasImageChanged: Boolean = false,
    val validation: PersonValidationState = PersonValidationState(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    val canSave: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                yearOfBirth != null &&
                sex != null &&
                !isSaving &&
                validation.isValid

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EditPersonState

        if (person != other.person) return false
        if (firstName != other.firstName) return false
        if (lastName != other.lastName) return false
        if (yearOfBirth != other.yearOfBirth) return false
        if (yearOfBirthText != other.yearOfBirthText) return false
        if (sex != other.sex) return false
        if (selectedProfileImage != null) {
            if (other.selectedProfileImage == null) return false
            if (!selectedProfileImage.contentEquals(other.selectedProfileImage)) return false
        } else if (other.selectedProfileImage != null) return false
        if (hasImageChanged != other.hasImageChanged) return false
        if (validation != other.validation) return false
        if (isLoading != other.isLoading) return false
        if (isSaving != other.isSaving) return false
        if (saveSuccess != other.saveSuccess) return false
        if (errorMessage != other.errorMessage) return false

        return true
    }

    override fun hashCode(): Int {
        var result = person?.hashCode() ?: 0
        result = 31 * result + firstName.hashCode()
        result = 31 * result + lastName.hashCode()
        result = 31 * result + (yearOfBirth ?: 0)
        result = 31 * result + yearOfBirthText.hashCode()
        result = 31 * result + (sex?.hashCode() ?: 0)
        result = 31 * result + (selectedProfileImage?.contentHashCode() ?: 0)
        result = 31 * result + hasImageChanged.hashCode()
        result = 31 * result + validation.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + isSaving.hashCode()
        result = 31 * result + saveSuccess.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        return result
    }
}
