package com.example.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DingdongSymbol
import com.example.model.DingdongSymbols
import com.example.ui.theme.DindongColorScheme
import com.example.viewmodel.GameUiState

@Composable
fun BettingControls(
    uiState: GameUiState,
    themeColors: DindongColorScheme,
    onSelectChip: (Int) -> Unit,
    onPlaceBet: (String) -> Unit,
    onDoubleAllBets: () -> Unit,
    onMaxBet: () -> Unit,
    onClearBets: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Chip Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🪙 Koin:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textMuted,
                    modifier = Modifier.padding(end = 4.dp)
                )

                val chipValues = listOf(1, 5, 10, 25, 50)
                chipValues.forEach { chip ->
                    val isSelected = uiState.selectedChip == chip
                    val chipBg = when (chip) {
                        1 -> Color(0xFF1E88E5)
                        5 -> Color(0xFFE53935)
                        10 -> Color(0xFF43A047)
                        25 -> Color(0xFF8E24AA)
                        else -> Color(0xFFFB8C00)
                    }

                    Box(
                        modifier = Modifier
                            .testTag("chip_button_$chip")
                            .padding(horizontal = 3.dp)
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(chipBg)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                            .clickable { onSelectChip(chip) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$chip",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Quick Bet Action Chips: 2x, Max, Clear
                Box(
                    modifier = Modifier
                        .testTag("double_bet_all_button")
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF312E81))
                        .border(1.dp, Color(0xFF6366F1), RoundedCornerShape(12.dp))
                        .clickable { onDoubleAllBets() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("2x Bet", color = Color(0xFFA5B4FC), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .testTag("max_bet_button")
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF831843))
                        .border(1.dp, Color(0xFFF43F5E), RoundedCornerShape(12.dp))
                        .clickable { onMaxBet() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text("MAX", color = Color(0xFFFECDD3), fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Symbols Bet Grid: Tap to bet on individual symbols!
            val symbols = DingdongSymbols.ALL_SYMBOLS
            // Top Row (5 items)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                symbols.take(5).forEach { sym ->
                    SymbolBetButton(
                        symbol = sym,
                        betAmount = uiState.bets[sym.id] ?: 0,
                        onBetClick = { onPlaceBet(sym.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Bottom Row (4 items)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                symbols.drop(5).forEach { sym ->
                    SymbolBetButton(
                        symbol = sym,
                        betAmount = uiState.bets[sym.id] ?: 0,
                        onBetClick = { onPlaceBet(sym.id) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SymbolBetButton(
    symbol: DingdongSymbol,
    betAmount: Int,
    onBetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasBet = betAmount > 0
    Box(
        modifier = modifier
            .testTag("bet_btn_${symbol.id}")
            .clip(RoundedCornerShape(10.dp))
            .background(if (hasBet) Color(symbol.colorHex).copy(alpha = 0.25f) else Color(0xFF1E1738))
            .border(
                width = if (hasBet) 1.5.dp else 1.dp,
                color = if (hasBet) Color(symbol.colorHex) else Color(0xFF372E5A),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onBetClick() }
            .padding(vertical = 5.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = symbol.emoji, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = if (symbol.isBonus) "JP" else "x${symbol.multiplier}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    color = Color(symbol.colorHex)
                )
            }

            if (hasBet) {
                Text(
                    text = "🪙 $betAmount",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700)
                )
            } else {
                Text(
                    text = "Pasang",
                    fontSize = 7.5.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
