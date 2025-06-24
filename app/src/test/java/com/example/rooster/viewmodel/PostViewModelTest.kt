package com.example.rooster.viewmodel

import app.cash.turbine.test
import com.example.rooster.model.Post
import com.example.rooster.repository.PostRepository
import com.example.rooster.repository.UserRepository
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {
    private lateinit var viewModel: PostViewModel
    private lateinit var postRepository: PostRepository
    private lateinit var userRepository: UserRepository
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        postRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        viewModel = PostViewModel(postRepository, userRepository)
    }

    @Test
    fun `initial posts list is empty`() =
        testScope.runTest {
            viewModel.posts.test {
                val posts = awaitItem()
                assertEquals(emptyList<Post>(), posts)
            }
        }

    // Add more tests for loadPosts, selectPost, deletePost, etc. as needed
}
