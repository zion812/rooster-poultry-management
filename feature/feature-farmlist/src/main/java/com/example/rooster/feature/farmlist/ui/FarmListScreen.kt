package com.example.rooster.feature.farmlist.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HomeWork // Placeholder for farm icon
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
import com.example.rooster.core.common.model.Farm
import com.example.rooster.ui.theme.RoosterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmListScreen(
    viewModel: FarmListViewModel = hiltViewModel(),
    onNavigateToCreateFarm: () -> Unit,
    onNavigateToFarmDetails: (farmId: String) -> Unit,
    onNavigateBack: () -> Unit // If this screen can be navigated back from
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.navigateToCreateFarm) {
        if (uiState.navigateToCreateFarm) {
            onNavigateToCreateFarm()
            viewModel.navigationToCreateFarmComplete()
        }
    }

    LaunchedEffect(key1 = uiState.navigateToFarmDetailsId) {
        uiState.navigateToFarmDetailsId?.let { farmId ->
            onNavigateToFarmDetails(farmId)
            viewModel.navigationToFarmDetailsComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.farm_list_title)) }, // New string: "My Farms"
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.action_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.onAddFarmClicked() }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.farm_list_add_farm_fab_desc)) // New string
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
            } else {
                val errorToShow = when {
                    uiState.errorResId != null -> stringResource(id = uiState.errorResId!!)
                    uiState.errorMessage != null -> uiState.errorMessage
                    // Special case for empty list message if not treated as an errorResId by VM
                    uiState.farms.isEmpty() && uiState.errorResId == null && uiState.errorMessage == null -> stringResource(id = R.string.farm_list_no_farms_message)
                    else -> null
                }

                if (errorToShow != null) {
                    Text(
                        text = errorToShow,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        color = if (uiState.errorResId == R.string.farm_list_empty_tap_add || (uiState.farms.isEmpty() && uiState.errorResId == null && uiState.errorMessage == null)) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                // This explicit check for uiState.farms.isEmpty() might be redundant if ViewModel always sets errorResId for empty list
                } else if (uiState.farms.isEmpty() /* && errorToShow == null -- already implied */) {
                     Text(
                        text = stringResource(id = R.string.farm_list_no_farms_message),
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.farms, key = { farm -> farm.id }) { farm ->
                        FarmListItem(farm = farm, onClick = { viewModel.onFarmClicked(farm.id) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmListItem(
    farm: Farm,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.HomeWork, // Placeholder farm icon
                contentDescription = "Farm",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = farm.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = farm.location, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                // Optionally show flock/bird count if available directly on Farm model
                if (farm.flockCount > 0 || farm.totalBirdCount > 0) {
                     Text(
                        text = stringResource(R.string.farm_list_item_stats, farm.flockCount, farm.totalBirdCount), // New: "%1$d Flocks, %2$d Birds"
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = stringResource(id = R.string.farm_list_view_details_desc)) // New string
        }
    }
}

@Preview(showBackground = true, name = "Farm List Screen - Populated")
@Composable
fun FarmListScreenPreviewPopulated() {
    val sampleFarms = listOf(
        Farm(id = "1", name = "Green Acres", location = "Valley Town, AP", ownerId = "user1", flockCount = 3, totalBirdCount = 150),
        Farm(id = "2", name = "Sunrise Poultry", location = "Hilltop, AP", ownerId = "user1", flockCount = 5, totalBirdCount = 500)
    )
    // This preview needs a fake ViewModel or direct state for proper rendering
    // For now, just showing the Scaffold structure.
    // To preview FarmListItems, create a separate preview for FarmListItem.
    RoosterTheme {
        // Simplified for preview, doesn't use ViewModel here
        Scaffold(
            topBar = { TopAppBar(title = { Text("My Farms") }) },
            floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "") } }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sampleFarms) { farm ->
                    FarmListItem(farm = farm, onClick = {})
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Farm List Item")
@Composable
fun FarmListItemPreview() {
    RoosterTheme {
        FarmListItem(
            farm = Farm(id = "1", name = "Happy Chick Farm", location = "Rural Area, AP", ownerId = "user1", flockCount = 2, totalBirdCount = 80),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Farm List Screen - Empty")
@Composable
fun FarmListScreenPreviewEmpty() {
    RoosterTheme {
         Scaffold(
            topBar = { TopAppBar(title = { Text("My Farms") }) },
            floatingActionButton = { FloatingActionButton(onClick = {}) { Icon(Icons.Filled.Add, "") } }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("You haven't added any farms yet. Tap the '+' button to get started!")
            }
        }
    }
}

// New strings needed:
// R.string.farm_list_title ("My Farms")
// R.string.farm_list_add_farm_fab_desc ("Add new farm")
// R.string.farm_list_no_farms_message ("You haven't added any farms yet. Tap the '+' button to get started!")
// R.string.farm_list_item_stats ("%1$d Flocks, %2$d Birds")
// R.string.farm_list_view_details_desc ("View farm details")
```

**Key aspects of `FarmListScreen.kt`:**

*   **ViewModel Integration:** Uses `hiltViewModel()` to get `FarmListViewModel` and collects `uiState`.
*   **Navigation Triggers:** `LaunchedEffect` blocks observe `navigateToCreateFarm` and `navigateToFarmDetailsId` from the `uiState` to call the respective navigation lambdas passed to the screen.
*   **UI Structure:**
    *   `Scaffold` with a `TopAppBar` and a `FloatingActionButton` for adding new farms.
    *   Displays a `CircularProgressIndicator` when `isLoading` is true.
    *   Displays an error message if `uiState.error` is not null.
    *   Displays a specific message if the farm list is empty (and no other error is present).
    *   Uses a `LazyColumn` to display the list of farms.
*   **`FarmListItem` Composable:** A `Card`-based composable to display individual farm information (name, location, stats) with a click action.
*   **Previews:** Includes previews for populated list, individual item, and empty state.
*   **String Resources:** Identifies new string resources that will be needed.

This completes the UI implementation for `FarmListScreen`. The next steps in the plan are to update navigation, add strings, and write unit tests for the ViewModel.
