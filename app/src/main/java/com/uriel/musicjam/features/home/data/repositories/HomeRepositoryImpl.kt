package com.uriel.musicjam.features.home.data.repositories

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.data.datasources.remote.api.HomeApiService
import com.uriel.musicjam.features.home.data.datasources.remote.mapper.toDomain
import com.uriel.musicjam.features.home.domain.entities.SpotifyAlbum
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.home.domain.entities.UserProfile
import com.uriel.musicjam.features.home.domain.repositories.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApiService
) : HomeRepository {

    override suspend fun getMyAlbums(): Result<List<SpotifyAlbum>> {
        return try {
            val response = api.getMyAlbums()
            Result.Success(response.data?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener álbumes")
        }
    }

    override suspend fun getMyTopTracks(): Result<List<SpotifyTrack>> {
        return try {
            val response = api.getMyTopTracks()
            Result.Success(response.data?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener canciones")
        }
    }

    override suspend fun getMyProfile(): Result<UserProfile> {
        return try {
            val response = api.getMyProfile()
            val profile = response.data?.toDomain()
                ?: return Result.Error("Perfil no encontrado")
            Result.Success(profile)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener perfil")
        }
    }


}
