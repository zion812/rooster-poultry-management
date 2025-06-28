package com.example.rooster.feature.community.domain.repository

import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {
    fun getCommentsForPost(postId: String, forceRefresh: Boolean = false): Flow<Result<List<Comment>>>
    fun getRepliesForComment(commentId: String, forceRefresh: Boolean = false): Flow<Result<List<Comment>>>
    suspend fun addComment(comment: Comment): Result<String> // Returns ID of created comment
    suspend fun updateComment(comment: Comment): Result<Unit>
    suspend fun deleteComment(commentId: String, authorUserId: String): Result<Unit> // Ensure user can only delete own
    suspend fun likeComment(commentId: String, userId: String): Result<Unit>
    suspend fun unlikeComment(commentId: String, userId: String): Result<Unit>
}
