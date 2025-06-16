package com.example.rooster

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rooster.viewmodels.MarketplaceFilters
import com.example.rooster.viewmodels.MarketplaceViewModel

@Composable
fun TraceabilitySection(
    listing: SafeListing,
    isTeluguMode: Boolean,
) {
    Column(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
        Text(
            text = "Lineage Information",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text("Father ID: ${listing.fatherId}")
        Text("Mother ID: ${listing.motherId}")

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vaccination History",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        if (listing.vaccinations.isNotEmpty()) {
            listing.vaccinations.forEach { vaccination ->
                Text("- $vaccination")
            }
        } else {
            Text(if (isTeluguMode) "వాక్సినేషన్ సమాచారం లేదు" else "No vaccination information")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Growth Updates",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        if (listing.growthUpdates.isNotEmpty()) {
            listing.growthUpdates.forEach { update ->
                Text("- ${if (isTeluguMode) "వారం" else "Week"} ${update.week}: ${update.weight} ${if (isTeluguMode) "కేజీ" else "kg"}")
            }
        } else {
            Text(if (isTeluguMode) "వృద్ధి సమాచారం లేదు" else "No growth information")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Breeder Status: ${if (listing.isBreeder) (if (isTeluguMode) "అవును" else "Yes") else (if (isTeluguMode) "కాదు" else "No")}",
            style = MaterialTheme.typography.bodyMedium,
        )

        if (listing.isBloodlineVerified) {
            Text(
                text = "Verified Bloodline",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    navController: NavController,
    isTeluguMode: Boolean,
    onLanguageToggle: () -> Unit,
    viewModel: MarketplaceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val context = LocalContext.current

    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val listings by viewModel.listings.collectAsState()
    val filters by viewModel.filters.collectAsState()

    // Set context for ViewModel
    LaunchedEffect(context) {
        viewModel.setContext(context)
    }

    // Tab selection
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val tabs =
        listOf(
            if (isTeluguMode) "సాంప్రదాయ మార్కెట్లు" else "Marketplace",
            if (isTeluguMode) "ముందస్తు ఆర్డర్లు" else "Pre-Orders",
            if (isTeluguMode) "గుంపు కొనుగోలు" else "Group Buying",
            if (isTeluguMode) "మార్కెట్ ట్రెండ్స్" else "Market Trends",
            if (isTeluguMode) "వేలాలు" else "Auctions",
        )

    // Filter state
    var showFilter by remember { mutableStateOf(false) }
    var verifiedOnly by rememberSaveable { mutableStateOf(false) }
    var bloodlineOnly by rememberSaveable { mutableStateOf(false) }
    var selectedGender by rememberSaveable { mutableStateOf("Any") }
    var selectedAgeGroup by rememberSaveable { mutableStateOf("All") }
    var selectedBreed by rememberSaveable { mutableStateOf("All") }
    var priceRange by remember { mutableStateOf(0f..5000f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isTeluguMode) "మార్కెట్‌ప్లేస్" else "Marketplace",
                        fontWeight = FontWeight.Bold,
                    )
                },
                actions = {
                    IconButton(onClick = onLanguageToggle) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = "Toggle Language",
                            tint = Color(0xFFFF5722),
                        )
                    }
                    // Add refresh button
                    IconButton(onClick = { viewModel.refreshListings() }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = if (uiState.isLoading) Color.Gray else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    // Add debug menu
                    var showDebugMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showDebugMenu = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Debug",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }

                    // Debug dropdown menu
                    DropdownMenu(
                        expanded = showDebugMenu,
                        onDismissRequest = { showDebugMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Test Parse Connection") },
                            onClick = {
                                showDebugMenu = false
                                viewModel.testParseConnection()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Force Load Mock Data") },
                            onClick = {
                                showDebugMenu = false
                                viewModel.forceLoadMockData()
                            },
                        )
                        DropdownMenuItem(
                            text = { Text("Check Logs") },
                            onClick = {
                                showDebugMenu = false
                                android.util.Log.d(
                                    "MarketplaceScreen",
                                    "Current listings count: ${listings.size}",
                                )
                                android.util.Log.d(
                                    "MarketplaceScreen",
                                    "Is loading: ${uiState.isLoading}",
                                )
                                android.util.Log.d("MarketplaceScreen", "Error: ${uiState.error}")
                            },
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End,
            ) {
                // Create Auction FAB
                if (selectedTab == 4) { // Auctions tab
                    FloatingActionButton(
                        onClick = {
                            navController.navigate("create_auction/default_fowl")
                        },
                        containerColor = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            Icons.Default.Gavel,
                            contentDescription = if (isTeluguMode) "కొత్త వేలం" else "Create Auction",
                            tint = Color.White,
                        )
                    }
                }

                // Create Listing FAB
                FloatingActionButton(
                    onClick = {
                        navController.navigate("create_listing")
                    },
                    containerColor = Color(0xFFFF5722),
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = if (isTeluguMode) "కొత్త లిస్టింగ్" else "Create Listing",
                        tint = Color.White,
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
            ) {
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) },
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    modifier = Modifier.clickable { showFilter = true },
                )
            }

            // Show error if any
            uiState.error?.let { error ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            if (showFilter) {
                // Simple filter panel
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(MaterialTheme.colorScheme.surface),
                ) {
                    Text("Filters", style = MaterialTheme.typography.headlineSmall)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = verifiedOnly,
                            onCheckedChange = {
                                verifiedOnly = it
                                viewModel.toggleVerifiedOnly()
                            },
                        )
                        Text("Verified only")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = bloodlineOnly,
                            onCheckedChange = {
                                bloodlineOnly = it
                                viewModel.toggleBloodlineOnly()
                            },
                        )
                        Text("Has bloodline")
                    }
                    Text("Price range: ₹${priceRange.start.toInt()} - ₹${priceRange.endInclusive.toInt()}")
                    Slider(
                        value = priceRange.start,
                        onValueChange = { newValueStart ->
                            priceRange = newValueStart..priceRange.endInclusive
                            viewModel.filterByPriceRange(newValueStart.toDouble(), priceRange.endInclusive.toDouble())
                        },
                        valueRange = 0f..5000f,
                    )

                    // Gender Filter
                    var genderExpanded by remember { mutableStateOf(false) }
                    val genderOptions = listOf("Any", "Male", "Female", "Mixed")

                    Column {
                        Text(
                            text = "Gender",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                        ExposedDropdownMenuBox(
                            expanded = genderExpanded,
                            onExpandedChange = { genderExpanded = !genderExpanded },
                        ) {
                            OutlinedTextField(
                                value = selectedGender,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Select Gender") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderExpanded) },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            )
                            ExposedDropdownMenu(
                                expanded = genderExpanded,
                                onDismissRequest = { genderExpanded = false },
                            ) {
                                genderOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedGender = option
                                            genderExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // Age Group Filter
                    var ageGroupExpanded by remember { mutableStateOf(false) }
                    val ageGroupOptions =
                        listOf(
                            "All",
                            "Chicks (0-8 weeks)",
                            "Young (8-20 weeks)",
                            "Adult (20+ weeks)",
                            "Breeder Age (35+ weeks)",
                        )

                    Column {
                        Text(
                            text = "Age Group",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                        ExposedDropdownMenuBox(
                            expanded = ageGroupExpanded,
                            onExpandedChange = { ageGroupExpanded = !ageGroupExpanded },
                        ) {
                            OutlinedTextField(
                                value = selectedAgeGroup,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Select Age Group") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ageGroupExpanded) },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            )
                            ExposedDropdownMenu(
                                expanded = ageGroupExpanded,
                                onDismissRequest = { ageGroupExpanded = false },
                            ) {
                                ageGroupOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedAgeGroup = option
                                            ageGroupExpanded = false
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // Breed Filter
                    var breedExpanded by remember { mutableStateOf(false) }
                    val breedOptions =
                        listOf(
                            "All",
                            "Kadaknath",
                            "Aseel",
                            "Brahma",
                            "Rhode Island Red",
                            "Leghorn",
                            "Cochin",
                            "Desi",
                            "Country chickens",
                            "Broiler",
                            "Mixed Breed",
                        )

                    Column {
                        Text(
                            text = "Breed",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                        )
                        ExposedDropdownMenuBox(
                            expanded = breedExpanded,
                            onExpandedChange = { breedExpanded = !breedExpanded },
                        ) {
                            OutlinedTextField(
                                value = selectedBreed,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Select Breed") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = breedExpanded) },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            )
                            ExposedDropdownMenu(
                                expanded = breedExpanded,
                                onDismissRequest = { breedExpanded = false },
                            ) {
                                breedOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedBreed = option
                                            breedExpanded = false
                                            viewModel.filterByBreed(option)
                                        },
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedButton(
                            onClick = {
                                // Reset all filters
                                verifiedOnly = false
                                bloodlineOnly = false
                                selectedGender = "Any"
                                selectedAgeGroup = "All"
                                selectedBreed = "All"
                                priceRange = 0f..5000f
                                viewModel.updateFilters(MarketplaceFilters())
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Reset")
                        }

                        Button(
                            onClick = {
                                // Apply filters logic here
                                showFilter = false
                            },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Apply Filters")
                        }
                    }
                }
            }
            // contents based on selected tab,
            when (selectedTab) {
                0 ->
                    TraditionalMarketsTab(
                        isTeluguMode = isTeluguMode,
                        navController = navController,
                        listings = listings,
                        isLoading = uiState.isLoading,
                    )
                1 -> PreOrdersTab()
                2 -> GroupBuyingTab()
                3 -> MarketTrendsTab()
                4 -> com.example.rooster.ui.auction.AuctionListScreen(navController = navController)
            }
        }
    }
}

@Composable
fun SimpleSafeListingCard(
    listing: SafeListing,
    isTeluguMode: Boolean,
    navController: NavController,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Listing Image
            if (listing.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = listing.imageUrl,
                    contentDescription = "Fowl Image",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                )
            } else {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isTeluguMode) "చిత్రం లేదు" else "No Image",
                        color = Color.Gray,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            // Listing Details
            Text(
                text = listing.breed.ifEmpty { if (isTeluguMode) "జాతి తెలియదు" else "Breed Unknown" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = if (isTeluguMode) "వయస్సు: ${listing.age} వారాలు" else "Age: ${listing.age} weeks",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )

            Text(
                text = if (isTeluguMode) "ధర: ₹${listing.price}" else "Price: ₹${listing.price}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFF5722),
            )

            Text(
                text = if (isTeluguMode) "విక్రేత: ${listing.owner}" else "Seller: ${listing.owner}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )

            // Verified Bloodline Badge
            if (listing.isBloodlineVerified) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.verified_bloodline),
                    color = MaterialTheme.colorScheme.primary, // Use M3 colorScheme
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            TraceabilitySection(listing = listing, isTeluguMode = isTeluguMode)

            Spacer(modifier = Modifier.height(16.dp))

            // Buy Now Button
            Button(
                onClick = {
                    navController.navigate("payment/${listing.id}")
                },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5722),
                    ),
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "కొనుగోలు చేయండి" else "Buy Now",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bidding Button
            OutlinedButton(
                onClick = {
                    navController.navigate("bidding/${listing.id}")
                },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF5722),
                    ),
            ) {
                Icon(
                    Icons.Default.Gavel,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTeluguMode) "వేలం వేయండి" else "Place Bid",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// Mock data for demonstration
fun getMockListings(): List<SafeListing> {
    return listOf(
        SafeListing(
            id = "1",
            imageUrl = "", // Add a placeholder image URL if desired
            breed = "Kadaknath Rooster",
            age = 12,
            price = 2500.0,
            owner = "రాము గారు",
            sellerId = "seller1",
            fatherId = "KDF001",
            motherId = "KDM002",
            vaccinations = listOf("Mareks (Day 1)", "Ranikhet (Week 1)", "IBD (Week 2)"),
            growthUpdates =
                listOf(
                    GrowthUpdate(week = 4, weight = 0.5),
                    GrowthUpdate(week = 8, weight = 1.2),
                    GrowthUpdate(week = 12, weight = 1.8),
                ),
            isBreeder = true,
            isBloodlineVerified = true,
        ),
        SafeListing(
            id = "2",
            imageUrl = "",
            breed = "Aseel Hen",
            age = 8,
            price = 1800.0,
            owner = "సీత గారు",
            sellerId = "seller2",
            fatherId = "ASF003",
            motherId = "ASM004",
            vaccinations = listOf("Mareks (Day 1)", "Ranikhet (Week 1)"),
            growthUpdates =
                listOf(
                    GrowthUpdate(week = 4, weight = 0.4),
                    GrowthUpdate(week = 8, weight = 1.0),
                ),
            isBreeder = false,
            isBloodlineVerified = true,
        ),
        SafeListing(
            id = "3",
            imageUrl = "",
            breed = "Brahma Chicks",
            age = 4,
            price = 300.0,
            owner = "కృష్ణ గారు",
            sellerId = "seller3",
            // No traceability for chicks yet, or use N/A
            fatherId = "BRF005",
            motherId = "BRM006",
            vaccinations = listOf("Mareks (Day 1)"),
            growthUpdates = listOf(GrowthUpdate(week = 4, weight = 0.3)),
            isBreeder = false,
            isBloodlineVerified = false,
        ),
        SafeListing(
            id = "4",
            imageUrl = "",
            breed = "Country Chicken",
            age = 16,
            price = 1200.0,
            owner = "వెంకట్ రెడ్డి",
            sellerId = "seller4",
            fatherId = "CCF007",
            motherId = "CCM008",
            vaccinations = listOf("Newcastle", "IBD", "Fowl Pox"),
            growthUpdates =
                listOf(
                    GrowthUpdate(week = 8, weight = 1.0),
                    GrowthUpdate(week = 12, weight = 1.5),
                    GrowthUpdate(week = 16, weight = 2.0),
                ),
            isBreeder = true,
            isBloodlineVerified = false,
        ),
        SafeListing(
            id = "5",
            imageUrl = "",
            breed = "Rhode Island Red",
            age = 20,
            price = 2800.0,
            owner = "లక్ష్మి దేవి",
            sellerId = "seller5",
            fatherId = "RIR009",
            motherId = "RIR010",
            vaccinations =
                listOf(
                    "Mareks",
                    "Newcastle",
                    "IBD",
                    "Fowl Pox",
                    "Infectious Bronchitis",
                ),
            growthUpdates =
                listOf(
                    GrowthUpdate(week = 8, weight = 1.1),
                    GrowthUpdate(week = 12, weight = 1.6),
                    GrowthUpdate(week = 16, weight = 2.1),
                    GrowthUpdate(week = 20, weight = 2.5),
                ),
            isBreeder = true,
            isBloodlineVerified = true,
        ),
    )
}

// -- Stub composables for phase 3 filtering
@Composable
fun MeatListingList(
    verifiedOnly: Boolean,
    bloodlineOnly: Boolean,
    priceRange: ClosedFloatingPointRange<Float>,
    gender: String,
    ageGroup: String,
    breed: String,
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    val meatListings =
        remember {
            listOf(
                SafeListing(
                    id = "meat1",
                    breed = "Broiler Chicken",
                    age = 6,
                    price = 150.0,
                    owner = "Local Farm",
                    sellerId = "farm001",
                    fatherId = "N/A",
                    motherId = "N/A",
                    vaccinations = listOf("Newcastle", "IBD"),
                    isBreeder = false,
                    isBloodlineVerified = false,
                ),
                SafeListing(
                    id = "meat2",
                    breed = "Country chickens",
                    age = 16,
                    price = 350.0,
                    owner = "Village Coop",
                    sellerId = "coop001",
                    fatherId = "N/A",
                    motherId = "N/A",
                    vaccinations = listOf("Ranikhet", "Fowl Pox"),
                    isBreeder = false,
                    isBloodlineVerified = false,
                ),
            )
        }

    LazyColumn {
        items(meatListings.size) { index ->
            val listing = meatListings[index]
            SimpleSafeListingCard(
                listing = listing,
                isTeluguMode = isTeluguMode,
                navController = navController,
            )
        }
    }
}

@Composable
fun TraceableAdoptionList(
    verifiedOnly: Boolean,
    bloodlineOnly: Boolean,
    priceRange: ClosedFloatingPointRange<Float>,
    gender: String,
    ageGroup: String,
    breed: String,
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    val traceableListings =
        remember {
            listOf(
                SafeListing(
                    id = "trace1",
                    breed = "Pure Aseel",
                    age = 8,
                    price = 2500.0,
                    owner = "Heritage Breeder",
                    sellerId = "heritage001",
                    fatherId = "AS001",
                    motherId = "AS002",
                    vaccinations = listOf("Mareks", "Newcastle", "IBD"),
                    growthUpdates =
                        listOf(
                            GrowthUpdate(4, 0.4),
                            GrowthUpdate(6, 0.7),
                            GrowthUpdate(8, 1.1),
                        ),
                    isBreeder = true,
                    isBloodlineVerified = true,
                ),
                SafeListing(
                    id = "trace2",
                    breed = "Kadaknath",
                    age = 12,
                    price = 3500.0,
                    owner = "Premium Genetics",
                    sellerId = "premium001",
                    fatherId = "KD003",
                    motherId = "KD004",
                    vaccinations = listOf("Mareks", "Newcastle", "IBD", "Fowl Pox"),
                    growthUpdates =
                        listOf(
                            GrowthUpdate(4, 0.5),
                            GrowthUpdate(8, 1.0),
                            GrowthUpdate(12, 1.6),
                        ),
                    isBreeder = true,
                    isBloodlineVerified = true,
                ),
            )
        }

    LazyColumn {
        items(traceableListings.size) { index ->
            val listing = traceableListings[index]
            SimpleSafeListingCard(
                listing = listing,
                isTeluguMode = isTeluguMode,
                navController = navController,
            )
        }
    }
}

@Composable
fun NonTraceableAdoptionList(
    verifiedOnly: Boolean,
    bloodlineOnly: Boolean,
    priceRange: ClosedFloatingPointRange<Float>,
    gender: String,
    ageGroup: String,
    breed: String,
    navController: NavController,
    isTeluguMode: Boolean = false,
) {
    val nonTraceableListings =
        remember {
            listOf(
                SafeListing(
                    id = "non_trace1",
                    breed = "Mixed Breed",
                    age = 4,
                    price = 800.0,
                    owner = "Backyard Farmer",
                    sellerId = "backyard001",
                    fatherId = "Unknown",
                    motherId = "Unknown",
                    vaccinations = listOf("Basic Vaccines"),
                    isBreeder = false,
                    isBloodlineVerified = false,
                ),
                SafeListing(
                    id = "non_trace2",
                    breed = "Local Rooster",
                    age = 10,
                    price = 1200.0,
                    owner = "Rural Seller",
                    sellerId = "rural001",
                    fatherId = "Unknown",
                    motherId = "Unknown",
                    vaccinations = listOf("Newcastle"),
                    isBreeder = false,
                    isBloodlineVerified = false,
                ),
            )
        }

    LazyColumn {
        items(nonTraceableListings.size) { index ->
            val listing = nonTraceableListings[index]
            SimpleSafeListingCard(
                listing = listing,
                isTeluguMode = isTeluguMode,
                navController = navController,
            )
        }
    }
}

@Composable
fun TraditionalMarketsTab(
    isTeluguMode: Boolean = false,
    navController: NavController,
    listings: List<SafeListing>,
    isLoading: Boolean,
) {
    // Use our new Enhanced Marketplace implementation with ProductListItem
    var favoriteItems by remember { mutableStateOf(setOf<String>()) }

    // Add debugging logs
    LaunchedEffect(listings) {
        android.util.Log.d("TraditionalMarketsTab", "Listings updated: ${listings.size} items")
        listings.forEachIndexed { index, listing ->
            android.util.Log.d(
                "TraditionalMarketsTab",
                "Listing $index: ${listing.breed} - ${listing.owner}",
            )
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (isLoading) {
            item {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isTeluguMode) "లోడ్ అవుతోంది..." else "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else if (listings.isEmpty()) {
            item {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isTeluguMode) "ఎటువంటి లిస్టింగ్‌లు కనుగొనబడలేదు" else "No listings found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isTeluguMode) "కొత్త లిస్టింగ్ సృష్టించడానికి + బటన్‌ను నొక్కండి" else "Tap the + button to create a new listing",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            item {
                // Debug information card
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        ),
                ) {
                    Text(
                        text = "📊 Debug: Found ${listings.size} listings",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }

            items(listings) { listing ->
                com.example.rooster.ui.components.ProductListItem(
                    listing = listing,
                    onClick = { navController.navigate("payment/${listing.id}") },
                    onFavoriteClick = { isFavorited ->
                        favoriteItems =
                            if (isFavorited) {
                                favoriteItems + listing.id
                            } else {
                                favoriteItems - listing.id
                            }
                    },
                    onShareClick = {
                        // TODO: Implement share functionality
                        android.util.Log.d("Marketplace", "Share product: ${listing.id}")
                    },
                    isTeluguMode = isTeluguMode,
                    isFavorited = favoriteItems.contains(listing.id),
                    showFullDetails = true,
                )
            }
        }
    }
}

@Composable
fun PreOrdersTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text("[Pre-Orders] - Coming Soon")
    }
}

@Composable
fun GroupBuyingTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text("[Group Buying] - Coming Soon")
    }
}

@Composable
fun MarketTrendsTab() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text("[Market Trends] - Coming Soon")
    }
}
