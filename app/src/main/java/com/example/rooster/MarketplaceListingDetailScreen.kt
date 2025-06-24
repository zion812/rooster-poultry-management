package com.example.rooster

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.rooster.ui.components.StandardScreenLayout
import com.example.rooster.viewmodels.MarketplaceViewModel

@Composable
fun MarketplaceListingDetailScreen(
    navController: NavController,
    listingId: String,
    isTeluguMode: Boolean,
    onBack: () -> Unit = { navController.popBackStack() },
    viewModel: MarketplaceViewModel = hiltViewModel(),
) {
    val listing by viewModel.getListingById(listingId).collectAsState()

    StandardScreenLayout(
        horizontalAlignment = Alignment.CenterHorizontally,
        scrollable = true,
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (isTeluguMode) "లిస్టింగ్ వివరాలు" else "Listing Details",
                    fontWeight = FontWeight.Bold,
                )
            },
            navigationIcon = {
                Button(onClick = onBack) {
                    Text(if (isTeluguMode) "వెనుక" else "Back")
                }
            },
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (listing) {
            null -> {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
                Text(text = if (isTeluguMode) "లిస్టింగ్ లోడ్ అవుతోంది..." else "Loading listing...")
            }
            else -> {
                AsyncImage(
                    model = listing?.imageUrl,
                    contentDescription = if (isTeluguMode) "లిస్టింగ్ చిత్రం" else "Listing image",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = listing?.title ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isTeluguMode) "ధర: ₹${listing?.price}" else "Price: ₹${listing?.price}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = listing?.description ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { /* TODO: Implement Buy action */ }) {
                            Text(text = if (isTeluguMode) "తేలు కొనండి" else "Buy Now")
                        }
                        Button(onClick = { /* TODO: Implement Bid action */ }) {
                            Text(text = if (isTeluguMode) "బిడ్ ఉంచండి" else "Place Bid")
                        }
                    }
                }
            }
        }
    }
}
