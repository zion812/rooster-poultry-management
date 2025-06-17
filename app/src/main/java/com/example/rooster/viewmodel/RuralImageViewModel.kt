package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import com.example.rooster.services.optimized.ConnectionType
import com.example.rooster.services.optimized.ImageType
import com.example.rooster.services.optimized.RuralConnectivityOptimizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class RuralImageViewModel
    @Inject
    constructor(
        private val ruralConnectivityOptimizer: RuralConnectivityOptimizer,
    ) : ViewModel() {
        /**
         * Get optimized image URL based on connection type
         */
        fun getOptimizedImageUrl(
            originalUrl: String,
            connectionType: ConnectionType,
            imageType: ImageType,
        ): Flow<String> =
            flow {
                try {
                    val optimizedUrl =
                        ruralConnectivityOptimizer.getOptimizedImageUrl(
                            originalUrl = originalUrl,
                            connectionType = connectionType,
                            imageType = imageType,
                        )
                    emit(optimizedUrl)
                } catch (e: Exception) {
                    // Fallback to original URL on error
                    emit(originalUrl)
                }
            }

        /**
         * Get batch optimized URLs
         */
        fun getBatchOptimizedUrls(
            imageUrls: List<String>,
            connectionType: ConnectionType,
            imageType: ImageType,
        ): Flow<List<String>> =
            flow {
                try {
                    val optimizedUrls =
                        ruralConnectivityOptimizer.batchLoadImages(
                            imageUrls = imageUrls,
                            connectionType = connectionType,
                            imageType = imageType,
                        )
                    emit(optimizedUrls)
                } catch (e: Exception) {
                    // Fallback to original URLs on error
                    emit(imageUrls)
                }
            }
    }
