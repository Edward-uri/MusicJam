package com.uriel.musicjam.features.search.presentation.viewmodels

import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack

data class SearchUiState(
    val query: String = "",
    val searchResults: List<SpotifyTrack> = emptyList(),
    val isSearching: Boolean = false,
    val errorMessage: String? = null
)
