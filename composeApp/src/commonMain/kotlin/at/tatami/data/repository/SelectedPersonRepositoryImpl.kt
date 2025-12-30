package at.tatami.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import at.tatami.domain.repository.SelectedPersonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore implementation of SelectedPersonRepository
 */
class SelectedPersonRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SelectedPersonRepository {
    
    companion object {
        private val SELECTED_PERSON_ID_KEY = stringPreferencesKey("selected_person_id")
    }
    
    override suspend fun setSelectedPersonId(personId: String?) {
        println("[SelectedPerson] Setting selected person to: $personId")
        dataStore.edit { preferences ->
            if (personId != null) {
                preferences[SELECTED_PERSON_ID_KEY] = personId
            } else {
                preferences.remove(SELECTED_PERSON_ID_KEY)
            }
        }
    }
    
    override suspend fun getSelectedPersonId(): String? {
        return observeSelectedPersonId().first()
    }
    
    override fun observeSelectedPersonId(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                // If there's an error reading preferences, emit default
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[SELECTED_PERSON_ID_KEY]
            }
    }
    
    override suspend fun clearSelectedPerson() {
        setSelectedPersonId(null)
    }
}