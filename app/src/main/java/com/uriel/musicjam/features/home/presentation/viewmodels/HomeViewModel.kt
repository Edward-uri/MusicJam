package com.uriel.musicjam.features.home.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.features.authspotify.domain.usecases.ExchangeSpotifyCodeUseCase
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
    private val getMyTopTracks: GetMyTopTracksUseCase,
    private val exchangeSpotifyCodeUseCase: ExchangeSpotifyCodeUseCase,
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

    fun openProfileModal() {
        _uiState.update { it.copy(showProfileModal = true) }
    }

    fun closeProfileModal() {
        _uiState.update { it.copy(showProfileModal = false, spotifyLinkError = null) }
    }

    fun exchangeSpotifyCode(code: String) {
        // LOG 3: ¿Llegó al ViewModel?
        android.util.Log.d("SpotifyAuth", "HomeViewModel - Iniciando exchangeSpotifyCode con: $code")

        // 1. Aquí encendemos la carga
        _uiState.update { it.copy(isLinkingSpotify = true, spotifyLinkError = null) }

        viewModelScope.launch {
            val result = exchangeSpotifyCodeUseCase(code)

            // LOG 4: ¿Qué nos devolvió el caso de uso?
            android.util.Log.d("SpotifyAuth", "HomeViewModel - Resultado del UseCase: $result")

            when (result) {
                is Result.Success<*> -> {
                    android.util.Log.d("SpotifyAuth", "HomeViewModel - ¡Éxito al vincular!")

                    // 2. Apagamos la carga, cerramos el modal y disparamos el éxito
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLinkingSpotify = false,
                            spotifyLinkSuccess = true,
                            showProfileModal = false
                        )
                    }

                    // 3. ¡Recargamos la vista para mostrar las canciones de Spotify!
                    loadHomeData()
                }
                is Result.Error -> {
                    android.util.Log.e("SpotifyAuth", "HomeViewModel - Fallo la vinculación: ${result.message}")

                    // 2. Apagamos la carga y mostramos el error
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLinkingSpotify = false,
                            spotifyLinkError = result.message
                        )
                    }
                }
                is Result.Loading -> {}
            }
        }
    }


    fun clearSpotifyError() {
        _uiState.update { it.copy(spotifyLinkError = null) }
    }
}
