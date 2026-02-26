package com.uriel.musicjam.features.search.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.uriel.musicjam.core.navegation.AppScreens
import com.uriel.musicjam.features.home.presentation.components.HomeBottomBar
import com.uriel.musicjam.features.home.presentation.components.TrackItem
import com.uriel.musicjam.features.search.presentation.viewmodels.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(uiState.queueMessage) {
        uiState.queueMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearQueueMessage()
        }
    }

    Scaffold(
        containerColor = Color(0xFFFDF5E6),
        bottomBar = {
            HomeBottomBar(
                currentRoute = AppScreens.Search.route,
                onNavigate = { route ->
                    if (route != AppScreens.Search.route) {
                        navController.navigate(route) {
                            popUpTo(AppScreens.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // Solo aplicamos el padding superior al contenedor general
                .padding(top = paddingValues.calculateTopPadding())
                .padding(horizontal = 16.dp)
        ) {
            // Título
            Text(
                text = "Buscar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Input de búsqueda
            OutlinedTextField(
                // ... (tu configuración actual del OutlinedTextField se mantiene igual)
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    viewModel.onQueryChanged(query)
                    if (query.length > 2 && !uiState.isSearching) {
                        viewModel.searchTracks()
                    }
                },
                placeholder = { Text("Buscar canciones...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.onQueryChanged("")
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.5f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contenedor principal de resultados
            Box(modifier = Modifier.weight(1f)) { // Usamos weight(1f) para que ocupe el resto
                when {
                    uiState.isSearching -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    searchQuery.isEmpty() -> {
                        SearchPlaceholder("Busca tu música favorita", "Artistas, canciones o álbumes", Icons.Default.Search)
                    }
                    uiState.errorMessage != null -> {
                        SearchPlaceholder("Ocurrió un error", uiState.errorMessage!!, Icons.Default.SearchOff)
                    }
                    uiState.searchResults.isEmpty() -> {
                        SearchPlaceholder("Sin resultados", "No encontramos nada para \"$searchQuery\"", Icons.Default.SearchOff)
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(
                                bottom = paddingValues.calculateBottomPadding() + 16.dp
                            )
                        ) {
                            items(uiState.searchResults, key = { it.id }) { track ->
                                TrackItem(
                                    track = track,
                                    modifier = Modifier.fillMaxWidth(),
                                    // Comprobamos si el ID de esta canción está en nuestra lista de encoladas
                                    isQueued = track.id in uiState.queuedTrackIds,
                                    onAddToQueue = { viewModel.addTrackToQueue(track) },
                                    onClick = { /* Navegar al reproductor */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun SearchPlaceholder(title: String, subtitle: String, icon: ImageVector) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}