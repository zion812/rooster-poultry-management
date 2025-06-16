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
fun FlockDashboardScreen(vm: EnthusiastViewModel = viewModel()) {
    val flock by vm.flock.collectAsState()
    // TODO: Add growth stats and charts later

    Scaffold(
        topBar = { TopAppBar(title = { Text("Flock Dashboard") }) },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
        ) {
            if (flock.isEmpty()) {
                Text("No flocks to display.")
            } else {
                flock.forEach { entry ->
                    ListItem(
                        headlineContent = { Text(entry.name) },
                        supportingContent = { Text("Count: ${entry.count}") },
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
