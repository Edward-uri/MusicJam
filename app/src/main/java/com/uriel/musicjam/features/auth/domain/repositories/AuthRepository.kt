package com.uriel.musicjam.features.auth.domain.repositories

import com.uriel.musicjam.features.auth.domain.entities.AuthToken
import com.uriel.musicjam.features.auth.domain.entities.User

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<AuthToken>
    suspend fun register(username: String, email: String, password: String): Result<User>
}