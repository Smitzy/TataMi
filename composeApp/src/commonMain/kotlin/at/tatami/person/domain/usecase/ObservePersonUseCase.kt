package at.tatami.person.domain.usecase

import at.tatami.domain.model.Person
import at.tatami.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class ObservePersonUseCase(
    private val personRepository: PersonRepository
) {
    operator fun invoke(personId: String): Flow<Person?> {
        return personRepository.observePerson(personId)
    }
}