package at.tatami.common.data.service

import at.tatami.common.domain.service.FcmService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.messaging.messaging

expect class FcmServiceImpl : FcmService {
    override suspend fun initialize()
    override suspend fun getToken(): String?
}