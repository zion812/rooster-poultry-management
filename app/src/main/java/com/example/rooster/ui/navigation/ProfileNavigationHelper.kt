package com.example.rooster.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.rooster.NavigationRoute

// Profile Navigation Helper
object ProfileNavigationHelper {

    sealed class ProfileAction(
        val route: String,
        val icon: ImageVector,
        val label: String,
        val labelTelugu: String,
        val requiresVerification: Boolean = false
    ) {
        object EditProfile : ProfileAction(
            "profile_edit",
            Icons.Default.Edit,
            "Edit Profile",
            "ప్రొఫైల్ సవరించు",
            false
        )

        object VerificationStatus : ProfileAction(
            "verification_status",
            Icons.Default.Verified,
            "Verification Status",
            "ధృవీకరణ స్థితి",
            false
        )

        object FarmerDashboard : ProfileAction(
            NavigationRoute.FARMER_HOME.route,
            Icons.Default.Dashboard,
            "Farmer Dashboard",
            "రైతు డాష్‌బోర్డ్",
            true
        )

        object VetDashboard : ProfileAction(
            "vet_dashboard",
            Icons.Default.MedicalServices,
            "Veterinary Dashboard",
            "పశువైద్య డాష్‌బోర్డ్",
            true
        )

        object Settings : ProfileAction(
            NavigationRoute.SETTINGS.route,
            Icons.Default.Settings,
            "Settings",
            "సెట్టింగులు",
            false
        )

        object MyListings : ProfileAction(
            "my_listings",
            Icons.Default.Store,
            "My Listings",
            "నా జాబితాలు",
            false
        )

        object TransferHistory : ProfileAction(
            NavigationRoute.TRANSFERS.route,
            Icons.Default.SwapHoriz,
            "Transfer History",
            "బదిలీ చరితం",
            false
        )

        object FeedbackReports : ProfileAction(
            "feedback_reports",
            Icons.Default.Assessment,
            "Feedback & Reports",
            "అభిప్రాయం & నివేదికలు",
            false
        )
    }

    // Get contextual actions based on user role and verification status
    fun getContextualActions(
        userRole: String,
        isVerified: Boolean
    ): List<ProfileAction> {
        val baseActions = listOf(
            ProfileAction.EditProfile,
            ProfileAction.Settings,
            ProfileAction.MyListings,
            ProfileAction.TransferHistory
        )

        val roleSpecificActions = when (userRole.lowercase()) {
            "farmer" -> listOf(
                ProfileAction.FarmerDashboard,
                ProfileAction.VerificationStatus
            )

            "vet", "veterinarian" -> listOf(
                ProfileAction.VetDashboard,
                ProfileAction.VerificationStatus
            )

            else -> listOf(ProfileAction.VerificationStatus)
        }

        val verificationActions = if (!isVerified) {
            listOf(ProfileAction.VerificationStatus)
        } else {
            emptyList()
        }

        return (baseActions + roleSpecificActions + verificationActions).distinct()
    }

    // Navigate with proper context
    fun navigateToAction(
        navController: NavController,
        action: ProfileAction,
        userId: String? = null
    ) {
        val route = if (userId != null && action.route.contains("{userId}")) {
            action.route.replace("{userId}", userId)
        } else {
            action.route
        }

        navController.navigate(route) {
            // Clear backstack for dashboard navigation
            if (action is ProfileAction.FarmerDashboard || action is ProfileAction.VetDashboard) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // Check if action is available based on user permissions
    fun isActionAvailable(
        action: ProfileAction,
        isVerified: Boolean,
        userRole: String
    ): Boolean {
        return when {
            action.requiresVerification && !isVerified -> false
            action is ProfileAction.VetDashboard && userRole.lowercase() != "vet" -> false
            action is ProfileAction.FarmerDashboard && userRole.lowercase() != "farmer" -> false
            else -> true
        }
    }
}

// Profile Status Indicators
enum class ProfileStatus(val color: androidx.compose.ui.graphics.Color, val icon: ImageVector) {
    VERIFIED(androidx.compose.ui.graphics.Color(0xFF4CAF50), Icons.Default.Verified),
    PENDING_VERIFICATION(androidx.compose.ui.graphics.Color(0xFFFF9800), Icons.Default.Pending),
    UNVERIFIED(androidx.compose.ui.graphics.Color(0xFF9E9E9E), Icons.Default.Person),
    SUSPENDED(androidx.compose.ui.graphics.Color(0xFFF44336), Icons.Default.Block)
}

// User Role Types
enum class UserRole(val displayName: String, val displayNameTelugu: String) {
    FARMER("Farmer", "రైతు"),
    VET("Veterinarian", "పశువైద్యుడు"),
    BUYER("Buyer", "కొనుగోలుదారు"),
    ADMIN("Administrator", "నిర్వాహకుడు")
}