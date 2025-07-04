package com.example.rooster.feature.flockmanagement.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pets // Placeholder for Flock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rooster.core.common.R // For string resources
import com.example.rooster.core.common.model.Flock
import com.example.rooster.core.common.model.FlockType
import com.example.rooster.ui.theme.RoosterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlockManagementScreen(
    viewModel: FlockManagementViewModel = hiltViewModel(),
    // farmId is injected into ViewModel via SavedStateHandle
    onNavigateBack: () -> Unit,
    onNavigateToAddFlock: (farmId: String) -> Unit,
    onNavigateToViewFlock: (flockId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.navigateToAddFlockForFarmId) {
        uiState.navigateToAddFlockForFarmId?.let { farmId ->
            onNavigateToAddFlock(farmId)
            viewModel.navigationToAddFlockComplete()
        }
    }

    LaunchedEffect(key1 = uiState.navigateToViewFlockId) {
        uiState.navigateToViewFlockId?.let { flockId ->
            onNavigateToViewFlock(flockId)
            viewModel.navigationToViewFlockComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.farmName != null) stringResource(id = R.string.flock_management_title_for_farm, uiState.farmName!!) // New String
                        else stringResource(id = R.string.flock_management_title) // New String
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.action_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddFlockClicked() }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.flock_management_add_flock_fab)) // New string
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            } else if (uiState.flocks.isEmpty()) {
                 Text(
                    text = stringResource(id = R.string.flock_management_no_flocks), // New string
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.flocks, key = { flock -> flock.id }) { flock ->
                        FlockListItem(flock = flock, onClick = { viewModel.onFlockClicked(flock.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlockListItem( // Similar to FarmListItem, can be refactored if very similar
    flock: Flock,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Pets,
                contentDescription = stringResource(id = R.string.flock_icon_desc), // New String
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = flock.name.ifBlank { stringResource(id = R.string.flock_unnamed) }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = stringResource(R.string.flock_list_item_details, flock.breed, flock.flockType.name.replace("_", " ").capitalize(java.util.Locale.ROOT), flock.activeBirds), // New String: "%1$s (%2$s) - %3$d Birds"
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = stringResource(id = R.string.action_view_details))
        }
    }
}

@Preview(showBackground = true, name = "Flock Management Screen - Populated")
@Composable
fun FlockManagementScreenPreviewPopulated() {
    val sampleFlocks = listOf(
        Flock(id = "f1", farmId = "1", name = "Layer Hens Alpha", breed = "ISA Brown", flockType = FlockType.LAYER, activeBirds = 120),
        Flock(id = "f2", farmId = "1", name = "Broiler Batch #3", breed = "Ross 308", flockType = FlockType.BROILER, activeBirds = 500)
    )
    RoosterTheme {
        // This preview needs a fake ViewModel or direct state for proper rendering
        Scaffold(
            topBar = { TopAppBar(title = { Text("Manage Flocks for Sunrise Farm") }) },
            floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "") } }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sampleFlocks) { flock ->
                    FlockListItem(flock = flock, onClick = {})
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Flock Management Screen - Empty")
@Composable
fun FlockManagementScreenPreviewEmpty() {
     RoosterTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Manage Flocks for Green Valley") }) },
            floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "") } }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No flocks found for this farm. Tap '+' to add one.")
            }
        }
    }
}

// New Strings:
// R.string.flock_management_title ("Manage Flocks")
// R.string.flock_management_title_for_farm ("Flocks for %1$s")
// R.string.flock_management_add_flock_fab ("Add new flock")
// R.string.flock_management_no_flocks ("No flocks found for this farm. Tap '+' to add one.")
// R.string.flock_icon_desc ("Flock icon")
// R.string.flock_list_item_details ("%1$s (%2$s) - %3$d Active Birds") - Breed (Type) - Count
// (Reuses R.string.flock_unnamed, R.string.action_view_details, R.string.action_back)
```

**Key aspects of `FlockManagementScreen.kt`:**

*   **ViewModel Integration:** Uses `hiltViewModel()` and collects `FlockManagementUiState`.
*   **Navigation Triggers:** `LaunchedEffect` blocks observe `navigateToAddFlockForFarmId` and `navigateToViewFlockId` to call navigation lambdas.
*   **UI Structure:**
    *   `Scaffold` with a `TopAppBar` (dynamically showing farm name if available) and a `FloatingActionButton` for adding new flocks.
    *   Displays loading/error states.
    *   Uses a `LazyColumn` with a `FlockListItem` composable to display flocks.
*   **`FlockListItem` Composable:** Displays flock name, breed, type, and active bird count. Clickable to view/edit details.
*   **Previews:** Included for populated and empty states.
*   **String Resources:** New string resources identified.

This completes the UI implementation for `FlockManagementScreen`. Next steps are updating navigation, adding strings, and unit tests.
