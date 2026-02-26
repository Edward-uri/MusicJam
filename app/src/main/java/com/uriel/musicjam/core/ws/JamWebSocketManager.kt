package com.uriel.musicjam.core.ws

import android.util.Log
import com.uriel.musicjam.core.storage.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JamWebSocketManager @Inject constructor(
    private val tokenManager: TokenManager
) {
    private var session: StompSession? = null
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun connect(joinCode: String): Flow<JamEventDto> {
        val token = tokenManager.getToken()
            ?: throw IllegalStateException("No hay token JWT — inicia sesión primero")

        Log.d("WebSocket", "🔌 Conectando a /topic/jam/$joinCode")

        val client = StompClient(OkHttpWebSocketClient())
        session = client.connect(
            url = "wss://sporify.aleosh.online/ws-jam",
            customStompConnectHeaders = mapOf(
                "Authorization" to "Bearer $token"
            )
        )

        Log.d("WebSocket", "✅ Conectado al WebSocket")

        return session!!
            .subscribeText("/topic/jam/$joinCode")
            .map { raw ->
                Log.d("WebSocket", "📩 Evento recibido: $raw")
                json.decodeFromString<JamEventDto>(raw)
            }
    }

    suspend fun disconnect() {
        session?.disconnect()
        session = null
        Log.d("WebSocket", "🔌 WebSocket desconectado")
    }
}
