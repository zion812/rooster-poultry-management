package com.example.rooster.data

import com.example.rooster.models.*
import com.example.rooster.viewmodels.RepositoryResult
import com.example.rooster.viewmodels.RoosterData
import com.example.rooster.viewmodels.VerificationData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for rooster listing operations with safe coin spending support
 */
class RoosterRepository {
    suspend fun createRooster(roosterData: RoosterData): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                val listing = Listing()
                listing.title = roosterData.name
                listing.description = roosterData.description
                listing.price = roosterData.price
                listing.breed = roosterData.breed
                listing.age = roosterData.age
                listing.category = "rooster"
                listing.location = roosterData.location
                listing.imageUrls = roosterData.imageUrls
                listing.isPremium = roosterData.isPremium
                listing.listingType = if (roosterData.isPremium) "premium" else "standard"
                listing.isActive = true
                listing.coinDeducted = false // Initially false until coin deduction succeeds
                listing.sellerId = ParseUser.getCurrentUser()?.objectId ?: ""

                listing.save()

                FirebaseCrashlytics.getInstance()
                    .log("RoosterRepository: Created listing with ID: ${listing.objectId}")

                RepositoryResult(
                    success = true,
                    objectId = listing.objectId,
                )
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(
                    success = false,
                    errorMessage = "Failed to create listing: ${e.message}",
                )
            }
        }

    suspend fun markCoinDeducted(objectId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<Listing>("Listing")
                val listing = query.get(objectId)
                listing.coinDeducted = true
                listing.save()

                FirebaseCrashlytics.getInstance()
                    .log("RoosterRepository: Marked listing $objectId as coinDeducted")
                true
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
        }
}

/**
 * Repository for verification operations with safe coin spending support
 */
class VerificationRepository {
    suspend fun perform15WeekVerification(
        birdId: String,
        verificationData: VerificationData,
    ): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                val verification = VerificationRecord()
                verification.fowlId = birdId
                verification.verificationType = "15_week"
                verification.weight = verificationData.weight
                verification.height = verificationData.height
                verification.color = verificationData.color
                verification.healthStatus = verificationData.healthStatus
                verification.photoUrls = verificationData.photos
                verification.notes = verificationData.notes
                verification.location = verificationData.location
                verification.verifiedBy = ParseUser.getCurrentUser()?.objectId ?: ""
                verification.isValid = true
                verification.coinDeducted = false // Initially false until coin deduction succeeds

                verification.save()

                FirebaseCrashlytics.getInstance()
                    .log("VerificationRepository: Created 15-week verification with ID: ${verification.objectId}")

                RepositoryResult(
                    success = true,
                    objectId = verification.objectId,
                )
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(
                    success = false,
                    errorMessage = "Failed to create verification: ${e.message}",
                )
            }
        }

    suspend fun perform40WeekVerification(
        birdId: String,
        verificationData: VerificationData,
    ): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                val verification = VerificationRecord()
                verification.fowlId = birdId
                verification.verificationType = "40_week"
                verification.weight = verificationData.weight
                verification.height = verificationData.height
                verification.color = verificationData.color
                verification.healthStatus = verificationData.healthStatus
                verification.photoUrls = verificationData.photos
                verification.notes = verificationData.notes
                verification.location = verificationData.location
                verification.verifiedBy = ParseUser.getCurrentUser()?.objectId ?: ""
                verification.isValid = true
                verification.coinDeducted = false

                verification.save()

                FirebaseCrashlytics.getInstance()
                    .log("VerificationRepository: Created 40-week verification with ID: ${verification.objectId}")

                RepositoryResult(
                    success = true,
                    objectId = verification.objectId,
                )
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(
                    success = false,
                    errorMessage = "Failed to create re-verification: ${e.message}",
                )
            }
        }

    suspend fun processAnnualMaintenance(fowlIds: List<String>): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                val maintenance = MaintenanceRecord()
                maintenance.userId = ParseUser.getCurrentUser()?.objectId ?: ""
                maintenance.fowlIds = fowlIds
                maintenance.maintenanceYear =
                    java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
                maintenance.totalFowlCount = fowlIds.size
                maintenance.totalCost =
                    fowlIds.size * com.example.rooster.CoinCosts.ANNUAL_MAINTENANCE
                maintenance.status = "PROCESSED"
                maintenance.coinDeducted = false

                maintenance.save()

                FirebaseCrashlytics.getInstance()
                    .log("VerificationRepository: Created annual maintenance record with ID: ${maintenance.objectId}")

                RepositoryResult(
                    success = true,
                    objectId = maintenance.objectId,
                )
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(
                    success = false,
                    errorMessage = "Failed to process annual maintenance: ${e.message}",
                )
            }
        }

    suspend fun markCoinDeducted(objectId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                // Try VerificationRecord first
                try {
                    val query = ParseQuery.getQuery<VerificationRecord>("VerificationRecord")
                    val verification = query.get(objectId)
                    verification.coinDeducted = true
                    verification.save()

                    FirebaseCrashlytics.getInstance()
                        .log("VerificationRepository: Marked verification $objectId as coinDeducted")
                    return@withContext true
                } catch (e: ParseException) {
                    // If not found, try MaintenanceRecord
                    if (e.code == ParseException.OBJECT_NOT_FOUND) {
                        val query = ParseQuery.getQuery<MaintenanceRecord>("MaintenanceRecord")
                        val maintenance = query.get(objectId)
                        maintenance.coinDeducted = true
                        maintenance.save()

                        FirebaseCrashlytics.getInstance()
                            .log("VerificationRepository: Marked maintenance $objectId as coinDeducted")
                        return@withContext true
                    } else {
                        throw e
                    }
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
        }
}

/**
 * Repository for transfer operations with safe coin spending support
 */
class SafeTransferRepository {
    suspend fun initiateTransfer(
        fowlId: String,
        toOwnerId: String,
        transferPrice: Double,
        notes: String?,
    ): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                val transfer = TransferRequest()
                transfer.fowlId = fowlId
                transfer.fromOwnerId = ParseUser.getCurrentUser()?.objectId ?: ""
                transfer.toOwnerId = toOwnerId
                transfer.transferPrice = transferPrice
                transfer.transferNotes = notes
                transfer.status = "PENDING"
                transfer.coinDeducted = false

                transfer.save()

                FirebaseCrashlytics.getInstance()
                    .log("SafeTransferRepository: Created transfer request with ID: ${transfer.objectId}")

                RepositoryResult(
                    success = true,
                    objectId = transfer.objectId,
                )
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(
                    success = false,
                    errorMessage = "Failed to initiate transfer: ${e.message}",
                )
            }
        }

    suspend fun markCoinDeducted(objectId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<TransferRequest>("TransferRequest")
                val transfer = query.get(objectId)
                transfer.coinDeducted = true
                transfer.save()

                FirebaseCrashlytics.getInstance()
                    .log("SafeTransferRepository: Marked transfer $objectId as coinDeducted")
                true
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
        }
}
