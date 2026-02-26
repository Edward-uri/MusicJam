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
}
