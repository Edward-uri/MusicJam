package com.uriel.musicjam.features.auth.presentation.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.uriel.musicjam.features.auth.presentation.components.*
import com.uriel.musicjam.features.auth.presentation.viewmodels.RegisterViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun SignUpScreen(
    onNavigateUp: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
            onNavigateUp() // Devuelve al login automáticamente al registrar (HTTP 201)
        }
        if (uiState.error != null) {
            Toast.makeText(context, uiState.error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MusicJamBackground)
            .verticalScroll(rememberScrollState())
    ) {
        AuthHeader(title = "Regístrate", cornerSize = 70.dp)

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(MusicJamFieldBg, CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Foto",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.AddAPhoto, "Agregar", tint = MusicJamDark, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            MusicJamTextField(
                value = uiState.username,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = "Nombre de usuario"
            )

            Spacer(modifier = Modifier.height(16.dp))

            MusicJamTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = "Correo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            MusicJamTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            MusicJamTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = "Confirmar contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(40.dp))

            MusicJamButton(
                text = "Registrarse",
                isLoading = uiState.isLoading,
                onClick = {
                    val photoFile = selectedImageUri?.let { uriToFile(context, it) }
                    viewModel.register(photoFile)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Navegación de regreso al Login
            val annotatedText = buildAnnotatedString {
                append("¿Ya tienes una cuenta? ")
                withStyle(style = SpanStyle(color = MusicJamDark, fontWeight = FontWeight.Bold)) {
                    append("Inicia sesión")
                }
            }
            Text(
                text = annotatedText,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onNavigateUp() }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
// Función auxiliar para convertir la Uri temporal en un Archivo real
fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, "profile_pic_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        null
    }
}