package com.example.service.payment

enum class MockPaymentChannel(
    val id: String,
    val displayName: String,
    val iconEmoji: String,
    val categoryName: String,
    val brandHexColor: Long
) {
    QRIS(
        id = "qris",
        displayName = "QRIS Instan (Semua Bank & E-Wallet)",
        iconEmoji = "📱",
        categoryName = "QRIS Standar BI",
        brandHexColor = 0xFFE53935
    ),
    DANA(
        id = "dana",
        displayName = "DANA Dompet Digital",
        iconEmoji = "👛",
        categoryName = "E-Wallet",
        brandHexColor = 0xFF118EEA
    ),
    GOPAY(
        id = "gopay",
        displayName = "GoPay",
        iconEmoji = "🟢",
        categoryName = "E-Wallet",
        brandHexColor = 0xFF00AED6
    ),
    OVO(
        id = "ovo",
        displayName = "OVO Cash",
        iconEmoji = "🟣",
        categoryName = "E-Wallet",
        brandHexColor = 0xFF4C3494
    ),
    SHOPEEPAY(
        id = "shopeepay",
        displayName = "ShopeePay",
        iconEmoji = "🟠",
        categoryName = "E-Wallet",
        brandHexColor = 0xFFEE4D2D
    ),
    LINKAJA(
        id = "linkaja",
        displayName = "LinkAja",
        iconEmoji = "🔴",
        categoryName = "E-Wallet",
        brandHexColor = 0xFFD32F2F
    ),
    BANK_VA(
        id = "bank_va",
        displayName = "Virtual Account Bank",
        iconEmoji = "🏦",
        categoryName = "Transfer Bank",
        brandHexColor = 0xFF1E88E5
    )
}

enum class PaymentStatus {
    PENDING,
    PROCESSING,
    SUCCESS,
    FAILED,
    EXPIRED
}

data class PaymentSimulationRequest(
    val channel: MockPaymentChannel,
    val packageId: String,
    val amountRupiah: Int,
    val coinsAwarded: Int,
    val customerPhone: String = "",
    val customerName: String = "Pemain Dindong Pisang",
    val simulatedDelayMs: Long = 1200L,
    val shouldSimulateFailure: Boolean = false
)

data class PaymentSimulationResult(
    val isSuccess: Boolean,
    val status: PaymentStatus,
    val transactionCode: String,
    val channel: MockPaymentChannel,
    val channelDisplayName: String,
    val amountRupiah: Int,
    val coinsAwarded: Int,
    val qrStringPayload: String? = null,
    val deepLinkUrl: String? = null,
    val virtualAccountOrPhone: String? = null,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val failureReason: String? = null
)

sealed class PaymentSimulationState {
    object Idle : PaymentSimulationState()
    data class Initiating(val request: PaymentSimulationRequest) : PaymentSimulationState()
    data class AwaitingUserAction(val result: PaymentSimulationResult) : PaymentSimulationState()
    data class Processing(val progressText: String, val transactionCode: String) : PaymentSimulationState()
    data class Completed(val result: PaymentSimulationResult) : PaymentSimulationState()
    data class Error(val errorMessage: String, val canRetry: Boolean = true) : PaymentSimulationState()
}
