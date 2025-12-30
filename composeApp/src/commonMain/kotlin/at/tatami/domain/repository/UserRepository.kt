package at.tatami.domain.repository

import at.tatami.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getCurrentUser(): User?
    suspend fun getUserById(userId: String): User?
    suspend fun createUser(email: String, password: String): String // Returns userId
    suspend fun signIn(email: String, password: String): String // Returns userId
    suspend fun signOut()
    suspend fun updateUser(user: User): User
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun sendEmailVerification()
    suspend fun checkEmailVerification(): Boolean
    suspend fun updateEmailVerifiedStatus(verified: Boolean)
    suspend fun updateLastLoginAt()
    suspend fun updateFcmToken(token: String)
    fun observeCurrentUser(): Flow<User?>
}