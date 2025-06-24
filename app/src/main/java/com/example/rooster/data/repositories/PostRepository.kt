package com.example.rooster.data.repositories

import com.example.rooster.data.entities.Comment
import com.example.rooster.data.entities.InteractionType
import com.example.rooster.data.entities.Post
import com.example.rooster.data.entities.PostInteraction
import com.example.rooster.data.entities.PostType
import javax.inject.Inject
import javax.inject.Singleton
import com.example.rooster.domain.repository.PostRepository as DomainPostRepository

/**
 * Post repository implementation
 */
@Singleton
class PostRepository
    @Inject
    constructor() : DomainPostRepository {
        // Mock data storage
        private val posts = mutableListOf<Post>()
        private val comments = mutableListOf<Comment>()
        private val interactions = mutableListOf<PostInteraction>()

        override suspend fun createPost(
            userId: String,
            username: String,
            profilePicture: String?,
            content: String,
            mediaUrls: List<String>,
            type: PostType,
        ): Post {
            val post =
                Post(
                    userId = userId,
                    username = username,
                    profilePicture = profilePicture,
                    content = content,
                    mediaUrls = mediaUrls,
                    type = type,
                )
            posts.add(post)
            return post
        }

        override fun getPosts(userId: String): List<Post> {
            // Return all posts for now - in real app would implement proper feed logic
            return posts.sortedByDescending { it.createdAt }
        }

        override fun getPost(postId: String): Post? {
            return posts.find { it.id == postId }
        }

        override suspend fun updatePost(
            postId: String,
            content: String,
        ): Post? {
            val index = posts.indexOfFirst { it.id == postId }
            return if (index >= 0) {
                posts[index] =
                    posts[index].copy(
                        content = content,
                        updatedAt = System.currentTimeMillis(),
                    )
                posts[index]
            } else {
                null
            }
        }

        override suspend fun deletePost(postId: String) {
            posts.removeAll { it.id == postId }
            comments.removeAll { it.postId == postId }
            interactions.removeAll { it.postId == postId }
        }

        override suspend fun createComment(
            postId: String,
            userId: String,
            username: String,
            profilePicture: String?,
            content: String,
        ): Comment {
            val comment =
                Comment(
                    postId = postId,
                    userId = userId,
                    username = username,
                    profilePicture = profilePicture,
                    content = content,
                )
            comments.add(comment)

            // Update post comment count
            val postIndex = posts.indexOfFirst { it.id == postId }
            if (postIndex >= 0) {
                posts[postIndex] =
                    posts[postIndex].copy(
                        commentsCount = posts[postIndex].commentsCount + 1,
                    )
            }

            return comment
        }

        override fun getComments(postId: String): List<Comment> {
            return comments.filter { it.postId == postId }
                .sortedByDescending { it.createdAt }
        }

        override suspend fun createPostInteraction(
            postId: String,
            userId: String,
            type: InteractionType,
        ): PostInteraction {
            val interaction =
                PostInteraction(
                    postId = postId,
                    userId = userId,
                    type = type,
                )
            interactions.add(interaction)

            // Update post interaction counts
            val postIndex = posts.indexOfFirst { it.id == postId }
            if (postIndex >= 0) {
                when (type) {
                    InteractionType.LIKE -> {
                        posts[postIndex] =
                            posts[postIndex].copy(
                                likesCount = posts[postIndex].likesCount + 1,
                            )
                    }
                    InteractionType.SHARE -> {
                        posts[postIndex] =
                            posts[postIndex].copy(
                                sharesCount = posts[postIndex].sharesCount + 1,
                            )
                    }
                    else -> {} // Handle other interaction types as needed
                }
            }

            return interaction
        }

        override fun getPostInteractions(postId: String): List<PostInteraction> {
            return interactions.filter { it.postId == postId }
        }
    }
