package com.uriel.musicjam.features.player.data.repositories

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.player.data.datasources.remote.api.PlayerApiService
import com.uriel.musicjam.features.player.data.datasources.remote.mapper.toDomain
import com.uriel.musicjam.features.player.domain.entities.Jam
import com.uriel.musicjam.features.player.domain.entities.PlayerState
import com.uriel.musicjam.features.player.domain.repositories.PlayerRepository
import javax.inject.Inject

class PlayerRepositoryImpl @Inject constructor(
    private val api: PlayerApiService
) : PlayerRepository {

    override suspend fun createJam(): Result<Jam> = try {
        Result.Success(api.createJam().data!!.toDomain())
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al crear Jam")
    }

    override suspend fun joinJam(joinCode: String): Result<Jam> = try {
        Result.Success(api.joinJam(joinCode).data!!.toDomain())
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al unirse a la Jam")
    }

    override suspend fun leaveJam(joinCode: String): Result<Unit> = try {
        api.leaveJam(joinCode)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al salir de la Jam")
    }

    override suspend fun getPlayerState(joinCode: String): Result<PlayerState> = try {
        Result.Success(api.getPlayerState(joinCode).data!!.toDomain())
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al obtener estado del reproductor")
    }

    override suspend fun play(joinCode: String): Result<Unit> = try {
        api.play(joinCode)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al dar play")
    }

    override suspend fun pause(joinCode: String): Result<Unit> = try {
        api.pause(joinCode)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al pausar")
    }

    override suspend fun nextMusic(joinCode: String): Result<Unit> = try {
        api.next(joinCode)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al saltar canción")
    }

    override suspend fun previous(joinCode: String): Result<Unit> = try {
        api.previous(joinCode)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al regresar canción")
    }

    override suspend fun queueTrack(joinCode: String, trackId: String): Result<Unit> = try {
        api.queueTrack(joinCode, trackId)
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error al encolar canción")
    }
}
