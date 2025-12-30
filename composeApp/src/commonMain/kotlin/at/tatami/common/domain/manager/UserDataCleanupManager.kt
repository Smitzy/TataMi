package at.tatami.common.domain.manager

/**
 * Manager responsible for cleaning up user-specific data when a user signs out
 * or switches accounts. This ensures no data leakage between different users.
 */
interface UserDataCleanupManager {
    /**
     * Clears all user-specific data from local storage.
     * This should be called before signing out a user.
     */
    suspend fun clearAllUserData()
}