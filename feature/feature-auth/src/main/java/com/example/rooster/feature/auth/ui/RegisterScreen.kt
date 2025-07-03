package com.example.rooster.feature.auth.ui

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
fun RegisterScreen(
    onNavigateBack: () -> Unit // Example navigation callback
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Register") }) // Placeholder title
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = sixteenDp), // Assuming sixteenDp is defined in Dimens or similar
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register Screen Placeholder", // Placeholder text
                style = MaterialTheme.typography.headlineMedium
            )
            // TODO: Implement actual registration form fields (name, email, password, role, etc.)
            // TODO: Add Register button and logic
            // TODO: Add navigation to Login or back
        }
    }
}

// A common dimension value, assuming it would be in a Dimens.kt or similar
// For now, defined locally for the placeholder.
private val sixteenDp = androidx.compose.ui.unit.dp.constructor_impl(16.0f)

@Preview(showBackground = true, name = "Register Screen Light")
@Composable
fun RegisterScreenPreviewLight() {
    RoosterTheme(darkTheme = false) {
        RegisterScreen(onNavigateBack = {})
    }
}

@Preview(showBackground = true, name = "Register Screen Dark")
@Composable
fun RegisterScreenPreviewDark() {
    RoosterTheme(darkTheme = true) {
        RegisterScreen(onNavigateBack = {})
    }
}
