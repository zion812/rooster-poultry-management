package com.example.rooster.data

import com.example.rooster.models.CertificationRequest
import com.example.rooster.models.CertificationRequestParse
import com.example.rooster.models.RequestStatus
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseException
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Repository for certification requests, integrated with Parse backend.
 */
object CertificationRepository {
    // No longer needed: private val requests = ConcurrentHashMap<String, MutableList<CertificationRequest>>()

    /** Submit a new KYC certification request for a farmer. */
    suspend fun submitKYC(
        farmerId: String,
        docs: List<String>,
    ) {
        withContext(Dispatchers.IO) {
            try {
                val parseRequest = CertificationRequestParse()
                parseRequest.farmerId = farmerId
                parseRequest.docs = docs
                parseRequest.status = RequestStatus.SUBMITTED.name
                parseRequest.submittedAt = Date()
                parseRequest.save()
                FirebaseCrashlytics.getInstance().log("Certification request submitted for $farmerId: ${parseRequest.objectId}")
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                throw e // Re-throw to propagate error to ViewModel
            }
        }
    }

    /** Fetch all KYC requests for a farmer. */
    suspend fun getRequests(farmerId: String): List<CertificationRequest> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery(CertificationRequestParse::class.java)
                query.whereEqualTo("farmerId", farmerId)
                query.orderByDescending("submittedAt")
                val parseRequests = query.find()

                parseRequests.map { parseRequest ->
                    CertificationRequest(
                        requestId = parseRequest.objectId ?: "",
                        farmerId = parseRequest.farmerId ?: "",
                        docs = parseRequest.docs ?: emptyList(),
                        status = RequestStatus.valueOf(parseRequest.status ?: RequestStatus.SUBMITTED.name),
                        submittedAt = parseRequest.submittedAt?.time ?: 0L,
                    )
                }.also { list ->
                    FirebaseCrashlytics.getInstance().log("Fetched ${list.size} certification requests for $farmerId")
                }
            } catch (e: ParseException) {
                FirebaseCrashlytics.getInstance().recordException(e)
                emptyList() // Return empty list on error
            }
        }
    }
}
