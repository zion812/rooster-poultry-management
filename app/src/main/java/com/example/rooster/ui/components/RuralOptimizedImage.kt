package com.example.rooster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.example.rooster.services.optimized.ConnectionType
import com.example.rooster.services.optimized.ImageType
import com.example.rooster.viewmodel.ConnectivityViewModel
import com.example.rooster.viewmodel.RuralImageViewModel

/**
 * Rural-optimized image loader that adapts quality based on connection type
 */
@Composable
fun RuralOptimizedImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    imageType: ImageType = ImageType.GENERAL,
    contentScale: ContentScale = ContentScale.Crop,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    showProgressIndicator: Boolean = true,
    connectivityViewModel: ConnectivityViewModel = hiltViewModel(),
    imageViewModel: RuralImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val connectionType by connectivityViewModel.connectionType.collectAsStateWithLifecycle()
    val optimizedImageUrl by imageViewModel.getOptimizedImageUrl(imageUrl, connectionType, imageType).collectAsState(initial = null)
    
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        optimizedImageUrl?.let { url ->
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
                contentScale = contentScale,
                onState = { state ->
                    when (state) {
                        is AsyncImagePainter.State.Loading -> {
                            isLoading = true
                            hasError = false
                        }
                        is AsyncImagePainter.State.Success -> {
                            isLoading = false
                            hasError = false
                        }
                        is AsyncImagePainter.State.Error -> {
                            isLoading = false
                            hasError = true
                        }
                        else -> {}
                    }
                }
            )
        }

        // Loading indicator
        if (isLoading && showProgressIndicator) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    
                    // Connection-aware loading message
                    Text(
                        text = when (connectionType) {
                            ConnectionType.WIFI -> "లోడ్ అవుతోంది..."
                            ConnectionType.CELLULAR_4G -> "4G లో లోడ్ అవుతోంది..."
                            ConnectionType.CELLULAR_3G -> "3G లో లోడ్ అవుతోంది..."
                            ConnectionType.CELLULAR_2G -> "2G లో నెమ్మదిగా లోడ్ అవుతోంది..."
                            ConnectionType.UNKNOWN -> "లోడ్ అవుతోంది..."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Error state
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "చిత్రం లోడ్ కాలేదు",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Connection quality indicator
        if (connectionType != ConnectionType.WIFI && !isLoading && !hasError) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(
                        color = when (connectionType) {
                            ConnectionType.CELLULAR_4G -> Color(0xFF4CAF50)
                            ConnectionType.CELLULAR_3G -> Color(0xFFFFC107)
                            ConnectionType.CELLULAR_2G -> Color(0xFFFF9800)
                            else -> Color.Gray
                        }.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = when (connectionType) {
                        ConnectionType.CELLULAR_4G -> "4G"
                        ConnectionType.CELLULAR_3G -> "3G"
                        ConnectionType.CELLULAR_2G -> "2G"
                        else -> "?"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
