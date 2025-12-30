package at.tatami.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import at.tatami.domain.model.auth.AuthState
import at.tatami.auth.presentation.emailverification.EmailVerificationScreen
import at.tatami.auth.presentation.forgotpassword.ForgotPasswordScreen
import at.tatami.auth.presentation.login.LoginScreen
import at.tatami.auth.presentation.register.RegisterScreen
import at.tatami.person.presentation.list.PersonListScreen
import at.tatami.settings.presentation.SettingsScreen
import at.tatami.club.presentation.timezone.TimezonePickerScreen
import at.tatami.club.presentation.create.CreateClubScreen
import at.tatami.club.presentation.photoupload.ClubPhotoUploadScreen
import at.tatami.club.presentation.join.JoinClubScreen
import at.tatami.main.presentation.scaffold.MainScaffoldScreen
import at.tatami.person.presentation.create.CreatePersonScreen
import at.tatami.person.presentation.photoupload.PersonPhotoUploadScreen
import at.tatami.settings.presentation.ComponentPlaygroundScreen
import at.tatami.settings.presentation.account.AccountSettingsScreen
import at.tatami.settings.presentation.club.ClubSettingsScreen
import at.tatami.settings.presentation.system.SystemSettingsScreen
import at.tatami.event.presentation.create.EventCreateScreen
import at.tatami.event.presentation.detail.EventDetailScreen

@Composable
fun TatamiNavigationHost(
    navController: NavHostController = rememberNavController(),
    authState: AuthState = AuthState.NotAuthenticated
) {
    
    // Use remember to make startDestination stable and prevent NavHost recreation
    val startDestination = remember(authState) {
        when (authState) {
            is AuthState.Authenticated -> TatamiRoute.Main.PersonListRoot
            is AuthState.EmailNotVerified -> TatamiRoute.Auth.EmailVerification
            is AuthState.NotAuthenticated -> TatamiRoute.Auth.Login
            is AuthState.Loading -> TatamiRoute.Auth.Login // Should not happen
        }
    }
    
    // Navigate when auth state changes
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Always navigate to PersonListRoot when authenticated and coming from auth screens
                if (navController.isOnAuthScreen()) {
                    navController.navigateToAndClearStack(TatamiRoute.Main.PersonListRoot)
                }
            }
            is AuthState.EmailNotVerified -> {
                // Navigate to email verification if we're not already there
                if (!navController.isCurrentRoute<TatamiRoute.Auth.EmailVerification>()) {
                    navController.navigateToAndClearStack(TatamiRoute.Auth.EmailVerification)
                }
            }
            is AuthState.NotAuthenticated -> {
                // Navigate to login only if we're not on an auth screen
                if (!navController.isOnAuthScreen()) {
                    navController.navigateToAndClearStack(TatamiRoute.Auth.Login)
                }
            }
            is AuthState.Loading -> {
                // Do nothing
            }
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Auth routes
        composable<TatamiRoute.Auth.Login> {
            LoginScreen(navController = navController)
        }
        
        composable<TatamiRoute.Auth.Register> {
            RegisterScreen(navController = navController)
        }
        
        composable<TatamiRoute.Auth.ForgotPassword> {
            ForgotPasswordScreen(navController = navController)
        }
        
        composable<TatamiRoute.Auth.EmailVerification> {
            EmailVerificationScreen(navController = navController)
        }
        
        // Main routes
        
        composable<TatamiRoute.Main.PersonList> {
            PersonListScreen(navController = navController)
        }
        
        composable<TatamiRoute.Main.PersonListRoot> {
            PersonListScreen(
                navController = navController,
                showBackButton = false
            )
        }
        
        composable<TatamiRoute.Main.PersonDetail> { backStackEntry ->
            // TODO: Implement PersonDetail screen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Person Detail - TODO")
            }
        }
        
        composable<TatamiRoute.Main.CreatePerson> {
            CreatePersonScreen(navController = navController)
        }

        composable<TatamiRoute.Main.PersonPhotoUpload> { backStackEntry ->
            val route = backStackEntry.toRoute<TatamiRoute.Main.PersonPhotoUpload>()
            PersonPhotoUploadScreen(
                navController = navController,
                personId = route.personId
            )
        }

        composable<TatamiRoute.Main.EditPerson> { backStackEntry ->
            val route = backStackEntry.toRoute<TatamiRoute.Main.EditPerson>()
            at.tatami.person.presentation.edit.EditPersonScreen(
                navController = navController,
                personId = route.personId
            )
        }

        composable<TatamiRoute.Main.CreateClub> {
            CreateClubScreen(navController = navController)
        }

        composable<TatamiRoute.Main.ClubPhotoUpload> { backStackEntry ->
            val route = backStackEntry.toRoute<TatamiRoute.Main.ClubPhotoUpload>()
            ClubPhotoUploadScreen(
                navController = navController,
                clubId = route.clubId
            )
        }

        composable<TatamiRoute.Main.ClubList> {
            at.tatami.club.presentation.list.ClubListScreen(
                navController = navController
            )
        }
        
        composable<TatamiRoute.Main.ClubDetail> { backStackEntry ->
            // TODO: ClubDetailScreen
        }
        
        composable<TatamiRoute.Main.Dashboard> {
            MainScaffoldScreen(
                parentNavController = navController
            )
        }
        
        composable<TatamiRoute.Main.JoinClub> {
            JoinClubScreen(
                navController = navController
            )
        }

        composable<TatamiRoute.Main.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToClubSettings = { navController.navigate(TatamiRoute.Main.ClubSettings) },
                onNavigateToSystemSettings = { navController.navigate(TatamiRoute.Main.SystemSettings) },
                onNavigateToAccountSettings = { navController.navigate(TatamiRoute.Main.AccountSettings) },
                onNavigateToComponentPlayground = { navController.navigate(TatamiRoute.Main.ComponentPlayground) }
            )
        }

        composable<TatamiRoute.Main.SystemSettings> {
            SystemSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<TatamiRoute.Main.AccountSettings> {
            AccountSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<TatamiRoute.Main.ClubSettings> {
            ClubSettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToClubList = {
                    // Clear back stack and navigate to club list after club deletion
                    navController.navigate(TatamiRoute.Main.ClubList) {
                        popUpTo(TatamiRoute.Main.Dashboard) { inclusive = true }
                    }
                }
            )
        }

        composable<TatamiRoute.Main.TimezonePicker> {
            TimezonePickerScreen(
                navController = navController,
                onTimezoneSelected = { timezoneItem ->
                    // Navigate back with result - this will be handled by the parent screen
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_timezone", timezoneItem.id)
                }
            )
        }

        composable<TatamiRoute.Main.ComponentPlayground> {
            ComponentPlaygroundScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<TatamiRoute.Main.EventCreate> {
            EventCreateScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<TatamiRoute.Main.EventDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<TatamiRoute.Main.EventDetail>()
            EventDetailScreen(
                eventId = route.eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<TatamiRoute.Main.GroupCreate> {
            at.tatami.group.presentation.create.GroupCreateScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<TatamiRoute.Main.GroupDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<TatamiRoute.Main.GroupDetail>()
            at.tatami.group.presentation.detail.GroupDetailScreen(
                groupId = route.groupId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTrainingList = { groupId ->
                    navController.navigate(TatamiRoute.Main.TrainingList(groupId))
                }
            )
        }

        composable<TatamiRoute.Main.TrainingList> { backStackEntry ->
            val route = backStackEntry.toRoute<TatamiRoute.Main.TrainingList>()
            at.tatami.training.presentation.TrainingListScreen(
                groupId = route.groupId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTrainingDetail = { groupId, trainingId ->
                    navController.navigate(TatamiRoute.Main.TrainingDetail(groupId, trainingId))
                }
            )
        }

        composable<TatamiRoute.Main.TrainingDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<TatamiRoute.Main.TrainingDetail>()
            at.tatami.training.presentation.detail.TrainingDetailScreen(
                groupId = route.groupId,
                trainingId = route.trainingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}