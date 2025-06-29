package com.example.rooster.feature.community.domain.usecase

import com.example.rooster.core.common.Result
import com.example.rooster.core.common.user.UserIdProvider
import com.example.rooster.feature.community.domain.repository.PostRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val postRepository: PostRepository,
    private val userIdProvider: UserIdProvider
) {
    suspend operator fun invoke(postId: String): Result<Unit> {
        val currentUserId = userIdProvider.getCurrentUserId()
        return if (currentUserId != null) {
            postRepository.likePost(postId, currentUserId)
        } else {
            Result.Error(Exception("User not authenticated to like post"))
        }
    }
}
