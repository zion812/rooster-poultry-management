package com.rooster.buyerhome.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rooster.buyerhome.domain.model.MarketplaceRecommendationItem
import com.rooster.core.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter // Correct import for Coil
import com.example.rooster.core.ui.R as CoreUiR // Assuming a common drawable resource ID

@Composable
fun MarketplaceRecommendationItemCard(item: MarketplaceRecommendationItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!item.imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = item.imageUrl,
                        // It's good practice to define a shared placeholder in core.ui.R or similar
                        placeholder = rememberAsyncImagePainter(CoreUiR.drawable.ic_placeholder_image), // Replace with actual placeholder
                        error = rememberAsyncImagePainter(CoreUiR.drawable.ic_broken_image) // Replace with actual error image
                    ),
                    contentDescription = stringResource(R.string.product_image_content_description, item.productName), // Generic description
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp), // Adjust height as needed
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(item.productName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                stringResource(R.string.recommendation_seller_prefix) + " ${item.sellerName} ${item.location?.let { "($it)" } ?: ""}", // TODO: Define R.string.recommendation_seller_prefix
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.recommendation_price_prefix) + " ${item.price}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary) // TODO: Define R.string.recommendation_price_prefix
        }
    }
}
