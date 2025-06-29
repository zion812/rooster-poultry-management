package com.example.rooster.feature.community.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.rooster.feature.community.data.local.dao.CommentDao
import com.example.rooster.feature.community.data.local.dao.CommunityUserProfileDao
import com.example.rooster.feature.community.data.local.dao.PostDao
import com.example.rooster.feature.community.data.local.model.CommentEntity
import com.example.rooster.feature.community.data.local.model.CommunityUserProfileEntity
import com.example.rooster.feature.community.data.local.model.PostEntity

@Database(
    entities = [
        CommunityUserProfileEntity::class,
        PostEntity::class,
        CommentEntity::class
    ],
    version = 2, // Incremented for syncAttempts and lastSyncAttemptTimestamp
    exportSchema = true // Recommended
)
@TypeConverters(CommunityTypeConverters::class)
abstract class CommunityDatabase : RoomDatabase() {
    abstract fun communityUserProfileDao(): CommunityUserProfileDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
}
