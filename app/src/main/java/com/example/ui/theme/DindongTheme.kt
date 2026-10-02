package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.model.GameTheme

data class DindongColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val onBackground: Color,
    val onSurface: Color,
    val perimeterBorder: Color,
    val activeTileGlow: Color,
    val hudBackground: Color,
    val textMuted: Color
)

val CyberColors = DindongColorScheme(
    background = Color(0xFF0D0826),
    surface = Color(0xFF160E3B),
    surfaceVariant = Color(0xFF221652),
    primary = Color(0xFF00F5D4),
    secondary = Color(0xFFFF2A85),
    accent = Color(0xFFFFE600),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFE2E8F0),
    perimeterBorder = Color(0xFFFACC15),
    activeTileGlow = Color(0xFF00F5D4),
    hudBackground = Color(0xFF0B0720),
    textMuted = Color(0xFF94A3B8)
)

val Retro80sColors = DindongColorScheme(
    background = Color(0xFF150426),
    surface = Color(0xFF270845),
    surfaceVariant = Color(0xFF3B0D66),
    primary = Color(0xFFFF007F),
    secondary = Color(0xFF00F0FF),
    accent = Color(0xFFFFB800),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFF3E8FF),
    perimeterBorder = Color(0xFFFF007F),
    activeTileGlow = Color(0xFFFF007F),
    hudBackground = Color(0xFF0F021B),
    textMuted = Color(0xFFA855F7)
)

val JungleColors = DindongColorScheme(
    background = Color(0xFF051C10),
    surface = Color(0xFF0E3821),
    surfaceVariant = Color(0xFF165231),
    primary = Color(0xFF00E676),
    secondary = Color(0xFFFFD600),
    accent = Color(0xFFFF9100),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFE8F5E9),
    perimeterBorder = Color(0xFFFFD600),
    activeTileGlow = Color(0xFF00E676),
    hudBackground = Color(0xFF03140B),
    textMuted = Color(0xFF81C784)
)

val GoldColors = DindongColorScheme(
    background = Color(0xFF12100E),
    surface = Color(0xFF24201A),
    surfaceVariant = Color(0xFF383228),
    primary = Color(0xFFFFD700),
    secondary = Color(0xFFFF5252),
    accent = Color(0xFFFFE082),
    onBackground = Color(0xFFFFF9E6),
    onSurface = Color(0xFFFFECB3),
    perimeterBorder = Color(0xFFFFD700),
    activeTileGlow = Color(0xFFFFD700),
    hudBackground = Color(0xFF0A0908),
    textMuted = Color(0xFFBCAAA4)
)

fun getDindongThemeColors(theme: GameTheme): DindongColorScheme {
    return when (theme) {
        GameTheme.CYBER -> CyberColors
        GameTheme.RETRO_80S -> Retro80sColors
        GameTheme.JUNGLE -> JungleColors
        GameTheme.GOLD -> GoldColors
    }
}
