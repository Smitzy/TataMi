package at.tatami.common.data.service

import at.tatami.common.domain.service.FcmService

actual class FcmServiceImpl : FcmService {

    actual override suspend fun initialize() {
        println("FCM service initialized (iOS stub)")
    }

    actual override suspend fun getToken(): String? {
        println("FCM token retrieval not yet fully implemented for iOS")
        return null
    }
}