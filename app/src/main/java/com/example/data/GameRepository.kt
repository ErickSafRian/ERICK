package com.example.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class GameRepository(context: Context) {
    private val database: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "dindong_pisang.db"
    ).addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                initDefaultData()
            }
        }
    }).build()

    private val dao = database.dingdongDao()

    val walletFlow: Flow<WalletEntity?> = dao.getWallet()
    val transactionsFlow: Flow<List<TopUpTransactionEntity>> = dao.getAllTransactions()
    val missionsFlow: Flow<List<MissionEntity>> = dao.getAllMissions()

    suspend fun ensureInitialized() {
        if (dao.getWalletSync() == null) {
            initDefaultData()
        }
    }

    private suspend fun initDefaultData() {
        dao.insertWallet(
            WalletEntity(
                id = 1,
                coins = 50, // initial starter coins
                totalSpins = 0,
                totalWon = 0,
                totalBet = 0,
                highestWin = 0,
                selectedTheme = "CYBER",
                isSoundOn = true,
                rtpPercent = 75,
                lastDailyClaimTimestamp = 0L
            )
        )

        dao.insertMissions(
            listOf(
                MissionEntity(
                    id = "spin_5",
                    title = "Putar Mesin 5 Kali",
                    description = "Pasang taruhan dan putar mesin sebanyak 5 kali",
                    currentProgress = 1,
                    targetProgress = 5,
                    rewardCoins = 30
                ),
                MissionEntity(
                    id = "win_petir",
                    title = "Raih Petir x15",
                    description = "Menangkan taruhan pada simbol Petir atau lebih tinggi",
                    currentProgress = 0,
                    targetProgress = 1,
                    rewardCoins = 100
                ),
                MissionEntity(
                    id = "shake_machine",
                    title = "Guncang Mesin 3x",
                    description = "Tekan tombol Guncang Mesin atau goyang ponselmu",
                    currentProgress = 0,
                    targetProgress = 3,
                    rewardCoins = 25
                ),
                MissionEntity(
                    id = "first_topup",
                    title = "Top Up Pertama",
                    description = "Lakukan pengisian koin via QRIS atau DANA",
                    currentProgress = 0,
                    targetProgress = 1,
                    rewardCoins = 150
                ),
                MissionEntity(
                    id = "double_up_win",
                    title = "Menang Double Up (2x)",
                    description = "Berhasil gandakan kemenangan di mode tebak kartu",
                    currentProgress = 0,
                    targetProgress = 1,
                    rewardCoins = 75
                )
            )
        )
    }

    suspend fun updateWallet(wallet: WalletEntity) {
        dao.updateWallet(wallet)
    }

    suspend fun addCoins(coins: Long) {
        dao.addCoins(coins)
    }

    suspend fun recordTopUp(method: String, rupiah: Int, coins: Int): String {
        val transCode = "TOP-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date()) + "-" + (100..999).random()
        dao.insertTransaction(
            TopUpTransactionEntity(
                transCode = transCode,
                method = method,
                rupiahAmount = rupiah,
                coinsAdded = coins,
                timestamp = System.currentTimeMillis(),
                status = "BERHASIL"
            )
        )
        dao.addCoins(coins.toLong())

        // update mission
        updateMissionProgress("first_topup", 1)
        return transCode
    }

    suspend fun updateMissionProgress(missionId: String, increment: Int = 1) {
        // fetch current
        val currentMissions = dao.getAllMissions()
        // we can get the sync list or let UI trigger
    }

    suspend fun updateMission(mission: MissionEntity) {
        dao.updateMission(mission)
    }
}
