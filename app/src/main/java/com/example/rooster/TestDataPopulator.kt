package com.example.rooster

import android.util.Log
import com.parse.ParseObject
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.random.Random

/**
 * UAT Test Data Population Utility
 * Creates realistic test data for comprehensive UAT testing
 */
object TestDataPopulator {
    /**
     * Populates Parse backend with comprehensive test data for UAT
     */
    suspend fun populateUATTestData(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val report = StringBuilder("🎯 UAT Test Data Population Report\n\n")

                // Create test users across all roles
                val users = createTestUsers()
                report.append("✅ Created ${users.size} test users (Farmers, General, High-Level)\n")

                // Create fowl data with lineage
                val fowls = createTestFowls(users.filter { it.getString("userRole") == "FARMER" })
                report.append("✅ Created ${fowls.size} fowl records with lineage tracking\n")

                // Create marketplace listings
                val listings = createMarketplaceListings(users)
                report.append("✅ Created ${listings.size} marketplace listings\n")

                // Create traditional markets
                val markets = createTraditionalMarkets()
                report.append("✅ Created ${markets.size} traditional market schedules\n")

                // Create cultural events
                val events = createCulturalEvents()
                report.append("✅ Created ${events.size} cultural events and festivals\n")

                // Create social content
                val socialPosts = createSocialContent(users)
                report.append("✅ Created ${socialPosts.size} community posts\n")

                // Create transfer verification data
                val transfers = createTransferRequests(users, fowls)
                report.append("✅ Created ${transfers.size} transfer verification requests\n")

                // Create milestone tracking data
                createMilestoneData(fowls)
                report.append("✅ Created milestone tracking for all fowls\n")

                report.append("\n🎉 UAT Test Data Population Complete!\n")
                report.append(
                    "📊 Total Objects Created: ${users.size + fowls.size + listings.size + markets.size + events.size + socialPosts.size + transfers.size}\n",
                )
                report.append("⏱️ Ready for UAT Testing\n")

                Result.success(report.toString())
            } catch (e: Exception) {
                Result.failure(Exception("UAT Data Population Failed: ${e.message}", e))
            }
        }
    }

    private suspend fun createTestUsers(): List<ParseUser> {
        val users = mutableListOf<ParseUser>()

        // Farmer users (10)
        val farmerNames =
            listOf(
                "Ravi Kumar", "Suresh Reddy", "Mahesh Goud", "Ramesh Naidu", "Krishna Rao",
                "Venkat Sharma", "Ajay Patel", "Srinivas Reddy", "Rajesh Kumar", "Mohan Rao",
            )

        farmerNames.forEachIndexed { index, name ->
            val user = ParseUser()
            user.username = "farmer${index + 1}"
            user.setPassword("password123")
            user.email = "farmer${index + 1}@test.com"
            user.put("fullName", name)
            user.put("userRole", "FARMER")
            user.put(
                "region",
                listOf("Telangana", "Andhra Pradesh", "Karnataka", "Tamil Nadu").random(),
            )
            user.put("isVerifiedBreeder", Random.nextBoolean())
            user.put("experienceYears", Random.nextInt(1, 25))
            user.signUp()
            users.add(user)
            delay(100) // Prevent rate limiting
        }

        // General users (8)
        repeat(8) { index ->
            val user = ParseUser()
            user.username = "general${index + 1}"
            user.setPassword("password123")
            user.email = "general${index + 1}@test.com"
            user.put("fullName", "User ${index + 1}")
            user.put("userRole", "GENERAL")
            user.put(
                "region",
                listOf("Telangana", "Andhra Pradesh", "Karnataka", "Tamil Nadu").random(),
            )
            user.signUp()
            users.add(user)
            delay(100)
        }

        // High-level users (2)
        repeat(2) { index ->
            val user = ParseUser()
            user.username = "highlevel${index + 1}"
            user.setPassword("password123")
            user.email = "highlevel${index + 1}@test.com"
            user.put("fullName", "Admin ${index + 1}")
            user.put("userRole", "HIGH_LEVEL")
            user.put("region", "All India")
            user.signUp()
            users.add(user)
            delay(100)
        }

        return users
    }

    private suspend fun createTestFowls(farmers: List<ParseUser>): List<ParseObject> {
        val fowls = mutableListOf<ParseObject>()
        val breeds = listOf("Kadaknath", "Asil", "Brahma", "Leghorn", "Rhode Island Red", "Bantam")
        val genders = listOf("Male", "Female")

        farmers.forEach { farmer ->
            repeat(Random.nextInt(8, 15)) { index ->
                val fowl = ParseObject("Fowl")
                fowl.put("name", "Bird ${index + 1}")
                fowl.put("breed", breeds.random())
                fowl.put("gender", genders.random())
                fowl.put("age", Random.nextInt(4, 104)) // 4 weeks to 2 years
                fowl.put("weight", Random.nextDouble(0.8, 4.5))
                fowl.put("color", listOf("Black", "Brown", "White", "Red", "Mixed").random())
                fowl.put(
                    "healthStatus",
                    listOf("Excellent", "Good", "Fair", "Needs Attention").random(),
                )
                fowl.put("owner", farmer)
                fowl.put("isForSale", Random.nextBoolean())
                val randomPrice = if (Random.nextBoolean()) Random.nextInt(500, 5000) else null
                if (randomPrice != null) {
                    fowl.put("price", randomPrice)
                }
                fowl.put(
                    "registrationNumber",
                    "RG${farmer.objectId?.takeLast(4) ?: "UNKN"}${index + 1}",
                )
                fowl.put("verificationStatus", listOf("VERIFIED", "PENDING", "UNVERIFIED").random())
                fowl.put("createdBy", farmer)
                fowl.save()
                fowls.add(fowl)
                delay(50)
            }
        }

        return fowls
    }

    private suspend fun createMarketplaceListings(users: List<ParseUser>): List<ParseObject> {
        val listings = mutableListOf<ParseObject>()
        val farmers = users.filter { it.getString("userRole") == "FARMER" }

        farmers.forEach { farmer ->
            repeat(Random.nextInt(2, 6)) { index ->
                val listing = ParseObject("Listing")
                listing.put(
                    "title",
                    "Premium ${listOf("Kadaknath", "Asil", "Brahma").random()} for Sale",
                )
                listing.put(
                    "description",
                    "High quality breed with excellent lineage. Well maintained and healthy.",
                )
                listing.put("price", Random.nextInt(800, 8000))
                listing.put(
                    "fowlType",
                    listOf("Kadaknath", "Asil", "Brahma", "Leghorn", "Rhode Island Red").random(),
                )
                listing.put("age", Random.nextInt(12, 52)) // 3 months to 1 year
                listing.put("gender", listOf("Male", "Female").random())
                listing.put(
                    "location",
                    listOf("Hyderabad", "Warangal", "Vijayawada", "Bengaluru", "Chennai").random(),
                )
                listing.put("region", farmer.getString("region") ?: "Unknown Region")
                listing.put("seller", farmer)
                listing.put("isActive", true)
                listing.put("isVerified", Random.nextBoolean())
                listing.put("viewCount", Random.nextInt(5, 150))
                listing.put("likeCount", Random.nextInt(0, 25))
                listing.save()
                listings.add(listing)
                delay(50)
            }
        }

        return listings
    }

    private suspend fun createTraditionalMarkets(): List<ParseObject> {
        val markets = mutableListOf<ParseObject>()
        val marketData =
            listOf(
                Triple("Warangal Weekly Market", "Every Saturday", "Telangana"),
                Triple("Vijayawada Chicken Market", "Daily", "Andhra Pradesh"),
                Triple("Bengaluru Fowl Market", "Tuesday, Friday", "Karnataka"),
                Triple("Chennai Poultry Market", "Daily", "Tamil Nadu"),
                Triple("Hyderabad Traditional Market", "Sunday", "Telangana"),
                Triple("Tirupati Festival Market", "Monthly", "Andhra Pradesh"),
            )

        marketData.forEach { (name, schedule, region) ->
            val market = ParseObject("TraditionalMarket")
            market.put("name", name)
            market.put("location", name.split(" ").first())
            market.put("region", region)
            market.put("schedule", schedule)
            market.put("marketType", listOf("WEEKLY", "DAILY", "MONTHLY").random())
            market.put("specialties", listOf("Kadaknath", "Local Breeds", "Festival Birds"))
            market.put("isActive", true)
            market.put("averagePrice", Random.nextInt(1000, 3000))
            market.put("vendorCount", Random.nextInt(15, 50))
            market.put(
                "description",
                "Traditional market known for quality local breeds and cultural significance.",
            )
            market.save()
            markets.add(market)
            delay(100)
        }

        return markets
    }

    private suspend fun createCulturalEvents(): List<ParseObject> {
        val events = mutableListOf<ParseObject>()
        val festivals =
            listOf(
                Triple("Sankranti 2025", "Traditional rooster fighting competition", "January 14-16"),
                Triple("Holi Festival", "Spring celebration with rooster showcase", "March 14"),
                Triple("Dussehra Competition", "Annual breeding competition", "October 12"),
                Triple("Diwali Celebration", "Festival of lights rooster display", "November 1"),
                Triple("Regional Championship", "South India rooster championship", "December 15"),
            )

        festivals.forEach { (name, description, date) ->
            val event = ParseObject("Festival")
            event.put("name", name)
            event.put("description", description)
            event.put("dateRange", date)
            event.put("category", "TRADITIONAL_FESTIVAL")
            event.put(
                "region",
                listOf("Telangana", "Andhra Pradesh", "Karnataka", "Tamil Nadu").random(),
            )
            event.put("isActive", true)
            event.put("registrationRequired", true)
            event.put("entryFee", if (Random.nextBoolean()) Random.nextInt(100, 500) else 0)
            event.put("maxParticipants", Random.nextInt(50, 200))
            event.put("culturalSignificance", "HIGH")
            event.save()
            events.add(event)
            delay(100)
        }

        return events
    }

    private suspend fun createSocialContent(users: List<ParseUser>): List<ParseObject> {
        val posts = mutableListOf<ParseObject>()
        val postContent =
            listOf(
                "Just welcomed a new batch of Kadaknath chicks! 🐣 Excited to see them grow.",
                "My Asil rooster won first place at the local competition! 🏆",
                "Looking for advice on feeding schedule for 8-week-old chicks.",
                "Beautiful sunrise at the farm today. The roosters are singing! 🌅",
                "Anyone interested in Brahma eggs? Fresh batch available.",
                "Traditional festival preparations starting. Getting the best birds ready! 🎉",
            )

        users.forEach { user ->
            repeat(Random.nextInt(1, 4)) {
                val post = ParseObject("CommunityPost")
                post.put("content", postContent.random())
                post.put("author", user)
                post.put("authorName", user.getString("fullName") ?: "Unknown User")
                post.put(
                    "category",
                    listOf("GENERAL", "BREEDING", "HEALTH", "MARKETPLACE", "FESTIVAL").random(),
                )
                post.put("likeCount", Random.nextInt(0, 50))
                post.put("commentCount", Random.nextInt(0, 15))
                post.put("isPublic", true)
                post.put("region", user.getString("region") ?: "Unknown Region")
                post.save()
                posts.add(post)
                delay(50)
            }
        }

        return posts
    }

    private suspend fun createTransferRequests(
        users: List<ParseUser>,
        fowls: List<ParseObject>,
    ): List<ParseObject> {
        val transfers = mutableListOf<ParseObject>()
        val farmers = users.filter { it.getString("userRole") == "FARMER" }
        val buyers = users.filter { it.getString("userRole") != "HIGH_LEVEL" }

        repeat(10) {
            val seller = farmers.random()
            val buyer = buyers.filter { it.objectId != seller.objectId }.random()
            val fowl =
                fowls.filter { it.getParseUser("owner")?.objectId == seller.objectId }
                    .randomOrNull()

            if (fowl != null) {
                val transfer = ParseObject("TransferRequest")
                transfer.put("seller", seller)
                transfer.put("buyer", buyer)
                transfer.put("fowlId", fowl.objectId)
                transfer.put("fowlName", fowl.getString("name") ?: "Unknown Bird")
                transfer.put("agreedPrice", Random.nextInt(1000, 5000))
                transfer.put(
                    "status",
                    listOf(
                        "INITIATED",
                        "PENDING_BUYER_VERIFICATION",
                        "BUYER_VERIFIED",
                        "COMPLETED",
                    ).random(),
                )
                transfer.put("initiatedAt", Date())
                transfer.put("description", "Transfer of verified ${fowl.getString("breed")} bird")
                transfer.put("transferId", "TR${UUID.randomUUID().toString().takeLast(8)}")
                transfer.save()
                transfers.add(transfer)
                delay(100)
            }
        }

        return transfers
    }

    private suspend fun createMilestoneData(fowls: List<ParseObject>) {
        fowls.forEach { fowl ->
            val age = fowl.getInt("age")
            val milestoneTypes =
                when {
                    age >= 52 ->
                        listOf(
                            "BIRTH",
                            "VACCINATION_WEEK_1",
                            "VACCINATION_WEEK_3",
                            "WEEK_5_ASSESSMENT",
                            "WEEK_20_CRITICAL",
                            "BREEDER_QUALIFICATION",
                        )

                    age >= 20 ->
                        listOf(
                            "BIRTH",
                            "VACCINATION_WEEK_1",
                            "VACCINATION_WEEK_3",
                            "WEEK_5_ASSESSMENT",
                            "WEEK_20_CRITICAL",
                        )

                    age >= 5 ->
                        listOf(
                            "BIRTH",
                            "VACCINATION_WEEK_1",
                            "VACCINATION_WEEK_3",
                            "WEEK_5_ASSESSMENT",
                        )

                    else -> listOf("BIRTH")
                }

            milestoneTypes.forEach { type ->
                val milestone = ParseObject("FowlMilestone")
                milestone.put("fowlId", fowl.objectId)
                milestone.put("milestoneType", type)
                milestone.put("status", listOf("COMPLETED", "PENDING", "OVERDUE").random())
                milestone.put("notes", "Automated milestone record for UAT testing")
                val fowlOwner = fowl.getParseUser("owner")
                if (fowlOwner != null) {
                    milestone.put("recordedBy", fowlOwner)
                }
                milestone.put("isVerified", Random.nextBoolean())
                milestone.save()
                delay(25)
            }
        }
    }

    /**
     * Creates specific user accounts for UAT testing team
     */
    suspend fun createUATTestAccounts(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val testAccounts =
                    listOf(
                        Triple("uat_farmer", "UAT Farmer Test Account", "FARMER"),
                        Triple("uat_general", "UAT General Test Account", "GENERAL"),
                        Triple("uat_admin", "UAT Admin Test Account", "HIGH_LEVEL"),
                    )

                testAccounts.forEach { (username, fullName, role) ->
                    val user = ParseUser()
                    user.username = username
                    user.setPassword("uat_test_2025")
                    user.email = "$username@rooster-uat.com"
                    user.put("fullName", fullName)
                    user.put("userRole", role)
                    user.put("region", "UAT Test Region")
                    user.put("isUATAccount", true)
                    user.signUp()
                    delay(200)
                }

                Result.success(
                    "✅ UAT Test Accounts Created Successfully\n" +
                        "📱 Login Credentials:\n" +
                        "• Farmer: uat_farmer / uat_test_2025\n" +
                        "• General: uat_general / uat_test_2025\n" +
                        "• Admin: uat_admin / uat_test_2025\n",
                )
            } catch (e: Exception) {
                Result.failure(Exception("UAT Account Creation Failed: ${e.message}", e))
            }
        }
    }

    /**
     * Stress test data creation for performance validation
     */
    suspend fun createStressTestData(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val report = StringBuilder("🚀 Stress Test Data Generation\n\n")

                // Create 100 additional fowl records for performance testing
                val stressFowls = mutableListOf<ParseObject>()
                repeat(100) { index ->
                    val fowl = ParseObject("Fowl")
                    fowl.put("name", "StressBird_$index")
                    fowl.put("breed", "TestBreed")
                    fowl.put("isStressTest", true)
                    fowl.save()
                    stressFowls.add(fowl)
                    if (index % 10 == 0) delay(100) // Rate limiting
                }
                report.append("✅ Created 100 stress test fowl records\n")

                // Create 50 marketplace listings for load testing
                repeat(50) { index ->
                    val listing = ParseObject("Listing")
                    listing.put("title", "StressTest Listing $index")
                    listing.put("price", Random.nextInt(500, 2000))
                    listing.put("isStressTest", true)
                    listing.save()
                    if (index % 10 == 0) delay(100)
                }
                report.append("✅ Created 50 stress test marketplace listings\n")

                report.append("\n⚡ Stress Test Data Ready for 1000+ Concurrent User Simulation\n")
                Result.success(report.toString())
            } catch (e: Exception) {
                Result.failure(Exception("Stress Test Data Creation Failed: ${e.message}", e))
            }
        }
    }

    /**
     * Creates simple demo accounts for quick testing and UAT
     */
    suspend fun createDemoAccounts(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val demoAccounts =
                    listOf(
                        Triple("demo_farmer", "Demo Farmer", "farmer"),
                        Triple("demo_buyer", "Demo Buyer", "general"),
                        Triple("demo_admin", "Demo Admin", "highLevel"),
                    )

                val report = StringBuilder("🧪 Demo Accounts Created:\n\n")

                demoAccounts.forEach { (username, fullName, role) ->
                    try {
                        val user = ParseUser()
                        user.username = username
                        user.setPassword("demo123")
                        user.email = "$username@demo.com"
                        user.put("fullName", fullName)
                        user.put("userRole", role.uppercase())
                        user.put("region", "Demo Region")
                        user.put("isDemoAccount", true)
                        user.signUp()

                        report.append("✅ $fullName: $username / demo123 (Role: $role)\n")
                        delay(200) // Prevent rate limiting
                    } catch (e: Exception) {
                        if (e.message?.contains("username", true) == true) {
                            report.append("ℹ️ $fullName: $username already exists\n")
                        } else {
                            Log.e("TestDataPopulator", "Failed to create demo user: $username", e)
                            report.append("❌ $fullName: Failed to create ($e)\n")
                        }
                    }
                }

                report.append("\n🎯 Ready for Authentication Testing!")
                Result.success(report.toString())
            } catch (e: Exception) {
                Result.failure(Exception("Demo Account Creation Failed: ${e.message}", e))
            }
        }
    }
}

/**
 * UAT Helper extension for quick data cleanup
 */
object UATDataCleaner {
    suspend fun cleanupTestData(): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val report = StringBuilder("🧹 UAT Test Data Cleanup\n\n")

                // Mark test data for cleanup rather than deleting
                // This preserves UAT history while preparing for fresh tests
                val testUsers = ParseUser.getQuery().whereContains("username", "uat_").find()
                testUsers.forEach { user ->
                    user.put("isArchived", true)
                    user.save()
                }
                report.append("✅ Archived ${testUsers.size} UAT test accounts\n")

                report.append("🎯 UAT Environment Reset Complete\n")
                Result.success(report.toString())
            } catch (e: Exception) {
                Result.failure(Exception("UAT Cleanup Failed: ${e.message}", e))
            }
        }
    }
}
