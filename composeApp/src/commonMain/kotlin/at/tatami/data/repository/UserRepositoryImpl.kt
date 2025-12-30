package at.tatami.data.repository

import at.tatami.data.mapper.toDomain
import at.tatami.data.mapper.toFirebase
import at.tatami.data.model.FirebaseUser
import at.tatami.domain.model.User
import at.tatami.domain.repository.UserRepository
import at.tatami.common.util.LocaleProvider
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.functions.FirebaseFunctions
import dev.gitlive.firebase.functions.HttpsCallableResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

class UserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions,
    private val localeProvider: LocaleProvider
) : UserRepository {
    
    private val usersCollection = firestore.collection("users")
    
    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val userId = firebaseUser.uid
        return try {
            val document = usersCollection.document(userId).get()
            val data = document.data(FirebaseUser.serializer())
            data.toDomain(userId)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getUserById(userId: String): User? {
        return try {
            val document = usersCollection.document(userId).get()
            val data = document.data(FirebaseUser.serializer())
            data.toDomain(userId)
        } catch (e: Exception) {
            null
        }
    }
    
    @OptIn(ExperimentalTime::class)
    override suspend fun createUser(email: String, password: String): String {
        val authResult = auth.createUserWithEmailAndPassword(email, password)
        val firebaseUser = authResult.user ?: throw Exception("Failed to create user")
        val userId = firebaseUser.uid
        
        // Set language code for verification email
        val languageCode = localeProvider.getCurrentLanguageCode()
        auth.languageCode = languageCode
        
        // Send email verification
        firebaseUser.sendEmailVerification()
        
        // Add delay to give the automatic cloud function time to create user document
        delay(2000)
        
        return userId
    }
    
    override suspend fun signIn(email: String, password: String): String {
        val authResult = auth.signInWithEmailAndPassword(email, password)
        val firebaseUser = authResult.user ?: throw Exception("Failed to sign in")
        return firebaseUser.uid
    }
    
    override suspend fun signOut() {
        auth.signOut()
    }
    
    override suspend fun updateUser(user: User): User {
        usersCollection.document(user.id).set(user.toFirebase())
        return user
    }
    
    override suspend fun sendPasswordResetEmail(email: String) {
        // Set the language code for localized emails
        val languageCode = localeProvider.getCurrentLanguageCode()
        auth.languageCode = languageCode
        
        auth.sendPasswordResetEmail(email)
    }
    
    override suspend fun sendEmailVerification() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            // Set the language code for localized emails
            val languageCode = localeProvider.getCurrentLanguageCode()
            auth.languageCode = languageCode
            
            firebaseUser.sendEmailVerification()
        } else {
            throw Exception("No user signed in")
        }
    }
    
    override suspend fun checkEmailVerification(): Boolean {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            // Reload user to get latest email verification status
            firebaseUser.reload()
            val isVerified = firebaseUser.isEmailVerified

            println("DEBUG: Email verification check - isVerified=$isVerified, userEmail=${firebaseUser.email}")

            // Update user document via cloud function if verification status changed
            if (isVerified) {
                val currentUser = getCurrentUser()
                if (currentUser != null && !currentUser.emailVerified) {
                    try {
                        // Call cloud function to sync email verification status from Firebase Auth
                        val updateFunction = functions.httpsCallable("updateEmailVerifiedStatus")
                        updateFunction.invoke()
                        println("DEBUG: Successfully called updateEmailVerifiedStatus cloud function")
                    } catch (e: Exception) {
                        // Log error but don't fail the verification check
                        println("Failed to update email verification status: ${e.message}")
                    }
                }
            }

            return isVerified
        }
        return false
    }
    
    @OptIn(ExperimentalTime::class)
    override suspend fun updateEmailVerifiedStatus(verified: Boolean) {
        val currentUser = getCurrentUser() ?: return
        val updatedUser = currentUser.copy(emailVerified = verified)
        updateUser(updatedUser)
    }

    override suspend fun updateLastLoginAt() {
        try {
            // Small delay to ensure auth token is propagated to Functions client
            delay(100)
            // Call cloud function to update lastLoginAt
            val updateFunction = functions.httpsCallable("updateLastLoginAt")
            updateFunction.invoke()
        } catch (e: Exception) {
            // Log error but don't fail the sign in
            println("Failed to update lastLoginAt: ${e.message}")
        }
    }

    override suspend fun updateFcmToken(token: String) {
        val firebaseUser = auth.currentUser ?: return
        val userId = firebaseUser.uid

        try {
            usersCollection.document(userId).updateFields {
                "fcmToken" to token
            }
        } catch (e: Exception) {
            // Log but don't throw - token update is best effort
            println("Failed to update FCM token: ${e.message}")
        }
    }

    override fun observeCurrentUser(): Flow<User?> = flow {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            emit(null)
        } else {
            val userId = firebaseUser.uid
            emitAll(
                usersCollection.document(userId)
                    .snapshots
                    .map { snapshot ->
                        try {
                            val data = snapshot.data(FirebaseUser.serializer())
                            data.toDomain(userId)
                        } catch (e: Exception) {
                            null
                        }
                    }
            )
        }
    }
}