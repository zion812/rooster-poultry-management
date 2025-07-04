package com.example.rooster.testing

import com.rooster.adminhome.domain.model.UserManagementInfo // Import from adminhome
import kotlin.random.Random

/**
 * Centralized provider for mock data used across tests and UI previews.
 * This helps in maintaining consistency and realism of mock data.
 */
object MockDataProvider {

    // Shared utilities
    fun randomInRange(min: Int, max: Int): Int = Random.nextInt(min, max + 1)
    // Add other common mock data generation utilities if needed (e.g., random strings, dates)

    object Admin {
        fun userManagementSummary(): UserManagementInfo {
            val totalUsers = randomInRange(500, 5000)
            val newToday = randomInRange(0, 50)
            val active = randomInRange(totalUsers / 2, totalUsers - newToday)
            val pending = randomInRange(0, 20)
            return UserManagementInfo(
                totalUsers = totalUsers,
                newUsersToday = newToday,
                activeUsers = active,
                pendingVerifications = pending
            )
        }

        // TODO: Add mock data functions for other Admin features
        // fun systemMetrics(): List<SystemMetric> { ... }
        fun financialHighlights(): List<com.rooster.adminhome.domain.model.FinancialAnalyticHighlight> { // Ensure correct import
            return listOf(
                com.rooster.adminhome.domain.model.FinancialAnalyticHighlight(
                    title = "Total Revenue (MTD)",
                    value = "₹${randomInRange(100000, 500000)}",
                    trendPercentage = randomInRange(-5, 15) + (Random.nextFloat() * 100).toInt() / 100.0,
                    period = "Month-to-Date"
                ),
                com.rooster.adminhome.domain.model.FinancialAnalyticHighlight(
                    title = "Transaction Volume (24h)",
                    value = randomInRange(200, 1500).toString(),
                    trendPercentage = randomInRange(-10, 20) + (Random.nextFloat() * 100).toInt() / 100.0,
                    period = "Last 24h"
                ),
                com.rooster.adminhome.domain.model.FinancialAnalyticHighlight(
                    title = "New Subscriptions (Weekly)",
                    value = randomInRange(10, 100).toString(),
                    trendPercentage = null, // No trend for this one
                    period = "Last 7 Days"
                )
            )
        }
        fun contentModerationQueue(count: Int = 5): List<com.rooster.adminhome.domain.model.ContentModerationItem> {
            val items = mutableListOf<com.rooster.adminhome.domain.model.ContentModerationItem>()
            val reasons = listOf("Spam", "Inappropriate language", "Off-topic", "Misinformation", null)
            val snippets = listOf(
                "Check out this amazing new product! #ad",
                "This is not relevant to the discussion at all.",
                "I disagree with the previous statement...",
                "The sky is actually green, not blue.",
                "Great post, very informative!"
            )
            val contentTypes = com.rooster.adminhome.domain.model.ContentType.values()

            for (i in 1..count) {
                val contentType = contentTypes.random()
                items.add(
                    com.rooster.adminhome.domain.model.ContentModerationItem(
                        id = "mod${randomInRange(1000, 9999)}",
                        contentType = contentType,
                        contentSnippet = snippets.random().take(randomInRange(20,50)),
                        reportedByUserId = if (Random.nextBoolean()) "user${randomInRange(100,200)}" else null,
                        reasonForFlag = reasons.random(),
                        submissionDate = java.util.Date(System.currentTimeMillis() - randomInRange(1, 72) * 3600000L), // 1-72 hours ago
                        status = com.rooster.adminhome.domain.model.ModerationStatus.PENDING_REVIEW
                    )
                )
            }
            return items
        }
    }

    object FarmerHome {
        // TODO: Add mock data for FarmerHomeScreen features
        // fun weatherData(): WeatherData { ... }
    }

    object BuyerHome {
        // TODO: Add mock data for BuyerHomeScreen features
    }

    object VetHome {
        fun consultationQueue(vetId: String, count: Int = 5): List<com.rooster.vethome.domain.model.ConsultationQueueItem> {
            val items = mutableListOf<com.rooster.vethome.domain.model.ConsultationQueueItem>()
            val farmerNames = listOf("Mr. Rao", "Mrs. Lakshmi", "Mr. Reddy", "Ms. Kumari", "Mr. Gupta")
            val farmLocations = listOf("Krishna Dist.", "Guntur Dist.", "Vijayawada Rural", "Nuzvid Area", "Machilipatnam Outskirts")
            val flockTypes = listOf("Broilers (Batch A)", "Layers (Shed 3)", "Country Chickens", "Mixed Flock", "New Chicks")
            val issues = listOf(
                "Sudden increase in mortality, need urgent advice.",
                "Respiratory issues observed in several birds.",
                "Egg production dropped significantly this week.",
                "Skin lesions on some chickens, unsure of cause.",
                "Routine health checkup request for new flock."
            )
            val statuses = com.rooster.vethome.domain.model.ConsultationRequestStatus.values()

            for (i in 1..count) {
                items.add(
                    com.rooster.vethome.domain.model.ConsultationQueueItem(
                        id = "consult${randomInRange(500, 999)}-${vetId.takeLast(3)}",
                        farmerName = farmerNames.random(),
                        farmLocation = farmLocations.random(),
                        flockType = flockTypes.random(),
                        issueSummary = issues.random(),
                        requestTime = java.util.Date(System.currentTimeMillis() - randomInRange(0, 24*5) * 3600000L), // 0-5 days ago
                        status = statuses.filter { it != com.rooster.vethome.domain.model.ConsultationRequestStatus.COMPLETED && it != com.rooster.vethome.domain.model.ConsultationRequestStatus.CANCELLED }.random(), // Mostly pending/active
                        priority = randomInRange(1, 3)
                    )
                )
            }
            return items.sortedBy { it.requestTime }
        }

        fun recentPatientSummaries(vetId: String, count: Int = 5): List<com.rooster.vethome.domain.model.PatientHistorySummary> {
            val summaries = mutableListOf<com.rooster.vethome.domain.model.PatientHistorySummary>()
            val farmNames = listOf("Green Valley", "Sunrise Eggs", "Krishna Farms", "Godavari Poultry", "Rural Coop")
            val species = listOf("Broiler Chicken", "Layer Hen", "Country Chicken", "Turkey", "Quail")
            val diagnoses = listOf(
                "Mild respiratory infection", "Avian Influenza (Suspected)",
                "Newcastle Disease (Vaccination due)", "Calcium deficiency",
                "Routine Checkup", null
            )

            for (i in 1..count) {
                summaries.add(
                    com.rooster.vethome.domain.model.PatientHistorySummary(
                        patientId = "flock${randomInRange(100,200)}-farm${randomInRange(1,5)}",
                        farmName = farmNames.random(),
                        lastVisitDate = if(Random.nextBoolean()) java.util.Date(System.currentTimeMillis() - randomInRange(1, 90) * 24 * 3600000L) else null,
                        briefDiagnosis = diagnoses.random(),
                        species = species.random()
                    )
                )
            }
            return summaries
        }

        fun activeHealthAlerts(vetId: String, count: Int = 3): List<com.rooster.vethome.domain.model.VetHealthAlert> {
            val alerts = mutableListOf<com.rooster.vethome.domain.model.VetHealthAlert>()
            val farmNames = listOf("Sunrise Farms", "Pioneer Poultry", "Healthy Hens Co.", "AgriVet Partners")
            val titles = listOf(
                "Unusual mortality pattern reported", "Suspected Contagious Disease Outbreak",
                "Request for urgent farm visit", "Critical feed contamination alert"
            )
            val descriptions = listOf(
                "Farm has reported 5 unexplained deaths in Flock B over 24h.",
                "Symptoms align with Avian Influenza, requires immediate investigation.",
                "Farmer reports widespread lethargy and coughing. Needs on-site check.",
                "A batch of feed from 'FeedWell Inc.' might be contaminated. Check if farm uses this."
            )
            val severities = com.rooster.vethome.domain.model.VetAlertSeverity.values()

            for (i in 1..count) {
                alerts.add(
                    com.rooster.vethome.domain.model.VetHealthAlert(
                        alertId = "vetAlert${randomInRange(100,999)}",
                        farmId = "farm${randomInRange(10,50)}",
                        farmName = farmNames.random(),
                        title = titles.random(),
                        description = descriptions.random(),
                        severity = severities.filter { it != com.rooster.vethome.domain.model.VetAlertSeverity.INFO }.random(), // More likely to be urgent/critical
                        timestamp = java.util.Date(System.currentTimeMillis() - randomInRange(0, 12) * 3600000L), // 0-12 hours ago
                        suggestedActionsForVet = listOf("Contact farmer immediately.", "Prepare for potential farm visit.", "Review biosecurity protocols."),
                        isAcknowledged = Random.nextBoolean()
                    )
                )
            }
            return alerts.sortedByDescending { it.timestamp }
        }
    }

    // Add other domain-specific mock data providers as needed
}
