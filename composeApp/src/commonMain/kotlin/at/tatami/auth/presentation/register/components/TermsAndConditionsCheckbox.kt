package at.tatami.auth.presentation.register.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tatami.composeapp.generated.resources.*

@Composable
fun TermsAndConditionsCheckbox(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    onShowTermsOfService: () -> Unit,
    onShowPrivacyPolicy: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            enabled = enabled
        )
        Spacer(modifier = Modifier.width(8.dp))
        
        val annotatedText = buildAnnotatedString {
            append(stringResource(Res.string.accept_terms_prefix))
            append(" ")
            
            withLink(
                LinkAnnotation.Clickable(
                    tag = "terms",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ),
                    linkInteractionListener = {
                        onShowTermsOfService()
                    }
                )
            ) {
                append(stringResource(Res.string.terms_of_service))
            }
            
            append(" ")
            append(stringResource(Res.string.and_text))
            append(" ")
            
            withLink(
                LinkAnnotation.Clickable(
                    tag = "privacy",
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline
                        )
                    ),
                    linkInteractionListener = {
                        onShowPrivacyPolicy()
                    }
                )
            ) {
                append(stringResource(Res.string.privacy_policy))
            }
        }
        
        Text(
            text = annotatedText,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}