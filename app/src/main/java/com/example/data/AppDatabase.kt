package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = 1,
    val coins: Long = 100,
    val totalSpins: Int = 0,
    val totalWon: Long = 0,
    val totalBet: Long = 0,
    val highestWin: Long = 0,
    val selectedTheme: String = "CYBER",
    val isSoundOn: Boolean = true,
    val rtpPercent: Int = 75,
    val lastDailyClaimTimestamp: Long = 0
)

@Entity(tableName = "transactions")
data class TopUpTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transCode: String,
    val method: String,
    val rupiahAmount: Int,
    val coinsAdded: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "BERHASIL"
)

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val rewardCoins: Int,
    val isClaimed: Boolean = false
)

@Dao
interface DingdongDao {
    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    fun getWallet(): Flow<WalletEntity?>

    @Query("SELECT * FROM wallet WHERE id = 1 LIMIT 1")
    suspend fun getWalletSync(): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: WalletEntity)

    @Update
    suspend fun updateWallet(wallet: WalletEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TopUpTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TopUpTransactionEntity)

    @Query("SELECT * FROM missions")
    fun getAllMissions(): Flow<List<MissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<MissionEntity>)

    @Update
    suspend fun updateMission(mission: MissionEntity)

    @Query("UPDATE wallet SET coins = coins + :addedCoins WHERE id = 1")
    suspend fun addCoins(addedCoins: Long)
}

@Database(
    entities = [WalletEntity::class, TopUpTransactionEntity::class, MissionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dingdongDao(): DingdongDao
}
