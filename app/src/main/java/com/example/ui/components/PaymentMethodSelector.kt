package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DindongColorScheme

data class PaymentMethodItem(
    val id: String,
    val name: String,
    val category: String, // e.g., "QRIS & E-Wallet" or "Transfer Bank"
    val iconEmoji: String,
    val description: String,
    val badgeText: String? = null,
    val badgeColor: Color = Color(0xFF00E676),
    val brandColor: Color
)

object PaymentMethodsData {
    val ALL_METHODS = listOf(
        PaymentMethodItem(
            id = "qris",
            name = "QRIS Instan (Semua Pembayaran)",
            category = "QRIS & Dompet Digital",
            iconEmoji = "📱",
            description = "Scan via BCA, Mandiri, BRI, DANA, GoPay, OVO, ShopeePay",
            badgeText = "OTOMATIS 1 DETIK",
            badgeColor = Color(0xFFE53935),
            brandColor = Color(0xFFE53935)
        ),
        PaymentMethodItem(
            id = "dana",
            name = "DANA (Dompet Digital)",
            category = "QRIS & Dompet Digital",
            iconEmoji = "👛",
            description = "Koneksi langsung & konfirmasi 1 ketukan",
            badgeText = "POPULER",
            badgeColor = Color(0xFF118EEA),
            brandColor = Color(0xFF118EEA)
        ),
        PaymentMethodItem(
            id = "gopay",
            name = "GoPay",
            category = "QRIS & Dompet Digital",
            iconEmoji = "🟢",
            description = "Pembayaran via aplikasi Gojek / GoPay",
            badgeText = "BEBAS BIAYA",
            badgeColor = Color(0xFF00AED6),
            brandColor = Color(0xFF00AED6)
        ),
        PaymentMethodItem(
            id = "ovo",
            name = "OVO Cash",
            category = "QRIS & Dompet Digital",
            iconEmoji = "🟣",
            description = "Notifikasi pembayaran langsung ke nomor OVO",
            badgeText = "INSTAN",
            badgeColor = Color(0xFF4C3494),
            brandColor = Color(0xFF4C3494)
        ),
        PaymentMethodItem(
            id = "shopeepay",
            name = "ShopeePay",
            category = "QRIS & Dompet Digital",
            iconEmoji = "🟠",
            description = "Gunakan saldo ShopeePay atau SPayLater",
            badgeText = "PROMO",
            badgeColor = Color(0xFFEE4D2D),
            brandColor = Color(0xFFEE4D2D)
        ),
        PaymentMethodItem(
            id = "va_bca",
            name = "BCA Virtual Account",
            category = "Transfer Bank & Virtual Account",
            iconEmoji = "🏦",
            description = "Transfer via BCA Mobile, KlikBCA, atau ATM BCA",
            badgeText = "24 JAM",
            badgeColor = Color(0xFF005DAA),
            brandColor = Color(0xFF005DAA)
        ),
        PaymentMethodItem(
            id = "va_mandiri",
            name = "Mandiri Virtual Account",
            category = "Transfer Bank & Virtual Account",
            iconEmoji = "🏛️",
            description = "Transfer via Livin' by Mandiri atau ATM Mandiri",
            badgeText = "24 JAM",
            badgeColor = Color(0xFF003082),
            brandColor = Color(0xFF003082)
        ),
        PaymentMethodItem(
            id = "va_bri",
            name = "BRI BRIVA",
            category = "Transfer Bank & Virtual Account",
            iconEmoji = "💳",
            description = "Transfer via BRImo atau ATM Bank BRI",
            badgeText = "24 JAM",
            badgeColor = Color(0xFF00529C),
            brandColor = Color(0xFF00529C)
        )
    )
}

@Composable
fun PaymentMethodSelector(
    selectedMethodId: String,
    onSelectMethod: (PaymentMethodItem) -> Unit,
    themeColors: DindongColorScheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val grouped = PaymentMethodsData.ALL_METHODS.groupBy { it.category }

        grouped.forEach { (categoryName, methods) ->
            Text(
                text = categoryName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.accent,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )

            methods.forEach { method ->
                val isSelected = selectedMethodId == method.id
                val borderCol by animateColorAsState(
                    targetValue = if (isSelected) method.brandColor else themeColors.surfaceVariant,
                    animationSpec = tween(200),
                    label = "borderCol"
                )
                val bgCol by animateColorAsState(
                    targetValue = if (isSelected) method.brandColor.copy(alpha = 0.15f) else themeColors.surface,
                    animationSpec = tween(200),
                    label = "bgCol"
                )

                Card(
                    modifier = Modifier
                        .testTag("payment_method_${method.id}")
                        .fillMaxWidth()
                        .clickable { onSelectMethod(method) },
                    colors = CardDefaults.cardColors(containerColor = bgCol),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = borderCol
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Brand Icon Badge
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(method.brandColor.copy(alpha = 0.25f))
                                .border(1.dp, method.brandColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = method.iconEmoji, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Text details
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = method.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                method.badgeText?.let { badge ->
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(method.badgeColor)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = badge,
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = method.description,
                                fontSize = 9.5.sp,
                                color = themeColors.textMuted,
                                lineHeight = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Selection Indicator (Radio / Check)
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) method.brandColor else Color.Transparent)
                                .border(
                                    width = if (isSelected) 2.dp else 1.5.dp,
                                    color = if (isSelected) Color.White else Color(0xFF64748B),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Text(
                                    text = "✓",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
