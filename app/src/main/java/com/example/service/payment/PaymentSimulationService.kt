package com.example.service.payment

import com.example.data.GameRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

interface PaymentSimulationService {
    fun generateQrisPayload(amountRupiah: Int, merchantName: String = "DINDONG PISANG ARCADE"): String
    fun validatePhoneNumber(phoneNumber: String): Boolean
    fun formatRupiah(amount: Int): String
    fun generateTransactionCode(channel: MockPaymentChannel): String

    fun simulateQrisPayment(
        amountRupiah: Int,
        coinsAwarded: Int,
        packageId: String,
        shouldFail: Boolean = false
    ): Flow<PaymentSimulationState>

    fun simulateDanaPayment(
        phoneNumber: String,
        amountRupiah: Int,
        coinsAwarded: Int,
        packageId: String,
        shouldFail: Boolean = false
    ): Flow<PaymentSimulationState>

    fun simulateEWalletPayment(
        channel: MockPaymentChannel,
        phoneNumber: String,
        amountRupiah: Int,
        coinsAwarded: Int,
        packageId: String,
        shouldFail: Boolean = false
    ): Flow<PaymentSimulationState>

    fun processPaymentRequest(request: PaymentSimulationRequest): Flow<PaymentSimulationState>
    suspend fun recordSuccessfulPayment(result: PaymentSimulationResult): String
}

class MockPaymentSimulationServiceImpl(
    private val repository: GameRepository
) : PaymentSimulationService {

    override fun generateQrisPayload(amountRupiah: Int, merchantName: String): String {
        // ASPI / BI Standard QRIS TLV (Tag-Length-Value) mock generator
        val cleanMerchant = merchantName.take(25).uppercase()
        val amountStr = amountRupiah.toString()
        val invoiceRef = "DND" + System.currentTimeMillis().toString().takeLast(8)

        val tlvBuilder = StringBuilder()
        // 00: Payload Format Indicator
        tlvBuilder.append("000201")
        // 01: Point of Initiation Method (12 = Dynamic QR)
        tlvBuilder.append("010212")
        // 26: Merchant Account Information (NMID)
        val nmidValue = "0016ID.CO.DINDONG.WWW01189360099900000000010215$invoiceRef"
        tlvBuilder.append("26${formatLength(nmidValue.length)}$nmidValue")
        // 52: Merchant Category Code (5812: Gaming & Amusement)
        tlvBuilder.append("52045812")
        // 53: Transaction Currency (360 = IDR)
        tlvBuilder.append("5303360")
        // 54: Transaction Amount
        tlvBuilder.append("54${formatLength(amountStr.length)}$amountStr")
        // 58: Country Code (ID)
        tlvBuilder.append("5802ID")
        // 59: Merchant Name
        tlvBuilder.append("59${formatLength(cleanMerchant.length)}$cleanMerchant")
        // 60: Merchant City
        val city = "JAKARTA PUSAT"
        tlvBuilder.append("60${formatLength(city.length)}$city")
        // 61: Postal Code
        tlvBuilder.append("610510110")
        // 63: CRC16 prefix (checksum placeholder)
        tlvBuilder.append("6304")

        val rawData = tlvBuilder.toString()
        val crc = calculateCrc16(rawData)
        return rawData + crc
    }

    private fun formatLength(len: Int): String {
        return if (len < 10) "0$len" else "$len"
    }

    private fun calculateCrc16(input: String): String {
        var crc = 0xFFFF
        val polynomial = 0x1021
        for (b in input.toByteArray(Charsets.ISO_8859_1)) {
            for (i in 0 until 8) {
                val bit = ((b.toInt() ushr (7 - i)) and 1) == 1
                val c15 = ((crc ushr 15) and 1) == 1
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor polynomial
            }
        }
        crc = crc and 0xFFFF
        return String.format(Locale.US, "%04X", crc)
    }

    override fun validatePhoneNumber(phoneNumber: String): Boolean {
        val clean = phoneNumber.trim().replace("-", "").replace(" ", "")
        val digits = if (clean.startsWith("+62")) "0" + clean.substring(3) else clean
        return digits.startsWith("08") && digits.length in 10..14 && digits.all { it.isDigit() }
    }

    override fun formatRupiah(amount: Int): String {
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "Rp " + format.format(amount)
    }

    override fun generateTransactionCode(channel: MockPaymentChannel): String {
        val dateStr = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date())
        val randomNum = (1000..9999).random()
        val prefix = when (channel) {
            MockPaymentChannel.QRIS -> "QRIS"
            MockPaymentChannel.DANA -> "DANA"
            MockPaymentChannel.GOPAY -> "GOPAY"
            MockPaymentChannel.OVO -> "OVO"
            MockPaymentChannel.SHOPEEPAY -> "SPAY"
            MockPaymentChannel.LINKAJA -> "LINK"
            MockPaymentChannel.BANK_VA -> "VA"
        }
        return "$prefix-$dateStr-$randomNum"
    }

    override fun simulateQrisPayment(
        amountRupiah: Int,
        coinsAwarded: Int,
        packageId: String,
        shouldFail: Boolean
    ): Flow<PaymentSimulationState> {
        val request = PaymentSimulationRequest(
            channel = MockPaymentChannel.QRIS,
            packageId = packageId,
            amountRupiah = amountRupiah,
            coinsAwarded = coinsAwarded,
            shouldSimulateFailure = shouldFail
        )
        return processPaymentRequest(request)
    }

    override fun simulateDanaPayment(
        phoneNumber: String,
        amountRupiah: Int,
        coinsAwarded: Int,
        packageId: String,
        shouldFail: Boolean
    ): Flow<PaymentSimulationState> {
        val request = PaymentSimulationRequest(
            channel = MockPaymentChannel.DANA,
            packageId = packageId,
            amountRupiah = amountRupiah,
            coinsAwarded = coinsAwarded,
            customerPhone = phoneNumber,
            shouldSimulateFailure = shouldFail
        )
        return processPaymentRequest(request)
    }

    override fun simulateEWalletPayment(
        channel: MockPaymentChannel,
        phoneNumber: String,
        amountRupiah: Int,
        coinsAwarded: Int,
        packageId: String,
        shouldFail: Boolean
    ): Flow<PaymentSimulationState> {
        val request = PaymentSimulationRequest(
            channel = channel,
            packageId = packageId,
            amountRupiah = amountRupiah,
            coinsAwarded = coinsAwarded,
            customerPhone = phoneNumber,
            shouldSimulateFailure = shouldFail
        )
        return processPaymentRequest(request)
    }

    override fun processPaymentRequest(request: PaymentSimulationRequest): Flow<PaymentSimulationState> = flow {
        emit(PaymentSimulationState.Initiating(request))

        // Validate phone number if E-Wallet / DANA
        if (request.channel != MockPaymentChannel.QRIS && request.channel != MockPaymentChannel.BANK_VA) {
            if (!validatePhoneNumber(request.customerPhone)) {
                emit(
                    PaymentSimulationState.Error(
                        errorMessage = "Nomor telepon '${request.customerPhone}' tidak valid! Format harus diawali 08xx (10-13 digit).",
                        canRetry = true
                    )
                )
                return@flow
            }
        }

        delay(400L) // Network handshake latency

        val transCode = generateTransactionCode(request.channel)
        val qrPayload = if (request.channel == MockPaymentChannel.QRIS) {
            generateQrisPayload(request.amountRupiah)
        } else null

        val deepLinkUrl = when (request.channel) {
            MockPaymentChannel.DANA -> "https://link.dana.id/pay?pref=$transCode&amt=${request.amountRupiah}"
            MockPaymentChannel.GOPAY -> "gojek://gopay/merchanttransfer?ref=$transCode"
            MockPaymentChannel.OVO -> "ovo://payment?trxId=$transCode"
            MockPaymentChannel.SHOPEEPAY -> "shopeepay://pay?invoice=$transCode"
            else -> null
        }

        // Processing / Gateway switch verification
        emit(
            PaymentSimulationState.Processing(
                progressText = "Menghubungkan ke gateway ${request.channel.displayName}...",
                transactionCode = transCode
            )
        )
        delay(700L)

        emit(
            PaymentSimulationState.Processing(
                progressText = "Memverifikasi saldo & otorisasi pembayaran...",
                transactionCode = transCode
            )
        )
        delay(600L)

        if (request.shouldSimulateFailure) {
            val failureResult = PaymentSimulationResult(
                isSuccess = false,
                status = PaymentStatus.FAILED,
                transactionCode = transCode,
                channel = request.channel,
                channelDisplayName = request.channel.displayName,
                amountRupiah = request.amountRupiah,
                coinsAwarded = 0,
                qrStringPayload = qrPayload,
                deepLinkUrl = deepLinkUrl,
                virtualAccountOrPhone = request.customerPhone,
                message = "Transaksi simulasi ditolak oleh payment provider (Simulated Error).",
                failureReason = "INSUFFICIENT_FUNDS_OR_REJECTED"
            )
            emit(PaymentSimulationState.Error("Pembayaran gagal diproses oleh gateway.", canRetry = true))
            return@flow
        }

        // Payment Success: Record in Room database & wallet
        val successResult = PaymentSimulationResult(
            isSuccess = true,
            status = PaymentStatus.SUCCESS,
            transactionCode = transCode,
            channel = request.channel,
            channelDisplayName = request.channel.displayName,
            amountRupiah = request.amountRupiah,
            coinsAwarded = request.coinsAwarded,
            qrStringPayload = qrPayload,
            deepLinkUrl = deepLinkUrl,
            virtualAccountOrPhone = request.customerPhone.ifEmpty { "082188889999" },
            message = "Pembayaran via ${request.channel.displayName} sebesar ${formatRupiah(request.amountRupiah)} berhasil diverifikasi!"
        )

        recordSuccessfulPayment(successResult)
        emit(PaymentSimulationState.Completed(successResult))
    }

    override suspend fun recordSuccessfulPayment(result: PaymentSimulationResult): String {
        val methodName = "${result.channel.displayName}${if (!result.virtualAccountOrPhone.isNullOrEmpty() && result.channel != MockPaymentChannel.QRIS) " (${result.virtualAccountOrPhone})" else ""}"
        return repository.recordTopUp(
            method = methodName,
            rupiah = result.amountRupiah,
            coins = result.coinsAwarded
        )
    }
}
