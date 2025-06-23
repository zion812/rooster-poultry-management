package com.example.rooster

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rooster.ui.components.StandardScreenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceListingCreateScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    StandardScreenLayout(
        horizontalAlignment = Alignment.CenterHorizontally,
        scrollable = true
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isTeluguMode) "కొత్త లిస్టింగ్ సృష్టించండి" else "Create New Listing",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                Button(onClick = onBack) {
                    Text(if (isTeluguMode) "వెనుక" else "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }

        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(if (isTeluguMode) "శీర్షిక" else "Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (isTeluguMode) "వివరణ" else "Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(if (isTeluguMode) "ధర" else "Price") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* TODO: Implement listing creation logic */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isTeluguMode) "లిస్టింగ్ సృష్టించండి" else "Create Listing")
            }
        }
    }
}
