package com.example.rooster.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.CommunityGroup
import com.example.rooster.fetchCommunityGroups
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel() {
    private val _groups = MutableStateFlow<List<CommunityGroup>>(emptyList())
    val groups: StateFlow<List<CommunityGroup>> = _groups.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var currentSkip = 0
    private val pageSize = 20
    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    /**
     * Load community groups (optionally filtered by type or region). Resets pagination.
     */
    fun loadGroups(
        groupType: String? = null,
        region: String? = null,
    ) {
        Log.d("CommunityViewModel", "=== LOADING GROUPS ===")
        Log.d("CommunityViewModel", "Group type: $groupType")
        Log.d("CommunityViewModel", "Region: $region")
        Log.d("CommunityViewModel", "Current skip: $currentSkip")

        currentSkip = 0
        _hasMore.value = true
        _groups.value = emptyList()
        fetchPage(groupType, region)
    }

    /**
     * Search groups by name or username
     */
    fun searchGroups(
        query: String,
        location: String? = null,
    ) {
        Log.d("CommunityViewModel", "=== SEARCHING GROUPS ===")
        Log.d("CommunityViewModel", "Search query: '$query'")
        Log.d("CommunityViewModel", "Location filter: '$location'")

        currentSkip = 0
        _hasMore.value = true
        _groups.value = emptyList()

        // Use the existing fetchPage with custom parameters
        fetchPageWithSearch(query, location)
    }

    /**
     * Load next page if available.
     */
    fun loadNextPage(
        groupType: String? = null,
        region: String? = null,
    ) {
        if (_loading.value || !_hasMore.value) return
        fetchPage(groupType, region)
    }

    private fun fetchPage(
        groupType: String? = null,
        region: String? = null,
    ) {
        Log.d("CommunityViewModel", "=== FETCHING PAGE ===")
        Log.d("CommunityViewModel", "Skip: $currentSkip, Limit: $pageSize")
        Log.d("CommunityViewModel", "Loading state before: ${_loading.value}")

        viewModelScope.launch {
            _loading.value = true
            Log.d("CommunityViewModel", "Loading state set to true")

            fetchCommunityGroups(
                skip = currentSkip,
                limit = pageSize,
                groupType = groupType,
                region = region,
                onResult = { list ->
                    Log.d("CommunityViewModel", "=== FETCH SUCCESS ===")
                    Log.d("CommunityViewModel", "Received ${list.size} groups")
                    list.forEachIndexed { index, group ->
                        Log.d(
                            "CommunityViewModel",
                            "Group $index: ${group.name} (${group.memberCount} members)",
                        )
                    }

                    // Append or set
                    _groups.value = _groups.value + list
                    _error.value = null
                    _loading.value = false
                    if (list.size < pageSize) {
                        _hasMore.value = false
                    } else {
                        currentSkip += pageSize
                    }

                    Log.d("CommunityViewModel", "Total groups now: ${_groups.value.size}")
                    Log.d("CommunityViewModel", "Has more: ${_hasMore.value}")
                    Log.d("CommunityViewModel", "==================")
                },
                onError = { msg ->
                    Log.e("CommunityViewModel", "=== FETCH ERROR ===")
                    Log.e("CommunityViewModel", "Error message: $msg")

                    _error.value = msg
                    _loading.value = false

                    Log.e("CommunityViewModel", "Error state set, loading false")
                    Log.e("CommunityViewModel", "=================")
                },
                setLoading = { isLoading ->
                    Log.d("CommunityViewModel", "SetLoading callback: $isLoading")
                    _loading.value = isLoading
                },
            )
        }
    }

    private fun fetchPageWithSearch(
        query: String,
        location: String? = null,
    ) {
        Log.d("CommunityViewModel", "=== FETCHING PAGE WITH SEARCH ===")
        Log.d("CommunityViewModel", "Search query: '$query'")
        Log.d("CommunityViewModel", "Location: '$location'")
        Log.d("CommunityViewModel", "Skip: $currentSkip, Limit: $pageSize")

        viewModelScope.launch {
            _loading.value = true
            Log.d("CommunityViewModel", "Loading state set to true for search")

            // Use the existing fetchCommunityGroups with region parameter for location
            fetchCommunityGroups(
                skip = currentSkip,
                limit = pageSize,
                groupType = null,
                region = if (location != "All Locations") location else null,
                onResult = { list ->
                    Log.d("CommunityViewModel", "=== SEARCH FETCH SUCCESS ===")
                    Log.d("CommunityViewModel", "Received ${list.size} groups for search")

                    // Client-side filtering for search query since Parse doesn't support complex text search
                    val filteredList =
                        if (query.isNotBlank()) {
                            list.filter { group ->
                                group.name.contains(query, ignoreCase = true)
                            }
                        } else {
                            list
                        }

                    Log.d(
                        "CommunityViewModel",
                        "Filtered to ${filteredList.size} groups after search",
                    )
                    filteredList.forEachIndexed { index, group ->
                        Log.d(
                            "CommunityViewModel",
                            "Filtered Group $index: ${group.name} (${group.memberCount} members)",
                        )
                    }

                    // Append or set
                    _groups.value = _groups.value + filteredList
                    _error.value = null
                    _loading.value = false
                    if (filteredList.size < pageSize) {
                        _hasMore.value = false
                    } else {
                        currentSkip += pageSize
                    }

                    Log.d("CommunityViewModel", "Total groups now: ${_groups.value.size}")
                    Log.d("CommunityViewModel", "Has more: ${_hasMore.value}")
                    Log.d("CommunityViewModel", "=======================")
                },
                onError = { msg ->
                    Log.e("CommunityViewModel", "=== SEARCH FETCH ERROR ===")
                    Log.e("CommunityViewModel", "Error message: $msg")

                    _error.value = msg
                    _loading.value = false

                    Log.e("CommunityViewModel", "Error state set, loading false")
                    Log.e("CommunityViewModel", "========================")
                },
                setLoading = { isLoading ->
                    Log.d("CommunityViewModel", "SetLoading callback for search: $isLoading")
                    _loading.value = isLoading
                },
            )
        }
    }

    /**
     * Clear search results and reload all groups
     */
    fun clearSearch() {
        Log.d("CommunityViewModel", "=== CLEARING SEARCH ===")
        loadGroups()
    }
}
