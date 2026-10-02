package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DingdongSymbol
import com.example.model.DingdongSymbols
import com.example.ui.theme.DindongColorScheme
import com.example.viewmodel.GameUiState
import kotlin.math.roundToInt

@Composable
fun PerimeterMachineBoard(
    uiState: GameUiState,
    themeColors: DindongColorScheme,
    onPlaceBet: (String) -> Unit,
    onShakeMachine: () -> Unit,
    onClearBets: () -> Unit,
    onOpenDoubleUp: () -> Unit,
    onOpenTopUp: () -> Unit,
    onSpinWheel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "boardPulse")
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Shake animation
    val shakeOffset by animateFloatAsState(
        targetValue = if (uiState.isShaking) 8f else 0f,
        animationSpec = tween(80),
        label = "shake"
    )

    val currentSelectedSymbol = DingdongSymbols.PERIMETER_SLOTS.getOrNull(uiState.currentTileIndex)
        ?: DingdongSymbols.MECHA

    Box(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 620.dp)
            .offset { IntOffset(x = if (uiState.isShaking) (shakeOffset * (if (System.currentTimeMillis() % 2 == 0L) 1 else -1)).roundToInt() else 0, y = 0) }
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .border(
                width = 3.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        themeColors.perimeterBorder,
                        themeColors.perimeterBorder.copy(alpha = 0.8f),
                        themeColors.perimeterBorder
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
            .background(themeColors.surface)
            .padding(6.dp)
    ) {
        // Decorative Neon Corner LEDs
        NeonCornerLed(Modifier.align(Alignment.TopStart), themeColors.primary)
        NeonCornerLed(Modifier.align(Alignment.TopEnd), themeColors.secondary)
        NeonCornerLed(Modifier.align(Alignment.BottomStart), themeColors.accent)
        NeonCornerLed(Modifier.align(Alignment.BottomEnd), themeColors.primary)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP ROW: 6 slots (0 to 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0..5) {
                    val symbol = DingdongSymbols.PERIMETER_SLOTS[i]
                    val isLit = uiState.currentTileIndex == i
                    val betAmount = uiState.bets[symbol.id] ?: 0
                    PerimeterSlotTile(
                        slotIndex = i,
                        symbol = symbol,
                        isLit = isLit,
                        betAmount = betAmount,
                        themeColors = themeColors,
                        glowIntensity = glowAnim,
                        onTileClick = { onPlaceBet(symbol.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // MIDDLE SECTION: Left column (4 slots) + Center Console + Right column (4 slots)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT COLUMN (Indices 19 down to 16)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val leftIndices = listOf(19, 18, 17, 16)
                    leftIndices.forEach { idx ->
                        val symbol = DingdongSymbols.PERIMETER_SLOTS[idx]
                        val isLit = uiState.currentTileIndex == idx
                        val betAmount = uiState.bets[symbol.id] ?: 0
                        PerimeterSlotTile(
                            slotIndex = idx,
                            symbol = symbol,
                            isLit = isLit,
                            betAmount = betAmount,
                            themeColors = themeColors,
                            glowIntensity = glowAnim,
                            onTileClick = { onPlaceBet(symbol.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // CENTER CONSOLE (Spans between left and right columns: weight = 4f)
                Box(
                    modifier = Modifier
                        .weight(4f)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CenterMachineConsole(
                        uiState = uiState,
                        currentSymbol = currentSelectedSymbol,
                        themeColors = themeColors,
                        onShakeMachine = onShakeMachine,
                        onClearBets = onClearBets,
                        onOpenDoubleUp = onOpenDoubleUp,
                        onOpenTopUp = onOpenTopUp,
                        onSpinWheel = onSpinWheel
                    )
                }

                // RIGHT COLUMN (Indices 6 to 9)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val rightIndices = listOf(6, 7, 8, 9)
                    rightIndices.forEach { idx ->
                        val symbol = DingdongSymbols.PERIMETER_SLOTS[idx]
                        val isLit = uiState.currentTileIndex == idx
                        val betAmount = uiState.bets[symbol.id] ?: 0
                        PerimeterSlotTile(
                            slotIndex = idx,
                            symbol = symbol,
                            isLit = isLit,
                            betAmount = betAmount,
                            themeColors = themeColors,
                            glowIntensity = glowAnim,
                            onTileClick = { onPlaceBet(symbol.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // BOTTOM ROW: 6 slots (Indices 15 down to 10 from left to right)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val bottomIndices = listOf(15, 14, 13, 12, 11, 10)
                bottomIndices.forEach { idx ->
                    val symbol = DingdongSymbols.PERIMETER_SLOTS[idx]
                    val isLit = uiState.currentTileIndex == idx
                    val betAmount = uiState.bets[symbol.id] ?: 0
                    PerimeterSlotTile(
                        slotIndex = idx,
                        symbol = symbol,
                        isLit = isLit,
                        betAmount = betAmount,
                        themeColors = themeColors,
                        glowIntensity = glowAnim,
                        onTileClick = { onPlaceBet(symbol.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NeonCornerLed(modifier: Modifier, ledColor: Color) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .size(6.dp)
            .clip(CircleShape)
            .background(ledColor)
    )
}

@Composable
fun PerimeterSlotTile(
    slotIndex: Int,
    symbol: DingdongSymbol,
    isLit: Boolean,
    betAmount: Int,
    themeColors: DindongColorScheme,
    glowIntensity: Float,
    onTileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val litBackgroundColor by animateColorAsState(
        targetValue = if (isLit) {
            Color(symbol.colorHex).copy(alpha = 0.4f)
        } else {
            themeColors.surfaceVariant.copy(alpha = 0.6f)
        },
        animationSpec = tween(60),
        label = "tileColor"
    )

    val borderColor = if (isLit) {
        Color(symbol.colorHex)
    } else {
        themeColors.surfaceVariant
    }

    Box(
        modifier = modifier
            .testTag("slot_tile_$slotIndex")
            .clip(RoundedCornerShape(10.dp))
            .background(litBackgroundColor)
            .border(
                width = if (isLit) 2.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onTileClick() }
            .padding(vertical = 4.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emoji / Icon
            Text(
                text = symbol.emoji,
                fontSize = if (symbol.isBonus) 14.sp else 16.sp,
                modifier = Modifier.scale(if (isLit) 1.15f else 1f)
            )

            // Multiplier or BONUS text
            Text(
                text = if (symbol.isBonus) "BONUS" else "x${symbol.multiplier}",
                fontSize = if (symbol.isBonus) 8.sp else 9.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = if (symbol.isBonus) Color(0xFFFF1744) else Color(symbol.colorHex),
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }

        // Active Bet indicator badge
        if (betAmount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .clip(CircleShape)
                    .background(themeColors.accent)
                    .padding(horizontal = 3.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "$betAmount",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun CenterMachineConsole(
    uiState: GameUiState,
    currentSymbol: DingdongSymbol,
    themeColors: DindongColorScheme,
    onShakeMachine: () -> Unit,
    onClearBets: () -> Unit,
    onOpenDoubleUp: () -> Unit,
    onOpenTopUp: () -> Unit,
    onSpinWheel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Speech Bubble & Top Up Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Speech Bubble
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F2624))
                    .border(1.dp, Color(0xFF00F5D4), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = uiState.mascotQuote,
                    color = Color(0xFF00F5D4),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // + Top Up Button
            Box(
                modifier = Modifier
                    .testTag("top_up_button")
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF00E676), Color(0xFF00B0FF))
                        )
                    )
                    .border(1.5.dp, Color.White, RoundedCornerShape(12.dp))
                    .clickable { onOpenTopUp() }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "+",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Top Up",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Mascot Avatar (Featuring custom rendered Pisang Mecha image!) & Symbol Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(themeColors.surfaceVariant)
                    .border(2.dp, Color(currentSymbol.colorHex), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pisang_mecha_mascot_1790977766503),
                    contentDescription = "Mascot Pisang Mecha",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = currentSymbol.name.uppercase(),
                    color = Color(currentSymbol.colorHex),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (currentSymbol.isBonus) "JACKPOT BONUS" else "MULTIPLE: x${currentSymbol.multiplier}",
                    color = themeColors.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Action Buttons: Guncang Mesin, Hapus Taruhan, Double Up (2x)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Guncang Mesin
            Box(
                modifier = Modifier
                    .testTag("shake_machine_button")
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF453011))
                    .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(10.dp))
                    .clickable { onShakeMachine() }
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👆", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Guncang Mesin",
                        color = Color(0xFFFFD54F),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Hapus Taruhan
            Box(
                modifier = Modifier
                    .testTag("clear_bet_button")
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF64748B), RoundedCornerShape(10.dp))
                    .clickable { onClearBets() }
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗑", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Hapus Taruhan",
                        color = Color(0xFFE2E8F0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Double Up (2x)
            val canDoubleUp = uiState.lastWin > 0 && !uiState.isSpinning
            Box(
                modifier = Modifier
                    .testTag("double_up_button")
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (canDoubleUp) Color(0xFF4C1D95) else Color(0xFF281C3D))
                    .border(
                        1.dp,
                        if (canDoubleUp) Color(0xFFA855F7) else Color(0xFF5B3980),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable(enabled = canDoubleUp) { onOpenDoubleUp() }
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎲", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (uiState.lastWin > 0) "Double Up (${uiState.lastWin * 2})" else "Double Up (2x)",
                        color = if (canDoubleUp) Color(0xFFE9D5FF) else Color(0xFF8B7AA8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Big Arcade PUTAR MESIN button
            val totalBet = uiState.bets.values.sum()
            val canSpin = !uiState.isSpinning && (totalBet > 0 || uiState.isBonusFrenzy)
            Box(
                modifier = Modifier
                    .testTag("spin_machine_button")
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (canSpin) {
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFF0055), Color(0xFFFF5252), Color(0xFFFF9100))
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(Color(0xFF374151), Color(0xFF1F2937))
                            )
                        }
                    )
                    .border(
                        1.5.dp,
                        if (canSpin) Color(0xFFFFE600) else Color(0xFF4B5563),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(enabled = canSpin) { onSpinWheel() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎰", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (uiState.isSpinning) "MEMUTAR..." else "PUTAR MESIN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}
