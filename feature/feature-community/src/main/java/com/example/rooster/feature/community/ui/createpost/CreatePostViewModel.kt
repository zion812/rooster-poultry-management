package com.example.rooster.feature.community.ui.createpost // New package

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.user.UserIdProvider
import com.example.rooster.feature.community.domain.model.Post
import com.example.rooster.feature.community.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreatePostFormState(
    val contentText: String = "",
    // TODO: Add fields for imageUris, videoUri, tags, location etc. later
    val isSubmitting: Boolean = false,
    val submissionError: String? = null,
    val submissionSuccess: Boolean = false,
    val createdPostId: String? = null
)

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userIdProvider: UserIdProvider // To get authorUserId
) : ViewModel() {

    private val _formState = MutableStateFlow(CreatePostFormState())
    val formState: StateFlow<CreatePostFormState> = _formState.asStateFlow()

    fun onContentTextChange(text: String) {
        _formState.value = _formState.value.copy(contentText = text, submissionError = null, submissionSuccess = false)
    }

    fun submitPost() {
        val currentContent = _formState.value.contentText
        if (currentContent.isBlank()) {
            _formState.value = _formState.value.copy(submissionError = "Post content cannot be empty.")
            return
        }

        val authorId = userIdProvider.getCurrentUserId()
        if (authorId == null) {
            _formState.value = _formState.value.copy(submissionError = "User not authenticated. Please log in.", isSubmitting = false)
            return
        }

        // For authorDisplayName and authorProfilePictureUrl, these would ideally be fetched
        // from the current user's profile. For simplicity in this ViewModel,
        // they could be passed in or fetched upon ViewModel init if needed for the Post object.
        // Or the backend/repository could populate them based on authorId.
        // For now, sending minimal info, assuming backend or a use case might enrich it.
        // A better approach would be to fetch current user's display name here.
        val placeholderDisplayName = "Current User" // TODO: Replace with actual display name

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSubmitting = true, submissionError = null, submissionSuccess = false)

            val newPost = Post(
                postId = UUID.randomUUID().toString(), // Repository can also generate if ID is empty
                authorUserId = authorId,
                authorDisplayName = placeholderDisplayName, // Placeholder
                authorProfilePictureUrl = null, // Placeholder
                contentText = currentContent.trim(),
                createdTimestamp = System.currentTimeMillis(),
                updatedTimestamp = null,
                // Other fields like imageUrls, videoUrl, tags will be empty for this basic version
            )

            val result = postRepository.createPost(newPost)
            _formState.value = when (result) {
                is Result.Success -> _formState.value.copy(
                    isSubmitting = false,
                    submissionSuccess = true,
                    createdPostId = result.data,
                    contentText = "" // Clear content on success
                )
                is Result.Error -> _formState.value.copy(
                    isSubmitting = false,
                    submissionError = result.exception.message ?: "Failed to create post."
                )
                Result.Loading -> _formState.value.copy(isSubmitting = true) // Should not happen from suspend fun
            }
        }
    }

    fun resetSubmissionStatus() {
        _formState.value = _formState.value.copy(submissionError = null, submissionSuccess = false, createdPostId = null)
    }
}
