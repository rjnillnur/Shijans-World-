package com.example.model

enum class NotificationType {
    GLOBAL,
    USER_SPECIFIC,
    TASK,
    WITHDRAWAL,
    PROMOTIONAL,
    SYSTEM
}

data class AppNotification(
    val id: String = "",
    val userId: String = "", // empty for global
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val actionUrl: String = ""
)
