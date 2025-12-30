package at.tatami.person.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.tatami.common.ui.components.ErrorMessage
import at.tatami.common.ui.components.PersonAvatar
import at.tatami.common.ui.components.TatamiLoadingIndicator
import at.tatami.common.ui.components.SignOutDialog
import at.tatami.domain.model.Person
import at.tatami.navigation.TatamiRoute
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonListScreen(
    navController: NavController,
    showBackButton: Boolean = true,
    viewModel: PersonListViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSignOutDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.person_list_title)) },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.back)
                            )
                        }
                    } else {
                        IconButton(onClick = { showSignOutDialog = true }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = stringResource(Res.string.sign_out)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleEditMode() }) {
                        Icon(
                            imageVector = if (state.isEditMode) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = stringResource(
                                if (state.isEditMode) Res.string.done else Res.string.edit
                            )
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp),
                onClick = { navController.navigate(TatamiRoute.Main.CreatePerson) }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.person_add)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> TatamiLoadingIndicator()
                state.error != null -> {
                    ErrorMessage(message = state.error ?: stringResource(Res.string.unknown_error))
                }
                state.persons.isEmpty() -> EmptyPersonList()
                else -> PersonList(
                    persons = state.persons,
                    isEditMode = state.isEditMode,
                    onPersonClick = { person ->
                        if (state.isEditMode) {
                            // In edit mode, navigate to edit screen
                            navController.navigate(TatamiRoute.Main.EditPerson(person.id))
                        } else {
                            // Set the selected person and wait for completion before navigating
                            viewModel.selectPersonAndNavigate(
                                person = person,
                                onNavigate = { selectedPerson ->
                                    if (selectedPerson.clubIds.isEmpty()) {
                                        // Person has no club, navigate to JoinOrCreateClub
                                        navController.navigate(TatamiRoute.Main.ClubList)
                                    } else {
                                        // Person has club(s), navigate to club select screen
                                        navController.navigate(TatamiRoute.Main.ClubList)
                                    }
                                }
                            )
                        }
                    }
                )
            }
        }
    }
    
    // Sign out dialog
    if (showSignOutDialog) {
        SignOutDialog(
            onConfirm = {
                showSignOutDialog = false
                viewModel.signOut()
            },
            onDismiss = {
                showSignOutDialog = false
            }
        )
    }
}

@Composable
private fun EmptyPersonList(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(Res.string.person_list_empty_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = stringResource(Res.string.person_list_empty_message),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PersonList(
    persons: List<Person>,
    isEditMode: Boolean,
    onPersonClick: (Person) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(persons) { person ->
            PersonListItem(
                person = person,
                isEditMode = isEditMode,
                onClick = { onPersonClick(person) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PersonListItem(
    person: Person,
    isEditMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.clickable { onClick() },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = {
            Text(
                text = "${person.firstName} ${person.lastName}",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        leadingContent = {
            PersonAvatar(
                name = "${person.firstName} ${person.lastName}",
                profileImageUrl = person.personImgUrl
            )
        },
        trailingContent = {
            Icon(
                imageVector = if (isEditMode)
                    Icons.Default.Edit
                else
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}
