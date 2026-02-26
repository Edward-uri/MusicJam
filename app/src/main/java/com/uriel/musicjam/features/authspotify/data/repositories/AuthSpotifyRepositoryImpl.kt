package com.uriel.musicjam.features.authspotify.data.repositories

import com.uriel.musicjam.features.authspotify.data.datasources.remote.api.SpotifyApi
import com.uriel.musicjam.features.authspotify.data.datasources.remote.dtos.SpotifyCodeExchangeRequestDto
import com.uriel.musicjam.features.authspotify.domain.repositories.AuthSpotifyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthSpotifyRepositoryImpl @Inject constructor(
    private val api: SpotifyApi
) : AuthSpotifyRepository {
    override suspend fun exchangeCode(code: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.exchangeCode(SpotifyCodeExchangeRequestDto(code))
            if (response.success) {
                Result.success(response.message ?: "Vinculado correctamente")
            } else {
                Result.failure(Exception(response.message ?: "Error al vincular con Spotify"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red al contactar con el servidor"))
        }
    }
}