package at.tatami.auth.presentation.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import at.tatami.auth.presentation.register.components.PrivacyPolicyBottomSheet
import at.tatami.auth.presentation.register.components.TermsAndConditionsCheckbox
import at.tatami.auth.presentation.register.components.TermsOfServiceBottomSheet
import at.tatami.common.ui.components.*
import at.tatami.common.ui.theme.slacksideOneFamily
import at.tatami.navigation.TatamiRoute
import at.tatami.navigation.navigateToAndClearStack
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    
    // Navigate to email verification when registration is successful
    LaunchedEffect(state.isRegistrationSuccessful) {
        if (state.isRegistrationSuccessful) {
            navController.navigateToAndClearStack(TatamiRoute.Auth.EmailVerification)
        }
    }
    
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    // Show bottom sheets based on state
    if (state.showTermsDialog) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onDismissTermsOfService() },
            sheetState = bottomSheetState,
            modifier = Modifier.systemBarsPadding()
        ) {
            TermsOfServiceBottomSheet(
                onDismiss = { viewModel.onDismissTermsOfService() }
            )
        }
    }

    if (state.showPrivacyDialog) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.onDismissPrivacyPolicy() },
            sheetState = bottomSheetState,
            modifier = Modifier.systemBarsPadding()
        ) {
            PrivacyPolicyBottomSheet(
                onDismiss = { viewModel.onDismissPrivacyPolicy() }
            )
        }
    }
    
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
                // Logo/Title
                AnimatedBouncingText(
                    text = stringResource(Res.string.app_name),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = slacksideOneFamily()
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = listOf((-8).dp, (-4).dp, (-3).dp, (0).dp, (0).dp)
                )

                Spacer(modifier = Modifier.height(28.dp))
                
                // Register Form
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 400.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    
                    // Email Field
                    TatamiTextField(
                        value = state.email,
                        onValueChange = viewModel::onEmailChange,
                        label = stringResource(Res.string.email),
                        placeholder = stringResource(Res.string.email_placeholder),
                        enabled = !state.isLoading,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Password Field
                    TatamiPasswordField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        label = stringResource(Res.string.password),
                        placeholder = stringResource(Res.string.password_placeholder_hint),
                        enabled = !state.isLoading,
                        isPasswordVisible = state.isPasswordVisible,
                        onPasswordVisibilityChange = { viewModel.onTogglePasswordVisibility() },
                        imeAction = ImeAction.Next,
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Confirm Password Field
                    TatamiPasswordField(
                        value = state.confirmPassword,
                        onValueChange = viewModel::onConfirmPasswordChange,
                        label = stringResource(Res.string.confirm_password),
                        placeholder = stringResource(Res.string.confirm_password_placeholder_alt),
                        enabled = !state.isLoading,
                        isPasswordVisible = state.isConfirmPasswordVisible,
                        onPasswordVisibilityChange = { viewModel.onToggleConfirmPasswordVisibility() },
                        imeAction = ImeAction.Done,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (state.acceptedTerms) {
                                    viewModel.onSignUp()
                                }
                            }
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Terms and Conditions Checkbox
                    TermsAndConditionsCheckbox(
                        checked = state.acceptedTerms,
                        onCheckedChange = { viewModel.onToggleTermsAcceptance() },
                        onShowTermsOfService = { viewModel.onShowTermsOfService() },
                        onShowPrivacyPolicy = { viewModel.onShowPrivacyPolicy() },
                        enabled = !state.isLoading
                    )

                    InlineErrorMessage(
                        message = state.errorMessage
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Sign Up Button
                    TatamiButton(
                        onClick = viewModel::onSignUp,
                        enabled = state.email.isNotEmpty() &&
                                 state.password.isNotEmpty() &&
                                 state.confirmPassword.isNotEmpty() &&
                                 state.acceptedTerms,
                        loading = state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(Res.string.sign_up))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Sign In Link
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    Text(
                        text = stringResource(Res.string.already_have_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = {
                            navController.navigate(TatamiRoute.Auth.Login) {
                                popUpTo(TatamiRoute.Auth.Login) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(Res.string.sign_in_link))
                    }
                    }
                }
            }
    }
}