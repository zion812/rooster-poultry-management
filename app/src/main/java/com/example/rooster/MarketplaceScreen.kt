package com.example.rooster

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rooster.ui.components.StandardScreenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
) {
    StandardScreenLayout(
        horizontalAlignment = Alignment.CenterHorizontally,
        scrollable = true,
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isTeluguMode) "మార్కెట్‌ప్లేస్" else "Marketplace",
                    fontWeight = FontWeight.Bold,
                )
            },
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (isTeluguMode) "మార్కెట్‌ప్లేస్ త్వరలో వస్తుంది" else "Marketplace coming soon",
            style = MaterialTheme.typography.titleLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("auctions") },
        ) {
            Text(if (isTeluguMode) "వేలాలు చూడండి" else "View Auctions")
        }
    }
}
