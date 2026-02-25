package com.uriel.musicjam.features.auth.domain.usecases

import com.uriel.musicjam.features.auth.domain.entities.AuthToken
import com.uriel.musicjam.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<AuthToken> {
        return try {
            if (username.isBlank() || password.isBlank()) {
                return Result.failure(Exception("El usuario y la contraseña no pueden estar vacíos"))
            }

            repository.login(username, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}