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
fun PrivacyPolicyBottomSheet(
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
                text = "Privacy Policy",
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
                PLACEHOLDER PRIVACY POLICY
                
                Last Updated: [Date]
                
                1. INFORMATION WE COLLECT
                - Personal information: name, date of birth, gender
                - Contact information: email address
                - Sports data: belt level, competition results
                - Photos and media you upload
                
                2. HOW WE USE YOUR INFORMATION
                - To provide and maintain the service
                - To manage club memberships
                - To communicate with you about training and events
                - To improve our services
                
                3. DATA SHARING
                - We share data with your sports club(s)
                - We do not sell your personal information
                - We may share data when required by law
                
                4. DATA SECURITY
                We implement appropriate security measures to protect your data.
                
                5. YOUR RIGHTS
                - Access your personal data
                - Correct inaccurate data
                - Request deletion of your data
                - Export your data
                
                6. CHILDREN'S PRIVACY
                For users under 16, parental consent may be required.
                
                7. CONTACT US
                [Contact information]
                
                [This is placeholder text - replace with actual privacy policy before launch]
                """.trimIndent(),
                style = MaterialTheme.typography.bodySmall,
                lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5
            )
        }
        
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}