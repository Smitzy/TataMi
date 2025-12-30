package at.tatami.common.util

import platform.UIKit.UIPasteboard

actual object ClipboardManager {
    actual fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}