package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.service.payment.PaymentSimulationState
import com.example.ui.theme.DindongColorScheme

@Composable
fun PaymentSimulationModal(
    state: PaymentSimulationState,
    themeColors: DindongColorScheme,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (state is PaymentSimulationState.Idle) return

    Dialog(
        onDismissRequest = {
            if (state is PaymentSimulationState.Completed || state is PaymentSimulationState.Error) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = state is PaymentSimulationState.Completed || state is PaymentSimulationState.Error,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .clip(RoundedCornerShape(22.dp))
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                themeColors.primary,
                                themeColors.accent,
                                themeColors.secondary
                            )
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ),
                color = Color(0xFF0F172A)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Sandbox Header Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF00E676), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🛡️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MOCK PAYMENT SANDBOX GATEWAY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00E676),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (state) {
                        is PaymentSimulationState.Initiating -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(46.dp),
                                color = themeColors.primary,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Menghubungkan ke ${state.request.channel.displayName}...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Membuat invoice transaksi Rp ${state.request.amountRupiah}",
                                fontSize = 10.sp,
                                color = themeColors.textMuted
                            )
                        }

                        is PaymentSimulationState.Processing -> {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF1E293B))
                                    .border(2.dp, themeColors.accent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(36.dp),
                                    color = themeColors.accent,
                                    strokeWidth = 3.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = state.progressText,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "No. Ref: ${state.transactionCode}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = themeColors.primary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Sedang mensimulasikan handshake payment switcher & validasi saldo...",
                                fontSize = 9.sp,
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center
                            )
                        }

                        is PaymentSimulationState.Completed -> {
                            val res = state.result
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF065F46))
                                    .border(2.dp, Color(0xFF10B981), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", fontSize = 32.sp, color = Color.White, fontWeight = FontWeight.Black)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "PEMBAYARAN SIMULASI SUKSES!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF00E676),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = res.message,
                                fontSize = 11.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Transaction Receipt Card
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    ReceiptRow("Metode:", "${res.channel.iconEmoji} ${res.channel.displayName}")
                                    ReceiptRow("Kode Transaksi:", res.transactionCode)
                                    ReceiptRow("Total Rupiah:", "Rp ${res.amountRupiah}")
                                    ReceiptRow("Koin Diperoleh:", "+${res.coinsAwarded} Koin 🪙", valueColor = Color(0xFFFFD700))
                                    ReceiptRow("Status:", "BERHASIL (TERCATAT)", valueColor = Color(0xFF00E676))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .testTag("dismiss_simulation_modal_button")
                                    .fillMaxWidth()
                                    .height(46.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "SELESAI & AMBIL KOIN 🪙",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }

                        is PaymentSimulationState.Error -> {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF7F1D1D))
                                    .border(2.dp, Color(0xFFEF4444), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✕", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Black)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "TRANSAKSI GAGAL",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFEF4444)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = state.errorMessage,
                                fontSize = 11.sp,
                                color = Color(0xFFFCA5A5),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onDismiss,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Batal", fontSize = 11.sp, color = Color.White)
                                }

                                if (state.canRetry && onRetry != null) {
                                    Button(
                                        onClick = onRetry,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Coba Lagi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 9.5.sp, color = Color(0xFF94A3B8))
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = valueColor
        )
    }
}
