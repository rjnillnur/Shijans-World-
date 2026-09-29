package com.example.model

enum class UserRole {
    SUPER_ADMIN,
    FINANCE_ADMIN,
    VERIFICATION_ADMIN,
    GAME_ADMIN,
    ADS_ADMIN,
    TASK_ADMIN,
    SUPPORT_ADMIN,
    CONTENT_ADMIN,
    ANALYTICS_ADMIN,
    ADMIN,
    USER
}

enum class AdminPermission {
    VIEW,
    CREATE,
    EDIT,
    DELETE,
    APPROVE,
    REJECT,
    EXPORT,
    MANAGE
}

data class AdminAccount(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.SUPER_ADMIN,
    val permissions: List<AdminPermission> = AdminPermission.values().toList(),
    val status: String = "ACTIVE",
    val createdAt: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis()
)

enum class VerificationStatus {
    UNVERIFIED,
    PENDING,
    VERIFIED,
    REJECTED,
    SUSPENDED
}

enum class AccountStatus {
    ACTIVE,
    PENDING,
    SUSPENDED,
    BANNED,
    DEACTIVATED
}

data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profilePicture: String = "",
    val referralCode: String = "",
    val referredBy: String = "",
    val role: UserRole = UserRole.USER,
    val verificationStatus: VerificationStatus = VerificationStatus.UNVERIFIED,
    val accountStatus: AccountStatus = AccountStatus.ACTIVE,
    val isBanned: Boolean = false,
    val banReason: String = "",
    val bannedAt: Long? = null,
    val joinDate: Long = System.currentTimeMillis(),
    val lastLogin: Long = System.currentTimeMillis(),
    val lastActive: Long = System.currentTimeMillis(),
    val level: Int = 1,
    val xp: Long = 120L,
    val dailyStreak: Int = 3,
    val bio: String = "Adventurer in the Magical Realm",
    val points: Long = 100L, // Welcome bonus
    val adsWatchedCount: Int = 0,
    val adsWatchedToday: Int = 0,
    val lastAdWatchedTime: Long = 0L,
    val tasksCompletedCount: Int = 0,
    val gamesPlayedCount: Int = 0,
    val totalReferralsCount: Int = 0,
    val totalEarnedPoints: Long = 100L,
    val totalWithdrawnAmount: Double = 0.0,
    val dailyBonusClaimedDate: String = "",
    val deviceModel: String = "Pixel 8 Pro (Android 15)",
    val ipAddress: String = "103.145.72.18",
    val fraudRiskScore: Int = 0 // 0-100 scale
)

enum class FraudStatus {
    FLAGGED,
    UNDER_REVIEW,
    CLEARED,
    ACTION_TAKEN
}

data class FraudAlert(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val reason: String = "",
    val evidence: String = "",
    val riskLevel: String = "MEDIUM", // LOW, MEDIUM, HIGH, CRITICAL
    val status: FraudStatus = FraudStatus.FLAGGED,
    val detectedAt: Long = System.currentTimeMillis(),
    val actionTaken: String = ""
)

data class DeviceSession(
    val id: String = "",
    val userId: String = "",
    val deviceName: String = "",
    val osVersion: String = "",
    val ipAddress: String = "",
    val lastActive: Long = System.currentTimeMillis(),
    val isCurrent: Boolean = false,
    val isSuspicious: Boolean = false
)

data class LeaderboardItem(
    val rank: Int = 1,
    val userId: String = "",
    val username: String = "",
    val fullName: String = "",
    val points: Long = 0L,
    val level: Int = 1,
    val badge: String = "Grandmaster"
)

data class AchievementItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "trophy",
    val rewardPoints: Long = 50L,
    val isUnlocked: Boolean = false,
    val progress: Float = 1f
)
