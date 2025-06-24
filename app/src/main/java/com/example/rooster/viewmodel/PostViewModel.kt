package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.entities.Comment
import com.example.rooster.data.entities.InteractionType
import com.example.rooster.data.entities.Post
import com.example.rooster.data.entities.PostInteraction
import com.example.rooster.data.entities.PostType
import com.example.rooster.domain.repository.PostRepository
import com.example.rooster.domain.repository.UserRepository
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel
    @Inject
    constructor(
        private val postRepository: PostRepository,
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _isPosting = MutableStateFlow(false)
        val isPosting: StateFlow<Boolean> = _isPosting

        private val _isInteracting = MutableStateFlow(false)
        val isInteracting: StateFlow<Boolean> = _isInteracting

        private val _posts = MutableStateFlow<List<Post>>(emptyList())
        val posts: StateFlow<List<Post>> = _posts

        private val _selectedPost = MutableStateFlow<Post?>(null)
        val selectedPost: StateFlow<Post?> = _selectedPost

        init {
            loadPosts()
        }

    private suspend fun getCurrentUserId(): String? {
        return userRepository.getCurrentUser()
    }

    private suspend fun getCurrentUserProfile(): UserProfile? {
        return try {
            val userId = getCurrentUserId()
            if (userId != null) {
                UserProfile(
                    userId = userId,
                    username = "User$userId",
                    profilePicture = null
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    data class UserProfile(
        val userId: String,
        val username: String,
        val profilePicture: String?
    )

    fun createPost(
            text: String,
            mediaUrls: List<String> = emptyList(),
            postType: PostType = PostType.TEXT,
            onResult: (postId: String) -> Unit = {},
        ) = viewModelScope.launch {
            _isPosting.value = true
            try {
                val currentUser = getCurrentUserProfile()
                val post =
                    postRepository.createPost(
                        userId = currentUser?.userId ?: "",
                        username = currentUser?.username ?: "Unknown",
                        profilePicture = currentUser?.profilePicture,
                        content = text,
                        mediaUrls = mediaUrls,
                        type = postType,
                    )
                onResult(post.id)
                loadPosts()
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            } finally {
                _isPosting.value = false
            }
        }

        fun loadPosts() {
            viewModelScope.launch {
                try {
                    val currentUser = getCurrentUserProfile()
                    _posts.value = postRepository.getPosts(currentUser?.userId ?: "")
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun likePost(postId: String) {
            viewModelScope.launch {
                _isInteracting.value = true
                try {
                    val currentUser = getCurrentUserProfile()
                    postRepository.createPostInteraction(
                        postId = postId,
                        userId = currentUser?.userId ?: "",
                        type = InteractionType.LIKE,
                    )
                    loadPosts()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                } finally {
                    _isInteracting.value = false
                }
            }
        }

        fun commentOnPost(
            postId: String,
            content: String,
        ) {
            viewModelScope.launch {
                _isInteracting.value = true
                try {
                    val currentUser = getCurrentUserProfile()
                    val comment =
                        postRepository.createComment(
                            postId = postId,
                            userId = currentUser?.userId ?: "",
                            username = currentUser?.username ?: "Unknown",
                            profilePicture = currentUser?.profilePicture,
                            content = content,
                        )
                    loadPosts()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                } finally {
                    _isInteracting.value = false
                }
            }
        }

        fun sharePost(postId: String) {
            viewModelScope.launch {
                _isInteracting.value = true
                try {
                    val currentUser = getCurrentUserProfile()
                    postRepository.createPostInteraction(
                        postId = postId,
                        userId = currentUser?.userId ?: "",
                        type = InteractionType.SHARE,
                    )
                    loadPosts()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                } finally {
                    _isInteracting.value = false
                }
            }
        }

        fun getPostComments(postId: String): List<Comment> {
            return postRepository.getComments(postId)
        }

        fun getPostInteractions(postId: String): List<PostInteraction> {
            return postRepository.getPostInteractions(postId)
        }

        fun loadPost(postId: String) {
            viewModelScope.launch {
                try {
                    val post = postRepository.getPost(postId)
                    _selectedPost.value = post
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun deletePost(postId: String) {
            viewModelScope.launch {
                try {
                    postRepository.deletePost(postId)
                    loadPosts()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }

        fun updatePost(
            postId: String,
            content: String,
        ) {
            viewModelScope.launch {
                try {
                    postRepository.updatePost(postId, content)
                    loadPosts()
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        }
    }
