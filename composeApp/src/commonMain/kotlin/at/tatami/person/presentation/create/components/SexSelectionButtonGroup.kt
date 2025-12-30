package at.tatami.person.presentation.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import at.tatami.domain.model.Sex
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.Res
import tatami.composeapp.generated.resources.female
import tatami.composeapp.generated.resources.male
import tatami.composeapp.generated.resources.other

/**
 * Single-Select Connected ButtonGroup for sex selection (MALE, FEMALE, OTHER).
 * Uses Material 3 Expressive API with connected button shapes and radio button semantics.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SexSelectionButtonGroup(
    selectedSex: Sex?,
    onSexSelected: (Sex) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val sexOptions = listOf(Sex.MALE, Sex.FEMALE, Sex.OTHER)
    val sexLabels = listOf(
        stringResource(Res.string.male),
        stringResource(Res.string.female),
        stringResource(Res.string.other)
    )
    val sexIcons = listOf(
        Icons.Default.Male,
        Icons.Default.Female,
        Icons.Default.Wc // Gender-neutral icon for OTHER
    )
    val selectedIndex = when (selectedSex) {
        Sex.MALE -> 0
        Sex.FEMALE -> 1
        Sex.OTHER -> 2
        else -> -1
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)
    ) {
        sexOptions.forEachIndexed { index, sex ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { onSexSelected(sex) },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    sexOptions.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                enabled = enabled
            ) {
                Icon(
                    imageVector = sexIcons[index],
                    contentDescription = null
                )
                //Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                //Text(sexLabels[index])
            }
        }
    }
}