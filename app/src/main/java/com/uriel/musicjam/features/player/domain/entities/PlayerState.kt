package com.uriel.musicjam.features.player.domain.entities

import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack

data class PlayerState(
    val track: SpotifyTrack?,
    val progressMs: Long,
    val isPlaying: Boolean
)
