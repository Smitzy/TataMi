package at.tatami.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import at.tatami.domain.repository.SelectedClubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore implementation of SelectedClubRepository
 */
class SelectedClubRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SelectedClubRepository {
    
    companion object {
        private val SELECTED_CLUB_ID_KEY = stringPreferencesKey("selected_club_id")
    }
    
    override suspend fun setSelectedClubId(clubId: String?) {
        println("[SelectedClub] Setting selected club to: $clubId")
        dataStore.edit { preferences ->
            if (clubId != null) {
                preferences[SELECTED_CLUB_ID_KEY] = clubId
            } else {
                preferences.remove(SELECTED_CLUB_ID_KEY)
            }
        }
    }
    
    override suspend fun getSelectedClubId(): String? {
        return observeSelectedClubId().first()
    }
    
    override fun observeSelectedClubId(): Flow<String?> {
        return dataStore.data
            .catch { exception ->
                // If there's an error reading preferences, emit default
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[SELECTED_CLUB_ID_KEY]
            }
    }
    
    override suspend fun clearSelectedClub() {
        setSelectedClubId(null)
    }
}