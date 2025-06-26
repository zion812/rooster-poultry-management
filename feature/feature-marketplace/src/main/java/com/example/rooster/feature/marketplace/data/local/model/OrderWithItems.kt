package com.example.rooster.feature.marketplace.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithItems(
    @Embedded
    val order: OrderEntity,

    @Relation(
        parentColumn = "orderId", // From OrderEntity
        entityColumn = "parentOrderId" // From OrderItemEntity
    )
    val items: List<OrderItemEntity>
)
