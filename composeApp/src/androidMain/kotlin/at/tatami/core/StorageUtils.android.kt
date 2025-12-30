package at.tatami.core

import dev.gitlive.firebase.storage.Data

actual fun createDataFromBytes(bytes: ByteArray): Data {
    return Data(bytes)
}