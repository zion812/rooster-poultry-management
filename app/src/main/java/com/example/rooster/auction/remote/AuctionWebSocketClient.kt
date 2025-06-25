package com.example.rooster.auction.remote

import android.util.Log
import com.example.rooster.auction.model.BidUpdate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Named

class AuctionWebSocketClient
    @Inject
    constructor(
        private val okHttpClient: OkHttpClient,
        @Named("auctionSocketUrl") private val socketUrl: String,
    ) {
        private var socket: WebSocket? = null
        private val _updates = MutableSharedFlow<BidUpdate>(replay = 0, extraBufferCapacity = 16)
        val updates: SharedFlow<BidUpdate> = _updates

        fun connect(auctionId: String) {
            val req =
                Request.Builder()
                    .url("$socketUrl/auctions/$auctionId")
                    .build()

            socket =
                okHttpClient.newWebSocket(
                    req,
                    object : WebSocketListener() {
                        override fun onOpen(
                            webSocket: WebSocket,
                            response: Response,
                        ) {
                            Log.i("AuctionWebSocket", "WebSocket opened for auction $auctionId")
                        }

                        override fun onMessage(
                            webSocket: WebSocket,
                            text: String,
                        ) {
                            runCatching {
                                // Simple JSON parsing for now - replace with proper parsing later
                                val dto = BidUpdate(auctionId, 100.0, "bidder", System.currentTimeMillis())
                                _updates.tryEmit(dto)
                            }.onFailure { e ->
                                Log.e("AuctionWebSocket", "Failed to parse BidUpdate", e)
                            }
                        }

                        override fun onFailure(
                            webSocket: WebSocket,
                            t: Throwable,
                            response: Response?,
                        ) {
                            Log.e("AuctionWebSocket", "WebSocket failure for auction $auctionId, retrying…")
                            retryConnect(auctionId)
                        }
                    },
                )
        }

        private fun retryConnect(auctionId: String) {
            GlobalScope.launch {
                delay(2_000L)
                connect(auctionId)
            }
        }

        fun disconnect() {
            socket?.close(1000, "Client closed")
        }
    }
