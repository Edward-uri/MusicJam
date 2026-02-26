package com.uriel.musicjam.core.spotify

import android.content.Context
import android.util.Log
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class SpotifyRemoteManager @Inject constructor() {

    private var spotifyAppRemote: SpotifyAppRemote? = null

    val isConnected: Boolean get() = spotifyAppRemote?.isConnected == true

    suspend fun connectWithToken(context: Context, accessToken: String): SpotifyAppRemote =
        suspendCancellableCoroutine { continuation ->
            spotifyAppRemote?.let { SpotifyAppRemote.disconnect(it) }
            spotifyAppRemote = null

            val params = ConnectionParams.Builder("8be0a4f09a6c4c3a9283f04f39cffc32")
                .setRedirectUri("musicjam://callback")
                .showAuthView(false)
                .build()

            SpotifyAppRemote.connect(context, params, object : Connector.ConnectionListener {
                override fun onConnected(remote: SpotifyAppRemote) {
                    Log.d("SpotifyRemote", "✅ Conectado con token externo")
                    spotifyAppRemote = remote
                    if (continuation.isActive) continuation.resume(remote)
                }

                override fun onFailure(error: Throwable) {
                    Log.e("SpotifyRemote", "❌ Error: ${error.message}")
                    // ✅ No crashea si ya fue resumida por onConnected
                    if (continuation.isActive) continuation.resumeWithException(error)
                }
            })

            // ✅ Si la corrutina se cancela, desconecta limpiamente
            continuation.invokeOnCancellation {
                spotifyAppRemote?.let { SpotifyAppRemote.disconnect(it) }
                spotifyAppRemote = null
            }
        }

    fun disconnect() {
        spotifyAppRemote?.let {
            SpotifyAppRemote.disconnect(it)
            spotifyAppRemote = null
        }
    }

    fun play(spotifyUri: String) {
        spotifyAppRemote?.playerApi?.play(spotifyUri)
            ?.setResultCallback { Log.d("SpotifyRemote", "✅ Playing: $spotifyUri") }
            ?.setErrorCallback { Log.e("SpotifyRemote", "❌ Play error: ${it.message}") }
            ?: Log.e("SpotifyRemote", "❌ spotifyAppRemote es null")
    }

    fun pause() {
        spotifyAppRemote?.playerApi?.pause()
    }

    fun resume() {
        spotifyAppRemote?.playerApi?.resume()
    }

    fun skipNext() {
        spotifyAppRemote?.playerApi?.skipNext()
    }

    fun skipPrevious() {
        spotifyAppRemote?.playerApi?.skipPrevious()
    }

    fun queue(spotifyUri: String) {
        spotifyAppRemote?.playerApi?.queue(spotifyUri)
    }

    fun subscribeToPlayerState(
        callback: (com.spotify.protocol.types.PlayerState) -> Unit
    ) {
        spotifyAppRemote?.playerApi
            ?.subscribeToPlayerState()
            ?.setEventCallback { state -> callback(state) }
    }
}
