package com.example.rooster.feature.farm.ui.familytree

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FamilyTreeScreen(
    fowlId: String,
    viewModel: FamilyTreeViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val ancestors by viewModel.ancestors.collectAsStateWithLifecycle()
    LaunchedEffect(fowlId) {
        viewModel.loadFamilyTree(fowlId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Family Tree", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(ancestors) { index, fowl ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = (index * 16).dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "${fowl.name} (${fowl.id})",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}