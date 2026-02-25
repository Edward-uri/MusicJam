package com.uriel.musicjam.features.home.domain.repositories

import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.domain.entities.SpotifyAlbum
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.home.domain.entities.UserProfile

interface HomeRepository {
    suspend fun getMyAlbums(): Result<List<SpotifyAlbum>>
    suspend fun getMyTopTracks(): Result<List<SpotifyTrack>>
    suspend fun getMyProfile(): Result<UserProfile>
}
