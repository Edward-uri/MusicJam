package com.uriel.musicjam.features.player.data.datasources.remote.mapper

import com.uriel.musicjam.features.home.data.datasources.remote.mapper.toDomain
import com.uriel.musicjam.features.player.data.datasources.remote.models.JamResponseDto
import com.uriel.musicjam.features.player.data.datasources.remote.models.SpotifyPlayerStateDto
import com.uriel.musicjam.features.player.domain.entities.Jam
import com.uriel.musicjam.features.player.domain.entities.PlayerState

fun JamResponseDto.toDomain() = Jam(
    id = id,
    joinCode = joinCode,
    activeSpeakerUserId = activeSpeakerUserId,
    active = active
)

fun SpotifyPlayerStateDto.toDomain() = PlayerState(
    track = track?.toDomain(),
    progressMs = progressMs,
    isPlaying = playing
)
