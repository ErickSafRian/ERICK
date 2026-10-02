package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MissionEntity
import com.example.ui.components.DailyGiftWheelDialog
import com.example.ui.theme.DindongColorScheme

@Composable
fun MissionsScreen(
    missions: List<MissionEntity>,
    themeColors: DindongColorScheme,
    onClaimMission: (MissionEntity) -> Unit,
    onOpenGiftWheel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Lucky Wheel / Gift CTA Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFF2A85))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "🎁 Roda Hadiah Harian",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFF2A85)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "Putar roda gratis setiap hari untuk menangkan hingga 200 koin!",
                                fontSize = 10.sp,
                                color = themeColors.textMuted
                            )
                        }

                        Button(
                            onClick = onOpenGiftWheel,
                            modifier = Modifier.testTag("open_wheel_btn_screen"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A85)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Buka Roda", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "🏆 Daftar Misi & Tantangan:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.accent
                )
                Spacer(modifier = Modifier.height(8.dp))

                missions.forEach { mission ->
                    val isComplete = mission.currentProgress >= mission.targetProgress
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (mission.isClaimed) Color(0xFF334155) else if (isComplete) Color(0xFF00E676) else themeColors.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = mission.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = mission.description,
                                        fontSize = 10.sp,
                                        color = themeColors.textMuted
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                if (mission.isClaimed) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFF1E293B))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Selesai ✓", fontSize = 9.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Button(
                                        onClick = { onClaimMission(mission) },
                                        enabled = isComplete,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isComplete) Color(0xFF00E676) else Color(0xFF334155)
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "+${mission.rewardCoins} 🪙",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isComplete) Color.Black else Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Progress Bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LinearProgressIndicator(
                                    progress = { (mission.currentProgress.toFloat() / mission.targetProgress).coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (isComplete) Color(0xFF00E676) else themeColors.primary,
                                    trackColor = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${mission.currentProgress}/${mission.targetProgress}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
