package com.example.rooster.feature.community.ui.feed // New package for feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.toUserFriendlyMessage
import com.example.rooster.feature.community.domain.model.Post
import com.example.rooster.feature.community.domain.repository.FeedType
import com.example.rooster.feature.community.domain.repository.PostRepository
import com.example.rooster.feature.community.domain.usecase.LikePostUseCase
import com.example.rooster.feature.community.domain.usecase.UnlikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import android.content.Context
import com.example.rooster.core.common.user.UserIdProvider
import kotlinx.coroutines.launch

sealed interface PostFeedUiState {
    data object Loading : PostFeedUiState
    data class Success(val posts: List<Post>) : PostFeedUiState
    data class Error(val message: String) : PostFeedUiState
}

sealed interface PostFeedSingleEvent {
    data class LikeUnlikeError(val message: String) : PostFeedSingleEvent
}

@HiltViewModel
class PostFeedViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val postRepository: PostRepository,
    private val likePostUseCase: LikePostUseCase,
    private val unlikePostUseCase: UnlikePostUseCase,
    private val userIdProvider: UserIdProvider
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
        _uiState.value = PostFeedUiState.Loading
        postRepository.getPosts(
            feedType = currentFeedType,
            userId = currentUserIdForFeed,
            forceRefresh = forceRefresh
        ).onEach { result ->
            when (result) {
                is Result.Success -> {
                    _uiState.value = PostFeedUiState.Success(result.data)
                }

                is Result.Error -> {
                    val msg = result.exception.toUserFriendlyMessage(appContext)
                    _uiState.value = PostFeedUiState.Error(msg)
                }

                is Result.Loading -> {
                    _uiState.value = PostFeedUiState.Loading
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setFeedType(feedType: FeedType) {
        currentFeedType = feedType
        fetchPosts(forceRefresh = true)
    }

    fun refreshPosts() {
        fetchPosts(forceRefresh = true)
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
                    _singleEventFlow.emit(
                        PostFeedSingleEvent.LikeUnlikeError(
                            result.exception.toUserFriendlyMessage(appContext)
                        )
                    )
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
                    _singleEventFlow.emit(
                        PostFeedSingleEvent.LikeUnlikeError(
                            result.exception.toUserFriendlyMessage(appContext)
                        )
                    )
                }
                else -> {}
            }
        }
    }
}
