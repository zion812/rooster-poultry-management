package com.example.rooster.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rooster.viewmodel.EnthusiastViewModel

@Composable
fun ActionSuggestionsWidget(
    birdId: String,
    vm: EnthusiastViewModel = viewModel(),
) {
    val suggestions by vm.suggestions.collectAsState()
    LaunchedEffect(birdId) {
        vm.fetchSuggestions(birdId)
        // Optionally trigger generateSuggestions if needed
        // vm.generateSuggestions(birdId)
    }

    Card(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Suggested Actions for $birdId", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            if (suggestions.isEmpty()) {
                Text("No suggestions right now.")
            } else {
                suggestions.forEach { suggestion ->
                    Text("- ${suggestion.message}")
                }
            }
        }
    }
}
