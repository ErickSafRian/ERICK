package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.DingdongSymbols
import com.example.ui.theme.DindongColorScheme

@Composable
fun LoungeScreen(
    currentRtp: Int,
    themeColors: DindongColorScheme,
    onSetRtp: (Int) -> Unit,
    onTestJackpot: () -> Unit = {},
    onTestDoubleUp: () -> Unit = {},
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
                // Hero Banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .border(1.5.dp, themeColors.primary, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.dindong_arcade_banner_1790977751916),
                            contentDescription = "Arcade Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                    )
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            Column {
                                Text(
                                    text = "✨ CASINO LOUNGE & VIP",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color(0xFFFFD700)
                                )
                                Text(
                                    text = "Atur persentase RTP dan pelajari paytable kelipatan koin!",
                                    fontSize = 10.sp,
                                    color = Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Showcase & Test Overlay Animations
                Text(
                    text = "✨ Uji Animasi Layar Perayaan:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .testTag("test_jackpot_button")
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFFFF9100), Color(0xFFFF1744))
                                )
                            )
                            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(12.dp))
                            .clickable { onTestJackpot() }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "💥 Tes Jackpot",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .testTag("test_double_up_button")
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF7C3AED), Color(0xFF00E5FF))
                                )
                            )
                            .border(1.dp, Color(0xFF00F5D4), RoundedCornerShape(12.dp))
                            .clickable { onTestDoubleUp() }
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎲 Tes Double Up",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // RTP Selector
                Text(
                    text = "Mode RTP Mesin (Return to Player):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rtpModes = listOf(
                        Pair(75, "Normal Arcade 75% ⚡"),
                        Pair(96, "Mode Gacor VIP 96% 🔥")
                    )
                    rtpModes.forEach { (rtpVal, label) ->
                        val isSelected = currentRtp == rtpVal
                        Box(
                            modifier = Modifier
                                .testTag("rtp_mode_$rtpVal")
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) themeColors.accent else themeColors.surface)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color.White else themeColors.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSetRtp(rtpVal) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Paytable
                Text(
                    text = "Tabel Kelipatan Simbol (Paytable):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                DingdongSymbols.ALL_SYMBOLS.forEach { sym ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(themeColors.surface)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(sym.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(sym.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Text(
                            text = if (sym.isBonus) "JACKPOT & 3 FRENZY" else "x${sym.multiplier}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(sym.colorHex)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Rules
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Panduan Bermain:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "• Pilih pecahan koin (1, 5, 10, 25, 50) dan pasang di simbol favoritmu.\n" +
                            "• Tekan 'PUTAR MESIN' untuk memutar lampu di trek 20 slot.\n" +
                            "• Jika lampu berhenti di simbol pilihanmu, hadiah langsung masuk!\n" +
                            "• Fitur Guncang Mesin: Tekan tombol atau goyang ponsel untuk efek hoki!\n" +
                            "• Menang? Mainkan 'Double Up (2x)' tebak kartu merah/hitam!",
                            fontSize = 10.sp,
                            color = themeColors.onSurface,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
