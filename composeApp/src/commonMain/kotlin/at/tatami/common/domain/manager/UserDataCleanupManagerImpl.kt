package at.tatami.common.domain.manager

import at.tatami.domain.repository.SelectedPersonRepository
import at.tatami.domain.repository.SelectedClubRepository

/**
 * Implementation of UserDataCleanupManager that handles clearing all user-specific data
 */
class UserDataCleanupManagerImpl(
    private val selectedPersonRepository: SelectedPersonRepository,
    private val selectedClubRepository: SelectedClubRepository
) : UserDataCleanupManager {
    
    override suspend fun clearAllUserData() {
        // Clear the selected person
        selectedPersonRepository.clearSelectedPerson()
        
        // Clear the selected club
        selectedClubRepository.clearSelectedClub()
        
        // Future: Add other user-specific data cleanup here
        // For example:
        // - Clear cached user preferences
        // - Clear any temporary user files
        // - Clear user-specific session data
    }
}