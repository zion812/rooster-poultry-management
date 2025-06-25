package com.example.rooster.data

import com.example.rooster.models.ChickenRecord
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import com.example.rooster.models.TransferRequest as TransferRequestModel

/**
 * Repository for handling transfers and lineage tracking with Parse backend.
 */
class TransferRepository {
    /**
     * Result wrapper for repository operations
     */
    data class RepositoryResult(
        val success: Boolean,
        val objectId: String? = null,
        val errorMessage: String? = null,
    )

    /**
     * Create a new transfer request
     */
    suspend fun createTransferRequest(
        fowlId: String,
        fromOwnerId: String,
        toOwnerId: String,
        proofPhotos: List<ParseFile> = emptyList(),
        location: String? = null,
        notes: String? = null,
    ): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                val transferRequest = TransferRequestModel()
                transferRequest.fowlId = fowlId
                transferRequest.fromOwnerId = fromOwnerId
                transferRequest.toOwnerId = toOwnerId
                transferRequest.status = "PENDING"
                transferRequest.requestDate = Date()
                transferRequest.transferNotes = notes
                transferRequest.coinDeducted = false

                // Add proof photos if provided
                if (proofPhotos.isNotEmpty()) {
                    transferRequest.proofPhoto = proofPhotos.first()
                }

                transferRequest.save()

                FirebaseCrashlytics.getInstance()
                    .log("TransferRepository: Created transfer request ${transferRequest.objectId}")

                RepositoryResult(success = true, objectId = transferRequest.objectId)
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(success = false, errorMessage = e.message)
            }
        }

    /**
     * Accept a transfer request and update ownership
     */
    suspend fun acceptTransfer(transferId: String): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                // Get the transfer request
                val query = ParseQuery.getQuery<TransferRequestModel>("TransferRequest")
                val transferRequest = query.get(transferId)

                if (transferRequest.status != "PENDING") {
                    return@withContext RepositoryResult(
                        success = false,
                        errorMessage = "Transfer is not in pending state",
                    )
                }

                // Update the asset ownership
                val fowlQuery = ParseQuery.getQuery<ChickenRecord>("ChickenRecord")
                val fowl = fowlQuery.get(transferRequest.fowlId!!)

                val previousOwner = fowl.currentOwner
                fowl.currentOwner = transferRequest.toOwnerId
                fowl.save()

                // Update transfer status
                transferRequest.status = "COMPLETED"
                transferRequest.completionDate = Date()
                transferRequest.receiverConfirmed = true
                transferRequest.save()

                // Create ownership record for lineage tracking
                createOwnershipRecord(
                    fowlId = transferRequest.fowlId!!,
                    previousOwnerId = previousOwner!!,
                    newOwnerId = transferRequest.toOwnerId!!,
                    transferRequestId = transferId,
                    transferDate = Date(),
                    transferLocation = "System Transfer",
                )

                FirebaseCrashlytics.getInstance()
                    .log("TransferRepository: Transfer $transferId accepted and ownership updated")

                RepositoryResult(success = true, objectId = transferId)
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(success = false, errorMessage = e.message)
            }
        }

    /**
     * Reject a transfer request
     */
    suspend fun rejectTransfer(
        transferId: String,
        reason: String? = null,
    ): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<TransferRequestModel>("TransferRequest")
                val transferRequest = query.get(transferId)

                transferRequest.status = "REJECTED"
                transferRequest.transferNotes = reason ?: "Rejected by recipient"
                transferRequest.save()

                FirebaseCrashlytics.getInstance()
                    .log("TransferRepository: Transfer $transferId rejected")

                RepositoryResult(success = true, objectId = transferId)
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(success = false, errorMessage = e.message)
            }
        }

    /**
     * Cancel a transfer request (sender side)
     */
    suspend fun cancelTransfer(transferId: String): RepositoryResult =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<TransferRequestModel>("TransferRequest")
                val transferRequest = query.get(transferId)

                if (transferRequest.status != "PENDING") {
                    return@withContext RepositoryResult(
                        success = false,
                        errorMessage = "Can only cancel pending transfers",
                    )
                }

                transferRequest.status = "CANCELLED"
                transferRequest.save()

                FirebaseCrashlytics.getInstance()
                    .log("TransferRepository: Transfer $transferId cancelled")

                RepositoryResult(success = true, objectId = transferId)
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                RepositoryResult(success = false, errorMessage = e.message)
            }
        }

    /**
     * Mark transfer as coin deducted
     */
    suspend fun markCoinDeducted(transferId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<TransferRequestModel>("TransferRequest")
                val transferRequest = query.get(transferId)
                transferRequest.coinDeducted = true
                transferRequest.save()
                true
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                false
            }
        }

    /**
     * Get asset lineage/ownership history
     */
    suspend fun getAssetLineage(fowlId: String): List<Map<String, Any?>> =
        withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("OwnershipRecord")
                query.whereEqualTo("fowlId", fowlId)
                query.orderByDescending("transferDate")

                val results = query.find()
                results.map { parseObject ->
                    mapOf(
                        "objectId" to parseObject.objectId,
                        "fowlId" to parseObject.getString("fowlId"),
                        "previousOwnerId" to parseObject.getString("previousOwnerId"),
                        "newOwnerId" to parseObject.getString("newOwnerId"),
                        "transferRequestId" to parseObject.getString("transferRequestId"),
                        "transferDate" to parseObject.getDate("transferDate"),
                        "transferPrice" to parseObject.getDouble("transferPrice"),
                        "transferLocation" to parseObject.getString("transferLocation"),
                        "verificationHash" to parseObject.getString("verificationHash"),
                        "blockchainTxId" to parseObject.getString("blockchainTxId"),
                        "isReversible" to parseObject.getBoolean("isReversible"),
                        "legalDocuments" to parseObject.getList<ParseFile>("legalDocuments"),
                    )
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                emptyList()
            }
        }

    /**
     * Create ownership record for lineage tracking
     */
    private suspend fun createOwnershipRecord(
        fowlId: String,
        previousOwnerId: String,
        newOwnerId: String,
        transferRequestId: String,
        transferDate: Date,
        transferLocation: String,
    ): String? =
        withContext(Dispatchers.IO) {
            try {
                val ownershipRecord = ParseObject("OwnershipRecord")
                ownershipRecord.put("fowlId", fowlId)
                ownershipRecord.put("previousOwnerId", previousOwnerId)
                ownershipRecord.put("newOwnerId", newOwnerId)
                ownershipRecord.put("transferRequestId", transferRequestId)
                ownershipRecord.put("transferDate", transferDate)
                ownershipRecord.put("transferPrice", 0.0)
                ownershipRecord.put("transferLocation", transferLocation)
                ownershipRecord.put("verificationHash", generateVerificationHash())
                ownershipRecord.put("isReversible", false)
                ownershipRecord.put("legalDocuments", emptyList<ParseFile>())

                ownershipRecord.save()
                ownershipRecord.objectId
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                null
            }
        }

    /**
     * Generate a simple verification hash for the transfer
     */
    private fun generateVerificationHash(): String {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16)
    }
}
