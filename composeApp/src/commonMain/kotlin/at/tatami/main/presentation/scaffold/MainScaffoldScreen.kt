package at.tatami.main.presentation.scaffold

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import at.tatami.main.presentation.event.EventScreen
import at.tatami.main.presentation.group.GroupScreen
import at.tatami.navigation.TatamiRoute
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffoldScreen(
    parentNavController: NavController,
    viewModel: MainScaffoldViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = state.selectedClubName ?: stringResource(Res.string.app_name),
                        maxLines = 1
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                //modifier = Modifier.height(110.dp)
            ) {
                val items = listOf(
                    BottomNavItem(
                        route = TatamiRoute.Main.Event::class.qualifiedName!!,
                        icon = Icons.Default.Event,
                        labelRes = Res.string.events
                    ),
                    BottomNavItem(
                        route = TatamiRoute.Main.Group::class.qualifiedName!!,
                        icon = Icons.Default.Group,
                        labelRes = Res.string.group
                    ),
                    BottomNavItem(
                        route = TatamiRoute.Main.Settings::class.qualifiedName!!,
                        icon = Icons.Default.Settings,
                        labelRes = Res.string.settings
                    )
                )
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = null,
                        selected = currentDestination == item.route,
                        onClick = {
                            if (item.route == TatamiRoute.Main.Settings::class.qualifiedName!!) {
                                // Navigate to settings using parent navigator
                                parentNavController.navigate(TatamiRoute.Main.Settings)
                            } else {
                                bottomNavController.navigate(item.route) {
                                    popUpTo(bottomNavController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = TatamiRoute.Main.Event::class.qualifiedName!!,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(TatamiRoute.Main.Event::class.qualifiedName!!) {
                EventScreen(
                    onNavigateToCreateEvent = { parentNavController.navigate(TatamiRoute.Main.EventCreate) },
                    onNavigateToEventDetail = { eventId -> parentNavController.navigate(TatamiRoute.Main.EventDetail(eventId)) }
                )
            }
            
            composable(TatamiRoute.Main.Group::class.qualifiedName!!) {
                GroupScreen(
                    onNavigateToCreateGroup = { parentNavController.navigate(TatamiRoute.Main.GroupCreate) },
                    onNavigateToGroupDetail = { groupId ->
                        parentNavController.navigate(TatamiRoute.Main.GroupDetail(groupId))
                    }
                )
            }
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelRes: org.jetbrains.compose.resources.StringResource
)