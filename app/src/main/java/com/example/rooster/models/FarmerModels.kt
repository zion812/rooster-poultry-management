package com.example.rooster.models

import java.util.*

// --- Auction Models ---
enum class BidStatus { PENDING, ACCEPTED, REJECTED }

data class AuctionBid(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val bidderId: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: BidStatus = BidStatus.PENDING,
    val isApproved: Boolean = false
)

// --- Certification Models ---
enum class RequestStatus { SUBMITTED, IN_REVIEW, APPROVED, REJECTED }

data class CertificationRequest(
    val requestId: String = UUID.randomUUID().toString(),
    val farmerId: String,
    val docs: List<String>, // URIs of uploaded KYC docs
    val status: RequestStatus = RequestStatus.SUBMITTED,
    val submittedAt: Long = System.currentTimeMillis(),
)

// --- Vaccination Template Models ---
data class VaccinationTemplate(
    val farmId: String,
    val templateId: String = UUID.randomUUID().toString(),
    val name: String,
    val schedule: List<String>, // e.g., ["Week 1: Marek's", ...]
    val uploadedAt: Long = System.currentTimeMillis(),
)

// --- Events & Elects Models ---
data class EventItem(
    val eventId: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val date: Long,
    val type: String, // e.g., "ELECT", "CULTURAL"
)
