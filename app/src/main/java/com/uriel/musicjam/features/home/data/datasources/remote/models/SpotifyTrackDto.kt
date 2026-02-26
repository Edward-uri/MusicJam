package com.uriel.musicjam.features.home.data.datasources.remote.models

data class SpotifyTrackDto(
    val id: String,
    val name: String,
    val artist: String,
    val imageUrl: String,
    val durationMs: Long = 0L
)
