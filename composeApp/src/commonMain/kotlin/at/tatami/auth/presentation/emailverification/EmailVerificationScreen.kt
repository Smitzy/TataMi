package at.tatami.auth.presentation.emailverification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.tatami.common.ui.components.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

@Composable
fun EmailVerificationScreen(
    navController: NavController,
    viewModel: EmailVerificationViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    // Navigation is handled by auth state in NavigationHost.kt
    // When email is verified, AuthStateManager will update to Authenticated
    // and NavigationHost will navigate to PersonListRoot automatically
    
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Email icon
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Title
                Text(
                    text = stringResource(Res.string.verify_email_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Subtitle with email
                Text(
                    text = if (state.userEmail.isNotEmpty()) {
                        stringResource(Res.string.verify_email_subtitle, state.userEmail)
                    } else {
                        ""
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Instructions
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.verify_email_instructions),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = stringResource(Res.string.verify_email_check_spam),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
                Spacer(modifier = Modifier.height(32.dp))
                
                // Check verification status button
                TatamiButton(
                    onClick = viewModel::checkVerificationStatus,
                    modifier = Modifier.fillMaxWidth(),
                    loading = state.isCheckingVerification
                ) {
                    Text(stringResource(Res.string.check_verification_status))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Resend email button
                TatamiOutlinedButton(
                    onClick = viewModel::resendVerificationEmail,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = state.canResendEmail,
                    loading = state.isResendingEmail
                ) {
                    if (state.canResendEmail) {
                        Text(stringResource(Res.string.resend_verification_email))
                    } else {
                        Text(stringResource(Res.string.resend_email_wait, state.resendCooldownSeconds))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign out link
                TextButton(
                    onClick = viewModel::signOut
                ) {
                    Text(stringResource(Res.string.sign_out_different_account))
                }
                
                // Success message
                if (state.showSuccessMessage) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.verification_email_sent),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                // Error message
                state.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(16.dp))
                    ErrorMessage(
                        message = error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}