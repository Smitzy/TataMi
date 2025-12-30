package at.tatami.auth.presentation.login

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
import at.tatami.common.ui.components.*
import at.tatami.common.ui.theme.slacksideOneFamily
import at.tatami.navigation.TatamiRoute
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    // Navigation is handled by auth state in App.kt
    // No need to navigate here
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            AnimatedBouncingText(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = slacksideOneFamily()
                ),
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = listOf((-8).dp, (-4).dp, (-3).dp, (0).dp, (0).dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login Form
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

                Spacer(modifier = Modifier.height(16.dp))

                // Password Field - Using our reusable component
                TatamiPasswordField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    label = stringResource(Res.string.password),
                    placeholder = stringResource(Res.string.password_placeholder),
                    enabled = !state.isLoading,
                    isPasswordVisible = state.isPasswordVisible,
                    onPasswordVisibilityChange = { viewModel.onTogglePasswordVisibility() },
                    imeAction = ImeAction.Done,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.onSignIn()
                        }
                    )
                )

                InlineErrorMessage(
                    message = state.errorMessage
                )


                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                TatamiButton(
                    onClick = viewModel::onSignIn,
                    enabled = state.email.isNotEmpty() && state.password.isNotEmpty(),
                    loading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.sign_in))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Forgot Password
                TextButton(
                    onClick = {
                        navController.navigate(TatamiRoute.Auth.ForgotPassword) {
                            launchSingleTop = true
                        }
                    },
                    enabled = !state.isLoading
                ) {
                    Text(stringResource(Res.string.forgot_password))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign Up Link
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.dont_have_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(
                        onClick = {
                            navController.navigate(TatamiRoute.Auth.Register) {
                                launchSingleTop = true
                            }
                        },
                        enabled = !state.isLoading
                    ) {
                        Text(stringResource(Res.string.sign_up))
                    }
                }
            }
        }
    }
}
