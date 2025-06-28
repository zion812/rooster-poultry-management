package com.example.rooster.feature.community.data.remote

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.Comment
import com.example.rooster.feature.community.domain.model.Post
import com.example.rooster.feature.community.domain.repository.FeedType // Keep for filtering logic

// Interface for primary CRUD of Posts and Comments on Parse
interface PostCommentParseDataSource {
    // Posts
    suspend fun getPosts(
        feedType: FeedType,
        userId: String?, // For USER_SPECIFIC or potentially posts by users someone follows
        tag: String?,    // For TAG_SPECIFIC
        pageSize: Int,
        page: Int
    ): Result<List<Post>>

    suspend fun getPostDetails(postId: String): Result<Post?>
    suspend fun createPost(post: Post): Result<String> // Returns ID of created post
    suspend fun updatePost(post: Post): Result<Unit>
    suspend fun deletePost(postId: String): Result<Unit> // Auth handled by Parse ACLs / Cloud Code

    // Comments
    suspend fun getCommentsForPost(postId: String, pageSize: Int, page: Int): Result<List<Comment>>
    suspend fun getRepliesForComment(commentId: String, pageSize: Int, page: Int): Result<List<Comment>>
    suspend fun addComment(comment: Comment): Result<String> // Returns ID of created comment
    suspend fun updateComment(comment: Comment): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit> // Auth handled by Parse ACLs / Cloud Code

    // Like/Unlike might directly interact with Parse objects or Cloud Code
    // These could also be part of a separate "InteractionDataSource"
    suspend fun likePost(postId: String, userId: String): Result<Unit>
    suspend fun unlikePost(postId: String, userId: String): Result<Unit>
    suspend fun likeComment(commentId: String, userId: String): Result<Unit>
    suspend fun unlikeComment(commentId: String, userId: String): Result<Unit>
}
