package com.uriel.musicjam.features.player.domain.repositories

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.player.domain.entities.Jam
import com.uriel.musicjam.features.player.domain.entities.PlayerState

interface PlayerRepository {
    suspend fun createJam(): Result<Jam>
    suspend fun joinJam(joinCode: String): Result<Jam>
    suspend fun leaveJam(joinCode: String): Result<Unit>
    suspend fun getPlayerState(joinCode: String): Result<PlayerState>
    suspend fun play(joinCode: String): Result<Unit>
    suspend fun pause(joinCode: String): Result<Unit>
    suspend fun nextMusic(joinCode: String): Result<Unit>
    suspend fun previous(joinCode: String): Result<Unit>
    suspend fun queueTrack(joinCode: String, trackId: String): Result<Unit>
}
