package com.uriel.musicjam.features.search.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.core.storage.JamCodeManager
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.search.domain.usecases.AddTrackToQueueUseCase
import com.uriel.musicjam.features.search.domain.usecases.SearchTracksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTracks: SearchTracksUseCase,
    private val addTrackToQueueUseCase: AddTrackToQueueUseCase,
    private val jamCodeManager: JamCodeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun searchTracks() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSearching = true,
                    errorMessage = null
                )
            }

            val result = searchTracks(uiState.value.query)

            _uiState.update { state ->
                state.copy(
                    isSearching = false,
                    searchResults = when (result) {
                        is Result.Success -> result.data
                        else -> emptyList()
                    },
                    errorMessage = when (result) {
                        is Result.Error -> result.message
                        else -> null
                    }
                )
            }
        }
    }

    fun addTrackToQueue(track: SpotifyTrack) {
        // 1. OPTIMISTIC UPDATE: Asumimos éxito inmediato (se actualiza la UI al instante)
        _uiState.update { state ->
            state.copy(
                queuedTrackIds = state.queuedTrackIds + track.id, // Añadimos visualmente a la cola
                queueMessage = "Añadida a la cola: ${track.name}"
            )
        }

        // 2. HACEMOS LA PETICIÓN AL SERVIDOR
        viewModelScope.launch {
            // Obtenemos el código. Si no hay (es null), mandamos "INVALID" para forzar el error en el backend
            val joinCode = jamCodeManager.getActiveJoinCode() ?: "INVALID"

            val result = addTrackToQueueUseCase(joinCode, track.id)

            // 3. ROLLBACK: Si falló (inevitable si el código era inválido o null), revertimos el cambio visual
            if (result is Result.Error) {
                _uiState.update { state ->
                    state.copy(
                        queuedTrackIds = state.queuedTrackIds - track.id, // Lo quitamos visualmente
                        queueMessage = "No se pudo añadir ${track.name}. Revirtiendo..."
                    )
                }
            }
        }
    }

    fun clearQueueMessage() {
        _uiState.update { it.copy(queueMessage = null) }
    }
}
