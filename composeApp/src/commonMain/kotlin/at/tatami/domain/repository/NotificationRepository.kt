package at.tatami.domain.repository

import kotlinx.serialization.Serializable

/**
 * Result of sending notifications to persons
 */
@Serializable
data class NotificationSendResult(
    val success: Boolean,
    val tokensFound: Int,
    val notificationsSent: Int,
    val notificationsFailed: Int,
    val message: String
)

/**
 * Repository for sending push notifications.
 *
 * Abstracts the Cloud Functions layer for sending bulk notifications
 * to multiple persons.
 */
interface NotificationRepository {
    /**
     * Sends a push notification to multiple persons.
     *
     * Note: The caller is responsible for filtering out the creator
     * or any other persons that should not receive the notification.
     *
     * @param title Notification title
     * @param body Notification body
     * @param personIds List of person IDs to send notifications to
     * @return NotificationSendResult with detailed metrics
     */
    suspend fun sendNotificationToPersons(
        title: String,
        body: String,
        personIds: List<String>
    ): NotificationSendResult
}