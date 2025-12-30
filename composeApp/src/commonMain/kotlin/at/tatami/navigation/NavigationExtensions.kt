package at.tatami.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Navigate to a specific route with optional navigation options.
 * This is a convenience function that provides type-safe navigation.
 * 
 * @param route The destination route to navigate to
 * @param builder Optional navigation options like animations, pop behavior, etc.
 */
fun NavController.navigateTo(
    route: TatamiRoute,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(route, builder)
}

/**
 * Navigate to a route and clear the entire back stack.
 * Useful for navigating to a new root destination, like after login/logout.
 * 
 * @param route The destination route that will become the new root
 */
fun NavController.navigateToAndClearStack(route: TatamiRoute) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Check if the current destination matches a specific route
 */
inline fun <reified T : TatamiRoute> NavController.isCurrentRoute(): Boolean {
    return currentDestination?.route == T::class.qualifiedName
}

/**
 * Check if the current route is an authentication screen
 */
fun NavController.isOnAuthScreen(): Boolean {
    val route = currentDestination?.route ?: return false

    val currentRoute = when (route) {
        TatamiRoute.Auth.Login::class.qualifiedName -> TatamiRoute.Auth.Login
        TatamiRoute.Auth.Register::class.qualifiedName -> TatamiRoute.Auth.Register
        TatamiRoute.Auth.ForgotPassword::class.qualifiedName -> TatamiRoute.Auth.ForgotPassword
        TatamiRoute.Auth.EmailVerification::class.qualifiedName -> TatamiRoute.Auth.EmailVerification
        else -> null
    }
    return currentRoute != null
}
