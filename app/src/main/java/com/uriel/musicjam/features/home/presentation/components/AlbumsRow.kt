package com.uriel.musicjam.features.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.uriel.musicjam.features.home.domain.entities.SpotifyAlbum

@Composable
fun AlbumsRow(
    albums: List<SpotifyAlbum>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(albums, key = { it.id }) { album ->
            AlbumItem(album = album)
        }
    }
}

@Composable
private fun AlbumItem(
    album: SpotifyAlbum,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = album.imageUrl,
        contentDescription = album.name,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(90.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}
