package com.example.rooster.feature.community.ui.feed // New package for feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.Post
import com.example.rooster.feature.community.domain.repository.FeedType
// import com.example.rooster.feature.community.domain.repository.PostRepository // Now using use cases
import com.example.rooster.feature.community.domain.usecase.LikePostUseCase
import com.example.rooster.feature.community.domain.usecase.UnlikePostUseCase
import com.example.rooster.feature.community.domain.usecase.GetPostsUseCase // Assuming a GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

sealed interface PostFeedUiState {
    data object Loading : PostFeedUiState
    data class Success(val posts: List<Post>) : PostFeedUiState
    data class Error(val message: String) : PostFeedUiState
}

// import com.example.rooster.feature.community.domain.repository.PostRepository // Now using use cases
import com.example.rooster.feature.community.domain.usecase.LikePostUseCase
import com.example.rooster.feature.community.domain.usecase.UnlikePostUseCase
import com.example.rooster.feature.community.domain.repository.PostRepository // Keep for getPosts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostFeedUiState {
    data object Loading : PostFeedUiState
    data class Success(val posts: List<Post>) : PostFeedUiState
    data class Error(val message: String) : PostFeedUiState
}

// For one-off events like error messages from like/unlike
sealed interface PostFeedSingleEvent {
    data class LikeUnlikeError(val message: String) : PostFeedSingleEvent
}

import com.example.rooster.core.common.user.UserIdProvider // Added

@HiltViewModel
class PostFeedViewModel @Inject constructor(
    private val postRepository: PostRepository, // Keep for fetching posts
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val userIdProvider: UserIdProvider // Added
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostFeedUiState>(PostFeedUiState.Loading)
    val uiState: StateFlow<PostFeedUiState> = _uiState.asStateFlow()

    val currentUserId: StateFlow<String?> = MutableStateFlow(userIdProvider.getCurrentUserId()).asStateFlow()


    // TODO: Allow changing feedType (e.g., Global, Following, UserSpecific, TagSpecific) via UI actions
    private var currentFeedType: FeedType = FeedType.GLOBAL_RECENT
    private var currentUserIdForFeed: String? = null // For USER_SPECIFIC feed
    private var currentTagForFeed: String? = null   // For TAG_SPECIFIC feed

    init {
        fetchPosts()
    }

    fun fetchPosts(forceRefresh: Boolean = false) {
        // For USER_SPECIFIC or TAG_SPECIFIC, ensure the relevant ID/tag is set before calling
        // For now, defaults to GLOBAL_RECENT
        postRepository.getPosts(
            feedType = currentFeedType,
            userId = if (currentFeedType == FeedType.USER_SPECIFIC) currentUserIdForFeed else null,
            // TODO: Pass tag if currentFeedType is TAG_SPECIFIC and PostRepository.getPosts is updated
            forceRefresh = forceRefresh
        )
        .onEach { result ->
            _uiState.value = when (result) {
                is Result.Loading -> PostFeedUiState.Loading
                is Result.Success -> PostFeedUiState.Success(result.data)
                is Result.Error -> PostFeedUiState.Error(result.exception.message ?: "Unknown error fetching posts")
            }
        }
        .launchIn(viewModelScope)
    }

    fun setFeedType(feedType: FeedType, associatedId: String? = null) {
        currentFeedType = feedType
        when (feedType) {
            FeedType.USER_SPECIFIC -> currentUserIdForFeed = associatedId
            FeedType.TAG_SPECIFIC -> currentTagForFeed = associatedId // Assuming associatedId is the tag
            else -> {
                currentUserIdForFeed = null
                currentTagForFeed = null
            }
        }
        fetchPosts(forceRefresh = true) // Refresh when feed type changes
    }

    // TODO: Add methods for pagination (loadMorePosts)

    private val _singleEventFlow = MutableSharedFlow<PostFeedSingleEvent>()
    val singleEventFlow: SharedFlow<PostFeedSingleEvent> = _singleEventFlow

    fun onLikeClicked(postId: String) {
        viewModelScope.launch {
            when (val result = likePostUseCase(postId)) {
                is Result.Success -> {
                    // Data is updated locally by the repository, which should trigger recomposition
                    // of the list via the _uiState flow.
                    // No explicit state update needed here if PostRepository's Flow emits on change.
                }
                is Result.Error -> {
                    _singleEventFlow.emit(PostFeedSingleEvent.LikeUnlikeError(result.exception.message ?: "Failed to like post"))
                }
                else -> { /* Loading state not typically handled for this kind of action directly in VM event */ }
            }
        }
    }

    fun onUnlikeClicked(postId: String) {
        viewModelScope.launch {
            when (val result = unlikePostUseCase(postId)) {
                is Result.Success -> {
                    // Similar to like, local data update in repo should refresh UI.
                }
                is Result.Error -> {
                    _singleEventFlow.emit(PostFeedSingleEvent.LikeUnlikeError(result.exception.message ?: "Failed to unlike post"))
                }
                else -> {}
            }
        }
    }
}
