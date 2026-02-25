package com.uriel.musicjam.features.auth.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uriel.musicjam.features.auth.presentation.components.*
import com.uriel.musicjam.features.auth.presentation.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) onNavigateToHome()
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MusicJamBackground)
    ) {
        AuthHeader(title = "Inicia\n\nsesión")

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            // 1. Aumentamos el padding horizontal a 48.dp
            modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido de nuevo",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 2. Usamos el estado del ViewModel
            MusicJamTextField(
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = "Nombre de usuario",
            )

            Spacer(modifier = Modifier.height(24.dp))

            MusicJamTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(48.dp))

            MusicJamButton(
                text = "Ingresar",
                isLoading = uiState.isLoading,
                onClick = { viewModel.login() }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Texto de navegación al registro
            val annotatedText = buildAnnotatedString {
                append("¿No tienes una cuenta? ")
                withStyle(style = SpanStyle(color = MusicJamDark, fontWeight = FontWeight.Bold)) {
                    append("Regístrate")
                }
            }
            Text(
                text = annotatedText,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}