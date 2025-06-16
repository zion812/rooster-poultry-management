package com.example.rooster

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Composable
fun DiagnosticsScreen() {
    val crashlytics = FirebaseCrashlytics.getInstance()
    var simulate2G by remember { mutableStateOf(false) }
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
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
        Text("CulturalEventsScreen:", style = MaterialTheme.typography.titleMedium)
        // Invoke Composables to verify they load
        CulturalEventsScreen(isTeluguMode = false, onLanguageToggle = {})
        Spacer(modifier = Modifier.height(8.dp))
        Text("MarketplaceScreen:", style = MaterialTheme.typography.titleMedium)
        MarketplaceScreen(
            navController = rememberNavController(),
            isTeluguMode = false,
            onLanguageToggle = {},
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("ProfileScreen:", style = MaterialTheme.typography.titleMedium)
        ProfileScreen(
            navController = rememberNavController(),
            isTeluguMode = false,
            onLanguageToggle = {},
            onLogout = {},
        )
    }
}
