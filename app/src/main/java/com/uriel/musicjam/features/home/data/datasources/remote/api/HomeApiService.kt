package com.uriel.musicjam.features.home.data.datasources.remote.api

import com.uriel.musicjam.features.auth.data.datasources.remote.dtos.BaseResponse
import com.uriel.musicjam.features.home.data.datasources.remote.models.SpotifyAlbumDto
import com.uriel.musicjam.features.home.data.datasources.remote.models.SpotifyTrackDto
import com.uriel.musicjam.features.home.data.datasources.remote.models.UserResponseDto
import retrofit2.http.GET

interface HomeApiService {

    @GET("spotify/me/albums")
    suspend fun getMyAlbums(): BaseResponse<List<SpotifyAlbumDto>>

    @GET("spotify/me/top-tracks")
    suspend fun getMyTopTracks(): BaseResponse<List<SpotifyTrackDto>>

    @GET("users/me")
    suspend fun getMyProfile(): BaseResponse<UserResponseDto>
}
