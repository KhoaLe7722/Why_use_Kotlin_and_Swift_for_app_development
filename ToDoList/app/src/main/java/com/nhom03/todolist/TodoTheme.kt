package com.nhom03.todolist

import androidx.compose.foundation.Canvas
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size

internal val TodoColors = lightColorScheme(
    primary = Color(0xFF5541A0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFECE5FF),
    onPrimaryContainer = Color(0xFF34236F),
    background = Color(0xFFF8F7FC),
    onBackground = Color(0xFF211A32),
    surface = Color.White,
    onSurface = Color(0xFF211A32),
    surfaceVariant = Color(0xFFF0EDF6),
    onSurfaceVariant = Color(0xFF5E576E),
    outline = Color(0xFF777084),
    outlineVariant = Color(0xFFE3DDED),
    error = Color(0xFFA62B49),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TodoTopBar(title: String, onBack: () -> Unit) {
    val color = MaterialTheme.colorScheme.onBackground
    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onBack, modifier = Modifier.semantics {
                contentDescription = "Quay lại"
            }) {
                Canvas(Modifier.size(24.dp)) {
                    val start = Offset(size.width * .24f, center.y)
                    val width = 2.dp.toPx()
                    drawLine(color, start, Offset(size.width * .78f, center.y), width, StrokeCap.Round)
                    drawLine(color, start, Offset(center.x, size.height * .24f), width, StrokeCap.Round)
                    drawLine(color, start, Offset(center.x, size.height * .76f), width, StrokeCap.Round)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
    )
}
