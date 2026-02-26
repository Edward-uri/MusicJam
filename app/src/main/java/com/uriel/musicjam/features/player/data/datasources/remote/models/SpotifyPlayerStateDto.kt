package com.uriel.musicjam.features.player.data.datasources.remote.models

import com.uriel.musicjam.features.home.data.datasources.remote.models.SpotifyTrackDto

data class SpotifyPlayerStateDto(
    val progressMs: Long,
    val track: SpotifyTrackDto?,
    val playing: Boolean
)
