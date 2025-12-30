package at.tatami.person.presentation.photoupload

import at.tatami.domain.model.Person

data class PersonPhotoUploadState(
    val person: Person? = null,
    val selectedProfileImage: ByteArray? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val uploadSuccess: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PersonPhotoUploadState

        if (person != other.person) return false
        if (selectedProfileImage != null) {
            if (other.selectedProfileImage == null) return false
            if (!selectedProfileImage.contentEquals(other.selectedProfileImage)) return false
        } else if (other.selectedProfileImage != null) return false
        if (isLoading != other.isLoading) return false
        if (errorMessage != other.errorMessage) return false
        if (uploadSuccess != other.uploadSuccess) return false

        return true
    }

    override fun hashCode(): Int {
        var result = person?.hashCode() ?: 0
        result = 31 * result + (selectedProfileImage?.contentHashCode() ?: 0)
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + uploadSuccess.hashCode()
        return result
    }
}