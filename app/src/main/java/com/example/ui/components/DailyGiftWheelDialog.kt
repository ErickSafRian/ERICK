package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import kotlin.random.Random

@Composable
fun DailyGiftWheelDialog(
    isOpen: Boolean,
    themeColors: DindongColorScheme,
    onClaimGift: (Int) -> Unit,
    onClose: () -> Unit
) {
    if (!isOpen) return

    var wheelRotation by remember { mutableFloatStateOf(0f) }
    var isSpinningWheel by remember { mutableStateOf(false) }
    var wonPrize by remember { mutableStateOf<Int?>(null) }
    var hasClaimedDaily by remember { mutableStateOf(false) }

    val animatedRotation by animateFloatAsState(
        targetValue = wheelRotation,
        animationSpec = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
        finishedListener = {
            isSpinningWheel = false
        },
        label = "wheelAnim"
    )

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFFF2A85), RoundedCornerShape(24.dp)),
            color = themeColors.background
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
                        Text("🎁", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KADO & RODA HOKI",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFFF2A85)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .testTag("close_gift_wheel_button")
                            .clip(CircleShape)
                            .background(themeColors.surfaceVariant)
                            .clickable { onClose() }
                            .padding(6.dp)
                    ) {
                        Text("✕", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Card 1: Daily Gift Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Kado Harian Gratis", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Dapatkan 50 koin gratis setiap hari!", fontSize = 9.sp, color = themeColors.textMuted)
                        }

                        Button(
                            onClick = {
                                hasClaimedDaily = true
                                onClaimGift(50)
                            },
                            enabled = !hasClaimedDaily,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                if (hasClaimedDaily) "Diklaim ✓" else "Klaim 50 🪙",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Card 2: Lucky Wheel of Fortune
                Text(
                    text = "🎡 Roda Keberuntungan Pisang",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Wheel Visual
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFFFFD700), CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color(0xFFFF0055),
                                    Color(0xFF00E5FF),
                                    Color(0xFFFFEA00),
                                    Color(0xFF7C4DFF),
                                    Color(0xFF00E676),
                                    Color(0xFFFF6D00),
                                    Color(0xFFFF0055)
                                )
                            )
                        )
                        .rotate(animatedRotation),
                    contentAlignment = Alignment.Center
                ) {
                    // Center Hub
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍌", fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                wonPrize?.let { prize ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF065F46))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎉 Selamat! Mendapatkan +$prize Koin!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA7F3D0)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (!isSpinningWheel) {
                            isSpinningWheel = true
                            val prizes = listOf(15, 25, 50, 75, 100, 200)
                            val prize = prizes.random()
                            val extraSpins = 360f * 4 + Random.nextInt(360)
                            wheelRotation += extraSpins
                            wonPrize = prize
                            onClaimGift(prize)
                        }
                    },
                    enabled = !isSpinningWheel,
                    modifier = Modifier
                        .testTag("spin_lucky_wheel_button")
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A85)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        if (isSpinningWheel) "Memutar Roda..." else "Putar Roda Gratis!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }
    }
}
