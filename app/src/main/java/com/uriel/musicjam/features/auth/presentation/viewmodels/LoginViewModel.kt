package com.uriel.musicjam.features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uriel.musicjam.features.auth.domain.usecases.LoginUseCase
import com.uriel.musicjam.features.auth.presentation.screens.LoginUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUIState())
    val uiState = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        // 1. Mostramos el indicador de carga y limpiamos errores previos
        _uiState.update { it.copy(isLoading = true, error = null) }

        // 2. Ejecutamos la llamada en un hilo secundario
        viewModelScope.launch {
            val result = loginUseCase(username, password)

            // 3. Actualizamos el estado dependiendo del resultado
            _uiState.update { currentState ->
                result.fold(
                    onSuccess = {
                        currentState.copy(isLoading = false, isSuccess = true)
                    },
                    onFailure = { error ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                )
            }
        }
    }

    // Función útil para limpiar el error después de mostrar un Toast o un Snackbar
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}