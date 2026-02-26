package com.uriel.musicjam.features.player.data.di

import com.uriel.musicjam.features.player.data.datasources.remote.api.PlayerApiService
import com.uriel.musicjam.features.player.data.repositories.PlayerRepositoryImpl
import com.uriel.musicjam.features.player.domain.repositories.PlayerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerApiModule {
    @Provides @Singleton
    fun providePlayerApiService(retrofit: Retrofit): PlayerApiService =
        retrofit.create(PlayerApiService::class.java)
}

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerRepositoryModule {
    @Binds @Singleton
    abstract fun bindPlayerRepository(
        impl: PlayerRepositoryImpl
    ): PlayerRepository
}
