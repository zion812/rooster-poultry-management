package com.example.rooster.data.datasources.parse

import com.example.rooster.core.auth.datasources.UserDataSource
import com.example.rooster.core.auth.model.AuthUser
import com.example.rooster.core.auth.model.UserProfileData
import com.example.rooster.core.common.Result
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.File // For potential profile picture upload, though not directly in UserDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class ParseUserDataSource @Inject constructor() : UserDataSource {

    companion object {
        // Define ParseUser custom field keys for consistency
        const val KEY_DISPLAY_NAME = "displayName"
        const val KEY_PROFILE_PICTURE_URL = "profilePictureUrl"
        const val KEY_BIO = "bio"
        const val KEY_LOCATION = "location"
        const val KEY_FARM_NAME = "farmName"
        const val KEY_INTERESTS = "interests" // Store as List<String>
        const val KEY_PHONE_NUMBER = "phoneNumber"
        const val KEY_FOLLOWER_COUNT = "followerCount"
        const val KEY_FOLLOWING_COUNT = "followingCount"
        const val KEY_POST_COUNT = "postCount"
        const val KEY_LAST_ACTIVE_TIMESTAMP = "lastActiveTimestamp"
        // joinDateTimestamp is ParseUser.createdAt
        const val KEY_IS_VERIFIED_FARMER = "isVerifiedFarmer"
        const val KEY_IS_ENTHUSIAST = "isEnthusiast"
        const val KEY_IS_KYC_VERIFIED = "isKycVerified"
        const val KEY_ADDITIONAL_PROPERTIES = "additionalProperties" // Store as Map<String, String>
    }

    override suspend fun signUp(email: String, password: String, initialProfileData: Map<String, Any?>?): Result<AuthUser> {
        return suspendCancellableCoroutine { continuation ->
            val user = ParseUser()
            user.username = email // Parse typically uses username for login, can be same as email
            user.setEmail(email)
            user.setPassword(password)

            initialProfileData?.forEach { (key, value) ->
                user.put(key, value ?: continue) // Add initial profile data directly
            }

            user.signUpInBackground { e ->
                if (e == null) {
                    Timber.d("Parse SignUp successful for: $email")
                    continuation.resume(Result.Success(mapParseUserToAuthUser(user)))
                } else {
                    Timber.e(e, "Parse SignUp error for: $email")
                    continuation.resume(Result.Error(e))
                }
            }
        }
    }

    override suspend fun logIn(email: String, password: String): Result<AuthUser> {
        return suspendCancellableCoroutine { continuation ->
            ParseUser.logInInBackground(email, password) { user, e ->
                if (user != null && e == null) {
                    Timber.d("Parse LogIn successful for: $email")
                    continuation.resume(Result.Success(mapParseUserToAuthUser(user)))
                } else {
                    Timber.e(e, "Parse LogIn error for: $email")
                    continuation.resume(Result.Error(e ?: Exception("Login failed: Unknown error")))
                }
            }
        }
    }

    override suspend fun logOut(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            ParseUser.logOutInBackground { e ->
                if (e == null) {
                    Timber.d("Parse LogOut successful")
                    continuation.resume(Result.Success(Unit))
                } else {
                    Timber.e(e, "Parse LogOut error")
                    continuation.resume(Result.Error(e))
                }
            }
        }
    }

    override suspend fun getCurrentUser(): AuthUser? {
        return ParseUser.getCurrentUser()?.let { mapParseUserToAuthUser(it) }
    }

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        // Parse SDK doesn't have a direct auth state listener like Firebase.
        // Emitting current user initially. For reactive updates on login/logout,
        // the app would typically re-trigger this or use a custom event bus after auth operations.
        // A more robust solution might involve a BehaviorSubject/StateFlow in a singleton repository
        // that gets updated after login/logout calls.
        trySend(ParseUser.getCurrentUser()?.let { mapParseUserToAuthUser(it) })

        // This flow will only emit once unless closed and re-collected.
        // For true reactive auth state, higher-level logic needs to manage this.
        awaitClose { /* No listener to remove for basic getCurrentUser() */ }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            ParseUser.requestPasswordResetInBackground(email) { e ->
                if (e == null) {
                    Timber.d("Parse Password Reset email sent to: $email")
                    continuation.resume(Result.Success(Unit))
                } else {
                    Timber.e(e, "Parse Password Reset error for: $email")
                    continuation.resume(Result.Error(e))
                }
            }
        }
    }

    override suspend fun getUserProfile(userId: String): Result<UserProfileData> {
         return try {
            val user = ParseUser.getQuery().get(userId) // Fetches the user object
            if (user != null) {
                Result.Success(mapParseUserToUserProfileData(user))
            } else {
                Result.Error(Exception("User profile not found for ID: $userId"))
            }
        } catch (e: ParseException) {
            Timber.e(e, "Parse error fetching user profile for ID: $userId")
            Result.Error(e)
        } catch (e: Exception) {
            Timber.e(e, "Generic error fetching user profile for ID: $userId")
            Result.Error(e)
        }
    }

    override fun observeUserProfile(userId: String): Flow<UserProfileData?> = callbackFlow {
        // For real-time updates on a ParseUser object, you'd typically re-fetch or use LiveQuery if set up.
        // This basic version emits current state and then completes.
        // For live updates, a Parse LiveQuery subscription on the User class for this objectId would be needed.
        try {
            val user = ParseUser.getQuery().get(userId)
            trySend(user?.let { mapParseUserToUserProfileData(it) })
        } catch (e: ParseException) {
            Timber.e(e, "Parse error observing user profile for ID: $userId")
            trySend(null) // Or channel.close(e)
        }
        awaitClose {}
    }


    override suspend fun updateUserProfile(userId: String, profileData: UserProfileData): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val currentUser = ParseUser.getCurrentUser()
            if (currentUser == null || currentUser.objectId != userId) {
                continuation.resume(Result.Error(Exception("Not authorized or user not found to update profile.")))
                return@suspendCancellableCoroutine
            }

            profileData.displayName?.let { currentUser.put(KEY_DISPLAY_NAME, it) }
            profileData.profilePictureUrl?.let { currentUser.put(KEY_PROFILE_PICTURE_URL, it) } // Assumes URL is already uploaded
            profileData.bio?.let { currentUser.put(KEY_BIO, it) }
            profileData.location?.let { currentUser.put(KEY_LOCATION, it) }
            profileData.farmName?.let { currentUser.put(KEY_FARM_NAME, it) }
            profileData.interests?.let { currentUser.put(KEY_INTERESTS, it) }
            profileData.phoneNumber?.let { currentUser.put(KEY_PHONE_NUMBER, it) }
            // Counts are usually managed by backend logic/cloud functions, not set directly by client
            // currentUser.put(KEY_FOLLOWER_COUNT, profileData.followerCount)
            // currentUser.put(KEY_FOLLOWING_COUNT, profileData.followingCount)
            // currentUser.put(KEY_POST_COUNT, profileData.postCount)
            profileData.lastActiveTimestamp?.let { currentUser.put(KEY_LAST_ACTIVE_TIMESTAMP, it) }
            currentUser.put(KEY_IS_VERIFIED_FARMER, profileData.isVerifiedFarmer)
            currentUser.put(KEY_IS_ENTHUSIAST, profileData.isEnthusiast)
            currentUser.put(KEY_IS_KYC_VERIFIED, profileData.isKycVerified)
            profileData.additionalProperties?.let { currentUser.put(KEY_ADDITIONAL_PROPERTIES, it) }
            // Email is special, usually user.email = if different and verified
            // profileData.email?.let { if(currentUser.email != it) currentUser.email = it }


            currentUser.saveInBackground { e ->
                if (e == null) {
                    Timber.d("Parse UserProfile updated successfully for: $userId")
                    continuation.resume(Result.Success(Unit))
                } else {
                    Timber.e(e, "Parse UserProfile update error for: $userId")
                    continuation.resume(Result.Error(e))
                }
            }
        }
    }

    override fun getCurrentUserId(): String? {
        return ParseUser.getCurrentUser()?.objectId
    }

    // --- Mappers ---
    private fun mapParseUserToAuthUser(parseUser: ParseUser): AuthUser {
        return AuthUser(
            uid = parseUser.objectId,
            email = parseUser.email,
            isEmailVerified = parseUser.getBoolean("emailVerified"), // Default Parse field
            displayName = parseUser.getString(KEY_DISPLAY_NAME) ?: parseUser.username // Fallback to username
        )
    }

    private fun mapParseUserToUserProfileData(parseUser: ParseUser): UserProfileData {
        return UserProfileData(
            userId = parseUser.objectId,
            email = parseUser.email,
            displayName = parseUser.getString(KEY_DISPLAY_NAME) ?: parseUser.username,
            profilePictureUrl = parseUser.getString(KEY_PROFILE_PICTURE_URL),
            bio = parseUser.getString(KEY_BIO),
            location = parseUser.getString(KEY_LOCATION),
            farmName = parseUser.getString(KEY_FARM_NAME),
            interests = parseUser.getList<String>(KEY_INTERESTS),
            phoneNumber = parseUser.getString(KEY_PHONE_NUMBER),
            followerCount = parseUser.getInt(KEY_FOLLOWER_COUNT),
            followingCount = parseUser.getInt(KEY_FOLLOWING_COUNT),
            postCount = parseUser.getInt(KEY_POST_COUNT),
            lastActiveTimestamp = parseUser.getLong(KEY_LAST_ACTIVE_TIMESTAMP).takeIf { it > 0 },
            joinDateTimestamp = parseUser.createdAt?.time ?: System.currentTimeMillis(),
            isVerifiedFarmer = parseUser.getBoolean(KEY_IS_VERIFIED_FARMER),
            isEnthusiast = parseUser.getBoolean(KEY_IS_ENTHUSIAST),
            isKycVerified = parseUser.getBoolean(KEY_IS_KYC_VERIFIED),
            additionalProperties = parseUser.getMap<String>(KEY_ADDITIONAL_PROPERTIES)
        )
    }
}
