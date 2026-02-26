package com.uriel.musicjam.core.ws

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JamEventDto(
    val eventType: String,
    val triggeredBy: String,
    val state: JamStateDto? = null
)

@Serializable
data class JamStateDto(
    val isPlaying: Boolean? = null,      // ← nullable por si cambia
    val playing: Boolean? = null,        // ← el backend manda "playing" no "isPlaying"
    val progressMs: Long = 0L,
    val track: JamTrackDto? = null
) {
    // Unifica los dos posibles nombres
    val isActuallyPlaying: Boolean get() = isPlaying ?: playing ?: false
}

@Serializable
data class JamTrackDto(
    val id: String,
    val name: String,
    val artist: String,
    @SerialName("imageUrl")
    val albumCoverUrl: String = "",      // ← backend manda "imageUrl"
    val durationMs: Long = 0L            // ← opcional, no siempre viene
)
