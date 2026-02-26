package com.uriel.musicjam.features.player.data.datasources.remote.api

import com.uriel.musicjam.features.auth.data.datasources.remote.dtos.BaseResponse
import com.uriel.musicjam.features.player.data.datasources.remote.models.JamResponseDto
import com.uriel.musicjam.features.player.data.datasources.remote.models.SpotifyPlayerStateDto
import retrofit2.http.*

interface PlayerApiService {

    @POST("jams")
    suspend fun createJam(): BaseResponse<JamResponseDto>

    @POST("jams/join/{joinCode}")
    suspend fun joinJam(
        @Path("joinCode") joinCode: String
    ): BaseResponse<JamResponseDto>

    @DELETE("jams/{joinCode}/leave")
    suspend fun leaveJam(
        @Path("joinCode") joinCode: String
    ): BaseResponse<Void>

    @GET("jams/{joinCode}/player/state")
    suspend fun getPlayerState(
        @Path("joinCode") joinCode: String
    ): BaseResponse<SpotifyPlayerStateDto>

    @PUT("jams/{joinCode}/player/play")
    suspend fun play(
        @Path("joinCode") joinCode: String
    ): BaseResponse<Void>

    @PUT("jams/{joinCode}/player/pause")
    suspend fun pause(
        @Path("joinCode") joinCode: String
    ): BaseResponse<Void>

    @POST("jams/{joinCode}/player/next")
    suspend fun next(
        @Path("joinCode") joinCode: String
    ): BaseResponse<Void>

    @POST("jams/{joinCode}/player/previous")
    suspend fun previous(
        @Path("joinCode") joinCode: String
    ): BaseResponse<Void>

    @POST("jams/{joinCode}/player/queue")
    suspend fun queueTrack(
        @Path("joinCode") joinCode: String,
        @Query("trackId") trackId: String
    ): BaseResponse<Void>
}
