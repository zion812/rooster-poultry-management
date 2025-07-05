package com.example.rooster.services.backend

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.parse.ParseObject
import com.parse.ParseQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

// Transfer workflow states
enum class TransferStatus(
    val displayName: String,
    val displayNameTelugu: String,
    val color: Color,
    val icon: ImageVector,
    val allowsEdit: Boolean = false,
    val requiresAction: Boolean = false,
) {
    DRAFT(
        "Draft",
        "మసోదా",
        Color(0xFF6B7280),
        Icons.Default.Edit,
        allowsEdit = true,
    ),
    INITIATED(
        "Transfer Initiated",
        "బదిలీ ప్రారంభించబడింది",
        Color(0xFF3B82F6),
        Icons.Default.Send,
        requiresAction = true,
    ),
    VERIFICATION_PENDING(
        "Verification Pending",
        "ధృవీకరణ పెండింగ్",
        Color(0xFFFF9800),
        Icons.Default.Pending,
        requiresAction = true,
    ),
    RECEIVER_CONFIRMATION_PENDING(
        "Awaiting Receiver",
        "స్వీకర్త నిర్ధారణ పెండింగ్",
        Color(0xFFFCD34D),
        Icons.Default.PersonAdd,
        requiresAction = true,
    ),
    PAYMENT_PENDING(
        "Payment Pending",
        "చెల్లింపు పెండింగ్",
        Color(0xFFDC2626),
        Icons.Default.Payment,
        requiresAction = true,
    ),
    IN_TRANSIT(
        "In Transit",
        "రవాణాలో",
        Color(0xFF8B5CF6),
        Icons.Default.LocalShipping,
    ),
    DELIVERED(
        "Delivered",
        "డెలివరీ అయింది",
        Color(0xFF10B981),
        Icons.Default.CheckCircle,
    ),
    COMPLETED(
        "Completed",
        "పూర్తయింది",
        Color(0xFF059669),
        Icons.Default.Done,
    ),
    CANCELLED(
        "Cancelled",
        "రద్దు చేయబడింది",
        Color(0xFF6B7280),
        Icons.Default.Cancel,
    ),
    DISPUTED(
        "Disputed",
        "వివాదంలో",
        Color(0xFFDC2626),
        Icons.Default.Warning,
        requiresAction = true,
    ),
    REJECTED(
        "Rejected",
        "తిరస్కరించబడింది",
        Color(0xFFEF4444),
        Icons.Default.Close,
    ),
}

// Transfer workflow steps
data class TransferStep(
    val stepNumber: Int,
    val title: String,
    val titleTelugu: String,
    val description: String,
    val descriptionTelugu: String,
    val isCompleted: Boolean = false,
    val isActive: Boolean = false,
    val completedAt: Date? = null,
    val actor: String? = null, // Who needs to perform this step
    val estimatedDuration: String? = null,
)

// Transfer verification requirements
sealed class VerificationRequirement(
    val title: String,
    val titleTelugu: String,
    val isRequired: Boolean = true,
) {
    object OwnershipProof : VerificationRequirement(
        "Ownership Proof",
        "యాజమాన్య రుజువు",
        true,
    )

    object HealthCertificate : VerificationRequirement(
        "Health Certificate",
        "ఆరోగ్య ప్రమాణపత్రం",
        true,
    )

    object VaccinationRecords : VerificationRequirement(
        "Vaccination Records",
        "టీకా రికార్డులు",
        true,
    )

    object TransportDocument : VerificationRequirement(
        "Transport Document",
        "రవాణా పత్రం",
        false,
    )

    object PaymentProof : VerificationRequirement(
        "Payment Proof",
        "చెల్లింపు రుజువు",
        true,
    )

    object ReceiverConsent : VerificationRequirement(
        "Receiver Consent",
        "స్వీకర్త సమ్మతి",
        true,
    )
}

// Transfer workflow data
data class TransferWorkflow(
    val transferId: String,
    val fowlId: String,
    val senderId: String,
    val receiverId: String,
    val currentStatus: TransferStatus,
    val steps: List<TransferStep>,
    val verificationRequirements: List<VerificationRequirement>,
    val createdAt: Date,
    val updatedAt: Date,
    val estimatedCompletionDate: Date?,
    val metadata: Map<String, Any> = emptyMap(),
)

@Singleton
class TransferWorkflowService
    @Inject
    constructor() {
        private val tag = "TransferWorkflowService"

        // Events channel for real-time updates
        private val transferEventChannel = Channel<TransferEvent>(Channel.UNLIMITED)
        val transferEvents = transferEventChannel.receiveAsFlow()

        // Transfer events
        sealed class TransferEvent {
            data class StatusChanged(
                val transferId: String,
                val oldStatus: TransferStatus,
                val newStatus: TransferStatus,
                val timestamp: Date = Date(),
            ) : TransferEvent()

            data class StepCompleted(
                val transferId: String,
                val stepNumber: Int,
                val completedBy: String,
                val timestamp: Date = Date(),
            ) : TransferEvent()

            data class VerificationSubmitted(
                val transferId: String,
                val requirement: VerificationRequirement,
                val submittedBy: String,
                val timestamp: Date = Date(),
            ) : TransferEvent()

            data class ActionRequired(
                val transferId: String,
                val requiredAction: String,
                val requiredActionTelugu: String,
                val actor: String,
                val deadline: Date?,
            ) : TransferEvent()

            data class TransferError(
                val transferId: String,
                val errorMessage: String,
                val isRetryable: Boolean = true,
            ) : TransferEvent()
        }

        // Initialize a new transfer workflow
        suspend fun initializeTransfer(
            fowlId: String,
            senderId: String,
            receiverId: String,
            transferType: String = "sale",
        ): Result<String> =
            withContext(Dispatchers.IO) {
                try {
                    val transferId = generateTransferId()
                    val transfer =
                        ParseObject("Transfer").apply {
                            put("transferId", transferId)
                            put("fowlId", fowlId)
                            put("senderId", senderId)
                            put("receiverId", receiverId)
                            put("status", TransferStatus.DRAFT.name)
                            put("transferType", transferType)
                            put("createdAt", Date())
                            put("updatedAt", Date())
                        }

                    transfer.save()

                    // Initialize workflow steps
                    val steps = createTransferSteps(transferType)
                    saveTransferSteps(transferId, steps)

                    // Emit event
                    transferEventChannel.send(
                        TransferEvent.StatusChanged(
                            transferId = transferId,
                            oldStatus = TransferStatus.DRAFT,
                            newStatus = TransferStatus.DRAFT,
                        ),
                    )

                    Log.d(tag, "Transfer workflow initialized: $transferId")
                    Result.success(transferId)
                } catch (e: Exception) {
                    Log.e(tag, "Failed to initialize transfer workflow", e)
                    Result.failure(e)
                }
            }

        // Update transfer status with automatic step progression
        suspend fun updateTransferStatus(
            transferId: String,
            newStatus: TransferStatus,
            updatedBy: String,
            notes: String? = null,
        ): Result<Unit> =
            withContext(Dispatchers.IO) {
                try {
                    val query = ParseQuery.getQuery<ParseObject>("Transfer")
                    val transfer = query.get(transferId)
                    val oldStatus = TransferStatus.valueOf(transfer.getString("status") ?: "DRAFT")

                    // Validate status transition
                    if (!isValidStatusTransition(oldStatus, newStatus)) {
                        throw IllegalStateException("Invalid status transition: $oldStatus -> $newStatus")
                    }

                    // Update transfer
                    transfer.put("status", newStatus.name)
                    transfer.put("updatedAt", Date())
                    transfer.put("updatedBy", updatedBy)
                    notes?.let { transfer.put("lastNotes", it as Any) }

                    transfer.save()

                    // Update workflow steps
                    updateWorkflowSteps(transferId, newStatus)

                    // Send notifications if action required
                    if (newStatus.requiresAction) {
                        sendActionRequiredNotification(transferId, newStatus)
                    }

                    // Emit status change event
                    transferEventChannel.send(
                        TransferEvent.StatusChanged(
                            transferId = transferId,
                            oldStatus = oldStatus,
                            newStatus = newStatus,
                        ),
                    )

                    Log.d(tag, "Transfer status updated: $transferId -> $newStatus")
                    Result.success(Unit)
                } catch (e: Exception) {
                    Log.e(tag, "Failed to update transfer status", e)

                    transferEventChannel.send(
                        TransferEvent.TransferError(
                            transferId = transferId,
                            errorMessage = e.message ?: "Status update failed",
                        ),
                    )

                    Result.failure(e)
                }
            }

        // Submit verification document
        suspend fun submitVerification(
            transferId: String,
            requirement: VerificationRequirement,
            documentUrl: String,
            submittedBy: String,
        ): Result<Unit> =
            withContext(Dispatchers.IO) {
                try {
                    val verification =
                        ParseObject("TransferVerification").apply {
                            put("transferId", transferId)
                            put("requirementType", requirement::class.simpleName ?: "Unknown")
                            put("documentUrl", documentUrl)
                            put("submittedBy", submittedBy)
                            put("submittedAt", Date())
                            put("status", "PENDING")
                        }

                    verification.save()

                    // Check if all required verifications are complete
                    checkAndProgressTransfer(transferId)

                    // Emit verification event
                    transferEventChannel.send(
                        TransferEvent.VerificationSubmitted(
                            transferId = transferId,
                            requirement = requirement,
                            submittedBy = submittedBy,
                        ),
                    )

                    Log.d(tag, "Verification submitted: $transferId - ${requirement::class.simpleName}")
                    Result.success(Unit)
                } catch (e: Exception) {
                    Log.e(tag, "Failed to submit verification", e)
                    Result.failure(e)
                }
            }

        // Get transfer workflow details
        suspend fun getTransferWorkflow(transferId: String): Result<TransferWorkflow> =
            withContext(Dispatchers.IO) {
                try {
                    val query = ParseQuery.getQuery<ParseObject>("Transfer")
                    val transfer = query.get(transferId)

                    val status = TransferStatus.valueOf(transfer.getString("status") ?: "DRAFT")
                    val steps = getTransferSteps(transferId)
                    val requirements = getVerificationRequirements(transferId)

                    val workflow =
                        TransferWorkflow(
                            transferId = transferId,
                            fowlId = transfer.getString("fowlId") ?: "",
                            senderId = transfer.getString("senderId") ?: "",
                            receiverId = transfer.getString("receiverId") ?: "",
                            currentStatus = status,
                            steps = steps,
                            verificationRequirements = requirements,
                            createdAt = transfer.createdAt ?: Date(),
                            updatedAt = transfer.updatedAt ?: Date(),
                            estimatedCompletionDate = calculateEstimatedCompletion(steps),
                        )

                    Result.success(workflow)
                } catch (e: Exception) {
                    Log.e(tag, "Failed to get transfer workflow", e)
                    Result.failure(e)
                }
            }

        // Helper functions
        private fun generateTransferId(): String = "TXN_${System.currentTimeMillis()}_${Random().nextInt(1000)}"

        private fun createTransferSteps(transferType: String): List<TransferStep> {
            return when (transferType) {
                "sale" ->
                    listOf(
                        TransferStep(
                            1,
                            "Create Transfer",
                            "బదిలీ సృష్టించు",
                            "Initiate transfer request",
                            "బదిలీ అభ్యర్థన ప్రారంభించు",
                            actor = "sender",
                        ),
                        TransferStep(
                            2,
                            "Submit Documents",
                            "పత్రాలు సమర్పించు",
                            "Submit required verification documents",
                            "అవసరమైన ధృవీకరణ పత్రాలు సమర్పించు",
                            actor = "sender",
                        ),
                        TransferStep(
                            3,
                            "Receiver Confirmation",
                            "స్వీకర్త నిర్ధారణ",
                            "Receiver confirms acceptance",
                            "స్వీకర్త అంగీకారం నిర్ధారించు",
                            actor = "receiver",
                        ),
                        TransferStep(
                            4,
                            "Payment Processing",
                            "చెల్లింపు ప్రాసెసింగ్",
                            "Process payment transaction",
                            "చెల్లింపు లావాదేవీ ప్రాసెస్ చేయు",
                            actor = "system",
                        ),
                        TransferStep(
                            5,
                            "Verification Review",
                            "ధృవీకరణ సమీక్ష",
                            "Review and approve documents",
                            "పత్రాలను సమీక్షించి ఆమోదించు",
                            actor = "admin",
                        ),
                        TransferStep(
                            6,
                            "Transfer Execution",
                            "బదిలీ అమలు",
                            "Execute ownership transfer",
                            "యాజమాన్య బదిలీ అమలు చేయు",
                            actor = "system",
                        ),
                        TransferStep(
                            7,
                            "Completion",
                            "పూర్తి",
                            "Transfer completed successfully",
                            "బదిలీ విజయవంతంగా పూర్తయింది",
                            actor = "system",
                        ),
                    )

                else -> emptyList()
            }
        }

        private suspend fun saveTransferSteps(
            transferId: String,
            steps: List<TransferStep>,
        ) {
            steps.forEach { step ->
                val stepObject =
                    ParseObject("TransferStep").apply {
                        put("transferId", transferId)
                        put("stepNumber", step.stepNumber)
                        put("title", step.title)
                        put("titleTelugu", step.titleTelugu)
                        put("description", step.description)
                        put("descriptionTelugu", step.descriptionTelugu)
                        put("isCompleted", step.isCompleted)
                        put("isActive", step.stepNumber == 1) // First step is active
                        put("actor", step.actor ?: "")
                        put("estimatedDuration", step.estimatedDuration ?: "")
                    }
                stepObject.save()
            }
        }

        private suspend fun getTransferSteps(transferId: String): List<TransferStep> {
            val query = ParseQuery.getQuery<ParseObject>("TransferStep")
            query.whereEqualTo("transferId", transferId)
            query.orderByAscending("stepNumber")

            return query.find().map { stepObject ->
                TransferStep(
                    stepNumber = stepObject.getInt("stepNumber"),
                    title = stepObject.getString("title") ?: "",
                    titleTelugu = stepObject.getString("titleTelugu") ?: "",
                    description = stepObject.getString("description") ?: "",
                    descriptionTelugu = stepObject.getString("descriptionTelugu") ?: "",
                    isCompleted = stepObject.getBoolean("isCompleted"),
                    isActive = stepObject.getBoolean("isActive"),
                    completedAt = stepObject.getDate("completedAt"),
                    actor = stepObject.getString("actor"),
                    estimatedDuration = stepObject.getString("estimatedDuration"),
                )
            }
        }

        private suspend fun getVerificationRequirements(transferId: String): List<VerificationRequirement> {
            // For now, return standard requirements - can be customized per transfer type
            return listOf(
                VerificationRequirement.OwnershipProof,
                VerificationRequirement.HealthCertificate,
                VerificationRequirement.VaccinationRecords,
                VerificationRequirement.ReceiverConsent,
                VerificationRequirement.PaymentProof,
            )
        }

        private fun isValidStatusTransition(
            from: TransferStatus,
            to: TransferStatus,
        ): Boolean {
            return when (from) {
                TransferStatus.DRAFT -> to in listOf(TransferStatus.INITIATED, TransferStatus.CANCELLED)
                TransferStatus.INITIATED ->
                    to in
                        listOf(
                            TransferStatus.VERIFICATION_PENDING,
                            TransferStatus.CANCELLED,
                        )

                TransferStatus.VERIFICATION_PENDING ->
                    to in
                        listOf(
                            TransferStatus.RECEIVER_CONFIRMATION_PENDING,
                            TransferStatus.REJECTED,
                        )

                TransferStatus.RECEIVER_CONFIRMATION_PENDING ->
                    to in
                        listOf(
                            TransferStatus.PAYMENT_PENDING,
                            TransferStatus.REJECTED,
                        )

                TransferStatus.PAYMENT_PENDING ->
                    to in
                        listOf(
                            TransferStatus.IN_TRANSIT,
                            TransferStatus.DISPUTED,
                        )

                TransferStatus.IN_TRANSIT ->
                    to in
                        listOf(
                            TransferStatus.DELIVERED,
                            TransferStatus.DISPUTED,
                        )

                TransferStatus.DELIVERED ->
                    to in
                        listOf(
                            TransferStatus.COMPLETED,
                            TransferStatus.DISPUTED,
                        )

                TransferStatus.COMPLETED -> false // Terminal state
                TransferStatus.CANCELLED -> false // Terminal state
                TransferStatus.DISPUTED ->
                    to in
                        listOf(
                            TransferStatus.IN_TRANSIT,
                            TransferStatus.CANCELLED,
                            TransferStatus.COMPLETED,
                        )

                TransferStatus.REJECTED -> to in listOf(TransferStatus.DRAFT) // Can restart
            }
        }

        private suspend fun updateWorkflowSteps(
            transferId: String,
            newStatus: TransferStatus,
        ) {
            val stepToActivate =
                when (newStatus) {
                    TransferStatus.INITIATED -> 2
                    TransferStatus.VERIFICATION_PENDING -> 3
                    TransferStatus.RECEIVER_CONFIRMATION_PENDING -> 4
                    TransferStatus.PAYMENT_PENDING -> 5
                    TransferStatus.IN_TRANSIT -> 6
                    TransferStatus.DELIVERED -> 7
                    TransferStatus.COMPLETED -> -1 // All steps completed
                    else -> return
                }

            // Mark previous steps as completed and activate current step
            val query = ParseQuery.getQuery<ParseObject>("TransferStep")
            query.whereEqualTo("transferId", transferId)

            query.find().forEach { step ->
                val stepNumber = step.getInt("stepNumber")
                if (stepNumber < stepToActivate) {
                    step.put("isCompleted", true)
                    step.put("isActive", false)
                    step.put("completedAt", Date())
                } else if (stepNumber == stepToActivate) {
                    step.put("isActive", true)
                }
                step.save()
            }
        }

        private suspend fun checkAndProgressTransfer(transferId: String) {
            // Logic to check if all requirements are met and progress transfer automatically
            // This would involve checking verification statuses and updating transfer status
        }

        private suspend fun sendActionRequiredNotification(
            transferId: String,
            status: TransferStatus,
        ) {
            // Send notification to relevant parties about required actions
            transferEventChannel.send(
                TransferEvent.ActionRequired(
                    transferId = transferId,
                    requiredAction = "Action required: ${status.displayName}",
                    requiredActionTelugu = "చర్య అవసరం: ${status.displayNameTelugu}",
                    actor = determineActor(status),
                    deadline = calculateDeadline(status),
                ),
            )
        }

        private fun determineActor(status: TransferStatus): String {
            return when (status) {
                TransferStatus.VERIFICATION_PENDING -> "sender"
                TransferStatus.RECEIVER_CONFIRMATION_PENDING -> "receiver"
                TransferStatus.PAYMENT_PENDING -> "buyer"
                TransferStatus.DISPUTED -> "admin"
                else -> "system"
            }
        }

        private fun calculateDeadline(status: TransferStatus): Date? {
            val calendar = Calendar.getInstance()
            return when (status) {
                TransferStatus.VERIFICATION_PENDING -> {
                    calendar.add(Calendar.DAY_OF_MONTH, 3)
                    calendar.time
                }

                TransferStatus.RECEIVER_CONFIRMATION_PENDING -> {
                    calendar.add(Calendar.DAY_OF_MONTH, 2)
                    calendar.time
                }

                TransferStatus.PAYMENT_PENDING -> {
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                    calendar.time
                }

                else -> null
            }
        }

        private fun calculateEstimatedCompletion(steps: List<TransferStep>): Date? {
            val incompleteSteps = steps.filter { !it.isCompleted }
            if (incompleteSteps.isEmpty()) return null

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, incompleteSteps.size * 2) // Estimate 2 days per step
            return calendar.time
        }
    }
