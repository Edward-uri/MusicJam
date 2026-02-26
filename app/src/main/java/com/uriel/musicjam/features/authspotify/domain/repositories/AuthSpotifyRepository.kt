package com.uriel.musicjam.features.authspotify.domain.repositories

import com.uriel.musicjam.core.network.Result

interface AuthSpotifyRepository {
    suspend fun exchangeCode(code: String): Result<String>
    suspend fun getSpotifyAccessToken(): Result<String>
}