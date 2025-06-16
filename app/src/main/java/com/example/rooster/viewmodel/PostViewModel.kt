package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostViewModel : ViewModel() {
    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    fun createPost(
        text: String,
        mediaUrls: List<String>,
        mediaTypes: List<com.example.rooster.models.MediaType>,
        onResult: (postId: String) -> Unit,
    ) = viewModelScope.launch {
        _isPosting.value = true
        try {
            val post = PostRepository.createPost(text, mediaUrls, mediaTypes)
            onResult(post.id)
        } catch (_: Exception) {
        } finally {
            _isPosting.value = false
        }
    }
}
