package com.uriel.musicjam.features.authspotify.data.repositories

import com.uriel.musicjam.core.network.Result // 1. IMPORTANTE: Importa tu Result
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
        android.util.Log.d("SpotifyAuth", "Repository - Preparando petición API para el código: $code")

        try {
            val response = api.exchangeCode(SpotifyCodeExchangeRequestDto(code))

            // LOG 5: ¿Qué respondió exactamente el servidor de Spring Boot?
            android.util.Log.d("SpotifyAuth", "Repository - Respuesta HTTP exitosa. Body Success: ${response.success}, Message: ${response.message}")

            if (response.success) {
                Result.Success(response.message ?: "Vinculado correctamente")
            } else {
                Result.Error(response.message ?: "Error al vincular con Spotify")
            }
        } catch (e: Exception) {
            // LOG 6: ¡Aquí atrapamos si crashea la red, si el JSON está mal, o si Spring Boot da error 500!
            android.util.Log.e("SpotifyAuth", "Repository - EXCEPCIÓN CAUGHT: ${e.message}", e)
            Result.Error("Error de red al contactar con el servidor: ${e.message}")
        }
    }
    override suspend fun getSpotifyAccessToken(): Result<String> = try {
        Result.Success(api.getSpotifyAccessToken().data!!)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al obtener token de Spotify")
    }
}