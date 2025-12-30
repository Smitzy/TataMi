package at.tatami.common.util

expect object Platform {
    val isAndroid: Boolean
    val isIOS: Boolean
}