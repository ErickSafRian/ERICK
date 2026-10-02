package com.example

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.DingdongSymbols
import com.example.model.GameTheme
import com.example.ui.components.BettingControls
import com.example.ui.components.DailyGiftWheelDialog
import com.example.ui.components.DindongBottomNav
import com.example.ui.components.DindongHeader
import com.example.ui.components.DindongTab
import com.example.ui.components.DindongTopBar
import com.example.ui.components.DoubleUpDialog
import com.example.ui.components.LoungeDialog
import com.example.ui.components.MissionsDialog
import com.example.ui.components.PerimeterMachineBoard
import com.example.ui.components.TopUpDialog
import com.example.ui.components.WinCelebrationOverlay
import com.example.ui.screens.LoungeScreen
import com.example.ui.screens.MissionsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.getDindongThemeColors
import com.example.viewmodel.DindongViewModel
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DindongAppScreen()
            }
        }
    }
}

@Composable
fun DindongAppScreen(viewModel: DindongViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val missions by viewModel.missions.collectAsState()
    val paymentSimulationState by viewModel.paymentSimulationState.collectAsState()
    val themeColors = getDindongThemeColors(uiState.selectedTheme)
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(DindongTab.ARCADE) }

    // Register Accelerometer for physical shake detection
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        var lastShakeTime = 0L
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]
                    val gForce = sqrt((x * x + y * y + z * z).toDouble()) / SensorManager.GRAVITY_EARTH
                    if (gForce > 2.7) {
                        val now = System.currentTimeMillis()
                        if (now - lastShakeTime > 1500) {
                            lastShakeTime = now
                            viewModel.shakeMachine()
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        accelerometer?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(sensorListener)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = themeColors.background,
        topBar = {
            DindongTopBar(
                coins = uiState.wallet.coins,
                selectedTheme = uiState.selectedTheme,
                isSoundOn = uiState.isSoundOn,
                themeColors = themeColors,
                onOpenTopUp = { selectedTab = DindongTab.WALLET },
                onToggleSound = { viewModel.toggleSound() },
                onCycleTheme = {
                    val themes = GameTheme.values()
                    val nextTheme = themes[(uiState.selectedTheme.ordinal + 1) % themes.size]
                    viewModel.selectTheme(nextTheme)
                }
            )
        },
        bottomBar = {
            DindongBottomNav(
                selectedTab = selectedTab,
                themeColors = themeColors,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(themeColors.background)
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 600.dp

            when (selectedTab) {
                DindongTab.ARCADE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 660.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                // Header with Theme Chips, Stats HUD, and Hero Title
                                DindongHeader(
                                    uiState = uiState,
                                    themeColors = themeColors,
                                    onSelectTheme = { viewModel.selectTheme(it) },
                                    onToggleSound = { viewModel.toggleSound() },
                                    onOpenMissions = { viewModel.openMissions() },
                                    onOpenGiftWheel = { viewModel.openGiftWheel() },
                                    onOpenLounge = { viewModel.openLounge() }
                                )

                                // The 20-Perimeter Dindong Fruit Machine Board
                                PerimeterMachineBoard(
                                    uiState = uiState,
                                    themeColors = themeColors,
                                    onPlaceBet = { viewModel.placeBet(it) },
                                    onShakeMachine = { viewModel.shakeMachine() },
                                    onClearBets = { viewModel.clearBets() },
                                    onOpenDoubleUp = { viewModel.openDoubleUp() },
                                    onOpenTopUp = { selectedTab = DindongTab.WALLET },
                                    onSpinWheel = { viewModel.spinWheel() }
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Bottom Betting Controls (Chips, symbols bet grid, 2x bet, max bet)
                                BettingControls(
                                    uiState = uiState,
                                    themeColors = themeColors,
                                    onSelectChip = { viewModel.setChip(it) },
                                    onPlaceBet = { viewModel.placeBet(it) },
                                    onDoubleAllBets = { viewModel.doubleAllBets() },
                                    onMaxBet = { viewModel.maxBet() },
                                    onClearBets = { viewModel.clearBets() },
                                    modifier = Modifier.widthIn(max = 620.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }

                DindongTab.WALLET -> {
                    // Dedicated In-Page Wallet Screen (Never blocks bottom navigation)
                    WalletScreen(
                        themeColors = themeColors,
                        transactions = transactions,
                        onConfirmTopUp = { method, rupiah, coins ->
                            viewModel.performTopUp(method, rupiah, coins)
                        },
                        onRedeemPromo = { viewModel.redeemPromo(it) },
                        successMessage = uiState.topUpSuccessMessage,
                        paymentSimulationState = paymentSimulationState,
                        onSimulateQris = { rupiah, coins, pkgId, fail ->
                            viewModel.simulateQrisTopUp(rupiah, coins, pkgId, fail)
                        },
                        onSimulateDana = { phone, rupiah, coins, pkgId, fail ->
                            viewModel.simulateDanaTopUp(phone, rupiah, coins, pkgId, fail)
                        },
                        onSimulateEWallet = { channel, phone, rupiah, coins, pkgId, fail ->
                            viewModel.simulateEWalletTopUp(channel, phone, rupiah, coins, pkgId, fail)
                        },
                        onDismissSimulation = { viewModel.resetPaymentSimulationState() }
                    )
                }

                DindongTab.MISSIONS -> {
                    MissionsScreen(
                        missions = missions,
                        themeColors = themeColors,
                        onClaimMission = { viewModel.claimMission(it) },
                        onOpenGiftWheel = { viewModel.openGiftWheel() }
                    )
                }

                DindongTab.LOUNGE -> {
                    LoungeScreen(
                        currentRtp = uiState.wallet.rtpPercent,
                        themeColors = themeColors,
                        onSetRtp = { viewModel.setRtpPercent(it) },
                        onTestJackpot = { viewModel.triggerDemoJackpot() },
                        onTestDoubleUp = { viewModel.triggerDemoDoubleUp() }
                    )
                }
            }

            // MODALS & DIALOGS
            // Double Up (2x) Mini-Game Dialog
            DoubleUpDialog(
                isOpen = uiState.isDoubleUpOpen,
                currentPool = uiState.doubleUpPool,
                lastCard = uiState.doubleUpLastCard,
                cardIsRed = uiState.doubleUpIsRed,
                resultSuccess = uiState.doubleUpResultSuccess,
                themeColors = themeColors,
                onGuess = { viewModel.playDoubleUpGuess(it) },
                onCashOut = { viewModel.closeDoubleUp() },
                onClose = { viewModel.closeDoubleUp() }
            )

            // Missions / Achievements Dialog
            MissionsDialog(
                isOpen = uiState.isMissionsOpen,
                missions = missions,
                themeColors = themeColors,
                onClaimMission = { viewModel.claimMission(it) },
                onClose = { viewModel.closeMissions() }
            )

            // Daily Gift & Lucky Wheel Dialog
            DailyGiftWheelDialog(
                isOpen = uiState.isGiftWheelOpen,
                themeColors = themeColors,
                onClaimGift = { viewModel.claimDailyGift(it) },
                onClose = { viewModel.closeGiftWheel() }
            )

            // Casino Lounge & Paytable Dialog
            LoungeDialog(
                isOpen = uiState.isLoungeOpen,
                currentRtp = uiState.wallet.rtpPercent,
                themeColors = themeColors,
                onSetRtp = { viewModel.setRtpPercent(it) },
                onTestJackpot = { viewModel.triggerDemoJackpot() },
                onTestDoubleUp = { viewModel.triggerDemoDoubleUp() },
                onClose = { viewModel.closeLounge() }
            )

            // Top Up Modal Dialog (when triggered from buttons outside wallet tab)
            TopUpDialog(
                isOpen = uiState.isTopUpOpen,
                themeColors = themeColors,
                transactions = transactions,
                onClose = { viewModel.closeTopUp() },
                onConfirmTopUp = { method, rupiah, coins ->
                    viewModel.performTopUp(method, rupiah, coins)
                },
                onRedeemPromo = { viewModel.redeemPromo(it) },
                successMessage = uiState.topUpSuccessMessage,
                paymentSimulationState = paymentSimulationState,
                onSimulateQris = { rupiah, coins, pkgId, fail ->
                    viewModel.simulateQrisTopUp(rupiah, coins, pkgId, fail)
                },
                onSimulateDana = { phone, rupiah, coins, pkgId, fail ->
                    viewModel.simulateDanaTopUp(phone, rupiah, coins, pkgId, fail)
                },
                onSimulateEWallet = { channel, phone, rupiah, coins, pkgId, fail ->
                    viewModel.simulateEWalletTopUp(channel, phone, rupiah, coins, pkgId, fail)
                },
                onDismissSimulation = { viewModel.resetPaymentSimulationState() }
            )

            // Screen Celebration Overlay (Jackpot or Double Up Success)
            uiState.activeCelebration?.let { celebration ->
                WinCelebrationOverlay(
                    celebration = celebration,
                    onDismiss = { viewModel.dismissCelebration() },
                    onContinueDoubleUp = { viewModel.continueDoubleUp() },
                    onCashOutDoubleUp = { viewModel.cashOutDoubleUp() }
                )
            }
        }
    }
}
