package com.example.rooster

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rooster.ui.components.StandardScreenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    StandardScreenLayout(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        scrollable = true,
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = if (isTeluguMode) "సెట్టింగ్స్ ఐకాన్" else "Settings Icon",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isTeluguMode) "సెట్టింగ్స్" else "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isTeluguMode) "త్వరలో అందుబాటులోకి వస్తుంది" else "Coming Soon",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onLanguageToggle) {
            Text(
                text = if (isTeluguMode) "Switch to English" else "తెలుగుకు మారండి",
            )
        }
    }
}
