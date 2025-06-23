package com.example.rooster

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Composable
fun DiagnosticsScreen() {
    val crashlytics = FirebaseCrashlytics.getInstance()
    var simulate2G by remember { mutableStateOf(false) }
    StandardScreenLayout(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        scrollable = true
    ) {
        Text("Diagnostics", style = MaterialTheme.typography.headlineLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Simulate 2G Network:")
            Switch(
                checked = simulate2G,
                onCheckedChange = {
                    simulate2G = it
                    crashlytics.log("Diagnostics: simulate2G set to $it")
                },
            )
        }
        Text("Screen Tests:", style = MaterialTheme.typography.titleMedium)
        // Test simplified screens only
        Text("✅ CulturalEventsScreen: Simplified", style = MaterialTheme.typography.bodyMedium)
        Text("✅ ProfileScreen: Simplified", style = MaterialTheme.typography.bodyMedium)
        Text("✅ FeedbackScreen: Simplified", style = MaterialTheme.typography.bodyMedium)
        Text("✅ VetConsultationScreen: Simplified", style = MaterialTheme.typography.bodyMedium)
        Text("✅ SettingsScreen: Simplified", style = MaterialTheme.typography.bodyMedium)
        Text(
            "⚠️ MarketplaceScreen: Complex (Nav dependency)",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "MVP Status: Core screens simplified for Quick Build",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
