package com.uriel.musicjam.features.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.home.domain.usecases.GetMyAlbumsUseCase
import com.uriel.musicjam.features.home.domain.usecases.GetMyProfileUseCase
import com.uriel.musicjam.features.home.domain.usecases.GetMyTopTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMyProfile: GetMyProfileUseCase,
    private val getMyAlbums: GetMyAlbumsUseCase,
    private val getMyTopTracks: GetMyTopTracksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadHomeData() }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val profileResult = getMyProfile()
            val albumsResult = getMyAlbums()
            val tracksResult = getMyTopTracks()

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    profile = when (profileResult) {
                        is Result.Success -> profileResult.data
                        else -> null
                    },
                    albums = when (albumsResult) {
                        is Result.Success -> albumsResult.data
                        else -> emptyList()
                    },
                    topTracks = when (tracksResult) {
                        is Result.Success -> tracksResult.data
                        else -> emptyList()
                    },
                    errorMessage = when {
                        profileResult is Result.Error -> profileResult.message
                        albumsResult is Result.Error -> albumsResult.message
                        tracksResult is Result.Error -> tracksResult.message
                        else -> null
                    }
                )
            }
        }
    }
}
