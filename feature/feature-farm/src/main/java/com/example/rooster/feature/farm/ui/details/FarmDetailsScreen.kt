package com.example.rooster.feature.farm.ui.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmDetailsScreen(
    farmId: String,
    onBack: () -> Unit,
    viewModel: FarmDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(farmId) {
        viewModel.loadDetails(farmId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Details") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onBack() }
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is FarmDetailsUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is FarmDetailsUiState.Error -> {
                    val message = (uiState as FarmDetailsUiState.Error).message
                    Text(text = "Error: $message", color = Color.Red)
                }

                is FarmDetailsUiState.Success -> {
                    val flock = (uiState as FarmDetailsUiState.Success).flock
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = flock.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (flock.verified) {
                                Badge(text = "Verified", color = Color.Blue)
                            }
                            if (flock.certified) {
                                Badge(text = "Certified", color = Color.Green)
                            }
                        }
                        // Additional details can be added here
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
