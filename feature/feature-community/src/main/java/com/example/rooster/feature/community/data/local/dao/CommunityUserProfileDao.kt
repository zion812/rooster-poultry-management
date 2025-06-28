package com.example.rooster.feature.community.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.feature.community.data.local.model.CommunityUserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommunityUserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: CommunityUserProfileEntity)

    @Update
    suspend fun updateProfile(profile: CommunityUserProfileEntity)

    @Query("SELECT * FROM community_user_profiles WHERE userId = :userId")
    fun getProfileByUserId(userId: String): Flow<CommunityUserProfileEntity?>

    @Query("SELECT * FROM community_user_profiles WHERE userId = :userId")
    suspend fun getProfileByUserIdSuspend(userId: String): CommunityUserProfileEntity?

    @Query("SELECT * FROM community_user_profiles WHERE needsSync = 1")
    suspend fun getUnsyncedProfilesSuspend(): List<CommunityUserProfileEntity>

    // Add other queries as needed, e.g., search profiles by display name
    @Query("SELECT * FROM community_user_profiles WHERE displayName LIKE '%' || :query || '%'")
    fun searchProfiles(query: String): Flow<List<CommunityUserProfileEntity>>
}
