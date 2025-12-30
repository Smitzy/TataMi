package at.tatami

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import at.tatami.domain.model.auth.AuthState
import at.tatami.auth.domain.usecase.ObserveAuthStateUseCase
import at.tatami.common.domain.manager.ThemeManager
import at.tatami.domain.model.settings.ThemeMode
import at.tatami.settings.domain.usecase.LoadThemeSettingsUseCase
import at.tatami.common.ui.theme.TatamiTheme
import at.tatami.navigation.TatamiNavigationHost
import at.tatami.common.ui.theme.ThemeHandler
import at.tatami.common.domain.usecase.ValidateSelectionsUseCase
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import io.github.vinceglb.filekit.coil.addPlatformFileSupport
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun App() {
    // Initialize Coil image loader
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                addPlatformFileSupport()
            }
            .crossfade(true)
            .logger(DebugLogger())
            .build()
    }
    // Inject ThemeManager and load theme settings
    val themeManager = koinInject<ThemeManager>()
    val loadThemeSettings = koinInject<LoadThemeSettingsUseCase>()
    val themeSettings by themeManager.themeSettings.collectAsState()
    val isSystemInDarkTheme = isSystemInDarkTheme()

    // Load theme settings on startup
    LaunchedEffect(Unit) {
        loadThemeSettings()
    }
    
    // Determine if dark theme should be used
    val isDarkTheme = when (themeSettings.mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme
    }
    
    // Handle platform-specific theme settings (window colors, etc.)
    ThemeHandler(isDarkTheme = isDarkTheme)

    TatamiTheme(darkTheme = isDarkTheme)
    {
        // Observe authentication state
        val observeAuthState = koinInject<ObserveAuthStateUseCase>()
        val authState by observeAuthState().collectAsState()
        val validateSelections = koinInject<ValidateSelectionsUseCase>()
        val registerFcmToken = koinInject<at.tatami.auth.domain.usecase.RegisterFcmTokenUseCase>()

        // Validate selected entities when auth state changes to Authenticated
        LaunchedEffect(authState) {
            if (authState is AuthState.Authenticated) {
                validateSelections()
                registerFcmToken()
            }
        }

        when (authState) {
            is AuthState.Loading -> {
                // Show loading indicator while checking auth state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            is AuthState.Authenticated,
            is AuthState.EmailNotVerified,
            is AuthState.NotAuthenticated -> {
                // Navigate based on auth state
                TatamiNavigationHost(
                    authState = authState
                )
            }
        }
    }
}