package com.example.rooster.feature.farm.ui.navigation

data class FarmState(
    val farmDetails: com.example.rooster.feature.farm.domain.model.FarmDetails? = null,
    val badges: List<com.example.rooster.feature.farm.domain.model.FarmBadge> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class FlockStats(
    val totalFowls: Int = 0,
    val totalHens: Int = 0,
    val totalBreeders: Int = 0,
    val totalChicks: Int = 0,
    val activeFlocks: Int = 0
)