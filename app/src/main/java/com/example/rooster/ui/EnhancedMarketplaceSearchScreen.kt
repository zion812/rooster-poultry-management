@file:Suppress("ktlint:standard:no-wildcard-imports")

package com.example.rooster.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.rooster.services.localization.AgriculturalContext
import com.example.rooster.services.optimized.SearchResult
import com.example.rooster.viewmodel.SearchState
import com.example.rooster.viewmodel.VoiceSearchState
import com.example.rooster.viewmodel.VoiceSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedMarketplaceSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: VoiceSearchViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val voiceSearchState by viewModel.voiceSearchState.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val selectedContext by viewModel.selectedContext.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isVoiceSearchActive by remember { mutableStateOf(false) }
    var selectedLanguage by remember { mutableStateOf("te") }
    var showDistrictDropdown by remember { mutableStateOf(false) }

    // Voice recording permission
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            if (isGranted) {
                viewModel.startVoiceSearch(selectedLanguage)
                isVoiceSearchActive = true
            }
        }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (selectedLanguage == "te") "మార్కెట్‌ప్లేస్ వెతుకులు" else "Marketplace Search",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (selectedLanguage == "te") "వాయిస్ మరియు టెక్స్ట్ సెర్చ్ - స్థానీయ రైతుల కోసం" else "Voice & Text Search - For Local Farmers",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                )
            }
        }

        // Language Toggle and District Filter
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Language Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (selectedLanguage == "te") "భాష:" else "Language:",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            onClick = { selectedLanguage = "te" },
                            label = { Text("తెలుగు") },
                            selected = selectedLanguage == "te",
                        )
                        FilterChip(
                            onClick = { selectedLanguage = "en" },
                            label = { Text("English") },
                            selected = selectedLanguage == "en",
                        )
                    }
                }

                // District Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (selectedLanguage == "te") "జిల్లా:" else "District:",
                        style = MaterialTheme.typography.titleMedium,
                    )

                    Box {
                        TextButton(
                            onClick = { showDistrictDropdown = true }
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Text(
                                text = selectedDistrict
                                    ?: if (selectedLanguage == "te") "జిల్లా ఎంచుకోండి" else "Select District",
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showDistrictDropdown,
                            onDismissRequest = { showDistrictDropdown = false }
                        ) {
                            // All districts option
                            DropdownMenuItem(
                                text = {
                                    Text(if (selectedLanguage == "te") "అన్ని జిల్లాలు" else "All Districts")
                                },
                                onClick = {
                                    viewModel.setSelectedDistrict(null)
                                    showDistrictDropdown = false
                                }
                            )

                            // Telugu districts
                            viewModel.getTeluguDistricts().forEach { district ->
                                DropdownMenuItem(
                                    text = { Text(district) },
                                    onClick = {
                                        viewModel.setSelectedDistrict(district)
                                        showDistrictDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Show selected district
                if (selectedDistrict != null) {
                    AssistChip(
                        onClick = { viewModel.setSelectedDistrict(null) },
                        label = {
                            Text("${selectedDistrict} ✕")
                        }
                    )
                }
            }
        }

        // Search Input with Voice
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.updateSearchQuery(it, selectedLanguage)
                    },
                    label = {
                        Text(
                            if (selectedLanguage == "te")
                                "కోడిలు, మేత, వ్యాధులు వెతకండి..."
                            else
                                "Search chickens, feed, diseases..."
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (isVoiceSearchActive) {
                                    viewModel.stopVoiceSearch()
                                    isVoiceSearchActive = false
                                } else {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.RECORD_AUDIO,
                                        ) == PackageManager.PERMISSION_GRANTED
                                    ) {
                                        viewModel.startVoiceSearch(selectedLanguage)
                                        isVoiceSearchActive = true
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                        ) {
                            Icon(
                                if (isVoiceSearchActive) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = "Voice Search",
                                tint = if (isVoiceSearchActive) Color.Red else MaterialTheme.colorScheme.primary,
                            )
                        }
                    },
                )

                // Voice Search Status
                when (voiceSearchState) {
                    is VoiceSearchState.Recording -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Icon(
                                    Icons.Default.Mic,
                                    contentDescription = null,
                                    tint = Color.Red,
                                )
                                Text(
                                    text = if (selectedLanguage == "te") "వింటోంది... మాట్లాడండి" else "Listening... Speak now",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }

                    is VoiceSearchState.Error -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        ) {
                            Text(
                                text = (voiceSearchState as VoiceSearchState.Error).message,
                                modifier = Modifier.padding(12.dp),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        }
                    }
                    else -> {}
                }

                // Predictive Suggestions
                if (suggestions.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(suggestions) { suggestion ->
                            SuggestionChip(
                                onClick = {
                                    searchQuery = suggestion
                                    viewModel.executeSemanticSearch(suggestion, selectedLanguage)
                                },
                                label = { Text(suggestion) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }

        // Search Context Options
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = if (selectedLanguage == "te") "వెతుకులు రకం:" else "Search Context:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(AgriculturalContext.values()) { context ->
                        val contextName =
                            when (context) {
                                AgriculturalContext.GENERAL -> if (selectedLanguage == "te") "సాధారణ" else "General"
                                AgriculturalContext.AUCTION -> if (selectedLanguage == "te") "వేలం" else "Auction"
                                AgriculturalContext.HEALTH -> if (selectedLanguage == "te") "ఆరోగ్యం" else "Health"
                                AgriculturalContext.FEED -> if (selectedLanguage == "te") "మేత" else "Feed"
                                AgriculturalContext.BREEDING -> if (selectedLanguage == "te") "సంతానోత్పత్సి" else "Breeding"
                            }

                        FilterChip(
                            onClick = { viewModel.setSearchContext(context) },
                            label = { Text(contextName) },
                            selected = selectedContext == context,
                        )
                    }
                }
            }
        }

        // Search Results and Recommendations
        when (val state = searchState) {
            is SearchState.Loading -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(
                                text = if (selectedLanguage == "te") "వెతుకుతోంది..." else "Searching...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            is SearchState.Success -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Search Results
                    items(state.results) { result ->
                        SearchResultCard(
                            result = result,
                            language = selectedLanguage,
                            onItemClick = { viewModel.onSearchResultClick(result) },
                        )
                    }

                    // AI Recommendations based on district and context
                    if (state.results.isNotEmpty()) {
                        item {
                            Text(
                                text = if (selectedLanguage == "te")
                                    "మీ కోసం సిఫారసు చేసిన జాబితాలు"
                                else
                                    "Recommended for You",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(viewModel.getRecommendations()) { recommendation ->
                            SearchResultCard(
                                result = recommendation,
                                language = selectedLanguage,
                                onItemClick = { viewModel.onSearchResultClick(recommendation) },
                                isRecommendation = true
                            )
                        }
                    }
                }
            }

            is SearchState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                ) {
                    Text(
                        text = state.message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            SearchState.Idle -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = if (selectedLanguage == "te")
                                "కోడిలు, మేత, వ్యాధుల గురించి వెతకండి"
                            else
                                "Search for chickens, feed, diseases",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                        )
                        Text(
                            text = if (selectedLanguage == "te")
                                "వాయిస్ లేదా టెక్స్ట్ ఉపయోగించండి • జిల్లా ఎంచుకోండి"
                            else
                                "Use voice or text search • Select your district",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        )

                        // Show popular searches for the selected district
                        if (selectedDistrict != null) {
                            Text(
                                text = if (selectedLanguage == "te")
                                    "$selectedDistrict లో ప్రసిద్ధ వెతుకులు:"
                                else
                                    "Popular searches in $selectedDistrict:",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                val popularSearches = if (selectedLanguage == "te") {
                                    listOf("నాట్టు కోడి", "మేత ధరలు", "వ్యాధుల చికిత్స")
                                } else {
                                    listOf("Desi Chicken", "Feed Prices", "Disease Treatment")
                                }

                                popularSearches.forEach { search ->
                                    AssistChip(
                                        onClick = {
                                            searchQuery = search
                                            viewModel.executeSemanticSearch(
                                                search,
                                                selectedLanguage
                                            )
                                        },
                                        label = {
                                            Text(
                                                search,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Handle voice search results
    LaunchedEffect(voiceSearchState) {
        when (val currentState = voiceSearchState) {
            is VoiceSearchState.Success -> {
                searchQuery = currentState.transcript
                viewModel.executeSemanticSearch(currentState.transcript, selectedLanguage)
                isVoiceSearchActive = false
            }
            is VoiceSearchState.Error -> {
                isVoiceSearchActive = false
            }
            else -> { /* No action needed */ }
        }
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    language: String,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRecommendation: Boolean = false,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth(),
        onClick = onItemClick,
        colors = if (isRecommendation) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isRecommendation) {
                        Text(
                            text = if (language == "te") "సిఫారసు" else "Rec",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Text(
                        text = "${(result.relevanceScore * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Text(
                text = result.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            )

            // Tags
            if (result.tags.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    items(result.tags.chunked(3)) { tagGroup ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            tagGroup.forEach { tag ->
                                AssistChip(
                                    onClick = { },
                                    label = {
                                        Text(
                                            text = tag,
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
