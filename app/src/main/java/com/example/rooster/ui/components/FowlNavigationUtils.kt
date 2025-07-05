package com.example.rooster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Egg
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Fowl Status with visual indicators
enum class FowlStatus(
    val displayName: String,
    val displayNameTelugu: String,
    val color: Color,
    val icon: ImageVector,
    val priority: Int, // Higher priority = more urgent
) {
    QUARANTINE(
        "Quarantine",
        "దిగుమతి నిర్బంధం",
        Color(0xFFE53E3E),
        Icons.Default.HealthAndSafety,
        10,
    ),
    MORTALITY(
        "Mortality",
        "మరణించిన",
        Color(0xFF2D3748),
        Icons.Default.Emergency,
        9,
    ),
    SICK(
        "Sick",
        "అనారోగ్యం",
        Color(0xFFFF8C00),
        Icons.Default.LocalHospital,
        8,
    ),
    BREEDING(
        "Breeding",
        "సంతానోత్పత్తి",
        Color(0xFF8B5CF6),
        Icons.Default.FamilyRestroom,
        5,
    ),
    LAYING(
        "Laying",
        "గుడ్లు పెట్టుట",
        Color(0xFF10B981),
        Icons.Default.Egg,
        4,
    ),
    GROWING(
        "Growing",
        "పెరుగుట",
        Color(0xFF3B82F6),
        Icons.Default.TrendingUp,
        3,
    ),
    HEALTHY(
        "Healthy",
        "ఆరోగ్యకరమైన",
        Color(0xFF059669),
        Icons.Default.Check,
        1,
    ),
    SOLD(
        "Sold",
        "అమ్మబడిన",
        Color(0xFF6B7280),
        Icons.Default.ShoppingCart,
        0,
    ),
}

// Health Status for quick indicators
enum class HealthStatus(
    val color: Color,
    val icon: ImageVector,
) {
    EXCELLENT(Color(0xFF22C55E), Icons.Default.Favorite),
    GOOD(Color(0xFF84CC16), Icons.Default.ThumbUp),
    FAIR(Color(0xFFEAB308), Icons.Default.Warning),
    POOR(Color(0xFFEF4444), Icons.Default.Error),
    CRITICAL(Color(0xFF991B1B), Icons.Default.LocalHospital),
}

// Multi-step form navigation for fowl data entry
object FowlFormNavigation {
    sealed class FormStep(
        val stepNumber: Int,
        val title: String,
        val titleTelugu: String,
        val route: String,
        val isRequired: Boolean = true,
    ) {
        object BasicInfo : FormStep(
            1,
            "Basic Information",
            "ప్రాథమిక సమాచారం",
            "fowl_form_basic",
            true,
        )

        object PhysicalDetails : FormStep(
            2,
            "Physical Details",
            "శారీరిక వివరాలు",
            "fowl_form_physical",
            true,
        )

        object HealthInfo : FormStep(
            3,
            "Health Information",
            "ఆరోగ్య సమాచారం",
            "fowl_form_health",
            true,
        )

        object VaccinationRecords : FormStep(
            4,
            "Vaccination Records",
            "టీకా రికార్డులు",
            "fowl_form_vaccination",
            false,
        )

        object LineageInfo : FormStep(
            5,
            "Lineage Information",
            "వంశావళి సమాచారం",
            "fowl_form_lineage",
            false,
        )

        object MediaUpload : FormStep(
            6,
            "Photos & Videos",
            "ఫోటోలు & వీడియోలు",
            "fowl_form_media",
            false,
        )

        object Review : FormStep(
            7,
            "Review & Submit",
            "సమీక్ష & సమర్పణ",
            "fowl_form_review",
            true,
        )
    }

    fun getAllSteps(): List<FormStep> =
        listOf(
            FormStep.BasicInfo,
            FormStep.PhysicalDetails,
            FormStep.HealthInfo,
            FormStep.VaccinationRecords,
            FormStep.LineageInfo,
            FormStep.MediaUpload,
            FormStep.Review,
        )

    fun getRequiredSteps(): List<FormStep> = getAllSteps().filter { it.isRequired }

    fun getNextStep(currentStep: FormStep): FormStep? {
        val allSteps = getAllSteps()
        val currentIndex = allSteps.indexOf(currentStep)
        return if (currentIndex < allSteps.size - 1) allSteps[currentIndex + 1] else null
    }

    fun getPreviousStep(currentStep: FormStep): FormStep? {
        val allSteps = getAllSteps()
        val currentIndex = allSteps.indexOf(currentStep)
        return if (currentIndex > 0) allSteps[currentIndex - 1] else null
    }

    fun getProgressPercentage(currentStep: FormStep): Float {
        val allSteps = getAllSteps()
        val currentIndex = allSteps.indexOf(currentStep)
        return (currentIndex + 1).toFloat() / allSteps.size.toFloat()
    }
}

// Quick action navigation for fowl profiles
object FowlQuickActions {
    sealed class QuickAction(
        val route: String,
        val label: String,
        val labelTelugu: String,
        val icon: ImageVector,
        val color: Color,
        val requiresOwnership: Boolean = true,
    ) {
        object ViewDetails : QuickAction(
            "fowl_detail/{fowlId}",
            "View Details",
            "వివరాలు చూడండి",
            Icons.Default.Visibility,
            Color(0xFF3B82F6),
            false,
        )

        object EditInfo : QuickAction(
            "fowl_edit/{fowlId}",
            "Edit Info",
            "సమాచారం మార్చు",
            Icons.Default.Edit,
            Color(0xFF059669),
            true,
        )

        object HealthRecord : QuickAction(
            "fowl_health/{fowlId}",
            "Health Record",
            "ఆరోగ్య రికార్డు",
            Icons.Default.LocalHospital,
            Color(0xFFDC2626),
            true,
        )

        object VaccinationLog : QuickAction(
            "fowl_vaccination/{fowlId}",
            "Vaccination",
            "టీకాలు",
            Icons.Default.MedicalServices,
            Color(0xFF7C3AED),
            true,
        )

        object GrowthTracking : QuickAction(
            "fowl_growth/{fowlId}",
            "Growth Tracking",
            "వృద్ధి ట్రాకింగ్",
            Icons.Default.TrendingUp,
            Color(0xFF059669),
            true,
        )

        object BreedingHistory : QuickAction(
            "fowl_breeding/{fowlId}",
            "Breeding",
            "సంతానోత్పత్తి",
            Icons.Default.FamilyRestroom,
            Color(0xFFEC4899),
            true,
        )

        object CreateListing : QuickAction(
            "create_listing/{fowlId}",
            "Create Listing",
            "లిస్టింగ్ సృష్టించు",
            Icons.Default.Store,
            Color(0xFFF59E0B),
            true,
        )

        object TransferOwnership : QuickAction(
            "transfer_fowl/{fowlId}",
            "Transfer",
            "బదిలీ",
            Icons.Default.SwapHoriz,
            Color(0xFF6366F1),
            true,
        )

        object ViewLineage : QuickAction(
            "fowl_lineage/{fowlId}",
            "View Lineage",
            "వంశావళి చూడు",
            Icons.Default.AccountTree,
            Color(0xFF8B5CF6),
            false,
        )
    }

    fun getAvailableActions(
        isOwner: Boolean,
        fowlStatus: FowlStatus,
    ): List<QuickAction> {
        val baseActions =
            listOf(
                QuickAction.ViewDetails,
                QuickAction.ViewLineage,
            )

        val ownerActions =
            if (isOwner) {
                when (fowlStatus) {
                    FowlStatus.SOLD -> listOf(QuickAction.EditInfo) // Limited actions for sold fowl
                    FowlStatus.MORTALITY -> listOf(QuickAction.EditInfo, QuickAction.HealthRecord)
                    else ->
                        listOf(
                            QuickAction.EditInfo,
                            QuickAction.HealthRecord,
                            QuickAction.VaccinationLog,
                            QuickAction.GrowthTracking,
                            QuickAction.BreedingHistory,
                            QuickAction.CreateListing,
                            QuickAction.TransferOwnership,
                        )
                }
            } else {
                emptyList()
            }

        return baseActions + ownerActions
    }
}

// Visual status indicator component
@Composable
fun FowlStatusIndicator(
    status: FowlStatus,
    size: Float = 12f,
    showText: Boolean = true,
    isTeluguMode: Boolean = false,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Status dot
        Box(
            modifier =
                Modifier
                    .size(size.dp)
                    .clip(CircleShape)
                    .background(status.color),
        )

        // Status icon
        Icon(
            imageVector = status.icon,
            contentDescription = null,
            tint = status.color,
            modifier = Modifier.size((size * 1.2f).dp),
        )

        // Status text
        if (showText) {
            Text(
                text = if (isTeluguMode) status.displayNameTelugu else status.displayName,
                color = status.color,
                fontSize = (size * 0.8f).sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// Multi-step form progress indicator
@Composable
fun FowlFormProgressIndicator(
    currentStep: FowlFormNavigation.FormStep,
    isTeluguMode: Boolean = false,
) {
    val allSteps = FowlFormNavigation.getAllSteps()
    val currentIndex = allSteps.indexOf(currentStep)
    val progress = FowlFormNavigation.getProgressPercentage(currentStep)

    Column {
        // Progress bar
        LinearProgressIndicator(
            progress = { progress },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF059669),
            trackColor = Color(0xFFE5E7EB),
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Step indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "${currentStep.stepNumber}/${allSteps.size}",
                fontSize = 12.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium,
            )

            Text(
                text = if (isTeluguMode) currentStep.titleTelugu else currentStep.title,
                fontSize = 14.sp,
                color = Color(0xFF111827),
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// Fowl summary card with navigation
@Composable
fun FowlSummaryCard(
    fowlId: String,
    fowlName: String,
    fowlBreed: String,
    status: FowlStatus,
    healthStatus: HealthStatus,
    age: String,
    lastUpdated: String,
    imageUrl: String? = null,
    isOwner: Boolean = false,
    isTeluguMode: Boolean = false,
    navController: NavController,
    onClick: () -> Unit = {},
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            // Header with status indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fowlName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                    )
                    Text(
                        text = fowlBreed,
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    FowlStatusIndicator(
                        status = status,
                        size = 10f,
                        showText = false,
                        isTeluguMode = isTeluguMode,
                    )

                    // Health indicator
                    Icon(
                        imageVector = healthStatus.icon,
                        contentDescription = null,
                        tint = healthStatus.color,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "${if (isTeluguMode) "వయస్సు" else "Age"}: $age",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                )

                Text(
                    text = "${if (isTeluguMode) "చివరిసారి అప్డేట్" else "Updated"}: $lastUpdated",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                )
            }

            // Priority actions for urgent statuses
            if (status.priority >= 8) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        navController.navigate("fowl_health/$fowlId")
                    },
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor = status.color,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = status.icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isTeluguMode) "తక్షణ చర్య అవసరం" else "Immediate Action Required",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
