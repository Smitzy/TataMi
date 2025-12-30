package at.tatami.common.domain.service

interface FcmService {
    suspend fun initialize()
    suspend fun getToken(): String?
}