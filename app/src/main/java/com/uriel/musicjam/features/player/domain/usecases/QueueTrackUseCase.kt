package com.uriel.musicjam.features.player.domain.usecases
import com.uriel.musicjam.features.player.domain.repositories.PlayerRepository
import javax.inject.Inject

class QueueTrackUseCase @Inject constructor(private val repo: PlayerRepository) {
    suspend operator fun invoke(joinCode: String, trackId: String) = repo.queueTrack(joinCode, trackId)
}