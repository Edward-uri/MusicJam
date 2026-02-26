package com.uriel.musicjam.features.player.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.musicjam.core.network.Result
import com.uriel.musicjam.core.spotify.SpotifyRemoteManager
import com.uriel.musicjam.core.storage.JamCodeManager
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
    private val jamCodeManager: JamCodeManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var wsJob: Job? = null
    private var progressJob: Job? = null
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

    fun initializePlayer(context: Context, trackId: String?) {
        // Si la jam ya está cargada en memoria, no hacemos doble inicialización
        if (_uiState.value.jam != null) {
            // Si estábamos activos y mandaron un track nuevo, lo reproducimos en la jam actual
            if (trackId != null) {
                val joinCode = _uiState.value.jam!!.joinCode
                viewModelScope.launch {
                    queueTrackUseCase(joinCode, trackId)
                    nextTrackUseCase(joinCode)
                }
            }
            return
        }

        val savedJoinCode = jamCodeManager.getActiveJoinCode()

        if (savedJoinCode != null) {
            // 1. Hay una Jam previa guardada, intentamos restaurarla
            _uiState.update { it.copy(joinCode = savedJoinCode, isLoading = true) }

            viewModelScope.launch {
                when (val result = joinJamUseCase(savedJoinCode)) {
                    is Result.Success -> {
                        // Restauración exitosa
                        _uiState.update { it.copy(isLoading = false, jam = result.data) }
                        connectSpotify(context)
                        startWebSocket(result.data.joinCode)

                        // Si el usuario entró haciendo clic en una canción nueva, la reproducimos
                        if (trackId != null) {
                            queueTrackUseCase(savedJoinCode, trackId)
                            nextTrackUseCase(savedJoinCode)
                        }
                    }
                    is Result.Error -> {
                        // 2. Falló la reconexión (probablemente la Jam ya no existe en el backend)
                        jamCodeManager.clearActiveJoinCode()
                        _uiState.update { it.copy(isLoading = false, joinCode = "") }

                        // Si venía con un trackId, creamos una jam desde cero
                        if (trackId != null) {
                            createJamAndPlay(context, trackId)
                        }
                    }
                    else -> {}
                }
            }
        } else if (trackId != null) {
            // 3. No hay Jam guardada, creamos una nueva usando la canción
            createJamAndPlay(context, trackId)
        }
    }

    fun createJam(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = createJamUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false, jam = result.data) }
                    connectSpotify(context)
                    jamCodeManager.saveActiveJoinCode(result.data.joinCode)
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
                    jamCodeManager.saveActiveJoinCode(result.data.joinCode)
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
        val currentJam = _uiState.value.jam ?: return
        val joinCode = currentJam.joinCode

        viewModelScope.launch {
            spotifyRemote.pause()


            leaveJamUseCase(joinCode)
            jamCodeManager.clearActiveJoinCode()

            wsJob?.cancel()
            stopProgressTimer()
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
        val state = event.state ?: return

        val track = state.track
        val previousState = _uiState.value

        // 1. Sabemos que la música debe avanzar si el evento es PLAYING o TRACK_CHANGED
        val isNowPlaying = event.eventType == "PLAYING" || event.eventType == "TRACK_CHANGED"

        // 2. Actualizamos el UiState (Asegúrate de incluir isPlaying)
        _uiState.update {
            it.copy(
                isPlaying = isNowPlaying, // <-- ESTO ARREGLA EL BOTÓN DE PLAY/PAUSE
                progressMs = event.state.progressMs,
                durationMs = track?.durationMs ?: it.durationMs,
                currentTrackId = track?.id,
                currentTrackName = track?.name,
                currentArtist = track?.artist,
                currentAlbumCover = track?.albumCoverUrl
            )
        }

        // 3. Encendemos o apagamos la barra de progreso de la UI
        if (isNowPlaying) {
            startProgressTimer()
        } else {
            stopProgressTimer()
        }

        // 4. Control del SDK local de Spotify
        when (event.eventType) {
            "PLAYING", "TRACK_CHANGED" -> {
                /*track?.let {
                    // Validamos si es la misma canción que ya estaba en el reproductor
                    if (previousState.currentTrackId == it.id) {
                        // Si es la misma canción y el evento es PLAYING, solo le quitamos la pausa (resume)
                        // Si el evento es TRACK_CHANGED pero es la misma canción, se ignora para no reiniciarla
                        if (event.eventType == "PLAYING") {
                            Log.d("PlayerVM", "Resume local")
                            spotifyRemote.resume()
                        } else {
                            Log.d("PlayerVM", "Ignorando TRACK_CHANGED repetido para: ${it.id}")
                        }
                    } else {
                        // Es una canción nueva, por lo tanto inicia desde el principio
                        Log.d("PlayerVM", "Play local: spotify:track:${it.id}")
                        spotifyRemote.play("spotify:track:${it.id}")
                    }
                }*/
            }
            "PAUSED" -> {
                /*Log.d("PlayerVM", "Pause local")
                spotifyRemote.pause()*/
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

                    // 1. Conecta el SDK de Spotify localmente
                    connectSpotify(context)

                    // 2. Iniciamos el WebSocket ANTES de mandar comandos
                    // para asegurar que escuchemos el evento de cambio de canción
                    startWebSocket(joinCode)

                    // 3. Encolamos la canción seleccionada en el backend
                    queueTrackUseCase(joinCode, trackId)

                    // 4. Forzamos a Spotify a saltar a la siguiente canción en la cola
                    // (que será exactamente la que acabamos de encolar)
                    nextTrackUseCase(joinCode)
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
        stopProgressTimer() // <-- Añade esto
        spotifyRemote.disconnect()
    }

    private fun startProgressTimer() {
        if (progressJob?.isActive == true) {
            Log.d("PlayerProgress", "⏱️ El timer ya está corriendo. Ignorando nueva petición.")
            return
        }

        Log.d("PlayerProgress", "▶️ ¡Timer INICIADO!")

        progressJob = viewModelScope.launch {
            while (true) {
                delay(1000) // Un segundo exacto
                _uiState.update { currentState ->
                    val newProgress = currentState.progressMs + 1000
                    val duration = currentState.durationMs

                    // Calculamos el porcentaje para el log (solo informativo)
                    val percent = if (duration > 0) (newProgress.toFloat() / duration) * 100 else 0f

                    Log.d("PlayerProgress", "⏱️ Tick! Progreso: $newProgress ms / $duration ms (${percent.toInt()}%)")

                    // Solo avanzamos si no hemos superado el tiempo de la canción (con 2 segundos de tolerancia)
                    if (duration == 0L || newProgress <= duration + 2000) {
                        currentState.copy(progressMs = newProgress) // Al usar copy, forzamos a redibujar la UI
                    } else {
                        Log.d("PlayerProgress", "⏹️ Límite de la canción alcanzado. Congelando barra.")
                        currentState
                    }
                }
            }
        }
    }

    private fun stopProgressTimer() {
        Log.d("PlayerProgress", "⏸️ Timer DETENIDO.")
        progressJob?.cancel()
        progressJob = null
    }
}
