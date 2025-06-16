package com.example.rooster.models

import java.util.*

/**
 * Represents a transfer event in the lineage of a chicken.
 */
data class TransferRecord(
    val id: String = UUID.randomUUID().toString(),
    val fromOwnerId: String,
    val toOwnerId: String,
    val chickenId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val proofPhotoUrl: String? = null, // URL of sender’s proof photo
    val proofLatitude: Double? = null, // GPS latitude of proof
    val proofLongitude: Double? = null, // GPS longitude of proof
    val senderConfirmed: Boolean = false, // sender has uploaded and confirmed proof
    val receiverConfirmed: Boolean = false, // receiver has confirmed ownership
    val state: TransferState = TransferState.PENDING,
)

enum class TransferState {
    PENDING,
    VERIFIED,
    REJECTED,
}
