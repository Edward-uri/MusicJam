package com.uriel.musicjam.features.player.domain.usecases

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.player.domain.repositories.PlayerRepository
import javax.inject.Inject

class GetShareLinkUseCase @Inject constructor(
    private val repository: PlayerRepository
) {
    suspend operator fun invoke(joinCode: String): Result<String> =
        repository.getShareLink(joinCode)
}