package com.uriel.musicjam.features.authspotify.domain.usecases

import com.uriel.musicjam.features.authspotify.domain.repositories.AuthSpotifyRepository
import javax.inject.Inject

class GetSpotifyAccessTokenUseCase @Inject constructor(
    private val repo: AuthSpotifyRepository
) {
    suspend operator fun invoke() = repo.getSpotifyAccessToken()
}
