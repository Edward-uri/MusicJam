package com.uriel.musicjam.core.di

import com.uriel.musicjam.core.spotify.SpotifyRemoteManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyModule {

    @Provides @Singleton
    fun provideSpotifyRemoteManager(): SpotifyRemoteManager = SpotifyRemoteManager()
}
