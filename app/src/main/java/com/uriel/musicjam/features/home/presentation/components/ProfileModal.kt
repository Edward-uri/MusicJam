package com.uriel.musicjam.features.home.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.uriel.musicjam.features.auth.presentation.components.MusicJamDark
import com.uriel.musicjam.features.home.domain.entities.UserProfile

@Composable
fun ProfileModal(
    userProfile: UserProfile?,
    isLinkingSpotify: Boolean,
    onDismiss: () -> Unit,
    onLinkSpotifyClick: () -> Unit
) {
    if (userProfile == null) return

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF5E6))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto de perfil mediana
                AsyncImage(
                    model = userProfile.photo,
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Datos del usuario
                Text(
                    text = userProfile.username,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = userProfile.email,
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Botón de vincular
                Button(
                    onClick = onLinkSpotifyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)), // Verde Spotify
                    shape = RoundedCornerShape(50),
                    enabled = !isLinkingSpotify
                ) {
                    if (isLinkingSpotify) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Vincular con Spotify", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onDismiss) {
                    Text("Cerrar", color = MusicJamDark)
                }
            }
        }
    }
}