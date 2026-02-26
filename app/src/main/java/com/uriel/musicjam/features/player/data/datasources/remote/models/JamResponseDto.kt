package com.uriel.musicjam.features.player.data.datasources.remote.models

data class JamResponseDto(
    val id: String,
    val joinCode: String,
    val activeSpeakerUserId: String,
    val active: Boolean
)
