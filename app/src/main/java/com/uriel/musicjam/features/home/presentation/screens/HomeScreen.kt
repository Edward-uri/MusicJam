package com.uriel.musicjam.features.home.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.uriel.musicjam.core.navegation.AppScreens
import com.uriel.musicjam.features.home.domain.entities.SpotifyTrack
import com.uriel.musicjam.features.home.presentation.components.AlbumsRow
import com.uriel.musicjam.features.home.presentation.components.HomeBottomBar
import com.uriel.musicjam.features.home.presentation.components.HomeHeader
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        HomeHeader(profile = uiState.profile)
                        Spacer(Modifier.height(24.dp))
                    }
                    item {
                        Text(
                            text = "Tus álbumes",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        AlbumsRow(albums = uiState.albums)
                        Spacer(Modifier.height(24.dp))
                    }
                    item {
                        Text(
                            text = "Lo que más escuchas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    items(uiState.topTracks, key = { it.id }) { track ->
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
