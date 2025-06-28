package com.example.rooster.feature.community.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.Comment
import com.example.rooster.feature.community.domain.model.CommunityUserProfile
import com.example.rooster.feature.community.domain.model.Post
import com.example.rooster.feature.community.domain.repository.FeedType
import kotlinx.coroutines.flow.Flow

interface CommunityRemoteDataSource {
    // User Profiles
    fun getCommunityUserProfileStream(userId: String): Flow<Result<CommunityUserProfile?>>
    suspend fun createCommunityUserProfile(profile: CommunityUserProfile): Result<Unit>
    suspend fun updateCommunityUserProfile(profile: CommunityUserProfile): Result<Unit>

    // Posts
    fun getPostsStream(feedType: FeedType, userId: String? = null, tag: String? = null): Flow<Result<List<Post>>>
    fun getPostDetailsStream(postId: String): Flow<Result<Post?>>
    suspend fun createPost(post: Post): Result<String> // Returns ID
    suspend fun updatePost(post: Post): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit> // Simplified, auth handled by rules
    suspend fun likePost(postId: String, userId: String): Result<Unit>
    suspend fun unlikePost(postId: String, userId: String): Result<Unit>

    // Comments
    fun getCommentsStream(postId: String): Flow<Result<List<Comment>>>
    fun getCommentRepliesStream(commentId: String): Flow<Result<List<Comment>>>
    suspend fun addComment(comment: Comment): Result<String> // Returns ID
    suspend fun updateComment(comment: Comment): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit> // Simplified
    suspend fun likeComment(commentId: String, userId: String): Result<Unit>
    suspend fun unlikeComment(commentId: String, userId: String): Result<Unit>
}
