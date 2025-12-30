package at.tatami.auth.domain.usecase

import at.tatami.common.domain.service.FcmService
import at.tatami.domain.repository.UserRepository

class RegisterFcmTokenUseCase(
    private val fcmService: FcmService,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        try {
            // Initialize FCM (handles permissions)
            fcmService.initialize()

            // Get FCM token
            val token = fcmService.getToken()

            // Save to Firestore
            if (token != null) {
                userRepository.updateFcmToken(token)
                println("FCM token registered successfully")
            } else {
                println("FCM token not available")
            }
        } catch (e: Exception) {
            println("Failed to register FCM token: ${e.message}")
        }
    }
}