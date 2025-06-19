package com.example.rooster

import android.content.Context
import com.example.rooster.data.models.NetworkQualityLevel
import com.example.rooster.data.models.assessNetworkQualitySafely
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

/**
 * Health Management Service for Vaccination and Medication Tracking
 * Moderate-level feature implementation with FCM reminders and offline support
 */
class HealthManagementService(private val context: Context) {
    /**
     * Vaccination Management
     */
    suspend fun addVaccinationRecord(
        birdId: String,
        vaccinationRecord: VaccinationRecord,
    ): Result<VaccinationRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val parseObject = vaccinationRecord.toParseObject()
                parseObject.put("createdBy", ParseUser.getCurrentUser())
                parseObject.save()

                // Schedule reminder for next dose if applicable
                vaccinationRecord.nextDueDate?.let { nextDate ->
                    scheduleHealthReminder(
                        birdId = birdId,
                        title = "Vaccination Due: ${vaccinationRecord.vaccineName}",
                        description = "Next ${vaccinationRecord.vaccineName} vaccination is due",
                        scheduledDate = nextDate,
                        scheduleType = HealthScheduleType.VACCINATION,
                    )
                }

                val result = VaccinationRecord.fromParseObject(parseObject)
                FirebaseCrashlytics.getInstance().log(
                    "Vaccination recorded: ${vaccinationRecord.vaccineName} for bird $birdId",
                )
                Result.success(result)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to add vaccination record: ${e.message}", e))
            }
        }
    }

    suspend fun getVaccinationHistory(birdId: String): Result<List<VaccinationRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Vaccination")
                query.whereEqualTo("birdId", birdId)
                query.orderByDescending("administeredDate")
                query.include("createdBy")

                // Apply network-aware caching
                val networkQuality = assessNetworkQualitySafely(context)
                when (networkQuality) {
                    NetworkQualityLevel.POOR, NetworkQualityLevel.OFFLINE -> {
                        query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                        query.maxCacheAge = 10 * 60 * 1000L // 10 minutes
                    }

                    NetworkQualityLevel.FAIR -> {
                        query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                        query.maxCacheAge = 5 * 60 * 1000L // 5 minutes
                    }

                    else -> {
                        query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                    }
                }

                val results = query.find()
                val vaccinations = results.map { VaccinationRecord.fromParseObject(it) }

                // Cache for offline access
                try {
                    ParseObject.pinAllInBackground("vaccination_cache_$birdId", results)
                } catch (e: Exception) {
                    // Ignore cache errors
                }

                Result.success(vaccinations)
            } catch (e: Exception) {
                // Try to load from cache if network fails
                try {
                    val cachedResults =
                        ParseQuery.getQuery<ParseObject>("Vaccination")
                            .fromLocalDatastore()
                            .whereEqualTo("birdId", birdId)
                            .find()
                    val cached = cachedResults.map { VaccinationRecord.fromParseObject(it) }
                    Result.success(cached)
                } catch (cacheError: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Result.failure(Exception("Failed to get vaccination history: ${e.message}", e))
                }
            }
        }
    }

    /**
     * Medication Management
     */
    suspend fun addMedicationRecord(
        birdId: String,
        medicationRecord: MedicationRecord,
    ): Result<MedicationRecord> {
        return withContext(Dispatchers.IO) {
            try {
                val parseObject = medicationRecord.toParseObject()
                parseObject.put("createdBy", ParseUser.getCurrentUser())
                parseObject.save()

                // Schedule reminders for medication schedule
                if (!medicationRecord.isCompleted) {
                    scheduleMedicationReminders(medicationRecord)
                }

                val result = MedicationRecord.fromParseObject(parseObject)
                FirebaseCrashlytics.getInstance().log(
                    "Medication recorded: ${medicationRecord.medicineName} for bird $birdId",
                )
                Result.success(result)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to add medication record: ${e.message}", e))
            }
        }
    }

    suspend fun getMedicationHistory(birdId: String): Result<List<MedicationRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Medication")
                query.whereEqualTo("birdId", birdId)
                query.orderByDescending("startDate")
                query.include("createdBy")

                // Apply network-aware caching
                val networkQuality = assessNetworkQualitySafely(context)
                when (networkQuality) {
                    NetworkQualityLevel.POOR, NetworkQualityLevel.OFFLINE -> {
                        query.cachePolicy = ParseQuery.CachePolicy.CACHE_ELSE_NETWORK
                        query.maxCacheAge = 10 * 60 * 1000L
                    }

                    else -> {
                        query.cachePolicy = ParseQuery.CachePolicy.NETWORK_ELSE_CACHE
                    }
                }

                val results = query.find()
                val medications = results.map { MedicationRecord.fromParseObject(it) }

                // Cache for offline access
                try {
                    ParseObject.pinAllInBackground("medication_cache_$birdId", results)
                } catch (e: Exception) {
                    // Ignore cache errors
                }

                Result.success(medications)
            } catch (e: Exception) {
                // Try to load from cache if network fails
                try {
                    val cachedResults =
                        ParseQuery.getQuery<ParseObject>("Medication")
                            .fromLocalDatastore()
                            .whereEqualTo("birdId", birdId)
                            .find()
                    val cached = cachedResults.map { MedicationRecord.fromParseObject(it) }
                    Result.success(cached)
                } catch (cacheError: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                    Result.failure(Exception("Failed to get medication history: ${e.message}", e))
                }
            }
        }
    }

    /**
     * Health Schedule Management
     */
    suspend fun scheduleHealthReminder(
        birdId: String,
        title: String,
        description: String,
        scheduledDate: Date,
        scheduleType: HealthScheduleType,
        priority: HealthPriority = HealthPriority.MEDIUM,
    ): Result<HealthSchedule> {
        return withContext(Dispatchers.IO) {
            try {
                val schedule =
                    HealthSchedule(
                        birdId = birdId,
                        scheduleType = scheduleType,
                        title = title,
                        description = description,
                        scheduledDate = scheduledDate,
                        priority = priority,
                        region = ParseUser.getCurrentUser()?.getString("region") ?: "",
                        createdBy = ParseUser.getCurrentUser()?.objectId ?: "",
                    )

                val parseObject = schedule.toParseObject()
                parseObject.put("createdBy", ParseUser.getCurrentUser())
                parseObject.save()

                val result = HealthSchedule.fromParseObject(parseObject)
                Result.success(result)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to schedule health reminder: ${e.message}", e))
            }
        }
    }

    suspend fun getUpcomingHealthSchedules(userId: String): Result<List<HealthSchedule>> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("HealthSchedule")
                query.whereEqualTo("createdBy", ParseUser.getCurrentUser())
                query.whereEqualTo("isCompleted", false)
                query.whereGreaterThan("scheduledDate", Date())
                query.orderByAscending("scheduledDate")
                query.limit = 20

                val results = query.find()
                val schedules = results.map { HealthSchedule.fromParseObject(it) }
                Result.success(schedules)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to get health schedules: ${e.message}", e))
            }
        }
    }

    /**
     * Health Summary for Dashboard
     */
    suspend fun getHealthSummary(userId: String): Result<HealthSummary> {
        return withContext(Dispatchers.IO) {
            try {
                // Get user's fowl list first
                val fowlQuery = ParseQuery.getQuery<ParseObject>("Fowl")
                fowlQuery.whereEqualTo("owner", ParseUser.getCurrentUser())
                val fowlList = fowlQuery.find()
                val fowlIds = fowlList.map { it.objectId }

                if (fowlIds.isEmpty()) {
                    return@withContext Result.success(HealthSummary())
                }

                // Get vaccination summary
                val vaccinationQuery = ParseQuery.getQuery<ParseObject>("Vaccination")
                vaccinationQuery.whereContainedIn("birdId", fowlIds)
                val vaccinations = vaccinationQuery.find()

                // Get medication summary
                val medicationQuery = ParseQuery.getQuery<ParseObject>("Medication")
                medicationQuery.whereContainedIn("birdId", fowlIds)
                val medications = medicationQuery.find()

                // Get upcoming schedules
                val scheduleQuery = ParseQuery.getQuery<ParseObject>("HealthSchedule")
                scheduleQuery.whereEqualTo("createdBy", ParseUser.getCurrentUser())
                scheduleQuery.whereEqualTo("isCompleted", false)
                scheduleQuery.whereGreaterThan("scheduledDate", Date())
                val upcomingSchedules = scheduleQuery.find()

                // Calculate overdue vaccinations
                val now = Date()
                val overdueVaccinations =
                    vaccinations.count { vaccination ->
                        vaccination.getDate("nextDueDate")?.let { it.before(now) } ?: false
                    }

                // Calculate active medications
                val activeMedications =
                    medications.count { medication ->
                        !medication.getBoolean("isCompleted") &&
                            (medication.getDate("endDate")?.after(now) ?: true)
                    }

                // Calculate total health cost
                val totalVaccinationCost = vaccinations.sumOf { it.getDouble("cost") }
                val totalMedicationCost = medications.sumOf { it.getDouble("cost") }

                // Calculate health score (simplified)
                val totalBirds = fowlList.size
                val healthScore =
                    if (totalBirds > 0) {
                        val complianceRate =
                            ((vaccinations.size + medications.size).toDouble() / totalBirds) * 10
                        minOf(100, maxOf(0, (100 - overdueVaccinations * 10 + complianceRate).toInt()))
                    } else {
                        100
                    }

                val summary =
                    HealthSummary(
                        totalVaccinations = vaccinations.size,
                        pendingVaccinations = vaccinations.count { !it.getBoolean("isCompleted") },
                        overdueVaccinations = overdueVaccinations,
                        totalMedications = medications.size,
                        activeMedications = activeMedications,
                        completedMedications = medications.count { it.getBoolean("isCompleted") },
                        upcomingSchedules = upcomingSchedules.size,
                        totalHealthCost = totalVaccinationCost + totalMedicationCost,
                        lastVaccinationDate =
                            vaccinations.maxByOrNull {
                                it.getDate("administeredDate") ?: Date(0)
                            }?.getDate("administeredDate"),
                        lastMedicationDate =
                            medications.maxByOrNull {
                                it.getDate("startDate") ?: Date(0)
                            }?.getDate("startDate"),
                        healthScore = healthScore,
                    )

                Result.success(summary)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to get health summary: ${e.message}", e))
            }
        }
    }

    /**
     * Vaccination Schedule Templates
     */
    suspend fun getRecommendedVaccinations(
        birdId: String,
        birdAgeWeeks: Int,
    ): List<VaccinationTemplate> {
        return VaccinationSchedules.STANDARD_POULTRY_SCHEDULE.filter { template ->
            birdAgeWeeks >= template.ageWeeks && birdAgeWeeks <= template.ageWeeks + 2
        }
    }

    /**
     * Private helper methods
     */
    private suspend fun scheduleMedicationReminders(medication: MedicationRecord) {
        try {
            // Schedule daily reminders for active medication
            val calendar = Calendar.getInstance()
            calendar.time = medication.startDate

            for (day in 0 until medication.duration) {
                scheduleHealthReminder(
                    birdId = medication.birdId,
                    title = "Medication Reminder: ${medication.medicineName}",
                    description = "Give ${medication.dosage} ${medication.frequency}",
                    scheduledDate = calendar.time,
                    scheduleType = HealthScheduleType.MEDICATION,
                    priority = HealthPriority.HIGH,
                )
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Update medication completion status
     */
    suspend fun completeMedication(medicationId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("Medication")
                val medication = query.get(medicationId)
                medication.put("isCompleted", true)
                medication.put("endDate", Date())
                medication.save()
                Result.success(true)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to complete medication: ${e.message}", e))
            }
        }
    }

    /**
     * Mark health schedule as completed
     */
    suspend fun completeHealthSchedule(scheduleId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val query = ParseQuery.getQuery<ParseObject>("HealthSchedule")
                val schedule = query.get(scheduleId)
                schedule.put("isCompleted", true)
                schedule.save()
                Result.success(true)
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
                Result.failure(Exception("Failed to complete schedule: ${e.message}", e))
            }
        }
    }
}
