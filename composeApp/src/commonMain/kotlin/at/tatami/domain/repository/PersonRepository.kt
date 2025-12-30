package at.tatami.domain.repository

import at.tatami.core.ImageType
import at.tatami.domain.model.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    suspend fun createPerson(person: Person): Person
    suspend fun updatePerson(person: Person): Person
    suspend fun getPersonById(personId: String): Person?
    suspend fun getPersonsByUserId(userId: String): List<Person>
    suspend fun getPersonsByClubId(clubId: String): List<Person>
    suspend fun uploadPersonProfilePicture(personId: String, imageData: ByteArray, imageType: ImageType): String
    fun observePerson(personId: String): Flow<Person?>
    fun observePersonsByUser(userId: String): Flow<List<Person>>
}