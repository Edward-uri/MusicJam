package com.uriel.musicjam.features.auth.domain.entities

data class User (
    val id: String,
    val username: String,
    val email: String,
    val photo: String?
)