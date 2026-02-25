package com.uriel.musicjam.features.home.domain.entities

data class UserProfile(
    val id: String,
    val username: String,
    val email: String,
    val photo: String?
)
