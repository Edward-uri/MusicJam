package com.uriel.musicjam.features.home.data.datasources.remote.mapper

import com.uriel.musicjam.features.home.data.datasources.remote.models.SpotifyAlbumDto
import com.uriel.musicjam.features.home.data.datasources.remote.models.SpotifyTrackDto
import com.uriel.musicjam.features.home.data.datasources.remote.models.UserResponseDto
import com.uriel.musicjam.features.home.domain.entities.SpotifyAlbum
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.home.domain.entities.UserProfile

fun SpotifyTrackDto.toDomain() = SpotifyTrack(
    id = id,
    name = name,
    artist = artist,
    imageUrl = imageUrl
)

fun SpotifyAlbumDto.toDomain() = SpotifyAlbum(
    id = id,
    name = name,
    imageUrl = imageUrl
)

fun UserResponseDto.toDomain() = UserProfile(
    id = id,
    username = username,
    email = email,
    photo = photo
)
