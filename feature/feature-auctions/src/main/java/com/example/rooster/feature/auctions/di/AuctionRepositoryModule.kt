package com.example.rooster.feature.auctions.di

import com.example.rooster.feature.auctions.data.repository.ParseAuctionRepositoryImpl
import com.example.rooster.feature.auctions.domain.repository.AuctionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Or ViewModelComponent if appropriate
abstract class AuctionRepositoryModule {

    @Binds
    @Singleton // Or @ViewModelScoped
    abstract fun bindAuctionRepository(
        parseAuctionRepositoryImpl: ParseAuctionRepositoryImpl
    ): AuctionRepository
}
