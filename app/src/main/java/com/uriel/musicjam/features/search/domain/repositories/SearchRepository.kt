package com.uriel.musicjam.features.search.domain.repositories

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack

interface SearchRepository {
    suspend fun searchTracks(query: String): Result<List<SpotifyTrack>>
}
