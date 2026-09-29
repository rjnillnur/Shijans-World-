package com.example.model

enum class TaskType {
    VISIT_WEBSITE,
    WATCH_CONTENT,
    APP_DOWNLOAD,
    DAILY_QUEST,
    CUSTOM_TASK,
    SURVEY
}

enum class TaskStatus {
    AVAILABLE,
    STARTED,
    SUBMITTED,
    PENDING,
    APPROVED,
    REJECTED,
    COMPLETED
}

data class TaskItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val pointsReward: Long = 50L,
    val iconName: String = "quest",
    val targetUrl: String = "",
    val type: TaskType = TaskType.VISIT_WEBSITE,
    val verificationInstructions: String = "Complete the quest and submit verification proof or press complete.",
    val isPaused: Boolean = false,
    val dailyLimit: Int = 1,
    val userLimit: Int = 1,
    val completionsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class TaskSubmission(
    val id: String = "",
    val taskId: String = "",
    val taskTitle: String = "",
    val userId: String = "",
    val username: String = "",
    val proofText: String = "",
    val rewardPoints: Long = 0L,
    val status: TaskStatus = TaskStatus.PENDING,
    val adminNote: String = "",
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)
