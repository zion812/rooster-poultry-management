package com.example.rooster.core.data.fetcher

import com.example.rooster.core.data.model.ConsultationRequestTeaser
import com.example.rooster.core.data.model.FarmHealthAlertTeaser
import com.example.rooster.core.data.model.ScheduledAppointmentTeaser
import com.example.rooster.core.data.model.VetDashboardSummary
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockVeterinarianDashboardDataFetcher @Inject constructor() : VeterinarianDashboardDataFetcher {
    override suspend fun getDashboardSummary(vetId: String): Result<VetDashboardSummary> {
        delay(900) // Simulate network delay

        return if (vetId == "error_vet") {
            Result.failure(Exception("Mock fetch error for vet dashboard"))
        } else {
            Result.success(
                VetDashboardSummary(
                    vetName = "Dr. Anjali Rao",
                    pendingConsultationRequests = listOf(
                        ConsultationRequestTeaser("req1", "Reddy Farms", "High chick mortality", "Today, 9:00 AM"),
                        ConsultationRequestTeaser("req2", "Green Pastures Farm", "Respiratory issues in layers", "Today, 11:30 AM")
                    ),
                    upcomingAppointments = listOf(
                        ScheduledAppointmentTeaser("app1", "Sunrise Poultry", "Tomorrow, 10:00 AM", "Farm Visit"),
                        ScheduledAppointmentTeaser("app2", "Krishna Breeders", "Day after, 3:00 PM", "Vaccination Drive")
                    ),
                    recentFarmHealthAlerts = listOf(
                        FarmHealthAlertTeaser("alert1", "Reddy Farms", "Suspected Newcastle Disease outbreak", "Critical"),
                        FarmHealthAlertTeaser("alert2", "Giri Poultry", "Coccidiosis reported in Brooder #3", "Warning")
                    ),
                    unreadMessages = 5
                )
            )
        }
    }
}
