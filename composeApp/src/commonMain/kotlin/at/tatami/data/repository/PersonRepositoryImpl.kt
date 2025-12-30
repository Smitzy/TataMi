package at.tatami.data.repository

import at.tatami.core.ImageType
import at.tatami.core.StorageService
import at.tatami.data.mapper.toDomain
import at.tatami.data.mapper.toFirebase
import at.tatami.data.model.FirebasePerson
import at.tatami.domain.model.Person
import at.tatami.domain.repository.PersonRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import at.tatami.common.util.DataSourceLogger

class PersonRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storageService: StorageService
) : PersonRepository {
    
    private val personsCollection = firestore.collection("persons")
    
    override suspend fun createPerson(person: Person): Person {
        val personDto = person.toFirebase()
        val docRef = personsCollection.add(personDto)
        return person.copy(id = docRef.id)
    }
    
    override suspend fun updatePerson(person: Person): Person {
        personsCollection.document(person.id).set(person.toFirebase())
        return person
    }

    override suspend fun getPersonById(personId: String): Person? {
        return try {
            val snapshot = personsCollection.document(personId).get()
            val person = snapshot.data(FirebasePerson.serializer()).toDomain(personId)
            DataSourceLogger.logFirestoreFetch("Person", personId)
            person
        } catch (e: Exception) {
            DataSourceLogger.logNoData("Person", "fetch failed: ${e.message}")
            null
        }
    }
    
    override suspend fun getPersonsByUserId(userId: String): List<Person> {
        return try {
            val snapshot = personsCollection
                .where { "userId" equalTo userId }
                .get()
            snapshot.documents.map { doc ->
                doc.data(FirebasePerson.serializer()).toDomain(doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getPersonsByClubId(clubId: String): List<Person> {
        return try {
            val snapshot = personsCollection
                .where { "clubIds" contains clubId }
                .get()
            snapshot.documents.map { doc ->
                doc.data(FirebasePerson.serializer()).toDomain(doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun uploadPersonProfilePicture(personId: String, imageData: ByteArray, imageType: ImageType): String {
        val path = "persons/$personId/profile.${imageType.name.lowercase()}"
        return storageService.uploadImage(path, imageData, imageType)
    }
    
    override fun observePerson(personId: String): Flow<Person?> {
        return personsCollection.document(personId)
            .snapshots
            .map { snapshot ->
                val person = snapshot.data(FirebasePerson.serializer()).toDomain(personId)
                // Only log on actual data changes, not every emission
                if (snapshot.metadata.isFromCache) {
                    DataSourceLogger.logCacheHit("Person", "$personId (from Firestore cache)")
                } else {
                    DataSourceLogger.logFirestoreFetch("Person", personId)
                }
                person
            }
    }
    
    override fun observePersonsByUser(userId: String): Flow<List<Person>> {
        return personsCollection
            .where { "userId" equalTo userId }
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    doc.data(FirebasePerson.serializer()).toDomain(doc.id)
                }
            }
    }
}