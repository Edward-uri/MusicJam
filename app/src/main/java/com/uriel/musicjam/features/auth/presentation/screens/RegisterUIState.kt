package com.uriel.musicjam.features.auth.presentation.screens

data class RegisterUIState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)