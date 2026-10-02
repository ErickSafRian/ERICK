package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.TopUpTransactionEntity
import com.example.model.PaymentType
import com.example.model.TopUpPackage
import com.example.service.payment.MockPaymentChannel
import com.example.service.payment.PaymentSimulationState
import com.example.ui.components.PaymentSimulationModal
import com.example.ui.theme.DindongColorScheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TopUpDialog(
    isOpen: Boolean,
    themeColors: DindongColorScheme,
    transactions: List<TopUpTransactionEntity>,
    onClose: () -> Unit,
    onConfirmTopUp: (method: String, rupiah: Int, coins: Int) -> Unit,
    onRedeemPromo: (String) -> Boolean,
    successMessage: String? = null,
    paymentSimulationState: PaymentSimulationState = PaymentSimulationState.Idle,
    onSimulateQris: ((Int, Int, String, Boolean) -> Unit)? = null,
    onSimulateDana: ((String, Int, Int, String, Boolean) -> Unit)? = null,
    onSimulateEWallet: ((MockPaymentChannel, String, Int, Int, String, Boolean) -> Unit)? = null,
    onDismissSimulation: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, themeColors.primary, RoundedCornerShape(24.dp)),
            color = themeColors.background
        ) {
            TopUpContent(
                themeColors = themeColors,
                transactions = transactions,
                onClose = onClose,
                onConfirmTopUp = onConfirmTopUp,
                onRedeemPromo = onRedeemPromo,
                successMessage = successMessage,
                paymentSimulationState = paymentSimulationState,
                onSimulateQris = onSimulateQris,
                onSimulateDana = onSimulateDana,
                onSimulateEWallet = onSimulateEWallet,
                onDismissSimulation = onDismissSimulation,
                modifier = modifier
            )
        }
    }
}

@Composable
fun TopUpContent(
    themeColors: DindongColorScheme,
    transactions: List<TopUpTransactionEntity>,
    onClose: (() -> Unit)? = null,
    onConfirmTopUp: (method: String, rupiah: Int, coins: Int) -> Unit,
    onRedeemPromo: (String) -> Boolean,
    successMessage: String? = null,
    paymentSimulationState: PaymentSimulationState = PaymentSimulationState.Idle,
    onSimulateQris: ((Int, Int, String, Boolean) -> Unit)? = null,
    onSimulateDana: ((String, Int, Int, String, Boolean) -> Unit)? = null,
    onSimulateEWallet: ((MockPaymentChannel, String, Int, Int, String, Boolean) -> Unit)? = null,
    onDismissSimulation: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val packages = listOf(
        TopUpPackage("pkg_5k", 5000, 50, 0, "STARTER"),
        TopUpPackage("pkg_10k", 10000, 120, 20, "POPULER"),
        TopUpPackage("pkg_25k", 25000, 350, 100, "BEST VALUE"),
        TopUpPackage("pkg_50k", 50000, 800, 300, "VIP MECHA"),
        TopUpPackage("pkg_100k", 100000, 2000, 1000, "SULTAN JP")
    )

    var selectedPackage by remember { mutableStateOf(packages[1]) } // 10k popular default
    var selectedTab by remember { mutableStateOf("METODE") } // METODE, RIWAYAT, TUKAR
    var selectedPaymentType by remember { mutableStateOf(PaymentType.QRIS) }
    var selectedMethodItem by remember { mutableStateOf(PaymentMethodsData.ALL_METHODS[0]) } // QRIS default
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showAllMethodsList by remember { mutableStateOf(false) }
    var danaPhoneNumber by remember { mutableStateOf("081234567890") }
    var promoInput by remember { mutableStateOf("") }
    var promoFeedback by remember { mutableStateOf<String?>(null) }
    var isSimulatingPayment by remember { mutableStateOf(false) }
    var paymentCompletedAlert by remember { mutableStateOf(false) }
    var lastCompletedSummary by remember { mutableStateOf("") }
    var copyNotice by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Header Top Up
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("💰", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "TOP UP KOIN PISANG",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = themeColors.primary
                        )
                        Text(
                            text = "QRIS • DANA • E-Wallet • Bank Transfer",
                            fontSize = 9.sp,
                            color = themeColors.textMuted
                        )
                    }
                }

                if (onClose != null) {
                    Box(
                        modifier = Modifier
                            .testTag("close_top_up_dialog")
                            .clip(CircleShape)
                            .background(themeColors.surfaceVariant)
                            .clickable { onClose() }
                            .padding(6.dp)
                    ) {
                        Text("✕", fontSize = 14.sp, color = themeColors.onSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }

                Spacer(modifier = Modifier.height(8.dp))

                // Main Navigation Tabs: METODE PEMBAYARAN, RIWAYAT TRANSAKSI, TUKAR HADIAH
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val tabs = listOf(
                        "METODE" to "💳 Top Up",
                        "RIWAYAT" to "📜 Riwayat",
                        "TUKAR" to "🎁 Tarik Saldo"
                    )
                    tabs.forEach { (key, label) ->
                        val isTabSelected = selectedTab == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isTabSelected) themeColors.primary else themeColors.surface)
                                .clickable { selectedTab = key }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isTabSelected) Color.Black else themeColors.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Copy notice toast
                copyNotice?.let { notice ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF065F46))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(notice, color = Color(0xFFA7F3D0), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // TAB 1: METODE TOP UP
                if (selectedTab == "METODE") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Package Selector
                        Text(
                            text = "1. Pilih Paket Koin",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            packages.forEach { pkg ->
                                val isSelected = selectedPackage.id == pkg.id
                                Card(
                                    modifier = Modifier
                                        .testTag("package_${pkg.id}")
                                        .width(115.dp)
                                        .clickable { selectedPackage = pkg },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF1E293B) else themeColors.surface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) themeColors.primary else themeColors.surfaceVariant
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        pkg.tag?.let { tag ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (tag == "POPULER") Color(0xFFFF2A85) else Color(0xFF00E676))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text(tag, fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.White)
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                        }

                                        Text(
                                            text = "🪙 ${pkg.coins + pkg.bonusCoins}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFFFFD700)
                                        )
                                        if (pkg.bonusCoins > 0) {
                                            Text(
                                                text = "+${pkg.bonusCoins} Bonus",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00E5FF)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = formatRupiah(pkg.priceRupiah),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Payment Methods Selector Header & Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. Pilih Metode Pembayaran",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColors.accent
                            )

                            Box(
                                modifier = Modifier
                                    .testTag("toggle_all_methods_btn")
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (showAllMethodsList) themeColors.primary else themeColors.surfaceVariant)
                                    .clickable { showAllMethodsList = !showAllMethodsList }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (showAllMethodsList) "⚡ Tampilan Tab" else "📋 Daftar Semua",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showAllMethodsList) Color.Black else Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        if (showAllMethodsList) {
                            // Reusable PaymentMethodSelector Component
                            PaymentMethodSelector(
                                selectedMethodId = selectedMethodItem.id,
                                onSelectMethod = { method ->
                                    selectedMethodItem = method
                                    selectedPaymentType = when (method.id) {
                                        "qris" -> PaymentType.QRIS
                                        "dana" -> PaymentType.DANA
                                        "gopay", "ovo", "shopeepay" -> PaymentType.E_WALLET
                                        else -> PaymentType.BANK_VA
                                    }
                                },
                                themeColors = themeColors
                            )
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                PaymentType.values().forEach { pType ->
                                    val isSelected = selectedPaymentType == pType
                                    val activeColor = when (pType) {
                                        PaymentType.QRIS -> Color(0xFFE53935)
                                        PaymentType.DANA -> Color(0xFF118EEA)
                                        PaymentType.E_WALLET -> Color(0xFF00897B)
                                        PaymentType.BANK_VA -> Color(0xFF3949AB)
                                        PaymentType.PROMO_CODE -> Color(0xFFFFB300)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .testTag("payment_tab_${pType.name.lowercase()}")
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) activeColor.copy(alpha = 0.25f) else themeColors.surface)
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) activeColor else themeColors.surfaceVariant,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                selectedPaymentType = pType
                                                selectedMethodItem = when (pType) {
                                                    PaymentType.QRIS -> PaymentMethodsData.ALL_METHODS[0]
                                                    PaymentType.DANA -> PaymentMethodsData.ALL_METHODS[1]
                                                    PaymentType.E_WALLET -> PaymentMethodsData.ALL_METHODS[2]
                                                    PaymentType.BANK_VA -> PaymentMethodsData.ALL_METHODS[5]
                                                    PaymentType.PROMO_CODE -> selectedMethodItem
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 7.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(pType.icon, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = pType.title,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) activeColor else themeColors.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dialog Confirmation Trigger Button
                        Button(
                            onClick = { showConfirmationDialog = true },
                            modifier = Modifier
                                .testTag("open_confirm_dialog_btn")
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00E676)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "🧾 Konfirmasi Rincian & Bayar (${formatRupiah(selectedPackage.priceRupiah)})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // PAYMENT DETAILS VIEW ACCORDING TO SELECTED PAYMENT TYPE
                        when (selectedPaymentType) {
                            PaymentType.QRIS -> {
                                QrisPaymentView(
                                    selectedPackage = selectedPackage,
                                    themeColors = themeColors,
                                    isSimulating = isSimulatingPayment || paymentSimulationState is PaymentSimulationState.Initiating || paymentSimulationState is PaymentSimulationState.Processing,
                                    onSimulatePay = {
                                        if (onSimulateQris != null) {
                                            onSimulateQris(selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins, selectedPackage.id, false)
                                        } else {
                                            isSimulatingPayment = true
                                            coroutineScope.launch {
                                                delay(1400)
                                                isSimulatingPayment = false
                                                onConfirmTopUp("QRIS Instan", selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins)
                                                lastCompletedSummary = "QRIS Rp ${selectedPackage.priceRupiah} (+${selectedPackage.coins + selectedPackage.bonusCoins} Koin)"
                                                paymentCompletedAlert = true
                                            }
                                        }
                                    },
                                    onCopyNominal = {
                                        clipboardManager.setText(AnnotatedString("${selectedPackage.priceRupiah}"))
                                        copyNotice = "Nominal Rp ${selectedPackage.priceRupiah} disalin ke clipboard!"
                                        coroutineScope.launch {
                                            delay(2000)
                                            copyNotice = null
                                        }
                                    }
                                )
                            }

                            PaymentType.DANA -> {
                                DanaPaymentView(
                                    selectedPackage = selectedPackage,
                                    themeColors = themeColors,
                                    phoneNumber = danaPhoneNumber,
                                    onPhoneChange = { danaPhoneNumber = it },
                                    isSimulating = isSimulatingPayment || paymentSimulationState is PaymentSimulationState.Initiating || paymentSimulationState is PaymentSimulationState.Processing,
                                    onSimulatePay = {
                                        if (onSimulateDana != null) {
                                            onSimulateDana(danaPhoneNumber, selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins, selectedPackage.id, false)
                                        } else {
                                            isSimulatingPayment = true
                                            coroutineScope.launch {
                                                delay(1300)
                                                isSimulatingPayment = false
                                                onConfirmTopUp("DANA ($danaPhoneNumber)", selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins)
                                                lastCompletedSummary = "DANA Rp ${selectedPackage.priceRupiah} (+${selectedPackage.coins + selectedPackage.bonusCoins} Koin)"
                                                paymentCompletedAlert = true
                                            }
                                        }
                                    },
                                    onCopyDanaNumber = {
                                        clipboardManager.setText(AnnotatedString("082188889999"))
                                        copyNotice = "Nomor DANA Merchant 082188889999 disalin!"
                                        coroutineScope.launch {
                                            delay(2000)
                                            copyNotice = null
                                        }
                                    }
                                )
                            }

                            PaymentType.E_WALLET -> {
                                OtherEWalletPaymentView(
                                    selectedPackage = selectedPackage,
                                    themeColors = themeColors,
                                    isSimulating = isSimulatingPayment || paymentSimulationState is PaymentSimulationState.Initiating || paymentSimulationState is PaymentSimulationState.Processing,
                                    onSimulatePay = { walletName ->
                                        if (onSimulateEWallet != null) {
                                            val channel = when (walletName.lowercase()) {
                                                "gopay" -> MockPaymentChannel.GOPAY
                                                "ovo" -> MockPaymentChannel.OVO
                                                "shopeepay" -> MockPaymentChannel.SHOPEEPAY
                                                else -> MockPaymentChannel.GOPAY
                                            }
                                            onSimulateEWallet(channel, danaPhoneNumber, selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins, selectedPackage.id, false)
                                        } else {
                                            isSimulatingPayment = true
                                            coroutineScope.launch {
                                                delay(1300)
                                                isSimulatingPayment = false
                                                onConfirmTopUp(walletName, selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins)
                                                lastCompletedSummary = "$walletName Rp ${selectedPackage.priceRupiah} (+${selectedPackage.coins + selectedPackage.bonusCoins} Koin)"
                                                paymentCompletedAlert = true
                                            }
                                        }
                                    }
                                )
                            }

                            PaymentType.BANK_VA -> {
                                BankVaPaymentView(
                                    selectedPackage = selectedPackage,
                                    themeColors = themeColors,
                                    isSimulating = isSimulatingPayment,
                                    onSimulatePay = { bankName ->
                                        isSimulatingPayment = true
                                        coroutineScope.launch {
                                            delay(1400)
                                            isSimulatingPayment = false
                                            onConfirmTopUp("Virtual Account $bankName", selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins)
                                            lastCompletedSummary = "VA $bankName Rp ${selectedPackage.priceRupiah} (+${selectedPackage.coins + selectedPackage.bonusCoins} Koin)"
                                            paymentCompletedAlert = true
                                        }
                                    },
                                    onCopyVa = { va ->
                                        clipboardManager.setText(AnnotatedString(va))
                                        copyNotice = "Nomor VA $va berhasil disalin!"
                                        coroutineScope.launch {
                                            delay(2000)
                                            copyNotice = null
                                        }
                                    }
                                )
                            }

                            PaymentType.PROMO_CODE -> {
                                PromoCodeView(
                                    themeColors = themeColors,
                                    promoCode = promoInput,
                                    onPromoChange = { promoInput = it },
                                    feedback = promoFeedback,
                                    onRedeem = {
                                        val valid = onRedeemPromo(promoInput)
                                        if (valid) {
                                            promoFeedback = "Berhasil! Koin gratis telah ditambahkan!"
                                            promoInput = ""
                                        } else {
                                            promoFeedback = "Kode tidak valid atau kadaluarsa. Coba kode: DINDONGGACOR"
                                        }
                                    }
                                )
                            }
                        }

                        // Success notification banner inside dialog
                        if (paymentCompletedAlert) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF064E3B))
                                    .border(1.dp, Color(0xFF10B981), RoundedCornerShape(12.dp))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🎉 PEMBAYARAN BERHASIL DIVERIFIKASI!", color = Color(0xFFA7F3D0), fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(lastCompletedSummary, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // TAB 2: RIWAYAT TRANSAKSI TOP UP
                if (selectedTab == "RIWAYAT") {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Riwayat Pengisian Koin",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        if (transactions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("📜", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        "Belum ada riwayat top up",
                                        color = themeColors.textMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(transactions) { tx ->
                                    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                                        .format(Date(tx.timestamp))
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = tx.method,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "${tx.transCode} • $dateStr",
                                                    fontSize = 9.sp,
                                                    color = themeColors.textMuted
                                                )
                                                if (tx.rupiahAmount > 0) {
                                                    Text(
                                                        text = formatRupiah(tx.rupiahAmount),
                                                        fontSize = 10.sp,
                                                        color = themeColors.accent
                                                    )
                                                }
                                            }

                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    text = "+${tx.coinsAdded} 🪙",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color(0xFFFFD700)
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFF059669))
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = tx.status,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
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
                }

                // TAB 3: TARIK SALDO / TUKAR HADIAH SIMULATOR
                if (selectedTab == "TUKAR") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Tukar Koin Menang ke Hadiah",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColors.accent
                        )
                        Text(
                            text = "Koin hasil jackpot dan kemenangan dapat dicairkan kembali!",
                            fontSize = 9.5.sp,
                            color = themeColors.textMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val redeemRewards = listOf(
                            Triple("Saldo DANA Rp 25.000", 500, "👛 DANA"),
                            Triple("Pulsa All Operator Rp 10.000", 200, "📱 Pulsa"),
                            Triple("Saldo GoPay Rp 50.000", 1000, "🟢 GoPay"),
                            Triple("Voucher Google Play Rp 100.000", 2000, "🎮 Google Play")
                        )

                        redeemRewards.forEach { (title, reqCoins, provider) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text("Membutuhkan $reqCoins Koin • $provider", fontSize = 9.sp, color = themeColors.textMuted)
                                    }

                                    Button(
                                        onClick = {
                                            copyNotice = "Pengajuan penarikan $title diproses! Saldo akan masuk maksimal 1x24 jam."
                                            coroutineScope.launch {
                                                delay(2500)
                                                copyNotice = null
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Tukar", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

        // Confirmation Dialog for chosen Top-Up package & payment method
        TopUpConfirmationDialog(
            isOpen = showConfirmationDialog,
            packageItem = selectedPackage,
            paymentMethod = selectedMethodItem,
            themeColors = themeColors,
            onConfirm = {
                showConfirmationDialog = false
                isSimulatingPayment = true
                coroutineScope.launch {
                    delay(1200)
                    isSimulatingPayment = false
                    onConfirmTopUp(
                        selectedMethodItem.name,
                        selectedPackage.priceRupiah,
                        selectedPackage.coins + selectedPackage.bonusCoins
                    )
                    lastCompletedSummary = "${selectedMethodItem.name} Rp ${selectedPackage.priceRupiah} (+${selectedPackage.coins + selectedPackage.bonusCoins} Koin)"
                    paymentCompletedAlert = true
                }
            },
            onDismiss = { showConfirmationDialog = false }
        )

        // Modal for payment simulation service
        PaymentSimulationModal(
            state = paymentSimulationState,
            themeColors = themeColors,
            onDismiss = { onDismissSimulation?.invoke() },
            onRetry = {
                when (selectedPaymentType) {
                    PaymentType.QRIS -> onSimulateQris?.invoke(selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins, selectedPackage.id, false)
                    PaymentType.DANA -> onSimulateDana?.invoke(danaPhoneNumber, selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins, selectedPackage.id, false)
                    PaymentType.E_WALLET -> onSimulateEWallet?.invoke(MockPaymentChannel.GOPAY, danaPhoneNumber, selectedPackage.priceRupiah, selectedPackage.coins + selectedPackage.bonusCoins, selectedPackage.id, false)
                    else -> Unit
                }
            }
        )
    }
}

// -------------------------------------------------------------
// QRIS PAYMENT VIEW
// -------------------------------------------------------------
@Composable
private fun QrisPaymentView(
    selectedPackage: TopUpPackage,
    themeColors: DindongColorScheme,
    isSimulating: Boolean,
    onSimulatePay: () -> Unit,
    onCopyNominal: () -> Unit
) {
    // Unique 3 digit code for verification
    val uniqueCode = remember(selectedPackage.id) { (100..499).random() }
    val totalWithCode = selectedPackage.priceRupiah + uniqueCode

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // QRIS Header Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE53935))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("QRIS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pembayaran Cepat Nasional", fontSize = 8.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "DINDONG PISANG OFFICIAL",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color.White
            )
            Text(
                "NMID: ID1020268899DINDONG",
                fontSize = 8.sp,
                color = themeColors.textMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Realistic Generated QR Code Visual Canvas
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val squareSize = w / 15f

                    // Draw 3 corner finder patterns
                    fun drawFinder(x: Float, y: Float) {
                        drawRect(Color.Black, Offset(x, y), Size(squareSize * 4, squareSize * 4))
                        drawRect(Color.White, Offset(x + squareSize * 0.7f, y + squareSize * 0.7f), Size(squareSize * 2.6f, squareSize * 2.6f))
                        drawRect(Color.Black, Offset(x + squareSize * 1.3f, y + squareSize * 1.3f), Size(squareSize * 1.4f, squareSize * 1.4f))
                    }
                    drawFinder(0f, 0f)
                    drawFinder(w - squareSize * 4, 0f)
                    drawFinder(0f, h - squareSize * 4)

                    // Draw scattered QR data dots
                    val seed = 42
                    for (row in 0..14) {
                        for (col in 0..14) {
                            if ((row < 5 && col < 5) || (row < 5 && col > 9) || (row > 9 && col < 5)) continue
                            if ((row * 7 + col * 13 + seed) % 3 == 0) {
                                drawRect(
                                    Color.Black,
                                    Offset(col * squareSize + 1, row * squareSize + 1),
                                    Size(squareSize - 2, squareSize - 2)
                                )
                            }
                        }
                    }
                }

                // Center logo badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🍌", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Total Nominal to Transfer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0F172A))
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Total Nominal Transfer Tepat:", fontSize = 9.sp, color = themeColors.textMuted)
                Text(
                    text = formatRupiah(totalWithCode),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFFD700)
                )
                Text(
                    text = "*Kode unik $uniqueCode memastikan koin masuk otomatis!",
                    fontSize = 8.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Buttons: Salin Nominal & Simulasi Cek Pembayaran
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCopyNominal,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("📋 Salin Nominal", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Button(
                    onClick = onSimulatePay,
                    enabled = !isSimulating,
                    modifier = Modifier
                        .testTag("simulate_qris_payment_button")
                        .weight(1.3f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isSimulating) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color.Black)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Verifikasi...", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    } else {
                        Text("⚡ Simulasi Bayar", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DANA PAYMENT VIEW
// -------------------------------------------------------------
@Composable
private fun DanaPaymentView(
    selectedPackage: TopUpPackage,
    themeColors: DindongColorScheme,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    isSimulating: Boolean,
    onSimulatePay: () -> Unit,
    onCopyDanaNumber: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF118EEA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // DANA Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF118EEA))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("DANA", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Dompet Digital Indonesia", fontSize = 10.sp, color = themeColors.textMuted)
                }

                Text(
                    text = formatRupiah(selectedPackage.priceRupiah),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Opsi 1: Pembayaran Cepat Otomatis DANA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF60A5FA))
            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                label = { Text("Nomor Akun DANA Anda", fontSize = 10.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF118EEA),
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSimulatePay,
                enabled = !isSimulating && phoneNumber.length >= 10,
                modifier = Modifier
                    .testTag("simulate_dana_payment_button")
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF118EEA)),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSimulating) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Menghubungkan ke DANA...", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Text("👛 Bayar Sekarang dengan DANA", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = themeColors.surfaceVariant)
            Spacer(modifier = Modifier.height(10.dp))

            Text("Opsi 2: Transfer Manual ke Akun DANA Resmi", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeColors.accent)
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F172A))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("0821-8888-9999", fontSize = 13.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color.White)
                    Text("a/n DINDONG PISANG ENTERTAINMENT", fontSize = 8.sp, color = themeColors.textMuted)
                }

                Button(
                    onClick = onCopyDanaNumber,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Salin", fontSize = 10.sp, color = Color.White)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// OTHER E-WALLETS VIEW (GOPAY, OVO, SHOPEEPAY)
// -------------------------------------------------------------
@Composable
private fun OtherEWalletPaymentView(
    selectedPackage: TopUpPackage,
    themeColors: DindongColorScheme,
    isSimulating: Boolean,
    onSimulatePay: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00897B))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text("Pilih Dompet Digital Lainnya", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeColors.primary)
            Spacer(modifier = Modifier.height(8.dp))

            val wallets = listOf(
                Pair("GoPay", Color(0xFF00AED6)),
                Pair("OVO", Color(0xFF4C3494)),
                Pair("ShopeePay", Color(0xFFEE4D2D))
            )

            wallets.forEach { (name, brandColor) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, brandColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(brandColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(name.take(1), fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Bayar " + formatRupiah(selectedPackage.priceRupiah), fontSize = 9.sp, color = themeColors.textMuted)
                        }
                    }

                    Button(
                        onClick = { onSimulatePay(name) },
                        enabled = !isSimulating,
                        colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Bayar", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// BANK VA VIEW
// -------------------------------------------------------------
@Composable
private fun BankVaPaymentView(
    selectedPackage: TopUpPackage,
    themeColors: DindongColorScheme,
    isSimulating: Boolean,
    onSimulatePay: (String) -> Unit,
    onCopyVa: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3949AB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text("Pilih Bank Virtual Account (Verifikasi Otomatis)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF818CF8))
            Spacer(modifier = Modifier.height(8.dp))

            val bankVas = listOf(
                Triple("BCA Virtual Account", "88091209384728", Color(0xFF005DAA)),
                Triple("Mandiri Virtual Account", "89012398471928", Color(0xFF003082)),
                Triple("BRI Briva", "128394829103948", Color(0xFF00529C))
            )

            bankVas.forEach { (bankName, vaNumber, bankColor) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(bankName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(formatRupiah(selectedPackage.priceRupiah), fontSize = 10.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(vaNumber, fontSize = 12.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, color = Color(0xFF67E8F9))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Button(
                                onClick = { onCopyVa(vaNumber) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Salin", fontSize = 9.sp, color = Color.White)
                            }
                            Button(
                                onClick = { onSimulatePay(bankName) },
                                enabled = !isSimulating,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Bayar", fontSize = 9.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// PROMO CODE VIEW
// -------------------------------------------------------------
@Composable
private fun PromoCodeView(
    themeColors: DindongColorScheme,
    promoCode: String,
    onPromoChange: (String) -> Unit,
    feedback: String?,
    onRedeem: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = themeColors.surface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text("Masukkan Kode Voucher / Promo Gratis", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = themeColors.accent)
            Text("Dapatkan koin tambahan secara gratis tanpa bayar!", fontSize = 9.sp, color = themeColors.textMuted)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = promoCode,
                onValueChange = onPromoChange,
                placeholder = { Text("Contoh: DINDONGGACOR", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColors.accent,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRedeem,
                enabled = promoCode.isNotBlank(),
                modifier = Modifier
                    .testTag("redeem_promo_button")
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB300)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🎟️ Klaim Koin Promo", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }

            feedback?.let { msg ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = msg,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (msg.contains("Berhasil")) Color(0xFF10B981) else Color(0xFFF87171)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("Kode Promo Aktif:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = themeColors.textMuted)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("DINDONGGACOR (+100)", "PISANGEMAS (+50)", "SLOTBERKAH (+200)").forEach { codeTag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(codeTag, fontSize = 8.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun formatRupiah(number: Int): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    formatter.maximumFractionDigits = 0
    return formatter.format(number).replace("Rp", "Rp ")
}
