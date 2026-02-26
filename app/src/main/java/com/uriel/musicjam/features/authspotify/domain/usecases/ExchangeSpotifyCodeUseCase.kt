package com.uriel.musicjam.features.authspotify.domain.usecases

import com.uriel.musicjam.features.authspotify.domain.repositories.AuthSpotifyRepository
import javax.inject.Inject

class ExchangeSpotifyCodeUseCase @Inject constructor(
    private val repository: AuthSpotifyRepository
) {
    suspend operator fun invoke(code: String): Result<String> {
        if (code.isBlank()) return Result.failure(Exception("Código inválido"))
        return repository.exchangeCode(code)
    }
}