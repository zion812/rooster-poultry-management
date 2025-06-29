package com.example.rooster.feature.community.domain.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    fun getPosts(feedType: FeedType = FeedType.GLOBAL_RECENT, userId: String? = null, forceRefresh: Boolean = false): Flow<Result<List<Post>>>
    fun getPostDetails(postId: String): Flow<Result<Post?>>
    suspend fun createPost(post: Post): Result<String> // Returns ID of created post
    suspend fun updatePost(post: Post): Result<Unit>
    suspend fun deletePost(postId: String, authorUserId: String): Result<Unit> // Ensure user can only delete their own
    suspend fun likePost(postId: String, userId: String): Result<Unit> // Or addReaction
    suspend fun unlikePost(postId: String, userId: String): Result<Unit> // Or removeReaction

    // Methods for SyncWorker
    suspend fun getUnsyncedPostEntities(): List<com.example.rooster.feature.community.data.local.model.PostEntity>
    suspend fun syncPostRemote(post: Post): Result<Unit> // Returns ID of synced post from remote, or error
    suspend fun updateLocalPostEntity(postEntity: com.example.rooster.feature.community.data.local.model.PostEntity)
    fun mapPostEntityToDomain(postEntity: com.example.rooster.feature.community.data.local.model.PostEntity): Post

    // Add methods for reporting posts, getting user's own posts, etc.
}

enum class FeedType {
    GLOBAL_RECENT, // All recent posts
    FOLLOWING,     // Posts from users the current user follows
    USER_SPECIFIC, // Posts by a specific user (requires userId)
    TAG_SPECIFIC   // Posts with a specific tag (requires tag)
    // Consider pagination parameters for all feed types
}
