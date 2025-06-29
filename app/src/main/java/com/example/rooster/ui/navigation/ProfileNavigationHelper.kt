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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.rooster.NavigationRoute
import com.example.rooster.models.UserRole

// Profile Navigation Helper
object ProfileNavigationHelper {
    sealed class ProfileAction(
        val route: String,
        val icon: ImageVector,
        val label: String,
        val labelTelugu: String,
        val requiresVerification: Boolean = false,
    ) {
        object EditProfile : ProfileAction(
            "profile_edit",
            Icons.Default.Edit,
            "Edit Profile",
            "ప్రొఫైల్ సవరించు",
            false,
        )

        object VerificationStatus : ProfileAction(
            "verification_status",
            Icons.Default.Verified,
            "Verification Status",
            "ధృవీకరణ స్థితి",
            false,
        )

        object FarmerDashboard : ProfileAction(
            NavigationRoute.FARM_DASHBOARD.name,
            Icons.Default.Dashboard,
            "Farmer Dashboard",
            "రైతు డాష్‌బోర్డ్",
            true,
        )

        object VetDashboard : ProfileAction(
            NavigationRoute.VET_CONSULTATION.name,
            Icons.Default.MedicalServices,
            "Veterinary Dashboard",
            "పశువైద్య డాష్‌బోర్డ్",
            true,
        )

        object Settings : ProfileAction(
            NavigationRoute.SETTINGS.name,
            Icons.Default.Settings,
            "Settings",
            "సెట్టింగులు",
            false,
        )

        object MyListings : ProfileAction(
            "my_listings",
            Icons.Default.Store,
            "My Listings",
            "నా జాబితాలు",
            false,
        )

        object TransferHistory : ProfileAction(
            NavigationRoute.TRANSFERS.name,
            Icons.Default.SwapHoriz,
            "Transfer History",
            "బదిలీ చరితం",
            false,
        )

        object FeedbackReports : ProfileAction(
            "feedback_reports",
            Icons.Default.Assessment,
            "Feedback & Reports",
            "అభిప్రాయం & నివేదికలు",
            false,
        )
    }

    // Get contextual actions based on user role and verification status
    fun getContextualActions(
        userRole: UserRole,
        isVerified: Boolean,
    ): List<ProfileAction> {
        val baseActions =
            listOf(
                ProfileAction.EditProfile,
                ProfileAction.Settings,
                ProfileAction.MyListings,
                ProfileAction.TransferHistory,
            )

        val roleSpecificActions =
            when (userRole) {
                UserRole.FARMER -> listOf(ProfileAction.FarmerDashboard)
                UserRole.HIGH_LEVEL -> listOf(ProfileAction.VetDashboard)
                else -> listOf(ProfileAction.VerificationStatus)
            }

        val verificationActions =
            if (!isVerified) {
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
        userId: String? = null,
    ) {
        val route =
            if (userId != null && action.route.contains("{userId}")) {
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
        userRole: UserRole,
    ): Boolean {
        return when {
            action.requiresVerification && !isVerified -> false
            action is ProfileAction.VetDashboard && userRole != UserRole.HIGH_LEVEL -> false
            action is ProfileAction.FarmerDashboard && userRole != UserRole.FARMER -> false
            else -> true
        }
    }
}

// Profile Status Indicators
enum class ProfileStatus(val color: Color, val icon: ImageVector) {
    VERIFIED(Color(0xFF4CAF50), Icons.Default.Verified),
    PENDING_VERIFICATION(Color(0xFFFF9800), Icons.Default.Pending),
    UNVERIFIED(Color(0xFF9E9E9E), Icons.Default.Person),
    SUSPENDED(Color(0xFFF44336), Icons.Default.Block),
}
