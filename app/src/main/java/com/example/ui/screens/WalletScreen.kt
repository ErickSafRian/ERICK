package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.TopUpTransactionEntity
import com.example.service.payment.MockPaymentChannel
import com.example.service.payment.PaymentSimulationState
import com.example.ui.components.TopUpContent
import com.example.ui.theme.DindongColorScheme

@Composable
fun WalletScreen(
    themeColors: DindongColorScheme,
    transactions: List<TopUpTransactionEntity>,
    onConfirmTopUp: (String, Int, Int) -> Unit,
    onRedeemPromo: (String) -> Boolean,
    successMessage: String?,
    paymentSimulationState: PaymentSimulationState = PaymentSimulationState.Idle,
    onSimulateQris: ((Int, Int, String, Boolean) -> Unit)? = null,
    onSimulateDana: ((String, Int, Int, String, Boolean) -> Unit)? = null,
    onSimulateEWallet: ((MockPaymentChannel, String, Int, Int, String, Boolean) -> Unit)? = null,
    onDismissSimulation: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
        ) {
            TopUpContent(
                themeColors = themeColors,
                transactions = transactions,
                onClose = null, // No close button needed in bottom tab view!
                onConfirmTopUp = onConfirmTopUp,
                onRedeemPromo = onRedeemPromo,
                successMessage = successMessage,
                paymentSimulationState = paymentSimulationState,
                onSimulateQris = onSimulateQris,
                onSimulateDana = onSimulateDana,
                onSimulateEWallet = onSimulateEWallet,
                onDismissSimulation = onDismissSimulation
            )
        }
    }
}
