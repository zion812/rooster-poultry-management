package com.example.rooster.feature.farm.ui.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FarmBoardScreen(
    farmId: String,
    onBack: () -> Unit,
    viewModel: FarmBoardViewModel = hiltViewModel()
) {
    val fowls by viewModel.fowls.collectAsStateWithLifecycle()
    val hens by viewModel.hens.collectAsStateWithLifecycle()
    val breeders by viewModel.breeders.collectAsStateWithLifecycle()
    val chicks by viewModel.chicks.collectAsStateWithLifecycle()

    LaunchedEffect(farmId) {
        viewModel.loadBoard(farmId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        SectionList(title = "Fowls", items = fowls, onItemClick = { /*TODO*/ })
        Spacer(Modifier.height(8.dp))
        SectionList(title = "Hens", items = hens, onItemClick = { /*TODO*/ })
        Spacer(Modifier.height(8.dp))
        SectionList(title = "Breeders", items = breeders, onItemClick = { /*TODO*/ })
        Spacer(Modifier.height(8.dp))
        SectionList(title = "Chicks", items = chicks, onItemClick = { /*TODO*/ })
    }
}

@Composable
private fun SectionList(
    title: String,
    items: List<com.example.rooster.feature.farm.domain.model.Flock>,
    onItemClick: (com.example.rooster.feature.farm.domain.model.Flock) -> Unit
) {
    Text(text = title, style = MaterialTheme.typography.titleMedium)
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(items) { flock ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onItemClick(flock) }
            ) {
                Text(
                    text = flock.name,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}