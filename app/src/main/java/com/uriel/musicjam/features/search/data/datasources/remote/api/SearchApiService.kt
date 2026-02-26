package com.uriel.musicjam.features.search.data.datasources.remote.api

import com.uriel.musicjam.features.auth.data.datasources.remote.dtos.BaseResponse
import com.uriel.musicjam.features.home.data.datasources.remote.models.SpotifyTrackDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("spotify/me/search")
    suspend fun searchTracks(
        @Query("q") query: String
    ): BaseResponse<List<SpotifyTrackDto>>
}
