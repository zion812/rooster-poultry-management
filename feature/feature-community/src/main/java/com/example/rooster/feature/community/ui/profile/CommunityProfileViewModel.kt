package com.example.rooster.feature.community.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.community.domain.model.CommunityUserProfile
import com.example.rooster.feature.community.domain.repository.CommunityUserProfileRepository
// TODO: Import PostRepository and Post model if displaying user's posts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Success(
        val profile: CommunityUserProfile
        // val posts: List<Post> // TODO: Add user's posts
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

@HiltViewModel
class CommunityProfileViewModel @Inject constructor(
    private val userProfileRepository: CommunityUserProfileRepository,
    // private val postRepository: PostRepository, // TODO: Inject when fetching user's posts
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val userId: String = savedStateHandle.get<String>("userId") ?: ""

    init {
        if (userId.isNotBlank()) {
            fetchProfile(userId)
            // fetchUserPosts(userId) // TODO
        } else {
            _uiState.value = ProfileUiState.Error("User ID not provided.")
        }
    }

    fun fetchProfile(userIdToFetch: String = userId, forceRefresh: Boolean = false) {
        userProfileRepository.getCommunityUserProfile(userIdToFetch, forceRefresh)
            .onEach { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> ProfileUiState.Loading
                    is Result.Success -> {
                        result.data?.let { profile ->
                            // If already have posts, combine, else just profile
                            val currentSuccessState = _uiState.value as? ProfileUiState.Success
                            ProfileUiState.Success(
                                profile = profile
                                // posts = currentSuccessState?.posts ?: emptyList() // TODO
                            )
                        } ?: ProfileUiState.Error("User profile not found.")
                    }
                    is Result.Error -> ProfileUiState.Error(result.exception.message ?: "Unknown error fetching profile")
                }
            }.launchIn(viewModelScope)
    }

    // TODO: Implement fetchUserPosts(userId: String)
    // This would call postRepository.getPosts(FeedType.USER_SPECIFIC, userId)
    // and update the _uiState.Success with the posts.
    // Consider how to handle loading/error states for posts separately or combined.
}
