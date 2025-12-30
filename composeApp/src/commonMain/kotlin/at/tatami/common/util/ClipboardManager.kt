package at.tatami.common.util

expect object ClipboardManager {
    fun copyToClipboard(text: String)
}