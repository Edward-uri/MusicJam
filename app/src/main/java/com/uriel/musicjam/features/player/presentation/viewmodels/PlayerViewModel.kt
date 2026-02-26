package com.uriel.musicjam.features.player.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.core.spotify.SpotifyRemoteManager
import com.uriel.musicjam.core.ws.JamEventDto
import com.uriel.musicjam.core.ws.JamWebSocketManager
import com.uriel.musicjam.features.authspotify.domain.usecases.GetSpotifyAccessTokenUseCase
import com.uriel.musicjam.features.player.domain.usecases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val createJamUseCase: CreateJamUseCase,
    private val joinJamUseCase: JoinJamUseCase,
    private val leaveJamUseCase: LeaveJamUseCase,
    private val playUseCase: PlayUseCase,
    private val pauseUseCase: PauseUseCase,
    private val nextTrackUseCase: NextTrackUseCase,
    private val previousTrackUseCase: PreviousTrackUseCase,
    private val queueTrackUseCase: QueueTrackUseCase,
    private val spotifyRemote: SpotifyRemoteManager,
    private val getSpotifyAccessToken: GetSpotifyAccessTokenUseCase,
    private val jamWebSocket: JamWebSocketManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var wsJob: Job? = null

    // ─────────────────────────────────────────────
    // Spotify SDK
    // ─────────────────────────────────────────────

    fun connectSpotify(context: Context) {
        viewModelScope.launch {
            try {
                when (val tokenResult = getSpotifyAccessToken()) {
                    is Result.Success -> {
                        Log.d("PlayerVM", "🎵 Token Spotify obtenido")
                        spotifyRemote.connectWithToken(context, tokenResult.data)
                        _uiState.update { it.copy(isConnectedToSpotify = true) }
                        Log.d("PlayerVM", "✅ Spotify SDK conectado")
                    }
                    is Result.Error -> {
                        Log.e("PlayerVM", "❌ Error token Spotify: ${tokenResult.message}")
                        _uiState.update {
                            it.copy(errorMessage = "Vincula tu cuenta de Spotify primero")
                        }
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                Log.e("PlayerVM", "❌ Error conectando Spotify: ${e.message}")
                _uiState.update {
                    it.copy(errorMessage = "Error al conectar con Spotify: ${e.message}")
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    // Jam — crear / unirse / salir
    // ─────────────────────────────────────────────

    fun onJoinCodeChanged(code: String) {
        _uiState.update { it.copy(joinCode = code) }
    }

    fun createJam(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = createJamUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, jam = result.data) }
                    connectSpotify(context)
                    startWebSocket(result.data.joinCode)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun joinJam(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = joinJamUseCase(_uiState.value.joinCode)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, jam = result.data) }
                    connectSpotify(context)
                    startWebSocket(result.data.joinCode)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {}
            }
        }
    }

    fun leaveJam() {
        val joinCode = _uiState.value.jam?.joinCode ?: return
        viewModelScope.launch {
            leaveJamUseCase(joinCode)
            wsJob?.cancel()
            launch { jamWebSocket.disconnect() }
            spotifyRemote.disconnect()
            _uiState.update { PlayerUiState() }
        }
    }

    // ─────────────────────────────────────────────
    // WebSocket — escucha eventos en tiempo real
    // ─────────────────────────────────────────────

    private fun startWebSocket(joinCode: String) {
        wsJob?.cancel()
        wsJob = viewModelScope.launch {
            try {
                Log.d("PlayerVM", "📡 Iniciando WebSocket para jam: $joinCode")
                jamWebSocket.connect(joinCode).collect { event ->
                    Log.d("PlayerVM", "📩 Evento: ${event.eventType} por ${event.triggeredBy}")
                    handleJamEvent(event)
                }
            } catch (e: Exception) {
                Log.e("PlayerVM", "❌ WebSocket error: ${e.message}")
                _uiState.update {
                    it.copy(errorMessage = "Error de conexión en tiempo real: ${e.message}")
                }
            }
        }
    }

    private fun handleJamEvent(event: JamEventDto) {
        val track = event.state.track

        // Actualiza la UI con el estado recibido del servidor
        _uiState.update {
            it.copy(
                progressMs = event.state.progressMs,
                durationMs = track?.durationMs ?: it.durationMs,
                currentTrackId = track?.id,
                currentTrackName = track?.name,
                currentArtist = track?.artist,
                currentAlbumCover = track?.albumCoverUrl
            )
        }

        when (event.eventType) {
            "PLAYING" -> {
                track?.let {
                    Log.d("PlayerVM", "Play local: spotify:track:${it.id}")
                    spotifyRemote.play("spotify:track:${it.id}")
                }
            }
            "PAUSED" -> {
                Log.d("PlayerVM", "⏸️ Pause local")
                spotifyRemote.pause()
            }
            "TRACK_CHANGED" -> {
                track?.let {
                    Log.d("PlayerVM", "Track changed: spotify:track:${it.id}")
                    spotifyRemote.play("spotify:track:${it.id}")
                }
            }
        }
    }

    fun play() {
        val joinCode = _uiState.value.jam?.joinCode ?: run {
            Log.e("PlayerVM", "play() — no hay jam activo")
            return
        }
        viewModelScope.launch {
            // Solo le dice al backend que haga play
            // El WebSocket recibirá el evento PLAYING y reproducirá localmente
            when (val result = playUseCase(joinCode)) {
                is Result.Error -> {
                    Log.e("PlayerVM", "❌ play() error: ${result.message}")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> Log.d("PlayerVM", "✅ play() enviado al backend")
            }
        }
    }

    fun createJamAndPlay(context: Context, trackId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = createJamUseCase()) {
                is Result.Success -> {
                    val joinCode = result.data.joinCode
                    _uiState.update { it.copy(isLoading = false, jam = result.data) }

                    // 1. Conecta SDK
                    connectSpotify(context)

                    // 2. SDK activa Spotify localmente con la canción
                    //    Esto "despierta" el dispositivo para que el backend pueda controlarlo
                    spotifyRemote.play("spotify:track:$trackId")

                    // 3. Pequeño delay para que Spotify registre el dispositivo activo
                    delay(1500)

                    // 4. Encola en el backend
                    queueTrackUseCase(joinCode, trackId)

                    // 5. Backend toma control
                    playUseCase(joinCode)

                    // 6. WebSocket escucha eventos
                    startWebSocket(joinCode)
                }
                is Result.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
                else -> {}
            }
        }
    }



    fun pause() {
        val joinCode = _uiState.value.jam?.joinCode ?: run {
            Log.e("PlayerVM", "❌ pause() — no hay jam activo")
            return
        }
        viewModelScope.launch {
            when (val result = pauseUseCase(joinCode)) {
                is Result.Error -> {
                    Log.e("PlayerVM", "❌ pause() error: ${result.message}")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> Log.d("PlayerVM", "✅ pause() enviado al backend")
            }
        }
    }

    fun next() {
        val joinCode = _uiState.value.jam?.joinCode ?: run {
            Log.e("PlayerVM", "❌ next() — no hay jam activo")
            return
        }
        viewModelScope.launch {
            when (val result = nextTrackUseCase(joinCode)) {
                is Result.Error -> {
                    Log.e("PlayerVM", "❌ next() error: ${result.message}")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> Log.d("PlayerVM", "✅ next() enviado al backend")
            }
        }
    }

    fun previous() {
        val joinCode = _uiState.value.jam?.joinCode ?: run {
            Log.e("PlayerVM", "❌ previous() — no hay jam activo")
            return
        }
        viewModelScope.launch {
            when (val result = previousTrackUseCase(joinCode)) {
                is Result.Error -> {
                    Log.e("PlayerVM", "❌ previous() error: ${result.message}")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> Log.d("PlayerVM", "✅ previous() enviado al backend")
            }
        }
    }

    fun queueTrack(trackId: String) {
        val joinCode = _uiState.value.jam?.joinCode ?: run {
            Log.e("PlayerVM", "❌ queueTrack() — no hay jam activo")
            return
        }
        viewModelScope.launch {
            when (val result = queueTrackUseCase(joinCode, trackId)) {
                is Result.Success -> Log.d("PlayerVM", "✅ Track encolado: $trackId")
                is Result.Error -> {
                    Log.e("PlayerVM", "❌ queueTrack() error: ${result.message}")
                    _uiState.update { it.copy(errorMessage = result.message) }
                }
                else -> {}
            }
        }
    }

    // ─────────────────────────────────────────────
    // Utilidades
    // ─────────────────────────────────────────────

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    override fun onCleared() {
        super.onCleared()
        wsJob?.cancel()
        spotifyRemote.disconnect()
    }
}
