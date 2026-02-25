package com.uriel.musicjam.features.home.data.datasources.remote.models

data class UserResponseDto(
    val id: String,
    val username: String,
    val email: String,
    val photo: String?
)
