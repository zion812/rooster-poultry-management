package com.example.rooster.ui.community

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rooster.CommunityGroup
import com.example.rooster.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityListScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
    viewModel: CommunityViewModel = viewModel(),
) {
    val groups by viewModel.groups.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Search and filter states
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf("All Locations") }
    var showLocationFilter by remember { mutableStateOf(false) }
    var isSearchActiveState by remember { mutableStateOf(false) }

    // Available locations for filter
    val availableLocations =
        remember {
            listOf(
                "All Locations",
                "Hyderabad",
                "Bangalore",
                "Chennai",
                "Vizag",
                "Vijayawada",
                "Guntur",
                "Warangal",
                "Karimnagar",
                "Nizamabad",
            )
        }

    // Debug logging
    Log.d("CommunityScreen", "=== COMMUNITY SCREEN DEBUG ===")
    Log.d("CommunityScreen", "Groups count: ${groups.size}")
    Log.d("CommunityScreen", "Loading: $loading")
    Log.d("CommunityScreen", "Error: $error")
    Log.d("CommunityScreen", "Search query: '$searchQuery'")
    Log.d("CommunityScreen", "Selected location: '$selectedLocation'")
    Log.d("CommunityScreen", "==========================")

    // Create test data if needed
    LaunchedEffect(Unit) {
        Log.d("CommunityScreen", "LaunchedEffect: Loading groups...")
        viewModel.loadGroups()
    }

    // Filter and search logic
    val filteredGroups =
        remember(groups, searchQuery, selectedLocation) {
            val baseGroups = groups

            baseGroups.filter { group ->
                val matchesSearch =
                    if (searchQuery.isBlank()) {
                        true
                    } else {
                        group.name.contains(searchQuery, ignoreCase = true)
                    }

                val matchesLocation =
                    if (selectedLocation == "All Locations") {
                        true
                    } else {
                        group.name.contains(selectedLocation, ignoreCase = true)
                    }

                matchesSearch && matchesLocation
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActiveState) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search communities or users...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors =
                                OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                ),
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                        )
                                    }
                                }
                            },
                        )
                    } else {
                        Text("Community")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isSearchActiveState = !isSearchActiveState
                        if (!isSearchActiveState) searchQuery = ""
                    }) {
                        Icon(
                            if (isSearchActiveState) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = if (isSearchActiveState) "Close search" else "Search",
                        )
                    }

                    IconButton(onClick = { showLocationFilter = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter by location")
                    }

                    TextButton(onClick = onLanguageToggle) {
                        Text(if (isTeluguMode) "English" else "Telugu")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            // Active filters display
            if (selectedLocation != "All Locations" || searchQuery.isNotEmpty()) {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            if (searchQuery.isNotEmpty()) {
                                Text(
                                    text = "Search: \"$searchQuery\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                            if (selectedLocation != "All Locations") {
                                Text(
                                    text = "Location: $selectedLocation",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                searchQuery = ""
                                selectedLocation = "All Locations"
                            },
                        ) {
                            Text("Clear All")
                        }
                    }
                }
            }

            // Results count
            Text(
                text = "${filteredGroups.size} communities found",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    loading -> {
                        Log.d("CommunityScreen", "Showing loading indicator")
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                    filteredGroups.isEmpty() && error != null -> {
                        Log.d("CommunityScreen", "Showing error state")
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = error ?: "An error occurred",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp),
                            )
                            Button(
                                onClick = {
                                    Log.d("CommunityScreen", "Retry button clicked")
                                    viewModel.loadGroups()
                                },
                            ) {
                                Text("Retry")
                            }
                        }
                    }

                    filteredGroups.isEmpty() -> {
                        Log.d("CommunityScreen", "Showing empty state")
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Icon(
                                Icons.Default.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text =
                                    if (searchQuery.isNotEmpty() || selectedLocation != "All Locations") {
                                        "No communities match your search"
                                    } else {
                                        "No community groups available"
                                    },
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            if (searchQuery.isNotEmpty() || selectedLocation != "All Locations") {
                                TextButton(
                                    onClick = {
                                        searchQuery = ""
                                        selectedLocation = "All Locations"
                                    },
                                ) {
                                    Text("Clear filters")
                                }
                            }
                        }
                    }

                    else -> {
                        Log.d("CommunityScreen", "Showing ${filteredGroups.size} groups")
                        LazyColumn(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(filteredGroups) { group ->
                                EnhancedCommunityGroupItem(
                                    group = group,
                                    searchQuery = searchQuery,
                                    onClick = {
                                        Log.d("CommunityScreen", "Group clicked: ${group.name}")
                                        navController.navigate("chat/${group.id}")
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        // Location filter dialog
        if (showLocationFilter) {
            AlertDialog(
                onDismissRequest = { showLocationFilter = false },
                title = { Text("Filter by Location") },
                text = {
                    LazyColumn {
                        items(availableLocations) { location ->
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedLocation = location
                                            showLocationFilter = false
                                        }
                                        .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                RadioButton(
                                    selected = selectedLocation == location,
                                    onClick = {
                                        selectedLocation = location
                                        showLocationFilter = false
                                    },
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = location,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLocationFilter = false }) {
                        Text("Close")
                    }
                },
            )
        }
    }
}

@Composable
private fun EnhancedCommunityGroupItem(
    group: CommunityGroup,
    searchQuery: String,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Group avatar/icon
            Card(
                modifier = Modifier.size(48.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Default.People,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${group.memberCount} members",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Open group",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
