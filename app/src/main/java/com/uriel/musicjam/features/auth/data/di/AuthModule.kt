package com.uriel.musicjam.features.auth.data.di

import com.uriel.musicjam.features.auth.data.datasources.remote.api.AuthApi
import com.uriel.musicjam.features.auth.data.repositories.AuthRepositoryImpl
import com.uriel.musicjam.features.auth.domain.repositories.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

// Módulo para proveer Retrofit (El @Provides)
@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
}

// Módulo para enlazar la Interfaz con su Implementación (El @Binds)
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository
}