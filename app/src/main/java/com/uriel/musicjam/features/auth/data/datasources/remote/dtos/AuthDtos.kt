package com.uriel.musicjam.features.auth.data.datasources.remote.dtos

// El wrapper estándar que creaste en tu backend (BaseResponse)
data class BaseResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val status: String?
)

// Peticiones (Lo que enviamos)
data class AuthRequestDto(val username: String, val password: String)
data class RegisterRequestDto(val username: String, val email: String, val password: String)

// Respuestas (Lo que recibimos)
data class AuthResponseDto(val token: String)
data class UserDto(val id: String, val username: String, val email: String, val photo: String?)