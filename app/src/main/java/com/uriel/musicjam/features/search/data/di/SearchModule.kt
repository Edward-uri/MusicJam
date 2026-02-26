package com.uriel.musicjam.features.search.data.di

import com.uriel.musicjam.features.search.data.datasources.remote.api.SearchApiService
import com.uriel.musicjam.features.search.data.repositories.SearchRepositoryImpl
import com.uriel.musicjam.features.search.domain.repositories.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchNetworkModule {
    @Provides @Singleton
    fun provideSearchApi(retrofit: Retrofit): SearchApiService =
        retrofit.create(SearchApiService::class.java)
}


@Module
@InstallIn(SingletonComponent::class)
abstract class SearchRepositoryModule {
    @Binds @Singleton
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl
    ): SearchRepository

}

