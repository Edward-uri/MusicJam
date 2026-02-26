package com.uriel.musicjam.features.home.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.uriel.musicjam.core.navegation.AppScreens
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.home.presentation.components.AlbumsRow
import com.uriel.musicjam.features.home.presentation.components.HomeBottomBar
import com.uriel.musicjam.features.home.presentation.components.HomeHeader
import com.uriel.musicjam.features.home.presentation.components.ProfileModal
import com.uriel.musicjam.features.home.presentation.components.TrackItem
import com.uriel.musicjam.features.home.presentation.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    onTrackClick: (SpotifyTrack) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppScreens.Home.route

    val context = LocalContext.current

    if (uiState.showProfileModal) {
        ProfileModal(
            userProfile = uiState.profile,
            isLinkingSpotify = uiState.isLinkingSpotify,
            onDismiss = { viewModel.closeProfileModal() },
            onLinkSpotifyClick = {
                // Aquí construimos la URL y abrimos el navegador
                val clientId = "TU_CLIENT_ID_DE_SPOTIFY"
                val redirectUri = "musicjam://callback" // Esto lo configuraremos en el Manifest luego
                val scopes = "user-read-playback-state user-modify-playback-state user-read-currently-playing app-remote-control streaming playlist-read-private playlist-read-collaborative user-read-private user-read-email"

                val authUrl = "https://accounts.spotify.com/authorize?client_id=$clientId&response_type=code&redirect_uri=$redirectUri&scope=$scopes&show_dialog=true"

                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(authUrl))
                context.startActivity(intent)
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF5E6))
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                HomeHeader(profile = uiState.profile, activation = { viewModel.openProfileModal() })
                Spacer(Modifier.height(24.dp))
            }
            item {
                Text(
                    text = "Tus álbumes",
                    fontWeight = FontWeight(500),
                    fontSize = 24.sp,
                    color = Color(0xFF000000)
                )
                Spacer(Modifier.height(12.dp))
                AlbumsRow(albums = uiState.albums)
                Spacer(Modifier.height(24.dp))
            }
            item {
                Text(
                    text = "Lo que más escuchas",
                    fontWeight = FontWeight(500),
                    fontSize = 24.sp,
                    color = Color(0xFF000000)
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0x1A3C4037),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    uiState.topTracks.forEach { track ->
                        TrackItem(
                            track = track,
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }
        }

        HomeBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            currentRoute = currentRoute,
            onNavigate = { route ->
                if (route != currentRoute) {
                    navController.navigate(route) {
                        popUpTo(AppScreens.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }

}
