package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.viewmodel.EnthusiastViewModel

@Composable
fun FamilyTreeTransferScreen(
    chickenId: String,
    vm: EnthusiastViewModel = viewModel(),
) {
    val transfers by vm.transfers.collectAsState()
    LaunchedEffect(chickenId) {
        vm.fetchFamilyChain(chickenId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Transfer History: $chickenId") }) },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
        ) {
            if (transfers.isEmpty()) {
                Text("No transfer history found.")
            } else {
                transfers.forEach { transfer ->
                    ListItem(
                        headlineContent = { Text("From: ${transfer.fromOwnerId} To: ${transfer.toOwnerId}") },
                        supportingContent = { Text("Status: ${transfer.state}") },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
