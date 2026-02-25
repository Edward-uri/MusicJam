package com.uriel.musicjam.features.auth.data.repositories

import com.google.gson.Gson
import com.uriel.musicjam.core.storage.TokenManager
import com.uriel.musicjam.features.auth.data.datasources.remote.api.AuthApi
import com.uriel.musicjam.features.auth.data.datasources.remote.dtos.AuthRequestDto
import com.uriel.musicjam.features.auth.data.datasources.remote.dtos.RegisterRequestDto
import com.uriel.musicjam.features.auth.data.datasources.remote.mappers.toDomain
import com.uriel.musicjam.features.auth.domain.entities.AuthToken
import com.uriel.musicjam.features.auth.domain.entities.User
import com.uriel.musicjam.features.auth.domain.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    private val gson = Gson()

    override suspend fun login(username: String, password: String): Result<AuthToken> = withContext(Dispatchers.IO) {
        try {
            val response = api.login(AuthRequestDto(username, password))
            if (response.success && response.data != null) {
                // Guardamos el token en SharedPreferences
                tokenManager.saveToken(response.data.token)
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Error desconocido en el login"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: Verifica tu conexión o IP del servidor"))
        }
    }

    override suspend fun register(username: String, email: String, password: String, photo: File?): Result<User> = withContext(Dispatchers.IO) {
        try {
            // 1. Preparamos la parte del usuario (JSON)
            val requestDto = RegisterRequestDto(username, email, password)
            val userJson = gson.toJson(requestDto)
            val userPart = userJson.toRequestBody("application/json".toMediaTypeOrNull())

            // 2. Preparamos la parte de la foto (Si existe)
            val photoPart = photo?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            }

            // 3. Enviamos ambas partes a la API
            val response = api.register(userPart, photoPart)

            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Error al registrar el usuario"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de red: Verifica tu conexión o IP del servidor"))
        }
    }
}