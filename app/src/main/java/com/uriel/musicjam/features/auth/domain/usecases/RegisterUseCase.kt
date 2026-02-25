package com.uriel.musicjam.features.auth.domain.usecases

import com.uriel.musicjam.features.auth.domain.entities.User
import com.uriel.musicjam.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String): Result<User> {
        return try {
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                return Result.failure(Exception("Todos los campos son obligatorios"))
            }
            if (!email.contains("@")) {
                return Result.failure(Exception("El correo ingresado no es válido"))
            }
            if (password.length < 6) {
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }

            repository.register(username, email, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}