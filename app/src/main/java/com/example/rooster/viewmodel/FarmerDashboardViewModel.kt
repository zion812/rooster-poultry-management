package com.example.rooster.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rooster.data.AuctionRepository
import com.example.rooster.data.CertificationRepository
import com.example.rooster.data.EventsRepository
import com.example.rooster.data.VaccinationRepository
import com.example.rooster.models.AuctionBid
import com.example.rooster.models.CertificationRequest
import com.example.rooster.models.EventItem
import com.example.rooster.models.VaccinationTemplate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FarmerDashboardViewModel : ViewModel() {
    private val _activeAuctions = MutableStateFlow<List<String>>(emptyList())
    val activeAuctions: StateFlow<List<String>> = _activeAuctions

    private val _bids = MutableStateFlow<List<AuctionBid>>(emptyList())
    val bids: StateFlow<List<AuctionBid>> = _bids

    private val _kycRequests = MutableStateFlow<List<CertificationRequest>>(emptyList())
    val kycRequests: StateFlow<List<CertificationRequest>> = _kycRequests

    private val _templates = MutableStateFlow<List<VaccinationTemplate>>(emptyList())
    val templates: StateFlow<List<VaccinationTemplate>> = _templates

    private val _events = MutableStateFlow<List<EventItem>>(emptyList())
    val events: StateFlow<List<EventItem>> = _events

    fun loadAuctions() =
        viewModelScope.launch {
            try {
                // stub: load active product IDs
                _activeAuctions.value = listOf("prod1", "prod2")
                FirebaseCrashlytics.getInstance().log("Active auctions loaded")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    fun viewBids(productId: String) =
        viewModelScope.launch {
            try {
                val list = AuctionRepository.listBids(productId)
                _bids.value = list
                FirebaseCrashlytics.getInstance().log("Bids loaded for $productId: ${list.size}")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }

    fun approveBid(
        productId: String,
        bidId: String,
    ) = viewModelScope.launch {
        try {
            AuctionRepository.approveBid(productId, bidId)
            viewBids(productId)
            FirebaseCrashlytics.getInstance().log("Bid $bidId approved for $productId")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun submitKYC(
        farmerId: String,
        docs: List<String>,
    ) = viewModelScope.launch {
        try {
            CertificationRepository.submitKYC(farmerId, docs)
            _kycRequests.value = CertificationRepository.getRequests(farmerId)
            FirebaseCrashlytics.getInstance().log("KYC submitted for $farmerId")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun uploadTemplate(
        farmId: String,
        template: VaccinationTemplate,
    ) = viewModelScope.launch {
        try {
            VaccinationRepository.uploadTemplate(farmId, template)
            _templates.value = VaccinationRepository.fetchTemplates(farmId)
            FirebaseCrashlytics.getInstance().log("Template uploaded for $farmId")
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun loadEvents() =
        viewModelScope.launch {
            try {
                _events.value = EventsRepository.fetchEventsAndElects()
                FirebaseCrashlytics.getInstance().log("Events loaded: ${_events.value.size}")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
}
