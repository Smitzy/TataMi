package at.tatami.common.util

import android.content.Context
import android.content.ClipData
import android.content.ClipboardManager as AndroidClipboardManager

actual object ClipboardManager {
    private var context: Context? = null
    
    fun initialize(context: Context) {
        this.context = context.applicationContext
    }
    
    actual fun copyToClipboard(text: String) {
        val appContext = context ?: throw IllegalStateException("ClipboardManager not initialized. Call initialize(context) first.")
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager
        val clip = ClipData.newPlainText("text", text)
        clipboard.setPrimaryClip(clip)
    }
}