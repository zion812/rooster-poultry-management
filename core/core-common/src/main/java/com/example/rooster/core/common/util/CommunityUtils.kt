package com.example.rooster.core.common.util

import com.example.rooster.core.common.model.CommunityGroup

/**
 * Utility functions for community operations
 */
object CommunityUtils {

    /**
     * Fetch community groups (mock implementation)
     */
    suspend fun fetchCommunityGroups(
        region: String = "",
        category: String = "",
        onResult: (List<CommunityGroup>) -> Unit,
        onError: (String) -> Unit = {}
    ) {
        try {
            // Mock implementation - replace with actual API call
            val mockGroups = getSampleCommunityGroups(region, category)
            onResult(mockGroups)
        } catch (e: Exception) {
            onError(e.message ?: "Failed to fetch community groups")
        }
    }

    private fun getSampleCommunityGroups(region: String, category: String): List<CommunityGroup> {
        return listOf(
            CommunityGroup(
                id = "1",
                name = "Krishna District Poultry Farmers",
                description = "A community for poultry farmers in Krishna district",
                memberCount = 150,
                category = "Farming",
                region = "Krishna",
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CommunityGroup(
                id = "2",
                name = "Broiler Breeders Group",
                description = "Specialized group for broiler breeding",
                memberCount = 85,
                category = "Breeding",
                region = "Krishna",
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            ),
            CommunityGroup(
                id = "3",
                name = "Layer Farm Owners",
                description = "Community for layer farm management",
                memberCount = 120,
                category = "Layer Farming",
                region = "Krishna",
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        ).filter { group ->
            (region.isEmpty() || group.region.contains(region, ignoreCase = true)) &&
                    (category.isEmpty() || group.category.contains(category, ignoreCase = true))
        }
    }
}