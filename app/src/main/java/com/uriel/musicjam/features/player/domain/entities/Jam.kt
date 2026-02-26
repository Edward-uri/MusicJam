package com.uriel.musicjam.features.player.domain.entities

data class Jam(
    val id: String,
    val joinCode: String,
    val activeSpeakerUserId: String,
    val active: Boolean
)
