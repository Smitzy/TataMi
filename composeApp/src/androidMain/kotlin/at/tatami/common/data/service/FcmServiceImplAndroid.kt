package at.tatami.common.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import at.tatami.common.domain.service.FcmService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging

actual class FcmServiceImpl(
    private val context: Context
) : FcmService {

    actual override suspend fun initialize() {
        // Create notification channel for Android 8.0+
        createNotificationChannel()
        println("FCM service initialized with notification channel")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "club_notifications"
            val channelName = "Club Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = "Notifications for club events"

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    actual override suspend fun getToken(): String? {
        return try {
            Firebase.messaging.getToken()
        } catch (e: Exception) {
            println("Failed to get FCM token: ${e.message}")
            null
        }
    }
}