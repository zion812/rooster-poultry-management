package com.example.rooster.util

import com.parse.ParseACL
import com.parse.ParseException
import com.parse.ParseObject
import com.parse.ParseUser
import com.parse.SignUpCallback
import java.util.Date

object TestDataPopulator {
    fun populateUATTestData(onComplete: (Boolean, String?) -> Unit) {
        val operations = mutableListOf<Pair<String, () -> Unit>>()
        var success = true
        val errors = mutableListOf<String>()

        // 1. Create Test Users (Farmers, General, High-Level)
        operations.add("Creating Test Users" to { createTestUsers() })

        // 2. Create Fowl Records (Kadaknath, Aseel, Brahma with lineage)
        operations.add("Creating Fowl Records" to { createFowlRecords() })

        // 3. Create Marketplace Listings
        operations.add("Creating Marketplace Listings" to { createMarketplaceListings() })

        // 4. Create Traditional Markets
        operations.add("Creating Traditional Markets" to { createTraditionalMarkets() })

        // 5. Create Cultural Events
        operations.add("Creating Cultural Events" to { createCulturalEvents() })

        // Execute operations sequentially and collect errors
        for ((opName, operation) in operations) {
            try {
                operation.invoke()
            } catch (e: Exception) {
                success = false
                errors.add("Error during $opName: ${e.message}")
            }
        }

        if (success) {
            onComplete(true, "UAT Data Population Complete.")
        } else {
            onComplete(false, "UAT Data Population failed with errors: ${errors.joinToString()}")
        }
    }

    private fun createTestUsers() {
        val roles = listOf("farmer", "general", "highLevel")
        roles.forEachIndexed { index, role ->
            val user =
                ParseUser().apply {
                    username = "test${role}user$index@example.com"
                    setPassword("password123")
                    email = "test${role}user$index@example.com"
                    put("role", role)
                    put("isVerified", true)
                    put("region", listOf("Telangana", "Andhra Pradesh", "Karnataka", "Tamil Nadu").random())
                }
            user.signUp() // Using signUp() to handle new user creation directly
        }
    }

    private fun createFowlRecords() {
        val currentUser = ParseUser.getCurrentUser() ?: return // Need a logged-in user to be owner
        val breeds = listOf("Kadaknath", "Aseel", "Brahma")
        val fowlTypes = listOf("Rooster", "Hen")

        breeds.forEach { breed ->
            repeat(2) { // Create a pair for potential lineage
                val fowl =
                    ParseObject("ChickenRecord").apply {
                        put("name", "${breed}TestFowl${Date().time % 1000}")
                        put("breed", breed)
                        put("fowlType", fowlTypes.random())
                        put("birthDate", "2023-01-01") // Placeholder
                        put("ageInWeeks", (10..52).random())
                        put("owner", currentUser)
                        put("isBloodlineVerified", true)
                        put("status", "Healthy")
                        val acl = ParseACL(currentUser).apply { publicReadAccess = true }
                        setACL(acl)
                    }
                fowl.save()
            }
        }
        // TODO: Add lineage linking logic if parent IDs are stored on ChickenRecord
    }

    private fun createMarketplaceListings() {
        val currentUser = ParseUser.getCurrentUser() ?: return
        val breeds = listOf("Kadaknath", "Aseel", "Desi", "Cockerel")

        repeat(5) {
            val listing =
                ParseObject("Listing").apply { // Assuming 'Listing' is your Parse class name
                    put("title", "Healthy ${breeds.random()} for Sale")
                    put("description", "Good quality bird, vaccinated and healthy.")
                    put("price", (500..3000).random().toDouble())
                    put("breed", breeds.random())
                    put("ageInWeeks", (10..30).random())
                    put("gender", listOf("Rooster", "Hen").random())
                    put("owner", currentUser)
                    put("region", listOf("Telangana", "Andhra Pradesh").random())
                    put("isActive", true)
                    // put("imageUrl", "http://example.com/placeholder.jpg") // Add if you have placeholder images
                    val acl = ParseACL(currentUser).apply { publicReadAccess = true }
                    setACL(acl)
                }
            listing.save()
        }
    }

    private fun createTraditionalMarkets() {
        val marketTypes = listOf("Daily", "Weekly", "Festival Special")
        val regions = listOf("Telangana", "Andhra Pradesh", "Karnataka", "Tamil Nadu")
        val fowlTypes = listOf("Mixed Poultry", "Roosters Only", "Country Chicken Fair")

        repeat(3) {
            val market =
                ParseObject("TraditionalMarket").apply {
                    put("name", "${regions.random()} Local Market ${it + 1}")
                    put("region", regions.random())
                    put("marketType", marketTypes.random())
                    put("scheduleDescription", "Every Sunday 9 AM - 2 PM")
                    put("specialties", fowlTypes.random())
                    put("locationCoordinates", "17.3850,78.4867") // Placeholder Hyderabad coords
                    val acl =
                        ParseACL().apply {
                            publicReadAccess = true
                            publicWriteAccess = false
                        }
                    setACL(acl)
                }
            market.save()
        }
    }

    private fun createCulturalEvents() {
        val festivals =
            listOf(
                Pair("Sankranti Rooster Competitions", "సంక్రాంతి కోడి పందాలు"),
                Pair("Village Poultry Fair", "గ్రామ కోళ్ళ ప్రదర్శన"),
            )
        val regions = listOf("Telangana", "Andhra Pradesh")

        festivals.forEach {
            val event =
                ParseObject("CulturalEvent").apply { // Assuming 'CulturalEvent' Parse class
                    put("name", it.first)
                    put("name_te", it.second) // Example for Telugu name
                    put("description", "Annual cultural event celebrating poultry traditions.")
                    put("region", regions.random())
                    put("eventDate", Date()) // Placeholder, set to actual future date
                    put("status", "Scheduled")
                    val acl =
                        ParseACL().apply {
                            publicReadAccess = true
                            publicWriteAccess = false
                        }
                    setACL(acl)
                }
            event.save()
        }
    }
}

// Extension to simplify ParseUser sign up if it fails due to already existing user (for testing)
fun ParseUser.signUpIfNotExists(callback: SignUpCallback) {
    this.signUpInBackground { e ->
        if (e != null && e.code == ParseException.USERNAME_TAKEN) {
            // User already exists, try to log in instead if needed for test setup
            // For simplicity here, we'll just report it via callback as if sign up failed with that code
            callback.done(e)
        } else {
            callback.done(e)
        }
    }
}
