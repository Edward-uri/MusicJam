package com.uriel.musicjam.features.player.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerProgressBar(progressMs: Long, durationMs: Long) {
    val progress = if (durationMs > 0) progressMs.toFloat() / durationMs.toFloat() else 0f

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
        color = Color(0xFF1DB954),
        trackColor = Color.White.copy(alpha = 0.2f)
    )
    Spacer(Modifier.height(8.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(formatMs(progressMs), color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
        Text(formatMs(durationMs), color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp)
    }
}

fun formatMs(ms: Long): String {
    val totalSeconds = ms / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
