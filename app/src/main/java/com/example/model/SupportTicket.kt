package com.example.model

enum class TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}

data class TicketMessage(
    val senderName: String = "",
    val isAdmin: Boolean = false,
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class SupportTicket(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val subject: String = "",
    val category: String = "Withdrawal",
    val status: TicketStatus = TicketStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis(),
    val messages: List<TicketMessage> = emptyList()
)
