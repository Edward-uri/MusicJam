package com.uriel.musicjam.features.auth.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uriel.musicjam.core.theme.MusicJamTheme
import com.uriel.musicjam.features.auth.presentation.components.*
import com.uriel.musicjam.features.auth.presentation.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estados para los campos de texto
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Escuchar cambios en el estado (Éxito o Error)
    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            onNavigateToHome()
        }
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            viewModel.clearError() // Limpiamos para que no se muestre el toast múltiples veces
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MusicJamBackground) // El fondo beige
    ) {
        // 1. El encabezado oscuro con la curva
        AuthHeader(title = "Inicia\nsesión")

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. Título principal
            Text(
                text = "Bienvenido de nuevo",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. Campos de entrada
            MusicJamTextField(
                value = username,
                onValueChange = { username = it },
                label = "Nombre de usuario"
            )

            Spacer(modifier = Modifier.height(24.dp))

            MusicJamTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 4. Botón con indicador de carga
            MusicJamButton(
                text = "Ingresar",
                isLoading = uiState.isLoading,
                onClick = {
                    viewModel.login(username, password)
                }
            )
        }
    }
}