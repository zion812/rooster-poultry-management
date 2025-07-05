package com.rooster.buyerhome.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.rooster.buyerhome.domain.model.SupplierRatingInfo
import com.rooster.core.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import coil.compose.rememberAsyncImagePainter
import com.example.rooster.core.ui.R as CoreUiR // Assuming a common drawable resource ID

@Composable
fun SupplierRatingItemCard(supplier: SupplierRatingInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!supplier.profileImageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = supplier.profileImageUrl,
                        placeholder = rememberAsyncImagePainter(CoreUiR.drawable.ic_placeholder_profile), // Replace with actual placeholder
                        error = rememberAsyncImagePainter(CoreUiR.drawable.ic_broken_image) // Replace with actual error image
                    ),
                    contentDescription = stringResource(R.string.supplier_profile_image_desc, supplier.supplierName), // More specific desc
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(supplier.supplierName, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(R.string.rating_content_description), // TODO: Define R.string.rating_content_description
                        tint = Color(0xFFFFC107), // Amber/Gold color for star
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        " ${String.format("%.1f", supplier.averageRating)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        " (${supplier.numberOfReviews} " + stringResource(R.string.reviews_suffix) + ")", // TODO: Define R.string.reviews_suffix
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
            // TODO: Could add a "View Profile" button or similar action
        }
    }
}
