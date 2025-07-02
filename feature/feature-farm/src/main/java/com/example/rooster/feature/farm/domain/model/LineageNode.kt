package com.example.rooster.feature.farm.domain.model

data class LineageNode(
    val flockId: String,
    val name: String, // Denormalized for display
    val breed: String?, // Denormalized
    val type: FlockType, // Rooster, Hen, Chick
    val profileImageUrl: String? = null, // Small image
    val children: List<LineageNode> = emptyList(), // For displaying descendants
    // Parents are typically looked up or passed during tree construction rather than nested to avoid cycles here
    var father: LineageNode? = null, // Optional, can be populated during tree construction
    var mother: LineageNode? = null  // Optional
)

// Represents the full lineage, possibly centered around a specific flock
data class LineageInfo(
    val centralFlockId: String,
    val centralFlockNode: LineageNode, // The main subject of the lineage view
    // Ancestors and descendants can be structured in various ways depending on display needs.
    // For simplicity, the LineageNode itself can contain children.
    // Alternatively, separate lists for direct ancestors/descendants could be here.
    val generationDepthUp: Int = 0, // How many generations of ancestors are included
    val generationDepthDown: Int = 0 // How many generations of descendants are included
)
