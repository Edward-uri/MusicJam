package com.uriel.musicjam.features.home.presentation.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.core.util.Consumer

@Composable
fun HomeScreen(
    navController: NavController,
    onTrackClick: (SpotifyTrack) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AppScreens.Home.route

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // 1. Escuchar el Intent si la app ya estaba abierta (El caso cuando vuelves de Spotify)
    DisposableEffect(activity) {
        val listener = Consumer<Intent> { newIntent ->
            val uri = newIntent.data
            val code = uri?.getQueryParameter("code")

            if (code != null) {
                Log.d("SpotifyAuth", "¡Código atrapado por el sistema!: $code")
                viewModel.exchangeSpotifyCode(code)
                newIntent.data = null // Limpiamos la URL para que no se dispare dos veces
            }
        }
        activity?.addOnNewIntentListener(listener)

        onDispose {
            activity?.removeOnNewIntentListener(listener)
        }
    }

    // 2. Por si acaso: Revisar el Intent original por si la app estaba completamente cerrada
    LaunchedEffect(activity) {
        val uri = activity?.intent?.data
        val code = uri?.getQueryParameter("code")
        if (code != null) {
            android.util.Log.d("SpotifyAuth", "¡Código atrapado al abrir la app!: $code")
            viewModel.exchangeSpotifyCode(code)
            activity?.intent?.data = null
        }
    }

    // 3. Escuchar los mensajes de éxito/error del ViewModel
    LaunchedEffect(key1 = uiState.spotifyLinkSuccess, key2 = uiState.spotifyLinkError) {
        if (uiState.spotifyLinkSuccess) {
            Toast.makeText(context, "¡Cuenta de Spotify vinculada!", Toast.LENGTH_LONG).show()
        }
        if (uiState.spotifyLinkError != null) {
            Toast.makeText(context, uiState.spotifyLinkError, Toast.LENGTH_LONG).show()
            viewModel.clearSpotifyError()
        }
    }

    if (uiState.showProfileModal) {
        ProfileModal(
            userProfile = uiState.profile,
            isLinkingSpotify = uiState.isLinkingSpotify,
            onDismiss = { viewModel.closeProfileModal() },
            onLinkSpotifyClick = {
                val clientId = "8be0a4f09a6c4c3a9283f04f39cffc32"
                val redirectUri = "musicjam://callback"
                val scopes = "user-read-playback-state user-modify-playback-state user-read-currently-playing app-remote-control streaming playlist-read-private playlist-read-collaborative user-read-private user-read-email"

                // Usamos Uri.Builder para construir la URL oficial de Spotify de forma segura
                val authUri = android.net.Uri.parse("https://accounts.spotify.com/authorize")
                    .buildUpon()
                    .appendQueryParameter("client_id", clientId)
                    .appendQueryParameter("response_type", "code") // Le decimos que queremos el código
                    .appendQueryParameter("redirect_uri", redirectUri)
                    .appendQueryParameter("scope", scopes)
                    .appendQueryParameter("show_dialog", "true") // Fuerza la pantalla de login de Spotify
                    .build()

                // Lanzamos el navegador con la URI ya construida y formateada
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, authUri)
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
