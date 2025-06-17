package com.example.rooster.auction

import com.example.rooster.auction.remote.AuctionWebSocketClient
import com.example.rooster.auction.repo.AuctionRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuctionRepositoryTest {
    private val ws = mockk<AuctionWebSocketClient>()
    private lateinit var repo: AuctionRepositoryImpl

    @Before
    fun setup() {
        every { ws.connect(any()) } returns Unit
        every { ws.disconnect() } returns Unit
        repo = AuctionRepositoryImpl(ws)
    }

    @Test
    fun `placeBid returns success`() =
        runTest {
            val res = repo.placeBid("a1", 200.0)
            assertTrue(res.isSuccess)
        }
}
