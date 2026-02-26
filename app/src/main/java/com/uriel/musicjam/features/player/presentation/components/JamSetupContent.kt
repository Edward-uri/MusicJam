package com.uriel.musicjam.features.player.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JamSetupContent(
    joinCode: String,
    isLoading: Boolean,
    errorMessage: String?,
    onJoinCodeChanged: (String) -> Unit,
    onCreateJam: () -> Unit,
    onJoinJam: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            tint = Color(0xFF1DB954),
            modifier = Modifier.size(80.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text("MusicJam", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text(
            "Comparte música con tus amigos",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 16.sp
        )
        Spacer(Modifier.height(48.dp))

        errorMessage?.let {
            Text(it, color = Color(0xFFFF6B6B), fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = onCreateJam,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Crear nueva Jam", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.2f))
            Text("  o  ", color = Color.White.copy(alpha = 0.5f))
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White.copy(alpha = 0.2f))
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = joinCode,
            onValueChange = onJoinCodeChanged,
            placeholder = { Text("Código de Jam (ej. X8B9QA)", color = Color.White.copy(alpha = 0.4f)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1DB954),
                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF1DB954)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onJoinJam,
            enabled = joinCode.length == 6 && !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
        ) {
            Icon(Icons.Default.Group, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Unirse a Jam", fontWeight = FontWeight.SemiBold)
        }
    }
}
