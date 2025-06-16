package com.example.rooster.test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rooster.SafeListing
import com.example.rooster.ui.components.ProductListItem

/**
 * Simple test to verify our UI components work
 */
@Composable
fun ComponentTest() {
    val testListing =
        SafeListing(
            id = "test1",
            breed = "Test Chicken",
            age = 10,
            price = 1500.0,
            owner = "Test Farmer",
            sellerId = "test_seller",
            fatherId = "TF001",
            motherId = "TM001",
            vaccinations = listOf("Test Vaccine"),
            isBreeder = true,
            isBloodlineVerified = true,
            imageUrl = "",
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text(
            text = "UI Component Test",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        ProductListItem(
            listing = testListing,
            onClick = { /* Test click */ },
            onFavoriteClick = { /* Test favorite */ },
            onShareClick = { /* Test share */ },
            isTeluguMode = false,
            isFavorited = false,
            showFullDetails = true,
        )
    }
}
