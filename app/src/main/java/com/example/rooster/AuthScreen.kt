package com.example.rooster

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rooster.ui.components.StandardScreenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    StandardScreenLayout(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = if (isTeluguMode) "ప్రవేశం" else "Authentication",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate(NavigationRoute.FarmerHome.route) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isTeluguMode) "రైతుగా ప్రవేశించండి" else "Login as Farmer")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(NavigationRoute.HighLevelHome.route) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isTeluguMode) "అడ్మిన్‌గా ప్రవేశించండి" else "Login as Admin")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onLanguageToggle) {
            Text(if (isTeluguMode) "English" else "తెలుగు")
        }
    }
}
