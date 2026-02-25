package com.uriel.musicjam.features.home.domain.usecases

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.domain.entities.SpotifyAlbum
import com.uriel.musicjam.features.home.domain.repositories.HomeRepository
import javax.inject.Inject

class GetMyAlbumsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(): Result<List<SpotifyAlbum>> =
        repository.getMyAlbums()
}
