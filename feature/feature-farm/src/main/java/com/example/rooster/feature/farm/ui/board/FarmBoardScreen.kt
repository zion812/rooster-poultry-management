package com.example.rooster.feature.farm.ui.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@Composable
fun FarmBoardScreen(
    farmId: String,
    onBack: () -> Unit,
    viewModel: FarmBoardViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val syncFailedFlocks by viewModel.syncFailedFlocks.collectAsStateWithLifecycle()

    LaunchedEffect(farmId) {
        viewModel.loadBoard(farmId, context)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (syncFailedFlocks.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Some flocks failed to sync and may be out of date:",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    for (flock in syncFailedFlocks) {
                        Text(
                            text = "• ${flock.name} (${flock.id})",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "Please check your connection or retry from the details screen.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        when (uiState) {
            is FarmBoardViewModel.FarmBoardUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            }
            is FarmBoardViewModel.FarmBoardUiState.Error -> {
                val message = (uiState as FarmBoardViewModel.FarmBoardUiState.Error).message
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxSize().padding(32.dp)
                )
            }
            is FarmBoardViewModel.FarmBoardUiState.Success -> {
                val state = uiState as FarmBoardViewModel.FarmBoardUiState.Success
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    SectionList(title = "Fowls", items = state.fowls, onItemClick = { /*TODO*/ })
                    Spacer(Modifier.height(8.dp))
                    SectionList(title = "Hens", items = state.hens, onItemClick = { /*TODO*/ })
                    Spacer(Modifier.height(8.dp))
                    SectionList(title = "Breeders", items = state.breeders, onItemClick = { /*TODO*/ })
                    Spacer(Modifier.height(8.dp))
                    SectionList(title = "Chicks", items = state.chicks, onItemClick = { /*TODO*/ })
                }
            }
        }
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