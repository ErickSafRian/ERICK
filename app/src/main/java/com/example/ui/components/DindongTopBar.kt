package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameTheme
import com.example.ui.theme.DindongColorScheme

@Composable
fun DindongTopBar(
    coins: Long,
    selectedTheme: GameTheme,
    isSoundOn: Boolean,
    themeColors: DindongColorScheme,
    onOpenTopUp: () -> Unit,
    onToggleSound: () -> Unit,
    onCycleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "coinPulse")
    val coinGlow by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coinGlow"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = themeColors.hudBackground.copy(alpha = 0.95f),
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Identity & Brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onCycleTheme() }
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(Color(0xFFFFD700), Color(0xFF00F5D4), Color(0xFFFF2A85))
                            )
                        )
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(themeColors.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍌", fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "DINDONG PISANG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = themeColors.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tema: ${selectedTheme.displayName}",
                        fontSize = 9.sp,
                        color = themeColors.accent
                    )
                }
            }

            // Right Action Controls: Coin Pill (+ Top Up button) and Sound Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Coin Pill with Glow
                Box(
                    modifier = Modifier
                        .testTag("top_bar_coin_pill")
                        .scale(coinGlow)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1E293B))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFFFFD700), Color(0xFF00E676))
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onOpenTopUp() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$coins",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFFFD700)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "+",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }

                // Sound Toggle Button
                Box(
                    modifier = Modifier
                        .testTag("top_bar_sound_button")
                        .clip(CircleShape)
                        .background(themeColors.surface)
                        .border(1.dp, themeColors.surfaceVariant, CircleShape)
                        .clickable { onToggleSound() }
                        .padding(7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (isSoundOn) "🔊" else "🔇", fontSize = 12.sp)
                }
            }
        }
    }
}
