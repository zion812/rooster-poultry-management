package com.example.rooster.data

import com.example.rooster.models.Post
import kotlinx.coroutines.delay

/**
 * Stub repository for user posts.
 */
object PostRepository {
    private val posts = mutableMapOf<String, Post>()

    /** Creates a new post and returns it. */
    suspend fun createPost(
        text: String,
        mediaUrls: List<String>,
        mediaTypes: List<com.example.rooster.models.MediaType>,
    ): Post {
        delay(500)
        val id = System.currentTimeMillis().toString()
        val post =
            Post(
                id = id,
                authorId = "currentUser",
                text = text,
                mediaUrls = mediaUrls,
                mediaTypes = mediaTypes,
                timestamp = System.currentTimeMillis(),
            )
        posts[id] = post
        return post
    }

    /** Fetch all posts (stub). */
    suspend fun fetchPosts(): List<Post> {
        delay(200)
        return posts.values.toList()
    }
}
