package com.example.model

enum class GameType {
    HIGHWAY_RACING,
    BUS_DRIVER,
    TRAFFIC_CHALLENGE,
    PARKING_MASTER,
    TIME_TRIAL,
    ENDLESS_DRIVE,
    COIN_RUNNER,
    CRYSTAL_CAR_RUSH,
    MAGIC_GEM_TAPPER,
    FORTUNE_SPIN,
    CASTLE_RUNNER,
    PUZZLE_MATCH
}

data class GameItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: GameType = GameType.CRYSTAL_CAR_RUSH,
    val rewardPoints: Long = 35L,
    val dailyLimit: Int = 10,
    val playsToday: Int = 0,
    val isEnabled: Boolean = true,
    val difficulty: String = "Medium", // Easy, Medium, Hard, Extreme
    val interstitialAfterPlays: Int = 2,
    val category: String = "Arcade"
)

data class GameSession(
    val sessionId: String = "",
    val gameId: String = "",
    val userId: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val score: Int = 0,
    val pointsAwarded: Long = 0L,
    val isCompleted: Boolean = false,
    val validationHash: String = ""
)
