package com.example.rooster.feature.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.rooster.ui.theme.RoosterTheme
// Assuming a generic R for placeholder strings, actual one later
// import com.example.rooster.app.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    // Typically receives NavController or navigation lambdas
    onNavigateToProfile: () -> Unit // Example navigation callback
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home Dashboard") }) // Placeholder title
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = sixteenDp), // Assuming sixteenDp is defined
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Home Screen Placeholder", // Placeholder text
                style = MaterialTheme.typography.headlineMedium
            )
            // TODO: Implement role-based dashboard content
            // TODO: Display key metrics, summaries, quick actions
            // TODO: Add navigation to other features (e.g., Profile, Farm Management, Marketplace)
        }
    }
}

// A common dimension value, assuming it would be in a Dimens.kt or similar
// For now, defined locally for the placeholder.
private val sixteenDp = androidx.compose.ui.unit.dp.constructor_impl(16.0f)


@Preview(showBackground = true, name = "Home Screen Light")
@Composable
fun HomeScreenPreviewLight() {
    RoosterTheme(darkTheme = false) {
        HomeScreen(onNavigateToProfile = {})
    }
}

@Preview(showBackground = true, name = "Home Screen Dark")
@Composable
fun HomeScreenPreviewDark() {
    RoosterTheme(darkTheme = true) {
        HomeScreen(onNavigateToProfile = {})
    }
}
