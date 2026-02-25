package com.uriel.musicjam.features.home.data.repositories

import android.util.Log
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
            val response = api.getMyAlbums()  // devuelve List<SpotifyAlbumDto> directo
            Log.d("HomeRepo", response.toString())
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener álbumes")
        }
    }

    override suspend fun getMyTopTracks(): Result<List<SpotifyTrack>> {
        return try {
            val response = api.getMyTopTracks()  // devuelve List<SpotifyTrackDto> directo
            Log.d("HomeRepo", response.toString())
            Result.Success(response.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener canciones")
        }
    }

    override suspend fun getMyProfile(): Result<UserProfile> {
        return try {
            val response = api.getMyProfile()  // devuelve UserResponseDto directo
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error al obtener perfil")
        }
    }
}
