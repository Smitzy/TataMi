package at.tatami.settings.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.tatami.common.ui.components.TatamiSettingsCard
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*

@Composable
fun AccountManagementCard(
    onSignOut: () -> Unit
) {
    TatamiSettingsCard(
        title = stringResource(Res.string.account_management)
    ) {
        // Sign Out Button
        OutlinedButton(
            onClick = onSignOut,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(Res.string.sign_out))
        }
    }
}