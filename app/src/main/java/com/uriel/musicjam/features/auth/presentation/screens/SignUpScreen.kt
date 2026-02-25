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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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

    // Estados de los campos
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estado para la URI de la imagen seleccionada
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador del selector de fotos nativo de Android
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    // Escuchar el estado del ViewModel
    LaunchedEffect(uiState) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
            // Si el backend loguea automáticamente al registrar, vamos al Home.
            // Si no, volvemos al Login con onNavigateUp()
            onNavigateUp()
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
            .verticalScroll(rememberScrollState()) // ¡Vital para pantallas con muchos campos!
    ) {
        // 1. Encabezado con la curva suave (70.dp) como en tu Figma
        AuthHeader(title = "Regístrate", cornerSize = 70.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 2. Selector de Foto de Perfil
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(MusicJamFieldBg, CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        // Abre la galería filtrando solo imágenes
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
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Agregar foto",
                        tint = MusicJamDark,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Campos de texto
            MusicJamTextField(
                value = username,
                onValueChange = { username = it },
                label = "Nombre de usuario"
            )

            Spacer(modifier = Modifier.height(16.dp))

            MusicJamTextField(
                value = email,
                onValueChange = { email = it },
                label = "Correo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            MusicJamTextField(
                value = password,
                onValueChange = { password = it },
                label = "Contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            MusicJamTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirmar contraseña",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 4. Botón de Registro
            MusicJamButton(
                text = "Registrarse",
                isLoading = uiState.isLoading,
                onClick = {
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        return@MusicJamButton
                    }

                    // Convertimos la URI de Android a un File de Java para el backend
                    val photoFile = selectedImageUri?.let { uriToFile(context, it) }

                    viewModel.register(username, email, password, photoFile)
                }
            )

            Spacer(modifier = Modifier.height(40.dp)) // Espacio final para que el scroll termine bien
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