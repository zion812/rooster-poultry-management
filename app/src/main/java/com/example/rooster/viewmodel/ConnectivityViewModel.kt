package com.example.rooster.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import com.example.rooster.services.optimized.ConnectionType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ConnectivityViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _connectionType = MutableStateFlow(ConnectionType.UNKNOWN)
    val connectionType: StateFlow<ConnectionType> = _connectionType.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnected.value = true
            updateConnectionType()
        }

        override fun onLost(network: Network) {
            _isConnected.value = false
            _connectionType.value = ConnectionType.UNKNOWN
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            updateConnectionType()
        }
    }

    init {
        registerNetworkCallback()
        updateConnectionType()
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun updateConnectionType() {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        _connectionType.value = when {
            networkCapabilities == null -> ConnectionType.UNKNOWN
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                // Try to determine cellular generation based on capabilities
                when {
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) -> ConnectionType.CELLULAR_4G
                    networkCapabilities.linkDownstreamBandwidthKbps > 10000 -> ConnectionType.CELLULAR_4G
                    networkCapabilities.linkDownstreamBandwidthKbps > 1000 -> ConnectionType.CELLULAR_3G
                    else -> ConnectionType.CELLULAR_2G
                }
            }

            else -> ConnectionType.UNKNOWN
        }

        _isConnected.value =
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}