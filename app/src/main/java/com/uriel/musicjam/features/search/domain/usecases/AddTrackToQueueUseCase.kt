package com.uriel.musicjam.features.search.domain.usecases

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.search.domain.repositories.SearchRepository
import javax.inject.Inject

class AddTrackToQueueUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(trackId: String): Result<Unit> =
        repository.queueTrack(trackId)
}