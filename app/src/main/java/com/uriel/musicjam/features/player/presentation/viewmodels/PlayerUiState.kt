package com.uriel.musicjam.features.player.presentation.viewmodels

import com.uriel.musicjam.features.player.data.datasources.remote.models.JamResponseDto
import com.uriel.musicjam.features.player.domain.entities.Jam

data class PlayerUiState(
    val isLoading: Boolean = false,
    val isConnectedToSpotify: Boolean = false,
    val jam: Jam? = null,
    val joinCode: String = "",
    val isPlaying: Boolean = false,
    val progressMs: Long = 0L,
    val durationMs: Long = 0L,
    val currentTrackId: String? = null,
    val currentTrackName: String? = null,
    val currentArtist: String? = null,
    val currentAlbumCover: String? = null,
    val errorMessage: String? = null,

    // Estados para el modal de compartir
    val isShareDialogOpen: Boolean = false,
    val shareLink: String? = null,
    val isLoadingShareLink: Boolean = false,
    val shareLinkError: String? = null
)
