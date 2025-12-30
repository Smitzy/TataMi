@file:OptIn(ExperimentalMaterial3Api::class)

package at.tatami.group.presentation.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.tatami.group.presentation.create.components.MemberTrainerSelectionBottomSheetContent
import org.koin.compose.viewmodel.koinViewModel

/**
 * Screen for creating a new group.
 * Simpler than EventCreateScreen: only name field + member/trainer selection.
 */
@Composable
fun GroupCreateScreen(
    onNavigateBack: () -> Unit,
    viewModel: GroupCreateViewModel = koinViewModel()
) {
    var showMemberSelectionSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val name = viewModel.name.collectAsState()
    val selectedMemberIds = viewModel.selectedMemberIds.collectAsState()
    val selectedTrainerIds = viewModel.selectedTrainerIds.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val error = viewModel.error.collectAsState()
    val success = viewModel.success.collectAsState()

    // Navigate back on success
    LaunchedEffect(success.value) {
        if (success.value) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Group") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Group Name
            OutlinedTextField(
                value = name.value,
                onValueChange = { viewModel.setName(it) },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value,
                singleLine = true
            )

            // Member/Trainer Selection Button
            Button(
                onClick = { showMemberSelectionSheet = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value
            ) {
                Text("Select Members & Trainers (${selectedMemberIds.value.size} members, ${selectedTrainerIds.value.size} trainers)")
            }

            // Error message
            error.value?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Create button
            Button(
                onClick = { viewModel.createGroup() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading.value
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Group")
                }
            }
        }
    }

    // Member/Trainer Selection Bottom Sheet
    if (showMemberSelectionSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showMemberSelectionSheet = false
            },
            sheetState = sheetState
        ) {
            MemberTrainerSelectionBottomSheetContent(
                initialSelectedMemberIds = selectedMemberIds.value.toList(),
                initialSelectedTrainerIds = selectedTrainerIds.value.toList(),
                viewModel = viewModel
            )
        }
    }
}