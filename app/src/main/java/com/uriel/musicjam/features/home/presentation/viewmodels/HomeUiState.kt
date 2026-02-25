package com.uriel.musicjam.features.home.presentation.viewmodels

import com.uriel.musicjam.features.home.domain.entities.SpotifyAlbum
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.home.domain.entities.UserProfile

data class HomeUiState(
    val profile: UserProfile? = null,
    val albums: List<SpotifyAlbum> = emptyList(),
    val topTracks: List<SpotifyTrack> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
