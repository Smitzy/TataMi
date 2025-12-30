package at.tatami.data.repository

import at.tatami.domain.repository.NotificationRepository
import at.tatami.domain.repository.NotificationSendResult
import dev.gitlive.firebase.functions.FirebaseFunctions
import dev.gitlive.firebase.FirebaseNetworkException
import dev.gitlive.firebase.functions.FirebaseFunctionsException
import kotlinx.serialization.Serializable

class NotificationRepositoryImpl(
    private val functions: FirebaseFunctions
) : NotificationRepository {

    override suspend fun sendNotificationToPersons(
        title: String,
        body: String,
        personIds: List<String>
    ): NotificationSendResult {
        @Serializable
        data class SendNotificationRequest(
            val title: String,
            val body: String,
            val personIds: List<String>
        )

        @Serializable
        data class SendNotificationResponse(
            val success: Boolean,
            val tokensFound: Int,
            val notificationsSent: Int,
            val notificationsFailed: Int,
            val message: String
        )

        val request = SendNotificationRequest(
            title = title,
            body = body,
            personIds = personIds
        )

        try {
            val result = functions.httpsCallable("sendNotificationToPersons").invoke(request)
            val responseData = result.data<SendNotificationResponse>()

            return NotificationSendResult(
                success = responseData.success,
                tokensFound = responseData.tokensFound,
                notificationsSent = responseData.notificationsSent,
                notificationsFailed = responseData.notificationsFailed,
                message = responseData.message
            )
        } catch (e: FirebaseNetworkException) {
            // Log network error but return graceful result
            println("Notification network error: ${e.message}")
            return NotificationSendResult(
                success = false,
                tokensFound = 0,
                notificationsSent = 0,
                notificationsFailed = 0,
                message = "Network error: ${e.message}"
            )
        } catch (e: FirebaseFunctionsException) {
            // Log function error but return graceful result
            println("Notification function error: ${e.code} - ${e.message}")
            return NotificationSendResult(
                success = false,
                tokensFound = 0,
                notificationsSent = 0,
                notificationsFailed = 0,
                message = "Function error: ${e.message}"
            )
        } catch (e: Exception) {
            // Log unexpected error but return graceful result
            println("Notification unexpected error: ${e::class.simpleName} - ${e.message}")
            return NotificationSendResult(
                success = false,
                tokensFound = 0,
                notificationsSent = 0,
                notificationsFailed = 0,
                message = "Unexpected error: ${e.message}"
            )
        }
    }
}