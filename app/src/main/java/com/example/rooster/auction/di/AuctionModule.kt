package com.example.rooster.auction.di

import com.example.rooster.auction.remote.AuctionWebSocketClient
import com.example.rooster.auction.repo.AuctionRepository
import com.example.rooster.auction.repo.AuctionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuctionModule {
    @Provides
    @Named("auctionSocketUrl")
    fun provideSocketUrl(): String = "ws://localhost:8080" // Default URL

    @Provides
    @Singleton
    fun provideWebSocketClient(
        client: OkHttpClient, // Use the existing OkHttpClient from NetworkModule
        @Named("auctionSocketUrl") url: String,
    ) = AuctionWebSocketClient(client, url)

    @Provides
    @Singleton
    fun provideRepo(ws: AuctionWebSocketClient): AuctionRepository = AuctionRepositoryImpl(ws)
}
