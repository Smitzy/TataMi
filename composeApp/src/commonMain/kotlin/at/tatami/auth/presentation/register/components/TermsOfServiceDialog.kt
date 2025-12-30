package at.tatami.auth.presentation.register.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceBottomSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
        ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Terms of Service",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = """
                PLACEHOLDER TERMS OF SERVICE
                
                Last Updated: [Date]
                
                1. ACCEPTANCE OF TERMS
                By using the TataMi app, you agree to these Terms of Service.
                
                2. USE OF SERVICE
                - You must provide accurate information
                - You are responsible for maintaining account security
                - You must be at least 13 years old to use this service
                
                3. PRIVACY AND DATA
                Your use of our service is also governed by our Privacy Policy.
                
                4. SPORTS CLUB MEMBERSHIP
                - Club rules and regulations apply
                - Membership fees are set by individual clubs
                - Training participation is at your own risk
                
                5. USER CONTENT
                - You retain ownership of content you upload
                - You grant us license to use content for service operation
                
                6. PROHIBITED USES
                You may not use the service for illegal purposes or to harm others.
                
                7. LIMITATION OF LIABILITY
                We are not liable for injuries during sports activities.
                
                8. CHANGES TO TERMS
                We may update these terms from time to time.
                
                [This is placeholder text - replace with actual terms before launch]
                """.trimIndent(),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5
            )
        }
        
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}