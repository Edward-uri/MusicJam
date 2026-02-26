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
                HomeHeader(profile = uiState.profile)
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
