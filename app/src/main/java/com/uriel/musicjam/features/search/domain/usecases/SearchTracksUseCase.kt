package com.uriel.musicjam.features.search.domain.usecases

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.search.domain.repositories.SearchRepository
import javax.inject.Inject

class SearchTracksUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String): Result<List<SpotifyTrack>> =
        repository.searchTracks(query)
}
