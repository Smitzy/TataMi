package at.tatami.person.domain.usecase

import at.tatami.domain.model.Person
import at.tatami.domain.repository.PersonRepository
import kotlinx.coroutines.flow.Flow

class ObservePersonsByUserUseCase(
    private val personRepository: PersonRepository
) {
    operator fun invoke(userId: String): Flow<List<Person>> {
        return personRepository.observePersonsByUser(userId)
    }
}