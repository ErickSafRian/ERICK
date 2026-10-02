package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameTheme
import com.example.ui.theme.DindongColorScheme
import com.example.viewmodel.GameUiState

@Composable
fun DindongHeader(
    uiState: GameUiState,
    themeColors: DindongColorScheme,
    onSelectTheme: (GameTheme) -> Unit,
    onToggleSound: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenGiftWheel: () -> Unit,
    onOpenLounge: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Theme selector row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Text(
                    text = "🎨 Tema:",
                    color = themeColors.textMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            GameTheme.values().forEach { theme ->
                val isSelected = uiState.selectedTheme == theme
                Box(
                    modifier = Modifier
                        .testTag("theme_btn_${theme.name.lowercase()}")
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) themeColors.primary.copy(alpha = 0.25f)
                            else themeColors.surface
                        )
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) themeColors.primary else themeColors.surfaceVariant,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onSelectTheme(theme) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = theme.displayName,
                        color = if (isSelected) themeColors.primary else themeColors.onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Actions & Status Row: Trophy, Gift, Sound Toggle, Casino Lounge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trophy / Missions
            Box(
                modifier = Modifier
                    .testTag("trophy_button")
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2E240D))
                    .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(16.dp))
                    .clickable { onOpenMissions() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🏆", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "1/5",
                        color = Color(0xFFFFD700),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            // Gift Button
            Box(
                modifier = Modifier
                    .testTag("gift_button")
                    .clip(CircleShape)
                    .background(Color(0xFF38102A))
                    .border(1.dp, Color(0xFFFF4081), CircleShape)
                    .clickable { onOpenGiftWheel() }
                    .padding(8.dp)
            ) {
                Text("🎁", fontSize = 14.sp)
            }

            // Sound Toggle (⏸ 🎵 ON)
            Box(
                modifier = Modifier
                    .testTag("sound_toggle_button")
                    .clip(RoundedCornerShape(16.dp))
                    .background(themeColors.surface)
                    .border(1.dp, themeColors.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onToggleSound() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        if (uiState.isSoundOn) "▶ 🎵 ON" else "⏸ 🎵 OFF",
                        color = if (uiState.isSoundOn) themeColors.primary else themeColors.textMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            // Casino Lounge Dropdown
            Box(
                modifier = Modifier
                    .testTag("casino_lounge_button")
                    .clip(RoundedCornerShape(16.dp))
                    .background(themeColors.surface)
                    .border(1.dp, themeColors.accent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable { onOpenLounge() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("✨ Casino Lounge ∨", color = themeColors.accent, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Title: ⚡ DINDONG PISANG FANTASI ⚡
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.scale(pulseScale)
        ) {
            Text(
                text = "⚡",
                fontSize = 24.sp,
                color = themeColors.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "DINDONG",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp,
                    color = themeColors.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "PISANG FANTASI",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 3.sp,
                    color = themeColors.primary,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "⚡",
                fontSize = 24.sp,
                color = themeColors.primary
            )
        }

        Text(
            text = "Pasang taruhanmu pada Pisang Legendaris & Putar Mesin Dindong!",
            color = themeColors.primary.copy(alpha = 0.85f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        // HUD / Status Cards: TOTAL KOIN, TOTAL TARUHAN, MENANG TERAKHIR, HOUSE EDGE / RTP
        val totalBet = uiState.bets.values.sum()
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, themeColors.surfaceVariant, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = themeColors.hudBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                HudItem(
                    label = "TOTAL KOIN",
                    value = "${uiState.wallet.coins}",
                    valueColor = themeColors.primary,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(themeColors.surfaceVariant)
                )

                HudItem(
                    label = "TOTAL\nTARUHAN",
                    value = "$totalBet",
                    valueColor = themeColors.primary,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(themeColors.surfaceVariant)
                )

                HudItem(
                    label = "MENANG\nTERAKHIR",
                    value = "${uiState.lastWin}",
                    valueColor = if (uiState.lastWin > 0) Color(0xFFFFD700) else themeColors.primary,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(themeColors.surfaceVariant)
                )

                // RTP
                Column(
                    modifier = Modifier.weight(1.1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "HOUSE EDGE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.secondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RTP\n${uiState.wallet.rtpPercent}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = themeColors.secondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("⚡", fontSize = 12.sp, color = themeColors.accent)
                    }
                }
            }
        }
    }
}

@Composable
private fun HudItem(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            lineHeight = 11.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            color = valueColor,
            textAlign = TextAlign.Center
        )
    }
}
