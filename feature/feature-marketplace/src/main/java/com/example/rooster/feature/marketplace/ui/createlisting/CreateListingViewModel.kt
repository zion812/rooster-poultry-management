package com.example.rooster.feature.marketplace.ui.createlisting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.core.common.Result
import com.example.rooster.feature.marketplace.domain.model.ProductCategory
import com.example.rooster.feature.marketplace.domain.model.ProductListing
import com.example.rooster.feature.marketplace.domain.model.ListingStatus
import com.example.rooster.feature.marketplace.domain.repository.ProductListingRepository
import com.example.rooster.core.common.user.UserIdProvider // Import UserIdProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CreateListingFormState(
    val title: String = "",
    val description: String = "",
    val category: ProductCategory = ProductCategory.LIVE_BIRD_CHICKEN, // Default
    val breed: String = "",
    val ageInWeeks: String = "", // Input as String, convert to Int
    val weightInKg: String = "", // Input as String, convert to Double
    val price: String = "", // Input as String, convert to Double
    val quantityAvailable: String = "", // Input as String, convert to Int
    val locationCity: String = "",
    val locationDistrict: String = "",
    val imageUris: List<android.net.Uri> = emptyList(), // Store list of content URIs
    val isSubmitting: Boolean = false,
    val submissionError: String? = null,
    val submissionSuccess: Boolean = false
)

@HiltViewModel
class CreateListingViewModel @Inject constructor(
    private val productListingRepository: ProductListingRepository,
    private val userIdProvider: UserIdProvider // Inject UserIdProvider
) : ViewModel() {

    private val _formState = MutableStateFlow(CreateListingFormState())
    val formState: StateFlow<CreateListingFormState> = _formState.asStateFlow()

    // Removed placeholder currentSellerId

    fun onTitleChange(title: String) {
        _formState.value = _formState.value.copy(title = title, submissionError = null, submissionSuccess = false)
    }

    fun onDescriptionChange(description: String) {
        _formState.value = _formState.value.copy(description = description, submissionError = null, submissionSuccess = false)
    }

    fun onCategoryChange(category: ProductCategory) {
        _formState.value = _formState.value.copy(category = category, submissionError = null, submissionSuccess = false)
    }

    fun onBreedChange(breed: String) {
        _formState.value = _formState.value.copy(breed = breed, submissionError = null, submissionSuccess = false)
    }

    fun onAgeChange(age: String) {
        _formState.value = _formState.value.copy(ageInWeeks = age, submissionError = null, submissionSuccess = false)
    }

    fun onWeightChange(weight: String) {
        _formState.value = _formState.value.copy(weightInKg = weight, submissionError = null, submissionSuccess = false)
    }

    fun onPriceChange(price: String) {
        _formState.value = _formState.value.copy(price = price, submissionError = null, submissionSuccess = false)
    }

    fun onQuantityChange(quantity: String) {
        _formState.value = _formState.value.copy(quantityAvailable = quantity, submissionError = null, submissionSuccess = false)
    }

    fun onLocationCityChange(city: String) {
        _formState.value = _formState.value.copy(locationCity = city, submissionError = null, submissionSuccess = false)
    }

    fun onLocationDistrictChange(district: String) {
        _formState.value = _formState.value.copy(locationDistrict = district, submissionError = null, submissionSuccess = false)
    }

    // TODO: Add fun onImageSelected(uris: List<String>)

    fun onImagesSelected(uris: List<android.net.Uri>) {
        _formState.value = _formState.value.copy(imageUris = uris, submissionError = null, submissionSuccess = false)
    }

    fun removeImage(uri: android.net.Uri) {
        _formState.value = _formState.value.copy(
            imageUris = _formState.value.imageUris - uri,
            submissionError = null,
            submissionSuccess = false
        )
    }

    fun submitListing() {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSubmitting = true, submissionError = null, submissionSuccess = false)

            val currentSellerId = userIdProvider.getCurrentUserId()
            if (currentSellerId == null) {
                _formState.value = _formState.value.copy(isSubmitting = false, submissionError = "You must be logged in to create a listing.")
                return@launch
            }

            val currentState = _formState.value
            // Basic Validation (more robust validation needed)
            if (currentState.title.isBlank() || currentState.price.isBlank() || currentState.quantityAvailable.isBlank()) {
                _formState.value = currentState.copy(isSubmitting = false, submissionError = "Title, Price, and Quantity are required.")
                return@launch
            }

            val priceDouble = currentState.price.toDoubleOrNull()
            val quantityInt = currentState.quantityAvailable.toIntOrNull()
            val ageInt = currentState.ageInWeeks.toIntOrNull()
            val weightDouble = currentState.weightInKg.toDoubleOrNull()

            if (priceDouble == null || priceDouble <= 0) {
                _formState.value = currentState.copy(isSubmitting = false, submissionError = "Invalid price.")
                return@launch
            }
            if (quantityInt == null || quantityInt <= 0) {
                _formState.value = currentState.copy(isSubmitting = false, submissionError = "Invalid quantity.")
                return@launch
            }
            // Add more validation for age, weight if needed

            val now = System.currentTimeMillis()
            val newListing = ProductListing(
                id = UUID.randomUUID().toString(), // Repository can also generate if ID is empty
                sellerId = currentSellerId,
                title = currentState.title.trim(),
                description = currentState.description.trim(),
                category = currentState.category,
                breed = currentState.breed.takeIf { it.isNotBlank() },
                ageInWeeks = ageInt,
                weightInKg = weightDouble,
                price = priceDouble,
                quantityAvailable = quantityInt,
                imageUrls = currentState.imageUris.map { it.toString() }, // Store URI strings for now
                locationCity = currentState.locationCity.takeIf { it.isNotBlank() },
                locationDistrict = currentState.locationDistrict.takeIf { it.isNotBlank() },
                postedDateTimestamp = now,
                updatedDateTimestamp = now,
                status = ListingStatus.ACTIVE // Or PENDING_APPROVAL if moderation is implemented
            )

            val result = productListingRepository.createProductListing(newListing)
            _formState.value = when (result) {
                is Result.Success -> currentState.copy(isSubmitting = false, submissionSuccess = true)
                is Result.Error -> currentState.copy(
                    isSubmitting = false,
                    submissionError = result.exception.message ?: "Failed to create listing."
                )
                Result.Loading -> currentState.copy(isSubmitting = true) // Should not happen from suspend fun
            }
        }
    }
}
