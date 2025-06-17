package com.example.rooster.services

import android.content.Context
import android.util.Log
import com.parse.ParseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

data class ProfileFieldUpdate(
    val field: String,
    val value: String?,
    val checksum: String,
    val timestamp: Long,
)

@Singleton
class ProfileUpdateService
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val TAG = "ProfileUpdateService"

        // Profile fields that support partial updates
        private val partialUpdateFields =
            setOf(
                "username", "usernameTelugu", "bio", "bioTelugu",
                "location", "locationTelugu", "specializations",
                "phone", "farm_name", "farm_size", "experience_years",
            )

        // In-memory cache for quick access
        private val _profileCache = MutableStateFlow<Map<String, String?>>(emptyMap())
        val profileCache: StateFlow<Map<String, String?>> = _profileCache.asStateFlow()

        // Track pending updates
        private val _pendingUpdates = MutableStateFlow<Map<String, ProfileFieldUpdate>>(emptyMap())
        val pendingUpdates: StateFlow<Map<String, ProfileFieldUpdate>> = _pendingUpdates.asStateFlow()

        /**
         * Initialize profile cache with current user data
         */
        fun initializeCache() {
            try {
                val currentUser = ParseUser.getCurrentUser()
                if (currentUser != null) {
                    val fields = mutableMapOf<String, String?>()

                    partialUpdateFields.forEach { field ->
                        val value =
                            when (field) {
                                "specializations" -> currentUser.getList<String>(field)?.joinToString(",")
                                else -> currentUser.getString(field)
                            }
                        fields[field] = value
                    }

                    _profileCache.value = fields
                    Log.d(TAG, "Profile cache initialized with ${fields.size} fields")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize profile cache", e)
            }
        }

        /**
         * Update a single profile field with intelligent caching
         */
        suspend fun updateProfileField(
            field: String,
            value: String?,
            immediate: Boolean = false,
        ): Result<Unit> {
            return try {
                if (!partialUpdateFields.contains(field)) {
                    return Result.failure(IllegalArgumentException("Field $field not supported for partial updates"))
                }

                val currentUser =
                    ParseUser.getCurrentUser()
                        ?: return Result.failure(IllegalStateException("No current user"))

                // Calculate checksum for change detection
                val checksum = calculateChecksum(value ?: "")
                val currentCache = _profileCache.value.toMutableMap()
                val previousValue = currentCache[field]
                val previousChecksum = calculateChecksum(previousValue ?: "")

                // Skip if no actual change
                if (checksum == previousChecksum) {
                    Log.d(TAG, "No change detected for field $field, skipping update")
                    return Result.success(Unit)
                }

                // Update local cache immediately
                currentCache[field] = value
                _profileCache.value = currentCache

                // Create pending update record
                val update =
                    ProfileFieldUpdate(
                        field = field,
                        value = value,
                        checksum = checksum,
                        timestamp = System.currentTimeMillis(),
                    )

                val currentPending = _pendingUpdates.value.toMutableMap()
                currentPending[field] = update
                _pendingUpdates.value = currentPending

                if (immediate) {
                    // Apply update immediately
                    applyFieldUpdate(currentUser, field, value)
                    currentUser.save()
                    currentPending.remove(field)
                    _pendingUpdates.value = currentPending
                    Log.d(TAG, "Immediate update applied for field: $field")
                } else {
                    Log.d(TAG, "Field $field queued for batch update")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to update profile field: $field", e)
                Result.failure(e)
            }
        }

        /**
         * Batch update multiple profile fields
         */
        suspend fun batchUpdateProfile(updates: Map<String, String?>): Result<Unit> {
            return try {
                val currentUser =
                    ParseUser.getCurrentUser()
                        ?: return Result.failure(IllegalStateException("No current user"))

                val fieldsToUpdate = mutableMapOf<String, String?>()
                val currentCache = _profileCache.value.toMutableMap()

                updates.forEach { (field, value) ->
                    if (partialUpdateFields.contains(field)) {
                        val checksum = calculateChecksum(value ?: "")
                        val previousChecksum = calculateChecksum(currentCache[field] ?: "")

                        if (checksum != previousChecksum) {
                            fieldsToUpdate[field] = value
                            currentCache[field] = value
                        }
                    }
                }

                if (fieldsToUpdate.isNotEmpty()) {
                    _profileCache.value = currentCache

                    // Apply all field updates in single Parse operation
                    fieldsToUpdate.forEach { (field, value) ->
                        applyFieldUpdate(currentUser, field, value)
                    }

                    currentUser.save()

                    // Clear any pending updates for these fields
                    val currentPending = _pendingUpdates.value.toMutableMap()
                    fieldsToUpdate.keys.forEach { field ->
                        currentPending.remove(field)
                    }
                    _pendingUpdates.value = currentPending

                    Log.d(TAG, "Batch updated ${fieldsToUpdate.size} profile fields")
                }

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to batch update profile", e)
                Result.failure(e)
            }
        }

        /**
         * Sync pending updates to server
         */
        suspend fun syncPendingUpdates(): Result<Int> {
            return try {
                val pending = _pendingUpdates.value
                if (pending.isEmpty()) {
                    return Result.success(0)
                }

                val currentUser =
                    ParseUser.getCurrentUser()
                        ?: return Result.failure(IllegalStateException("No current user"))

                pending.forEach { (field, update) ->
                    applyFieldUpdate(currentUser, field, update.value)
                }

                currentUser.save()
                _pendingUpdates.value = emptyMap()

                Log.d(TAG, "Synced ${pending.size} pending profile updates")
                Result.success(pending.size)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync pending updates", e)
                Result.failure(e)
            }
        }

        /**
         * Get profile field value from cache
         */
        fun getProfileField(field: String): String? {
            return _profileCache.value[field]
        }

        /**
         * Check if there are unsaved changes
         */
        fun hasUnsavedChanges(): Boolean {
            return _pendingUpdates.value.isNotEmpty()
        }

        private fun applyFieldUpdate(
            user: ParseUser,
            field: String,
            value: String?,
        ) {
            when (field) {
                "specializations" -> {
                    val list = value?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
                    user.put(field, list ?: emptyList<String>())
                }
                else -> {
                    if (value != null) {
                        user.put(field, value)
                    } else {
                        user.remove(field)
                    }
                }
            }
        }

        private fun calculateChecksum(value: String): String {
            val bytes = MessageDigest.getInstance("MD5").digest(value.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
