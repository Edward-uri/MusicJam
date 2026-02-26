package com.uriel.musicjam.features.search.data.datasources.remote.mapper

import com.uriel.musicjam.features.home.data.datasources.remote.models.SpotifyTrackDto
import com.uriel.musicjam.features.home.data.datasources.remote.mapper.toDomain as homeToDomain
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack

fun SpotifyTrackDto.toDomain(): SpotifyTrack = this.homeToDomain()
