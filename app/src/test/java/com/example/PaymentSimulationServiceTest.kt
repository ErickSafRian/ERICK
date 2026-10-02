package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameRepository
import com.example.service.payment.MockPaymentChannel
import com.example.service.payment.MockPaymentSimulationServiceImpl
import com.example.service.payment.PaymentSimulationRequest
import com.example.service.payment.PaymentSimulationService
import com.example.service.payment.PaymentSimulationState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PaymentSimulationServiceTest {

    private lateinit var application: Application
    private lateinit var repository: GameRepository
    private lateinit var paymentService: PaymentSimulationService

    @Before
    fun setUp() = runTest {
        application = ApplicationProvider.getApplicationContext()
        repository = GameRepository(application)
        repository.ensureInitialized()
        paymentService = MockPaymentSimulationServiceImpl(repository)
    }

    @Test
    fun testGenerateQrisPayload() {
        val payload = paymentService.generateQrisPayload(50000, "DINDONG ARCADE")
        assertNotNull(payload)
        assertTrue(payload.startsWith("000201"))
        assertTrue(payload.contains("5303360")) // IDR currency code
        assertTrue(payload.contains("540550000")) // Tag 54 (Length 05, Amount 50000)
        assertTrue(payload.contains("5802ID")) // Country code ID
        assertTrue(payload.contains("6304")) // CRC prefix
        assertEquals(4, payload.takeLast(4).length) // 4 character CRC16
    }

    @Test
    fun testValidatePhoneNumber() {
        assertTrue(paymentService.validatePhoneNumber("081234567890"))
        assertTrue(paymentService.validatePhoneNumber("+6281298765432"))
        assertTrue(paymentService.validatePhoneNumber("0852-1122-3344"))
        assertTrue(paymentService.validatePhoneNumber("0877 8899 0011"))

        // Invalid numbers
        assertFalse(paymentService.validatePhoneNumber("0211234567")) // Landline
        assertFalse(paymentService.validatePhoneNumber("12345")) // Too short
        assertFalse(paymentService.validatePhoneNumber("0812abc456")) // Non-digit
        assertFalse(paymentService.validatePhoneNumber("")) // Empty
    }

    @Test
    fun testFormatRupiah() {
        val formatted = paymentService.formatRupiah(25000)
        assertTrue(formatted.contains("25"))
        assertTrue(formatted.startsWith("Rp"))
    }

    @Test
    fun testSimulateQrisPaymentSuccess() = runTest {
        val states = paymentService.simulateQrisPayment(
            amountRupiah = 10000,
            coinsAwarded = 120,
            packageId = "pkg_10k",
            shouldFail = false
        ).toList()

        assertTrue(states.any { it is PaymentSimulationState.Initiating })
        assertTrue(states.any { it is PaymentSimulationState.Processing })

        val finalState = states.last()
        assertTrue("Final state must be Completed", finalState is PaymentSimulationState.Completed)
        val completed = finalState as PaymentSimulationState.Completed
        assertTrue(completed.result.isSuccess)
        assertEquals(10000, completed.result.amountRupiah)
        assertEquals(120, completed.result.coinsAwarded)
        assertEquals(MockPaymentChannel.QRIS, completed.result.channel)
        assertNotNull(completed.result.qrStringPayload)
    }

    @Test
    fun testSimulateDanaPaymentSuccess() = runTest {
        val states = paymentService.simulateDanaPayment(
            phoneNumber = "081234567890",
            amountRupiah = 25000,
            coinsAwarded = 350,
            packageId = "pkg_25k",
            shouldFail = false
        ).toList()

        assertTrue(states.any { it is PaymentSimulationState.Initiating })
        val finalState = states.last()
        assertTrue("Final state must be Completed", finalState is PaymentSimulationState.Completed)
        val completed = finalState as PaymentSimulationState.Completed
        assertTrue(completed.result.isSuccess)
        assertEquals(MockPaymentChannel.DANA, completed.result.channel)
        assertEquals(25000, completed.result.amountRupiah)
        assertEquals(350, completed.result.coinsAwarded)
        assertNotNull(completed.result.deepLinkUrl)
    }

    @Test
    fun testSimulateDanaPaymentInvalidPhone() = runTest {
        val states = paymentService.simulateDanaPayment(
            phoneNumber = "invalid-phone",
            amountRupiah = 10000,
            coinsAwarded = 120,
            packageId = "pkg_10k"
        ).toList()

        val finalState = states.last()
        assertTrue("State must be Error on invalid phone", finalState is PaymentSimulationState.Error)
    }

    @Test
    fun testSimulateEWalletPaymentSuccess() = runTest {
        val states = paymentService.simulateEWalletPayment(
            channel = MockPaymentChannel.GOPAY,
            phoneNumber = "081987654321",
            amountRupiah = 50000,
            coinsAwarded = 800,
            packageId = "pkg_50k"
        ).toList()

        val finalState = states.last()
        assertTrue(finalState is PaymentSimulationState.Completed)
        val completed = finalState as PaymentSimulationState.Completed
        assertEquals(MockPaymentChannel.GOPAY, completed.result.channel)
        assertEquals(800, completed.result.coinsAwarded)
    }

    @Test
    fun testSimulatedFailure() = runTest {
        val request = PaymentSimulationRequest(
            channel = MockPaymentChannel.QRIS,
            packageId = "pkg_5k",
            amountRupiah = 5000,
            coinsAwarded = 50,
            shouldSimulateFailure = true
        )

        val states = paymentService.processPaymentRequest(request).toList()
        val finalState = states.last()
        assertTrue("State must be Error when shouldSimulateFailure is true", finalState is PaymentSimulationState.Error)
    }
}
