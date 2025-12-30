package at.tatami.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing the currently selected person
 */
interface SelectedPersonRepository {
    /**
     * Set the currently selected person ID
     */
    suspend fun setSelectedPersonId(personId: String?)
    
    /**
     * Get the currently selected person ID
     */
    suspend fun getSelectedPersonId(): String?
    
    /**
     * Observe changes to the selected person ID
     */
    fun observeSelectedPersonId(): Flow<String?>
    
    /**
     * Clear the selected person (set to null)
     */
    suspend fun clearSelectedPerson()
}