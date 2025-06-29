package com.example.rooster.feature.community.di

import android.content.Context
import androidx.room.Room
import com.example.rooster.feature.community.data.local.CommunityDatabase
import com.example.rooster.feature.community.data.local.CommunityDatabaseMigrations // Import Migrations
import com.example.rooster.feature.community.data.local.dao.CommentDao
import com.example.rooster.feature.community.data.local.dao.CommunityUserProfileDao
import com.example.rooster.feature.community.data.local.dao.PostDao
import com.example.rooster.feature.community.data.remote.CommunityRemoteDataSource
import com.example.rooster.feature.community.data.remote.FirebaseCommunityDataSource
import com.example.rooster.feature.community.data.repository.CommentRepositoryImpl // To be created
import com.example.rooster.feature.community.data.repository.CommunityUserProfileRepositoryImpl // To be created
import com.example.rooster.feature.community.data.repository.PostRepositoryImpl // To be created
import com.example.rooster.feature.community.domain.repository.CommentRepository
import com.example.rooster.feature.community.domain.repository.CommunityUserProfileRepository
import com.example.rooster.feature.community.domain.repository.PostRepository
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
        // TODO: Add proper migrations for production.
        // .fallbackToDestructiveMigration() // Replaced with addMigrations
 feature/phase1-foundations-community-likes
        .addMigrations(CommunityDatabaseMigrations.MIGRATION_1_2) // Added migration
=======
 feature/phase1-foundations-community-likes
        .addMigrations(CommunityDatabaseMigrations.MIGRATION_1_2) // Added migration
=======
        .addMigrations() // Add actual Migration objects here when schema changes
 main
 main
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
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CommunityBindsModule {

    @Binds
    @Singleton
    abstract fun bindCommunityRemoteDataSource(
        impl: FirebaseCommunityDataSource
    ): CommunityRemoteDataSource

    // Repository implementations are yet to be created.
    // These bindings are placeholders.

 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
<<jules/arch-assessment-1
 main
 main
 main
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
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======
 jules/arch-assessment-1
=======

    // @Binds
    // @Singleton
    // abstract fun bindCommunityUserProfileRepository(
    //     impl: CommunityUserProfileRepositoryImpl
    // ): CommunityUserProfileRepository

    // @Binds
    // @Singleton
    // abstract fun bindPostRepository(
    //     impl: PostRepositoryImpl
    // ): PostRepository

    // @Binds
    // @Singleton
    // abstract fun bindCommentRepository(
    //     impl: CommentRepositoryImpl
    // ): CommentRepository
main
 main
 main
 main
}
