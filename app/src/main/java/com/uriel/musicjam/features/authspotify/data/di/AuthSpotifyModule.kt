package com.uriel.musicjam.features.authspotify.data.di

import com.uriel.musicjam.features.auth.data.datasources.remote.api.AuthApi
import com.uriel.musicjam.features.authspotify.data.datasources.remote.api.SpotifyApi
import com.uriel.musicjam.features.authspotify.data.repositories.AuthSpotifyRepositoryImpl
import com.uriel.musicjam.features.authspotify.domain.repositories.AuthSpotifyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthSpotifyModule {
    @Provides
    @Singleton
    fun provideAuthSpotifyApi(retrofit: Retrofit): SpotifyApi {
        return retrofit.create(SpotifyApi::class.java)
    }
}

// Módulo para enlazar la Interfaz con su Implementación (El @Binds)
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthSpotifyRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthSpotifyRepository(
        authSpotifyRepositoryImpl: AuthSpotifyRepositoryImpl
    ): AuthSpotifyRepository
}