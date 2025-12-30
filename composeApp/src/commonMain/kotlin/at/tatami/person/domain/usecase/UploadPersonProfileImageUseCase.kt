package at.tatami.person.domain.usecase

import at.tatami.core.ImageType
import at.tatami.domain.model.Person
import at.tatami.domain.repository.PersonRepository

class UploadPersonProfileImageUseCase(
    private val personRepository: PersonRepository
) {
    suspend operator fun invoke(
        person: Person,
        imageData: ByteArray,
        imageType: ImageType
    ): Result<Person> {
        return try {
            // Upload the image and get the URL
            val imageUrl = personRepository.uploadPersonProfilePicture(person.id, imageData, imageType)

            // Update the person with the image URL
            val updatedPerson = person.copy(
                personImgUrl = imageUrl
            )

            val result = personRepository.updatePerson(updatedPerson)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}