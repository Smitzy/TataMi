package at.tatami

import androidx.compose.ui.window.ComposeUIViewController
import at.tatami.di.initKoin
import at.tatami.di.iosModule

/**
 * iOS entry point for the Compose Multiplatform app
 */
fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin {
            modules(iosModule)
        }
    }
) {
    App()
}