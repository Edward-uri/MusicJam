package com.uriel.musicjam.features.search.data.repositories

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.data.datasources.remote.mapper.toDomain
import com.uriel.musicjam.features.search.data.datasources.remote.api.SearchApiService
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.search.domain.repositories.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val api: SearchApiService
) : SearchRepository{

    override suspend fun searchTracks(query: String): Result<List<SpotifyTrack>> {
        return try {
            val response = api.searchTracks(query)
            Result.Success(response.data?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error en la búsqueda")
        }
    }

    override suspend fun queueTrack(trackId: String): Result<Unit> {
        return try {
            val response = api.queueTrack(trackId)
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message ?: "Error al añadir a la cola")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de red")
        }
    }
}
