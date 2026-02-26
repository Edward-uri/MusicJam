package com.uriel.musicjam.features.player.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.uriel.musicjam.features.player.presentation.components.JamSetupContent
import com.uriel.musicjam.features.player.presentation.components.PlayerControls
import com.uriel.musicjam.features.player.presentation.components.PlayerProgressBar
import com.uriel.musicjam.features.player.presentation.components.PlayerTrackInfo
import com.uriel.musicjam.features.player.presentation.viewmodels.PlayerViewModel

@Composable
fun PlayerScreen(
    navController: NavController,
    trackId: String? = null,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (trackId != null && uiState.jam == null) {
            viewModel.createJamAndPlay(context, trackId)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A2C1A), Color(0xFF0D1A0D))))
    ) {
        when {
            uiState.jam == null -> JamSetupContent(
                joinCode = uiState.joinCode,
                isLoading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                onJoinCodeChanged = { viewModel.onJoinCodeChanged(it) },
                onCreateJam = { viewModel.createJam(context) },  // ✅ context
                onJoinJam = { viewModel.joinJam(context) }       // ✅ context
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(64.dp))

                // Header Jam
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Jam: ${uiState.jam?.joinCode}",
                        color = Color(0xFF1DB954),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = { viewModel.leaveJam() }) {
                        Text("Salir", color = Color.White.copy(alpha = 0.6f))
                    }
                }

                Spacer(Modifier.height(24.dp))

                // ✅ Ya no usa uiState.playerState?.track
                // Los datos del track vienen directo del UiState
                PlayerTrackInfo(
                    trackName = uiState.currentTrackName,
                    artist = uiState.currentArtist,
                    albumCoverUrl = uiState.currentAlbumCover
                )

                Spacer(Modifier.height(32.dp))

                PlayerProgressBar(
                    progressMs = uiState.progressMs,
                    durationMs = uiState.durationMs
                )

                Spacer(Modifier.height(40.dp))

                PlayerControls(
                    isPlaying = uiState.isPlaying,
                    onPlay = { viewModel.play() },
                    onPause = { viewModel.pause() },
                    onNext = { viewModel.next() },
                    onPrevious = { viewModel.previous() }
                )
            }
        }

        // Back button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .statusBarsPadding()
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Cerrar",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
