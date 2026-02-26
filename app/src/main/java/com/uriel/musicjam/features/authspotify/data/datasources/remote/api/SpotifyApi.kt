package com.uriel.musicjam.features.authspotify.data.datasources.remote.api

import com.uriel.musicjam.features.auth.data.datasources.remote.dtos.BaseResponse
import com.uriel.musicjam.features.authspotify.data.datasources.remote.dtos.SpotifyCodeExchangeRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SpotifyApi {
    @POST("spotify/auth/exchange")
    suspend fun exchangeCode(@Body request: SpotifyCodeExchangeRequestDto): BaseResponse<String>
    @GET("spotify/auth/token")
    suspend fun getSpotifyAccessToken(): BaseResponse<String>

}