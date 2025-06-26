package com.example.rooster.feature.community.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.feature.community.data.local.model.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Update
    suspend fun updateComment(comment: CommentEntity)

    @Query("SELECT * FROM community_comments WHERE commentId = :commentId")
    fun getCommentById(commentId: String): Flow<CommentEntity?>

    @Query("SELECT * FROM community_comments WHERE postId = :postId AND parentCommentId IS NULL ORDER BY createdTimestamp ASC")
    fun getCommentsForPost(postId: String): Flow<List<CommentEntity>> // Top-level comments

    @Query("SELECT * FROM community_comments WHERE parentCommentId = :parentCommentId ORDER BY createdTimestamp ASC")
    fun getRepliesForComment(parentCommentId: String): Flow<List<CommentEntity>>

    @Query("SELECT * FROM community_comments WHERE needsSync = 1 ORDER BY createdTimestamp ASC")
    suspend fun getUnsyncedCommentsSuspend(): List<CommentEntity>

    @Query("DELETE FROM community_comments WHERE commentId = :commentId")
    suspend fun deleteCommentById(commentId: String)

    @Query("UPDATE community_comments SET likeCount = :newLikeCount WHERE commentId = :commentId")
    suspend fun updateLikeCount(commentId: String, newLikeCount: Int)
}
