package com.uriel.musicjam.features.authspotify.domain.usecases

import com.uriel.musicjam.core.network.Result // <-- Importar tu Result
import com.uriel.musicjam.features.authspotify.domain.repositories.AuthSpotifyRepository
import javax.inject.Inject

class ExchangeSpotifyCodeUseCase @Inject constructor(
    private val repository: AuthSpotifyRepository
) {
    suspend operator fun invoke(code: String): Result<String> {
        // Usamos tu Result.Error
        if (code.isBlank()) return Result.Error("Código inválido")
        return repository.exchangeCode(code)
    }
}