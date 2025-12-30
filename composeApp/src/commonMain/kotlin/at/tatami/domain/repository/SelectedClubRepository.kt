package at.tatami.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing the currently selected club
 */
interface SelectedClubRepository {
    /**
     * Set the currently selected club ID
     */
    suspend fun setSelectedClubId(clubId: String?)
    
    /**
     * Get the currently selected club ID
     */
    suspend fun getSelectedClubId(): String?
    
    /**
     * Observe changes to the selected club ID
     */
    fun observeSelectedClubId(): Flow<String?>
    
    /**
     * Clear the selected club (set to null)
     */
    suspend fun clearSelectedClub()
}