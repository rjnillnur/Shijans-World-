package com.example.model

enum class WithdrawalStatus {
    PENDING,
    PROCESSING,
    APPROVED,
    PAID,
    REJECTED,
    FAILED,
    CANCELLED
}

data class WithdrawalRequest(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val paymentMethod: String = "bKash",
    val accountNumber: String = "",
    val pointsDeducted: Long = 0L,
    val amountCurrency: Double = 0.0,
    val currencySymbol: String = "৳",
    val status: WithdrawalStatus = WithdrawalStatus.PENDING,
    val adminPaymentRef: String = "",
    val adminNote: String = "",
    val adminId: String = "",
    val requestedAt: Long = System.currentTimeMillis(),
    val processedAt: Long? = null
)
