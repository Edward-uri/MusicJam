package com.uriel.musicjam.features.authspotify.domain.repositories

interface AuthSpotifyRepository {
    suspend fun exchangeCode(code: String): Result<String>
}