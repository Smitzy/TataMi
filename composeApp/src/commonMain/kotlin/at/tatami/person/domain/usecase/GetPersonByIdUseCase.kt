package at.tatami.person.domain.usecase

import at.tatami.domain.model.Person
import at.tatami.domain.repository.PersonRepository

class GetPersonByIdUseCase(
    private val personRepository: PersonRepository
) {
    suspend operator fun invoke(personId: String): Person? {
        return personRepository.getPersonById(personId)
    }
}