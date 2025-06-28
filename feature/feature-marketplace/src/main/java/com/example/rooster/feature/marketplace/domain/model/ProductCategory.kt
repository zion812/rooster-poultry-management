package com.example.rooster.feature.marketplace.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProductCategory {
    LIVE_BIRD_CHICKEN, // General Nattu Kodi
    LIVE_BIRD_ROOSTER, // Specifically Roosters (e.g., for enthusiasts, festivals)
    LIVE_BIRD_HEN,
    LIVE_BIRD_CHICK,
    POULTRY_FEED,
    POULTRY_EQUIPMENT,
    POULTRY_MEDICINE,
    OTHER_SUPPLIES,
    SERVICES // e.g., Vet consultation, Hatching services
}
