package com.uriel.musicjam.features.home.data.di

import com.uriel.musicjam.features.home.data.datasources.remote.api.HomeApiService
import com.uriel.musicjam.features.home.data.repositories.HomeRepositoryImpl
import com.uriel.musicjam.features.home.domain.repositories.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

// Provee API
@Module
@InstallIn(SingletonComponent::class)
object HomeApiModule {
    @Provides @Singleton
    fun provideHomeApi(retrofit: Retrofit): HomeApiService =
        retrofit.create(HomeApiService::class.java)
}

// Bindea Repository
@Module
@InstallIn(SingletonComponent::class)
abstract class HomeRepositoryModule {
    @Binds @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}
