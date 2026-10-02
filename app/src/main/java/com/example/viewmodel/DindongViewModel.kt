package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.data.MissionEntity
import com.example.data.TopUpTransactionEntity
import com.example.data.WalletEntity
import com.example.model.CelebrationType
import com.example.model.DingdongSymbol
import com.example.model.DingdongSymbols
import com.example.model.GameTheme
import com.example.service.payment.MockPaymentChannel
import com.example.service.payment.MockPaymentSimulationServiceImpl
import com.example.service.payment.PaymentSimulationRequest
import com.example.service.payment.PaymentSimulationResult
import com.example.service.payment.PaymentSimulationService
import com.example.service.payment.PaymentSimulationState
import com.example.sound.ArcadeSoundManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val wallet: WalletEntity = WalletEntity(),
    val bets: Map<String, Int> = emptyMap(), // symbol.id -> coins bet
    val currentTileIndex: Int = 1, // matches screenshot (Pisang Mecha slot 1)
    val isSpinning: Boolean = false,
    val lastWin: Long = 0,
    val selectedChip: Int = 1,
    val selectedTheme: GameTheme = GameTheme.CYBER,
    val isSoundOn: Boolean = true,
    val mascotQuote: String = "\"RTP 75% bekerja dengan sempurna! Wkwkwk! 🙈\"",
    val isShaking: Boolean = false,
    val isBonusFrenzy: Boolean = false,
    val bonusSpinsRemaining: Int = 0,
    // Active Modals
    val isTopUpOpen: Boolean = false,
    val isDoubleUpOpen: Boolean = false,
    val isMissionsOpen: Boolean = false,
    val isGiftWheelOpen: Boolean = false,
    val isLoungeOpen: Boolean = false,
    // Double Up Game State
    val doubleUpPool: Long = 0,
    val doubleUpLastCard: Int? = null, // 1 to 13
    val doubleUpIsRed: Boolean? = null,
    val doubleUpResultSuccess: Boolean? = null,
    val doubleUpStreak: Int = 0,
    // Fullscreen Celebration Overlay
    val activeCelebration: CelebrationType? = null,
    // Top Up notifications
    val topUpSuccessMessage: String? = null
)

class DindongViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = GameRepository(application)
    val soundManager = ArcadeSoundManager()
    val paymentService: PaymentSimulationService = MockPaymentSimulationServiceImpl(repository)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _paymentSimulationState = MutableStateFlow<PaymentSimulationState>(PaymentSimulationState.Idle)
    val paymentSimulationState: StateFlow<PaymentSimulationState> = _paymentSimulationState.asStateFlow()

    val transactions: StateFlow<List<TopUpTransactionEntity>> = repository.transactionsFlow
        .let { flow ->
            val state = MutableStateFlow<List<TopUpTransactionEntity>>(emptyList())
            viewModelScope.launch {
                flow.collect { list -> state.value = list }
            }
            state.asStateFlow()
        }

    val missions: StateFlow<List<MissionEntity>> = repository.missionsFlow
        .let { flow ->
            val state = MutableStateFlow<List<MissionEntity>>(emptyList())
            viewModelScope.launch {
                flow.collect { list -> state.value = list }
            }
            state.asStateFlow()
        }

    private var spinJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
            repository.walletFlow.collect { wallet ->
                wallet?.let { w ->
                    soundManager.isSoundEnabled = w.isSoundOn
                    val theme = try {
                        GameTheme.valueOf(w.selectedTheme)
                    } catch (_: Exception) {
                        GameTheme.CYBER
                    }
                    _uiState.value = _uiState.value.copy(
                        wallet = w,
                        isSoundOn = w.isSoundOn,
                        selectedTheme = theme
                    )
                }
            }
        }
    }

    fun selectTheme(theme: GameTheme) {
        soundManager.playClick()
        val currentW = _uiState.value.wallet
        val updated = currentW.copy(selectedTheme = theme.name)
        _uiState.value = _uiState.value.copy(selectedTheme = theme, wallet = updated)
        viewModelScope.launch {
            repository.updateWallet(updated)
        }
    }

    fun toggleSound() {
        val newState = !_uiState.value.isSoundOn
        soundManager.isSoundEnabled = newState
        val currentW = _uiState.value.wallet.copy(isSoundOn = newState)
        _uiState.value = _uiState.value.copy(isSoundOn = newState, wallet = currentW)
        if (newState) soundManager.playClick()
        viewModelScope.launch {
            repository.updateWallet(currentW)
        }
    }

    fun setChip(value: Int) {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(selectedChip = value)
    }

    fun placeBet(symbolId: String) {
        if (_uiState.value.isSpinning) return

        val chip = _uiState.value.selectedChip
        val currentBets = _uiState.value.bets.toMutableMap()
        val currentTotalBet = currentBets.values.sum()
        val availableCoins = _uiState.value.wallet.coins

        if (currentTotalBet + chip > availableCoins) {
            // Not enough coins
            _uiState.value = _uiState.value.copy(
                mascotQuote = "\"Koinmu kurang bos! Ayo Top Up dulu biar lanjut! 🪙\""
            )
            soundManager.playDoubleUpLose()
            return
        }

        soundManager.playCoin()
        val currentBet = currentBets[symbolId] ?: 0
        currentBets[symbolId] = currentBet + chip
        _uiState.value = _uiState.value.copy(bets = currentBets)
    }

    fun clearBets() {
        if (_uiState.value.isSpinning) return
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(bets = emptyMap())
    }

    fun doubleAllBets() {
        if (_uiState.value.isSpinning) return
        val currentBets = _uiState.value.bets
        if (currentBets.isEmpty()) return

        val doubled = currentBets.mapValues { it.value * 2 }
        val newTotal = doubled.values.sum()
        if (newTotal > _uiState.value.wallet.coins) {
            _uiState.value = _uiState.value.copy(
                mascotQuote = "\"Koin tidak cukup untuk gandakan semua taruhan! 🙈\""
            )
            return
        }
        soundManager.playCoin()
        _uiState.value = _uiState.value.copy(bets = doubled)
    }

    fun maxBet() {
        if (_uiState.value.isSpinning) return
        val coins = _uiState.value.wallet.coins
        if (coins <= 0) return

        val symbolsToBet = listOf(
            DingdongSymbols.PISANG.id,
            DingdongSymbols.MECHA.id,
            DingdongSymbols.RAINBOW.id,
            DingdongSymbols.SNOW.id,
            DingdongSymbols.LIGHTNING.id
        )
        val perSymbol = (coins / symbolsToBet.size).toInt().coerceAtLeast(1)
        val newBets = symbolsToBet.associateWith { perSymbol }
        soundManager.playCoin()
        _uiState.value = _uiState.value.copy(bets = newBets)
    }

    fun spinWheel() {
        val state = _uiState.value
        if (state.isSpinning) return

        val totalBet = state.bets.values.sum()
        if (totalBet <= 0 && !state.isBonusFrenzy) {
            _uiState.value = state.copy(
                mascotQuote = "\"Pasang taruhanmu dulu bos! Klik simbol di bawah! 🍌\""
            )
            soundManager.playClick()
            return
        }

        if (totalBet > state.wallet.coins && !state.isBonusFrenzy) {
            _uiState.value = state.copy(
                mascotQuote = "\"Koin tidak mencukupi untuk taruhan ini! 🪙\""
            )
            return
        }

        // Deduct coins
        val remainingCoins = if (state.isBonusFrenzy) state.wallet.coins else state.wallet.coins - totalBet
        val updatedWallet = state.wallet.copy(
            coins = remainingCoins,
            totalSpins = state.wallet.totalSpins + 1,
            totalBet = state.wallet.totalBet + totalBet
        )

        // Determine destination tile index (0..19)
        val targetIndex = pickTargetIndex(state.wallet.rtpPercent)

        _uiState.value = state.copy(
            isSpinning = true,
            wallet = updatedWallet,
            lastWin = 0,
            mascotQuote = "\"Mesin berputar kencang! Semoga JP Pisang Fantasi! 🎰✨\""
        )

        spinJob?.cancel()
        spinJob = viewModelScope.launch {
            repository.updateWallet(updatedWallet)

            var currentIndex = _uiState.value.currentTileIndex
            val totalSlots = 20
            // Complete at least 2 full rotations plus offset to target
            val totalSteps = (totalSlots * 2) + ((targetIndex - currentIndex + totalSlots) % totalSlots)

            // Dynamic deceleration curve
            var delayMs = 35L
            for (step in 0 until totalSteps) {
                currentIndex = (currentIndex + 1) % totalSlots
                _uiState.value = _uiState.value.copy(currentTileIndex = currentIndex)
                soundManager.playTick(currentIndex)

                // Decelerate in the final 12 steps
                val remaining = totalSteps - step
                when {
                    remaining <= 3 -> delayMs = 380L
                    remaining <= 6 -> delayMs = 240L
                    remaining <= 9 -> delayMs = 140L
                    remaining <= 14 -> delayMs = 80L
                    step < 5 -> delayMs = 60L
                    else -> delayMs = 35L
                }
                delay(delayMs)
            }

            // Finished spin
            soundManager.playStop()
            delay(150)
            handleSpinResult(targetIndex, totalBet)
        }
    }

    private suspend fun handleSpinResult(targetIndex: Int, totalBet: Int) {
        val stoppedSymbol = DingdongSymbols.PERIMETER_SLOTS[targetIndex]
        val currentBets = _uiState.value.bets
        val betOnSymbol = currentBets[stoppedSymbol.id] ?: 0

        var winAmount = 0L
        var isBonusWon = false

        if (stoppedSymbol.isBonus) {
            isBonusWon = true
            // Bonus tile triggers instant jackpot coins + 3 free frenzy spins!
            winAmount = (totalBet.coerceAtLeast(5) * 10).toLong()
            soundManager.playJackpot()
        } else if (betOnSymbol > 0) {
            winAmount = (betOnSymbol * stoppedSymbol.multiplier).toLong()
            if (stoppedSymbol.multiplier >= 30 || winAmount >= 100) {
                soundManager.playJackpot()
            } else {
                soundManager.playWin()
            }
        } else {
            soundManager.playShakeThud()
        }

        val newCoins = _uiState.value.wallet.coins + winAmount
        val newHighestWin = maxOf(_uiState.value.wallet.highestWin, winAmount)
        val updatedWallet = _uiState.value.wallet.copy(
            coins = newCoins,
            totalWon = _uiState.value.wallet.totalWon + winAmount,
            highestWin = newHighestWin
        )

        val isJackpot = isBonusWon ||
                (betOnSymbol > 0 && stoppedSymbol.id == "dragon") ||
                (betOnSymbol > 0 && stoppedSymbol.id == "galaxy") ||
                winAmount >= 100

        val jackpotCelebration = if (isJackpot && winAmount > 0) {
            CelebrationType.Jackpot(
                winAmount = winAmount,
                title = when {
                    isBonusWon -> "💥 BONUS FANTASI PECAH! 💥"
                    stoppedSymbol.id == "dragon" -> "🐉 MEGA JACKPOT NAGA 50X! 🐉"
                    stoppedSymbol.id == "galaxy" -> "🌌 SUPER JACKPOT GALAKSI 30X! 🌌"
                    else -> "👑 JACKPOT SULTAN BESAR! 👑"
                },
                subtitle = when {
                    isBonusWon -> "Hadiah Instan Jackpot + 3 Putaran Frenzy Gratis!"
                    stoppedSymbol.id == "dragon" -> "Pukulan 50X Lipat Dari Sang Naga Emas!"
                    stoppedSymbol.id == "galaxy" -> "Ledakan Koin 30X Lipat Galaksi Bintang!"
                    else -> "Kemenangan Spektakuler Mesin Dindong!"
                },
                symbolEmoji = stoppedSymbol.emoji,
                symbolName = stoppedSymbol.name,
                multiplier = stoppedSymbol.multiplier
            )
        } else null

        val mascotQuote = when {
            isBonusWon -> "\"JACKPOT BONUS FANTASI PECAH!! GACOR PARAH! 🎁🔥\""
            winAmount > 500 -> "\"SULTAN BESAR! Menang ${winAmount} Koin! Luar biasa! 👑💎\""
            winAmount > 0 -> "\"HOKI BANGET! Menang ${winAmount} Koin dari ${stoppedSymbol.name}! 🍌🎉\""
            else -> "\"RTP ${_uiState.value.wallet.rtpPercent}% bekerja dengan sempurna! Wkwkwk! 🙈\""
        }

        _uiState.value = _uiState.value.copy(
            isSpinning = false,
            lastWin = winAmount,
            wallet = updatedWallet,
            mascotQuote = mascotQuote,
            isBonusFrenzy = isBonusWon,
            bonusSpinsRemaining = if (isBonusWon) 3 else 0,
            doubleUpPool = winAmount,
            activeCelebration = jackpotCelebration
        )

        repository.updateWallet(updatedWallet)
    }

    private fun pickTargetIndex(rtpPercent: Int): Int {
        // Dingdong probability distribution
        val rand = Random.nextInt(100)
        return when {
            // High jackpot / Dragon (x50) (Indices 8, 18)
            rand < 4 -> if (Random.nextBoolean()) 8 else 18
            // Galaxy (x30) (Indices 6, 15)
            rand < 9 -> if (Random.nextBoolean()) 6 else 15
            // Fire (x20) (Indices 5, 14)
            rand < 16 -> if (Random.nextBoolean()) 5 else 14
            // Lightning (x15) (Indices 4, 13)
            rand < 25 -> if (Random.nextBoolean()) 4 else 13
            // Snow (x10) (Indices 3, 12)
            rand < 37 -> if (Random.nextBoolean()) 3 else 12
            // Bonus (Indices 7, 16)
            rand < 45 -> if (Random.nextBoolean()) 7 else 16
            // Rainbow (x8) (Indices 2, 11, 19)
            rand < 62 -> listOf(2, 11, 19).random()
            // Robot Mecha (x5) (Indices 1, 10)
            rand < 78 -> if (Random.nextBoolean()) 1 else 10
            // Banana (x2) (Indices 0, 9, 17)
            else -> listOf(0, 9, 17).random()
        }
    }

    fun shakeMachine() {
        if (_uiState.value.isSpinning) return
        soundManager.playShakeThud()

        val funnyShakeQuotes = listOf(
            "\"WADUH JANGAN DIGOYANG BANG! RUSAK NANTI! 😂\"",
            "\"DIGOYANG TERUS BIAR KELUAR PETIRNYA! ⚡\"",
            "\"MANTAP! Sensor mesin bergetar! Hoki nambah! 🔥\"",
            "\"Aduh pusing kepalaku mas wkwkwk! 🙈\""
        )

        // 30% chance to nudge current tile by 1!
        val shouldNudge = Random.nextInt(10) < 3
        val nextTile = if (shouldNudge) (_uiState.value.currentTileIndex + 1) % 20 else _uiState.value.currentTileIndex

        _uiState.value = _uiState.value.copy(
            isShaking = true,
            currentTileIndex = nextTile,
            mascotQuote = funnyShakeQuotes.random()
        )

        viewModelScope.launch {
            delay(500)
            _uiState.value = _uiState.value.copy(isShaking = false)
        }
    }

    fun openDoubleUp() {
        if (_uiState.value.lastWin <= 0) {
            _uiState.value = _uiState.value.copy(
                mascotQuote = "\"Kamu harus menang dulu sebelum bisa Double Up! 🎲\""
            )
            return
        }
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(
            isDoubleUpOpen = true,
            doubleUpPool = _uiState.value.lastWin,
            doubleUpLastCard = null,
            doubleUpIsRed = null,
            doubleUpResultSuccess = null,
            doubleUpStreak = 0,
            activeCelebration = null
        )
    }

    fun closeDoubleUp() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isDoubleUpOpen = false, activeCelebration = null)
    }

    fun playDoubleUpGuess(guessRed: Boolean) {
        val currentPool = _uiState.value.doubleUpPool
        if (currentPool <= 0) return

        val cardVal = Random.nextInt(1, 14) // 1 to 13
        val cardIsRed = Random.nextBoolean() // Red or Black
        val won = (guessRed == cardIsRed)

        if (won) {
            soundManager.playJackpot()
            val newPool = currentPool * 2
            val updatedCoins = _uiState.value.wallet.coins + currentPool // add the doubled gain
            val updatedWallet = _uiState.value.wallet.copy(coins = updatedCoins)
            val nextStreak = _uiState.value.doubleUpStreak + 1

            val rankStr = when (cardVal) {
                1 -> "A"
                11 -> "J"
                12 -> "Q"
                13 -> "K"
                else -> "$cardVal"
            }
            val suitStr = if (cardIsRed) listOf("♥️", "♦️").random() else listOf("♠️", "♣️").random()

            val doubleUpCelebration = CelebrationType.DoubleUpSuccess(
                wonAmount = currentPool,
                newPool = newPool,
                cardRank = rankStr,
                cardSuit = suitStr,
                isRed = cardIsRed,
                roundStreak = nextStreak
            )

            _uiState.value = _uiState.value.copy(
                doubleUpPool = newPool,
                doubleUpLastCard = cardVal,
                doubleUpIsRed = cardIsRed,
                doubleUpResultSuccess = true,
                doubleUpStreak = nextStreak,
                wallet = updatedWallet,
                lastWin = newPool,
                activeCelebration = doubleUpCelebration,
                mascotQuote = "\"MENANG DOUBLE UP! Koinmu jadi $newPool! Mau lanjut? 🔥\""
            )
            viewModelScope.launch {
                repository.updateWallet(updatedWallet)
            }
        } else {
            soundManager.playDoubleUpLose()
            val deductedCoins = (_uiState.value.wallet.coins - currentPool).coerceAtLeast(0)
            val updatedWallet = _uiState.value.wallet.copy(coins = deductedCoins)
            _uiState.value = _uiState.value.copy(
                doubleUpPool = 0,
                doubleUpLastCard = cardVal,
                doubleUpIsRed = cardIsRed,
                doubleUpResultSuccess = false,
                doubleUpStreak = 0,
                wallet = updatedWallet,
                lastWin = 0,
                activeCelebration = null,
                mascotQuote = "\"Tebakanmu meleset! Wkwkwk coba lagi putaran depan! 🙈\""
            )
            viewModelScope.launch {
                repository.updateWallet(updatedWallet)
            }
        }
    }

    fun dismissCelebration() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(activeCelebration = null)
    }

    fun continueDoubleUp() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(
            activeCelebration = null,
            isDoubleUpOpen = true
        )
    }

    fun cashOutDoubleUp() {
        soundManager.playCoin()
        val pool = _uiState.value.doubleUpPool
        _uiState.value = _uiState.value.copy(
            activeCelebration = null,
            isDoubleUpOpen = false,
            mascotQuote = "\"Koin kemenangan $pool berhasil diamankan ke dompet! 🪙\""
        )
    }

    fun triggerDemoJackpot() {
        soundManager.playJackpot()
        _uiState.value = _uiState.value.copy(
            activeCelebration = CelebrationType.Jackpot(
                winAmount = 500L,
                title = "🐉 MEGA JACKPOT NAGA 50X! 🐉",
                subtitle = "Pukulan 50X Lipat Dari Sang Naga Emas!",
                symbolEmoji = "🐉",
                symbolName = "Naga Legendaris",
                multiplier = 50
            )
        )
    }

    fun triggerDemoDoubleUp() {
        soundManager.playJackpot()
        _uiState.value = _uiState.value.copy(
            isDoubleUpOpen = true,
            doubleUpPool = 200L,
            lastWin = 200L,
            activeCelebration = CelebrationType.DoubleUpSuccess(
                wonAmount = 100L,
                newPool = 200L,
                cardRank = "A",
                cardSuit = "♥️",
                isRed = true,
                roundStreak = 1
            )
        )
    }

    // Modal toggles
    fun openTopUp() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isTopUpOpen = true, topUpSuccessMessage = null)
    }

    fun closeTopUp() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isTopUpOpen = false)
    }

    fun openMissions() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isMissionsOpen = true)
    }

    fun closeMissions() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isMissionsOpen = false)
    }

    fun openGiftWheel() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isGiftWheelOpen = true)
    }

    fun closeGiftWheel() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isGiftWheelOpen = false)
    }

    fun openLounge() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isLoungeOpen = true)
    }

    fun closeLounge() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(isLoungeOpen = false)
    }

    fun setRtpPercent(rtp: Int) {
        val updated = _uiState.value.wallet.copy(rtpPercent = rtp)
        _uiState.value = _uiState.value.copy(wallet = updated)
        viewModelScope.launch {
            repository.updateWallet(updated)
        }
    }

    // Top up execution & Mock Gateway Simulations
    fun simulateQrisTopUp(amountRupiah: Int, coinsAwarded: Int, packageId: String, shouldFail: Boolean = false) {
        viewModelScope.launch {
            paymentService.simulateQrisPayment(amountRupiah, coinsAwarded, packageId, shouldFail)
                .collect { state ->
                    _paymentSimulationState.value = state
                    if (state is PaymentSimulationState.Completed) {
                        soundManager.playJackpot()
                        _uiState.value = _uiState.value.copy(
                            topUpSuccessMessage = "Top Up QRIS berhasil! +$coinsAwarded Koin ditambahkan ke dompet.",
                            mascotQuote = "\"MANTAP SULTAN! QRIS Berhasil! Gas putar lagi! 🚀💰\""
                        )
                    }
                }
        }
    }

    fun simulateDanaTopUp(phoneNumber: String, amountRupiah: Int, coinsAwarded: Int, packageId: String, shouldFail: Boolean = false) {
        viewModelScope.launch {
            paymentService.simulateDanaPayment(phoneNumber, amountRupiah, coinsAwarded, packageId, shouldFail)
                .collect { state ->
                    _paymentSimulationState.value = state
                    if (state is PaymentSimulationState.Completed) {
                        soundManager.playJackpot()
                        _uiState.value = _uiState.value.copy(
                            topUpSuccessMessage = "Top Up DANA ($phoneNumber) berhasil! +$coinsAwarded Koin ditambahkan.",
                            mascotQuote = "\"DOMPET DANA TERVERIFIKASI! +$coinsAwarded Koin masuk! 👛✨\""
                        )
                    }
                }
        }
    }

    fun simulateEWalletTopUp(channel: MockPaymentChannel, phoneNumber: String, amountRupiah: Int, coinsAwarded: Int, packageId: String, shouldFail: Boolean = false) {
        viewModelScope.launch {
            paymentService.simulateEWalletPayment(channel, phoneNumber, amountRupiah, coinsAwarded, packageId, shouldFail)
                .collect { state ->
                    _paymentSimulationState.value = state
                    if (state is PaymentSimulationState.Completed) {
                        soundManager.playJackpot()
                        _uiState.value = _uiState.value.copy(
                            topUpSuccessMessage = "Top Up ${channel.displayName} berhasil! +$coinsAwarded Koin ditambahkan.",
                            mascotQuote = "\"MANTAP! Pembayaran ${channel.displayName} Sukses! 🟢🪙\""
                        )
                    }
                }
        }
    }

    fun resetPaymentSimulationState() {
        _paymentSimulationState.value = PaymentSimulationState.Idle
    }

    fun performTopUp(method: String, rupiah: Int, coins: Int) {
        viewModelScope.launch {
            val transId = repository.recordTopUp(method, rupiah, coins)
            soundManager.playJackpot()
            _uiState.value = _uiState.value.copy(
                topUpSuccessMessage = "Top Up via $method berhasil! +$coins Koin ditambahkan ke saldo.",
                mascotQuote = "\"MANTAP SULTAN! Top Up Berhasil! Gas putar lagi! 🚀💰\""
            )
        }
    }

    fun redeemPromo(code: String): Boolean {
        val cleanCode = code.trim().uppercase()
        val reward = when (cleanCode) {
            "DINDONGGACOR" -> 100
            "PISANGEMAS" -> 50
            "SLOTBERKAH" -> 200
            "SULTANBARU" -> 250
            else -> 0
        }
        if (reward > 0) {
            soundManager.playWin()
            viewModelScope.launch {
                repository.recordTopUp("KODE PROMO: $cleanCode", 0, reward)
            }
            _uiState.value = _uiState.value.copy(
                topUpSuccessMessage = "Kode '$cleanCode' Valid! Selamat dapat +$reward Koin Gratis!",
                mascotQuote = "\"KODE PROMO BERKAH! +$reward Koin gratis masuk dompet! 🎉\""
            )
            return true
        } else {
            soundManager.playDoubleUpLose()
            return false
        }
    }

    fun claimMission(mission: MissionEntity) {
        if (mission.isClaimed || mission.currentProgress < mission.targetProgress) return
        soundManager.playWin()
        val updatedMission = mission.copy(isClaimed = true)
        viewModelScope.launch {
            repository.updateMission(updatedMission)
            repository.addCoins(mission.rewardCoins.toLong())
        }
    }

    fun claimDailyGift(coins: Int) {
        soundManager.playJackpot()
        viewModelScope.launch {
            repository.addCoins(coins.toLong())
        }
        _uiState.value = _uiState.value.copy(
            mascotQuote = "\"Selamat! Kado Harian +$coins Koin berhasil diklaim! 🎁\""
        )
    }
}
