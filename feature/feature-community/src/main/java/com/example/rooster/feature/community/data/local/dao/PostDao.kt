package com.example.rooster.feature.community.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rooster.feature.community.data.local.model.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("SELECT * FROM community_posts WHERE postId = :postId")
    fun getPostById(postId: String): Flow<PostEntity?>

    @Query("SELECT * FROM community_posts WHERE postId = :postId AND needsSync = 1")
    suspend fun getUnsyncedPostByIdSuspend(postId: String): PostEntity?

    @Query("SELECT * FROM community_posts WHERE postId = :postId") 
    suspend fun getPostByIdSuspend(postId: String): PostEntity?

    @Query("SELECT * FROM community_posts ORDER BY createdTimestamp DESC")
    fun getAllPosts(): Flow<List<PostEntity>> 

    @Query("SELECT * FROM community_posts WHERE authorUserId = :userId ORDER BY createdTimestamp DESC")
    fun getPostsByAuthor(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM community_posts WHERE tags LIKE '%' || :tag || '%' ORDER BY createdTimestamp DESC")
    fun getPostsByTag(tag: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM community_posts WHERE needsSync = 1 ORDER BY createdTimestamp ASC") 
    suspend fun getUnsyncedPostsSuspend(): List<PostEntity>

    @Query("DELETE FROM community_posts WHERE postId = :postId")
    suspend fun deletePostById(postId: String)

    @Query("UPDATE community_posts SET likeCount = :newLikeCount WHERE postId = :postId")
    suspend fun updateLikeCount(postId: String, newLikeCount: Int)

    @Query("UPDATE community_posts SET commentCount = :newCommentCount WHERE postId = :postId")
    suspend fun updateCommentCount(postId: String, newCommentCount: Int)
}
