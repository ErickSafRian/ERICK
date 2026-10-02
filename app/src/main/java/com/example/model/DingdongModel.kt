package com.example.model

import androidx.compose.ui.graphics.Color

enum class GameTheme(val displayName: String, val chipLabel: String) {
    CYBER("Cyber", "Cyber"),
    RETRO_80S("Retro 80s", "Retro 80s"),
    JUNGLE("Jungle", "Jungle"),
    GOLD("Gold", "Gold")
}

data class DingdongSymbol(
    val id: String,
    val name: String,
    val multiplier: Int,
    val emoji: String,
    val colorHex: Long,
    val isBonus: Boolean = false
)

object DingdongSymbols {
    val PISANG = DingdongSymbol("banana", "Pisang", 2, "🍌", 0xFFFFD600)
    val MECHA = DingdongSymbol("mecha", "Pisang Mecha", 5, "🤖", 0xFF00E5FF)
    val RAINBOW = DingdongSymbol("rainbow", "Pelangi", 8, "🌈", 0xFFFF4081)
    val SNOW = DingdongSymbol("snow", "Salju", 10, "❄️", 0xFF80D8FF)
    val LIGHTNING = DingdongSymbol("lightning", "Petir", 15, "⚡", 0xFFFFEA00)
    val FIRE = DingdongSymbol("fire", "Api Membara", 20, "🔥", 0xFFFF6D00)
    val GALAXY = DingdongSymbol("galaxy", "Bintang Galaksi", 30, "🌌", 0xFF7C4DFF)
    val DRAGON = DingdongSymbol("dragon", "Naga Legendaris", 50, "🐉", 0xFF00E676)
    val BONUS = DingdongSymbol("bonus", "BONUS FANTASI", 0, "🎁", 0xFFFF1744, isBonus = true)

    val ALL_SYMBOLS = listOf(
        PISANG, MECHA, RAINBOW, SNOW, LIGHTNING, FIRE, GALAXY, DRAGON, BONUS
    )

    // 20 perimeter slots ordered clockwise starting top-left (0 to 19):
    // Top row (6): Pisang(0), Mecha(1), Rainbow(2), Snow(3), Lightning(4), Fire(5)
    // Right col (4): Galaxy(6), Bonus(7), Dragon(8), Pisang(9)
    // Bottom row right to left (6): Mecha(10), Rainbow(11), Snow(12), Lightning(13), Fire(14), Galaxy(15)
    // Left col bottom to top (4): Bonus(16), Pisang(17), Dragon(18), Rainbow(19)
    val PERIMETER_SLOTS = listOf(
        // Top Row: Indices 0 to 5
        PISANG,      // 0
        MECHA,       // 1
        RAINBOW,     // 2
        SNOW,        // 3
        LIGHTNING,   // 4
        FIRE,        // 5

        // Right Column: Indices 6 to 9
        GALAXY,      // 6
        BONUS,       // 7
        DRAGON,      // 8
        PISANG,      // 9

        // Bottom Row (Right-to-Left): Indices 10 to 15
        MECHA,       // 10
        RAINBOW,     // 11
        SNOW,        // 12
        LIGHTNING,   // 13
        FIRE,        // 14
        GALAXY,      // 15

        // Left Column (Bottom-to-Top): Indices 16 to 19
        BONUS,       // 16
        PISANG,      // 17
        DRAGON,      // 18
        RAINBOW      // 19
    )
}

enum class PaymentType(val title: String, val icon: String, val badge: String) {
    QRIS("QRIS Instan", "📱", "OTOMATIS"),
    DANA("DANA", "👛", "POPULER"),
    E_WALLET("E-Wallet Lain", "💳", "GOPAY/OVO"),
    BANK_VA("Virtual Account", "🏦", "BCA/BRI"),
    PROMO_CODE("Kode Promo", "🎟️", "GRATIS")
}

data class TopUpPackage(
    val id: String,
    val priceRupiah: Int,
    val coins: Int,
    val bonusCoins: Int = 0,
    val tag: String? = null
)

sealed class CelebrationType {
    data class Jackpot(
        val winAmount: Long,
        val title: String,
        val subtitle: String,
        val symbolEmoji: String,
        val symbolName: String,
        val multiplier: Int = 0
    ) : CelebrationType()

    data class DoubleUpSuccess(
        val wonAmount: Long,
        val newPool: Long,
        val cardRank: String,
        val cardSuit: String,
        val isRed: Boolean,
        val roundStreak: Int = 1
    ) : CelebrationType()
}

