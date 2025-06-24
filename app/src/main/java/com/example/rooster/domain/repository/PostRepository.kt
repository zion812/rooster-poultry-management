package com.example.rooster.domain.repository

import com.example.rooster.data.entities.Comment
import com.example.rooster.data.entities.InteractionType
import com.example.rooster.data.entities.Post
import com.example.rooster.data.entities.PostInteraction
import com.example.rooster.data.entities.PostType

/**
 * Domain interface for post operations
 */
interface PostRepository {
    suspend fun createPost(
        userId: String,
        username: String,
        profilePicture: String?,
        content: String,
        mediaUrls: List<String>,
        type: PostType,
    ): Post

    fun getPosts(userId: String): List<Post>

    fun getPost(postId: String): Post?

    suspend fun updatePost(
        postId: String,
        content: String,
    ): Post?

    suspend fun deletePost(postId: String)

    suspend fun createComment(
        postId: String,
        userId: String,
        username: String,
        profilePicture: String?,
        content: String,
    ): Comment

    fun getComments(postId: String): List<Comment>

    suspend fun createPostInteraction(
        postId: String,
        userId: String,
        type: InteractionType,
    ): PostInteraction

    fun getPostInteractions(postId: String): List<PostInteraction>
}
