package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.DindongColorScheme

@Composable
fun DoubleUpDialog(
    isOpen: Boolean,
    currentPool: Long,
    lastCard: Int?,
    cardIsRed: Boolean?,
    resultSuccess: Boolean?,
    themeColors: DindongColorScheme,
    onGuess: (Boolean) -> Unit, // true = Red, false = Black
    onCashOut: () -> Unit,
    onClose: () -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFA855F7), RoundedCornerShape(24.dp)),
            color = Color(0xFF130924)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎲", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DOUBLE UP (2X)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE9D5FF)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .testTag("close_double_up_button")
                            .clip(CircleShape)
                            .background(Color(0xFF2E1065))
                            .clickable { onClose() }
                            .padding(6.dp)
                    ) {
                        Text("✕", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Current Gamble Pool
                Text("Taruhan Saat Ini:", fontSize = 10.sp, color = Color(0xFFA855F7))
                Text(
                    text = "🪙 $currentPool Koin",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "Tebak warna kartu: Jika benar, koin digandakan jadi 🪙 ${currentPool * 2}!",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFC084FC)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // The Playing Card Visual
                Card(
                    modifier = Modifier
                        .size(width = 110.dp, height = 150.dp)
                        .border(2.dp, Color(0xFFA855F7), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = if (lastCard != null) Color.White else Color(0xFF2E1065)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (lastCard != null && cardIsRed != null) {
                            val cardColor = if (cardIsRed) Color(0xFFDC2626) else Color(0xFF1E293B)
                            val suit = if (cardIsRed) "♥️" else "♠️"
                            val cardRank = when (lastCard) {
                                1 -> "A"
                                11 -> "J"
                                12 -> "Q"
                                13 -> "K"
                                else -> "$lastCard"
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(suit, fontSize = 28.sp)
                                Text(
                                    text = cardRank,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = cardColor
                                )
                                Text(
                                    text = if (cardIsRed) "MERAH" else "HITAM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = cardColor
                                )
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("❓", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "TEBAK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFC084FC),
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Result feedback banner
                resultSuccess?.let { success ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (success) Color(0xFF065F46) else Color(0xFF7F1D1D))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (success) "🎉 BENAR! Kemenangan digandakan!" else "❌ SALAH! Koin taruhan hangus!",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Guess Buttons (RED vs BLACK)
                if (currentPool > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Guess RED
                        Button(
                            onClick = { onGuess(true) },
                            modifier = Modifier
                                .testTag("guess_red_button")
                                .weight(1f)
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("♥️ MERAH", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }

                        // Guess BLACK
                        Button(
                            onClick = { onGuess(false) },
                            modifier = Modifier
                                .testTag("guess_black_button")
                                .weight(1f)
                                .height(46.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF64748B)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("♠️ HITAM", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cash out button
                    Button(
                        onClick = onCashOut,
                        modifier = Modifier
                            .testTag("cash_out_double_up_button")
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Ambil Koin (Simpan $currentPool Koin)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = onClose,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Tutup", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
