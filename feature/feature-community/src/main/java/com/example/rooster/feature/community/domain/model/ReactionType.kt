package com.example.rooster.feature.community.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class ReactionType {
    LIKE,
    LOVE,
    CELEBRATE,
    SUPPORT,
    INSIGHTFUL,
    FUNNY
    // Add other reactions as needed, similar to Facebook/LinkedIn
}
