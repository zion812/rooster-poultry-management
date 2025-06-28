package com.example.rooster.feature.community.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.feature.community.data.local.CommunityDatabase
import com.example.rooster.feature.community.data.local.dao.CommentDao
import com.example.rooster.feature.community.data.local.dao.CommunityUserProfileDao
import com.example.rooster.feature.community.data.local.dao.PostDao
import com.example.rooster.feature.community.data.remote.CommunityRemoteDataSource
import com.example.rooster.feature.community.data.remote.FirebaseCommunityDataSource
import com.example.rooster.feature.community.data.remote.ChatRemoteDataSource // New
import com.example.rooster.feature.community.data.remote.FirebaseRtdbSocialDataSource // New Impl for Chat
import com.example.rooster.feature.community.data.repository.CommentRepositoryImpl
import com.example.rooster.feature.community.data.repository.CommunityUserProfileRepositoryImpl
import com.example.rooster.feature.community.data.repository.PostRepositoryImpl
import com.example.rooster.feature.community.data.repository.ChatRepositoryImpl // New
import com.example.rooster.feature.community.domain.repository.CommentRepository
import com.example.rooster.feature.community.domain.repository.CommunityUserProfileRepository
import com.example.rooster.feature.community.domain.repository.PostRepository
import com.example.rooster.feature.community.domain.repository.ChatRepository // New
// Assuming FirebaseFirestore and DatabaseReference are provided by another module (e.g., app or core-firebase)
import com.google.firebase.database.DatabaseReference // For RTDB
import com.google.firebase.database.FirebaseDatabase // For RTDB
// Assuming FirebaseFirestore is provided by another module (e.g., app or core-firebase)
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommunityProvidesModule {

    @Provides
    @Singleton
    fun provideCommunityDatabase(@ApplicationContext context: Context): CommunityDatabase {
        return Room.databaseBuilder(
            context,
            CommunityDatabase::class.java,
            "community_database.db"
        )
        // TODO: Add proper migrations for production instead of fallbackToDestructiveMigration.
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideCommunityUserProfileDao(database: CommunityDatabase): CommunityUserProfileDao {
        return database.communityUserProfileDao()
    }

    @Provides
    @Singleton
    fun providePostDao(database: CommunityDatabase): PostDao {
        return database.postDao()
    }

    @Provides
    @Singleton
    fun provideCommentDao(database: CommunityDatabase): CommentDao {
        return database.commentDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseRealtimeDatabaseReference(): DatabaseReference {
        // Provides the root reference. Specific paths will be handled in DataSource.
        return FirebaseDatabase.getInstance().reference
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CommunityBindsModule {

    // For Firestore-based non-chat social data (e.g., if full Posts/UserProfiles are on Firestore)
    @Binds
    @Singleton
    abstract fun bindCommunityRemoteDataSource(
        impl: FirebaseCommunityDataSource
    ): CommunityRemoteDataSource

    // Specific for Chat on RTDB
    @Binds
    @Singleton
    abstract fun bindChatRemoteDataSource(
        impl: FirebaseRtdbSocialDataSource
    ): ChatRemoteDataSource


    @Binds
    @Singleton
    abstract fun bindCommunityUserProfileRepository(
        impl: CommunityUserProfileRepositoryImpl
    ): CommunityUserProfileRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        impl: PostRepositoryImpl
    ): PostRepository

    @Binds
    @Singleton
    abstract fun bindCommentRepository(
        impl: CommentRepositoryImpl
    ): CommentRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository
}
