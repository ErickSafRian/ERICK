package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ArcadeDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00F5D4),
    secondary = Color(0xFFFF2A85),
    tertiary = Color(0xFFFFE600),
    background = Color(0xFF0D0826),
    surface = Color(0xFF160E3B),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color(0xFFE2E8F0)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ArcadeDarkColorScheme,
        typography = Typography,
        content = content
    )
}
