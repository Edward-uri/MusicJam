package com.uriel.musicjam.features.home.domain.usecases

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.home.domain.repositories.HomeRepository
import javax.inject.Inject

class GetMyTopTracksUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): Result<List<SpotifyTrack>> =
        repository.getMyTopTracks()
}
