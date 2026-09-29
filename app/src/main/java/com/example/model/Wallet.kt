package com.example.model

enum class TransactionType {
    WELCOME_BONUS,
    DAILY_BONUS,
    DAILY_REWARD,
    AD_REWARD,
    TASK_REWARD,
    GAME_REWARD,
    REFERRAL_REWARD,
    ACHIEVEMENT_REWARD,
    ADMIN_ADJUSTMENT,
    WITHDRAWAL,
    REFUND,
    PENALTY
}

enum class TransactionStatus {
    COMPLETED,
    PENDING,
    PROCESSING,
    REJECTED,
    FAILED
}

data class TransactionRecord(
    val id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.AD_REWARD,
    val amountPoints: Long = 0L,
    val amountCurrency: Double = 0.0,
    val previousBalance: Long = 0L,
    val newBalance: Long = 0L,
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val title: String = "",
    val description: String = "",
    val referenceId: String = "",
    val adminId: String = "",
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class WalletInfo(
    val userId: String = "",
    val points: Long = 0L,
    val pendingPoints: Long = 0L,
    val withdrawableBalance: Double = 0.0,
    val totalEarnedPoints: Long = 0L,
    val totalWithdrawnAmount: Double = 0.0,
    val pendingWithdrawalAmount: Double = 0.0
)
