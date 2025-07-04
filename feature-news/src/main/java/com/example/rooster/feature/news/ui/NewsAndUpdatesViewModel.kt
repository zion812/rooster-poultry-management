package com.example.rooster.feature.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data models (could be in a separate 'domain/model' or 'ui/model' package)
data class NewsItemUiModel(
    val id: String,
    val title: String,
    val date: String, // Keep as raw date string from mock, format in UI
    val formattedDate: String, // Pre-formatted for display
    val source: String,
    val summary: String,
    val type: String,
    val category: String,
    val imageUrl: String?,
    val link: String?
)

data class NewsUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val newsItems: List<NewsItemUiModel> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "all",
    val hasMore: Boolean = true,
    val error: String? = null,
    val page: Int = 1
)

// --- Mock API for News & Updates (similar to RN version) ---
private val mockNewsData = listOf(
  NewsItemUiModel(id = "news1", title = "New Government Subsidies Announced for Organic Farmers", date = "2023-11-10", formattedDate = "Nov 10, 2023", source = "AgriGov Portal", summary = "The Ministry of Agriculture has unveiled a new package of subsidies aimed at supporting farmers transitioning to organic practices...", type = "Government Update", category = "Policy", imageUrl = "https://picsum.photos/seed/govnews/400/200", link = "https://example.com/gov-subsidies-organic"),
  NewsItemUiModel(id = "news2", title = "Market Watch: Tomato Prices Expected to Rise Next Quarter", date = "2023-11-08", formattedDate = "Nov 08, 2023", source = "Farmonomics Today", summary = "Analysts predict a 15% increase in tomato prices due to adverse weather conditions in key growing regions...", type = "Market Report", category = "Market Trends", imageUrl = "https://picsum.photos/seed/tomatomarket/400/200", link = "https://example.com/tomato-price-watch"),
  NewsItemUiModel(id = "news3", title = "Innovation Spotlight: AI-Powered Crop Monitoring System Launched", date = "2023-11-05", formattedDate = "Nov 05, 2023", source = "TechAgri World", summary = "AgriTech startup \"CropMind\" has launched its new AI system that promises early pest detection and yield optimization...", type = "Technology", category = "Innovation", imageUrl = "https://picsum.photos/seed/cropai/400/200", link = "https://example.com/cropmind-ai-launch"),
  NewsItemUiModel(id = "update1", title = "Platform Maintenance Scheduled for Nov 15th, 2 AM - 4 AM UTC", date = "2023-11-03", formattedDate = "Nov 03, 2023", source = "AgriConnect Team", summary = "Our platform will undergo scheduled maintenance. Services may be temporarily unavailable during this window.", type = "Platform Update", category = "Platform", imageUrl = null, link = null),
  NewsItemUiModel(id = "news4", title = "Weather Advisory: Early Frost Expected in Northern Regions", date = "2023-11-12", formattedDate = "Nov 12, 2023", source = "National Weather Service", summary = "Farmers in northern agricultural zones are advised to take precautions against an early frost expected late next week.", type = "Weather Alert", category = "Weather", imageUrl = "https://picsum.photos/seed/frostalert/400/200", link = "https://example.com/frost-advisory-north"),
  NewsItemUiModel(id = "news5", title = "Success Story: Local Co-op Boosts Sales Through Direct-to-Consumer Model", date = "2023-10-28", formattedDate = "Oct 28, 2023", source = "Farmers Weekly", summary = "The Greenfield Farmers Co-op shares how their new online platform has significantly increased their market reach and profitability.", type = "Community Story", category = "Success Stories", imageUrl = "https://picsum.photos/seed/coopsuccess/400/200", link = "https://example.com/greenfield-coop-story"),
)

private const val ITEMS_PER_PAGE_NEWS = 3 // Smaller for easier testing of pagination

private suspend fun mockFetchNewsAndUpdates(page: Int = 1, categoryFilter: String = "all"): Pair<List<NewsItemUiModel>, Boolean> {
    delay(700) // Simulate network delay
    val filteredData = if (categoryFilter.lowercase() == "all") {
        mockNewsData
    } else {
        mockNewsData.filter { it.category.equals(categoryFilter, ignoreCase = true) }
    }

    val sortedData = filteredData.sortedByDescending { it.date } // Sort by most recent
    val start = (page - 1) * ITEMS_PER_PAGE_NEWS
    val end = start + ITEMS_PER_PAGE_NEWS
    val paginatedItems = sortedData.drop(start).take(ITEMS_PER_PAGE_NEWS)

    return Pair(paginatedItems, end < sortedData.size)
}
// --- End Mock API ---


@HiltViewModel
class NewsAndUpdatesViewModel @Inject constructor(
    // TODO: Inject NewsRepository or GetNewsUseCase later
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val newsCategories = listOf("all") + mockNewsData.map { it.category }.distinct().sorted()

    init {
        _uiState.update { it.copy(categories = newsCategories) }
        loadNewsInternal(pageNum = 1, category = _uiState.value.selectedCategory, isRefreshing = false, isInitialLoad = true)
    }

    private fun loadNewsInternal(pageNum: Int, category: String, isRefreshing: Boolean, isInitialLoad: Boolean = false) {
        if (_uiState.value.isLoading || _uiState.value.isLoadingMore && !isRefreshing) {
            // Avoid concurrent loads unless it's a refresh action
            if (!isRefreshing && !isInitialLoad && pageNum > 1 && !_uiState.value.hasMore) return // No more items to load
            if (!isRefreshing && !isInitialLoad) return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = if (pageNum == 1 && !isRefreshing) true else it.isLoading,
                    isLoadingMore = if (pageNum > 1) true else false,
                    isRefreshing = isRefreshing,
                    error = null
                )
            }

            try {
                val (newItems, newHasMore) = mockFetchNewsAndUpdates(pageNum, category)
                _uiState.update { currentState ->
                    currentState.copy(
                        newsItems = if (pageNum == 1 || isRefreshing) newItems else currentState.newsItems + newItems,
                        hasMore = newHasMore,
                        page = pageNum,
                        isLoading = false,
                        isLoadingMore = false,
                        isRefreshing = false
                    )
                }
            } catch (e: Exception) { // Simulate error
                _uiState.update {
                    it.copy(
                        error = "Failed to load news and updates.",
                        isLoading = false,
                        isLoadingMore = false,
                        isRefreshing = false
                    )
                }
            }
        }
    }

    fun onCategorySelected(category: String) {
        if (_uiState.value.selectedCategory == category) return
        _uiState.update { it.copy(selectedCategory = category, page = 1, newsItems = emptyList(), hasMore = true) } // Reset list for new category
        loadNewsInternal(pageNum = 1, category = category, isRefreshing = false)
    }

    fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        loadNewsInternal(pageNum = 1, category = _uiState.value.selectedCategory, isRefreshing = true)
    }

    fun onLoadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore || _uiState.value.isLoading || _uiState.value.isRefreshing) return
        loadNewsInternal(pageNum = _uiState.value.page + 1, category = _uiState.value.selectedCategory, isRefreshing = false)
    }

    fun onRetry() {
        _uiState.update { it.copy(page = 1, newsItems = emptyList(), hasMore = true) } // Reset before retry
        loadNewsInternal(pageNum = 1, category = _uiState.value.selectedCategory, isRefreshing = false)
    }
}
