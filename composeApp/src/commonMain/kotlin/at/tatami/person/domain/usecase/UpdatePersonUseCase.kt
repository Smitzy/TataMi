package at.tatami.person.domain.usecase

import at.tatami.domain.model.Person
import at.tatami.domain.repository.PersonRepository

class UpdatePersonUseCase(
    private val personRepository: PersonRepository
) {
    suspend operator fun invoke(
        person: Person
    ): Result<Person> {
        return try {
            val result = personRepository.updatePerson(person)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}