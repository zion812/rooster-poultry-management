package com.example.rooster.auction.repo

import android.util.Log
import com.example.rooster.auction.model.BidUpdate
import com.example.rooster.auction.remote.AuctionWebSocketClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class AuctionRepositoryImpl @Inject constructor(
    private val wsClient: AuctionWebSocketClient
) : AuctionRepository {

    override fun observeBids(auctionId: String): Flow<BidUpdate> {
        return flow {
            wsClient.connect(auctionId)
            wsClient.updates.collect { update ->
                emit(update)
            }
        }.onCompletion {
            wsClient.disconnect()
        }
    }

    override suspend fun placeBid(auctionId: String, amount: Double): Result<Unit> {
        return runCatching {
            Log.i("AuctionRepo", "Placed bid ₹$amount on auction $auctionId")
            // API call would go here when service is working
        }
    }
}