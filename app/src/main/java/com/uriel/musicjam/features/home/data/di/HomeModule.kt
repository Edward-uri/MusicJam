package com.uriel.musicjam.features.home.data.di

import com.uriel.musicjam.features.home.data.datasources.remote.api.HomeApiService
import com.uriel.musicjam.features.home.data.repositories.HomeRepositoryImpl
import com.uriel.musicjam.features.home.domain.repositories.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomeApiService(retrofit: Retrofit): HomeApiService =
        retrofit.create(HomeApiService::class.java)

    @Provides
    @Singleton
    fun provideHomeRepository(
        api: HomeApiService
    ): HomeRepository = HomeRepositoryImpl(api)
}
