package com.example.rooster.feature.marketplace.ui.createlisting

import android.net.Uri
import app.cash.turbine.test
import com.example.rooster.core.common.Result
import com.example.rooster.core.common.storage.ImageUploadService
import com.example.rooster.core.common.user.UserIdProvider
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CreateListingViewModelTest {

    // Rule for Main dispatcher replacement for testing Coroutines
    // For newer versions, MainCoroutineRule might be part of JUnit5 extensions or custom.
    // Using TestCoroutineDispatcher directly for now.
    private val testDispatcher = StandardTestDispatcher() // or UnconfinedTestDispatcher()

    // Mock dependencies
    private lateinit var productListingRepository: ProductListingRepository
    private lateinit var userIdProvider: UserIdProvider
    private lateinit var imageUploadService: ImageUploadService

    private lateinit var viewModel: CreateListingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        productListingRepository = mockk()
        userIdProvider = mockk()
        imageUploadService = mockk()

        // Default mock behaviors
        coEvery { userIdProvider.getCurrentUserId() } returns "test_seller_id"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset Main dispatcher after the test
        unmockkAll()
    }

    @Test
    fun `onTitleChange updates formState title`() = runTest {
        viewModel = CreateListingViewModel(productListingRepository, userIdProvider, imageUploadService)
        val newTitle = "New Awesome Product"
        viewModel.onTitleChange(newTitle)
        assertEquals(newTitle, viewModel.formState.value.title)
        assertNull(viewModel.formState.value.submissionError)
        assertFalse(viewModel.formState.value.submissionSuccess)
    }

    @Test
    fun `onImagesSelected updates formState imageUris`() = runTest {
        viewModel = CreateListingViewModel(productListingRepository, userIdProvider, imageUploadService)
        val mockUri1 = mockk<Uri>()
        val mockUri2 = mockk<Uri>()
        val uris = listOf(mockUri1, mockUri2)

        viewModel.onImagesSelected(uris)

        assertEquals(uris, viewModel.formState.value.imageUris)
    }

    @Test
    fun `removeImage updates formState imageUris`() = runTest {
        viewModel = CreateListingViewModel(productListingRepository, userIdProvider, imageUploadService)
        val mockUri1 = mockk<Uri>()
        val mockUri2 = mockk<Uri>()
        viewModel.onImagesSelected(listOf(mockUri1, mockUri2))

        viewModel.removeImage(mockUri1)

        assertEquals(listOf(mockUri2), viewModel.formState.value.imageUris)
    }

    @Test
    fun `submitListing with empty title shows error`() = runTest {
        viewModel = CreateListingViewModel(productListingRepository, userIdProvider, imageUploadService)
        viewModel.onPriceChange("100")
        viewModel.onQuantityChange("1")

        viewModel.submitListing()

        viewModel.formState.test {
            val finalState = awaitItem() // This might be initial, then loading, then error
             // Depending on how state is updated, might need to skip items or await specific condition
            assertEquals("Title, Price, and Quantity are required.", finalState.submissionError)
            assertFalse(finalState.isSubmitting)
            assertFalse(finalState.submissionSuccess)
            coVerify(exactly = 0) { imageUploadService.uploadImages(any(), any()) }
            coVerify(exactly = 0) { productListingRepository.createProductListing(any()) }
        }
    }

    @Test
    fun `submitListing when not logged in shows error`() = runTest {
        coEvery { userIdProvider.getCurrentUserId() } returns null
        viewModel = CreateListingViewModel(productListingRepository, userIdProvider, imageUploadService)
        viewModel.onTitleChange("Test Title")
        viewModel.onPriceChange("100")
        viewModel.onQuantityChange("1")

        viewModel.submitListing()

        viewModel.formState.test {
            val finalState = awaitItem() // Skip initial/loading if necessary
            assertEquals("You must be logged in to create a listing.", finalState.submissionError)
            assertFalse(finalState.isSubmitting)
        }
    }

    @Test
    fun `submitListing with images success`() = runTest {
        val mockUri = mockk<Uri>()
        val imageUris = listOf(mockUri)
        val uploadedUrls = listOf("http://example.com/image.jpg")
        val createdListingId = "new_listing_123"

        coEvery { imageUploadService.uploadImages(imageUris, any()) } returns Result.Success(uploadedUrls)
        coEvery { productListingRepository.createProductListing(any()) } returns Result.Success(createdListingId)

        viewModel = CreateListingViewModel(productListingRepository, userIdProvider, imageUploadService)
        viewModel.onTitleChange("Test Title")
        viewModel.onPriceChange("100")
        viewModel.onQuantityChange("1")
        viewModel.onImagesSelected(imageUris)

        viewModel.submitListing()

        coVerify { imageUploadService.uploadImages(imageUris, "listings/test_seller_id/${any<String>()}") }
        coVerify { productListingRepository.createProductListing(match { it.imageUrls == uploadedUrls && it.sellerId == "test_seller_id" }) }

        viewModel.formState.test {
             // Skip initial states to get to the final one
            var item = awaitItem()
            while(item.isSubmitting || item.submissionError != null || !item.submissionSuccess) { // Await success
                item = awaitItem()
            }
            assertTrue(item.submissionSuccess)
            assertEquals(createdListingId, item.createdListingId)
            assertNull(item.submissionError)
            assertFalse(item.isSubmitting)
        }
    }

    @Test
    fun `submitListing image upload fails shows error`() = runTest {
        val mockUri = mockk<Uri>()
        val imageUris = listOf(mockUri)
        val exception = Exception("Upload failed")

        coEvery { imageUploadService.uploadImages(imageUris, any()) } returns Result.Error(exception)

        viewModel = CreateListingViewModel(productListingRepository, userIdProvider, imageUploadService)
        viewModel.onTitleChange("Test Title")
        viewModel.onPriceChange("100")
        viewModel.onQuantityChange("1")
        viewModel.onImagesSelected(imageUris)

        viewModel.submitListing()

        coVerify { imageUploadService.uploadImages(imageUris, any()) }
        coVerify(exactly = 0) { productListingRepository.createProductListing(any()) }

        viewModel.formState.test {
            var item = awaitItem()
            while(item.isSubmitting || item.submissionError == null) { // Await error
                item = awaitItem()
            }
            assertEquals("Upload failed", item.submissionError)
            assertFalse(item.submissionSuccess)
            assertFalse(item.isSubmitting)
        }
    }

    // Add more tests for other validation errors, repository failure, etc.
}
