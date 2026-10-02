package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.model.DingdongSymbols
import com.example.ui.theme.DindongColorScheme

@Composable
fun LoungeDialog(
    isOpen: Boolean,
    currentRtp: Int,
    themeColors: DindongColorScheme,
    onSetRtp: (Int) -> Unit,
    onTestJackpot: () -> Unit = {},
    onTestDoubleUp: () -> Unit = {},
    onClose: () -> Unit
) {
    if (!isOpen) return

    Dialog(onDismissRequest = onClose) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, themeColors.accent, RoundedCornerShape(24.dp)),
            color = themeColors.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CASINO LOUNGE",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = themeColors.accent
                        )
                    }

                    Box(
                        modifier = Modifier
                            .testTag("close_lounge_button")
                            .clip(CircleShape)
                            .background(themeColors.surfaceVariant)
                            .clickable { onClose() }
                            .padding(6.dp)
                    ) {
                        Text("✕", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Test Overlay Animations
                Text("✨ Uji Animasi Layar Perayaan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeColors.primary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .testTag("dialog_test_jackpot_button")
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFDC2626))
                            .border(1.dp, Color(0xFFFFD700), RoundedCornerShape(10.dp))
                            .clickable {
                                onTestJackpot()
                                onClose()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💥 Tes Jackpot", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .testTag("dialog_test_double_up_button")
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF7C3AED))
                            .border(1.dp, Color(0xFF00F5D4), RoundedCornerShape(10.dp))
                            .clickable {
                                onTestDoubleUp()
                                onClose()
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎲 Tes Double Up", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Setting RTP (Return to Player)
                Text("Pengaturan Mode RTP Mesin:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = themeColors.primary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rtpOptions = listOf(
                        Pair(75, "Normal 75% ⚡"),
                        Pair(96, "Gacor VIP 96% 🔥")
                    )
                    rtpOptions.forEach { (rtp, label) ->
                        val isSelected = currentRtp == rtp
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) themeColors.accent else themeColors.surface)
                                .border(1.dp, if (isSelected) Color.White else themeColors.surfaceVariant, RoundedCornerShape(10.dp))
                                .clickable { onSetRtp(rtp) }
                                .padding(vertical = 8.dp),
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
                Text("Tabel Kelipatan Kemenangan (Paytable):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = themeColors.primary)
                Spacer(modifier = Modifier.height(6.dp))

                val symbols = DingdongSymbols.ALL_SYMBOLS
                symbols.forEach { sym ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(themeColors.surface)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(sym.emoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(sym.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Text(
                            text = if (sym.isBonus) "JACKPOT & 3 FRENZY" else "x${sym.multiplier}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(sym.colorHex)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cara Bermain
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Cara Bermain Dindong Pisang:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeColors.accent)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "1. Pilih pecahan koin (1, 5, 10, 25, 50).\n" +
                            "2. Ketuk simbol buah/fantasi pada baris taruhan.\n" +
                            "3. Tekan 'PUTAR MESIN' dan saksikan lampu berlari!\n" +
                            "4. Jika lampu berhenti di simbol yang kamu pasang, kamu menang sesuai kelipatan!\n" +
                            "5. Menang? Gandakan hasilmu dengan 'Double Up (2x)'.\n" +
                            "6. Butuh koin? Gunakan fitur Top Up QRIS / DANA Instan!",
                            fontSize = 9.5.sp,
                            color = themeColors.onSurface,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
