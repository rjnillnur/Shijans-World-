package com.example.model

data class EmergencyControls(
    val isEmergencyShutdown: Boolean = false,
    val disableRewards: Boolean = false,
    val disableWithdrawals: Boolean = false,
    val disableAds: Boolean = false,
    val disableGames: Boolean = false,
    val disableTasks: Boolean = false,
    val disableReferrals: Boolean = false,
    val disableRegistrations: Boolean = false
)

data class HomeSectionConfig(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val subtitle: String = "",
    val isEnabled: Boolean = true,
    val orderIndex: Int = 0,
    val layoutStyle: String = "DEFAULT", // DEFAULT, GRID, CAROUSEL, LIST, COMPACT
    val customActionUrl: String = ""
)

data class ThemeConfig(
    val activeTheme: String = "Neon Fantasy",
    val primaryColorHex: String = "#38BDF8",
    val secondaryColorHex: String = "#A855F7",
    val accentGoldHex: String = "#FBBF24",
    val backgroundDarkHex: String = "#090D1E",
    val isDarkMode: Boolean = true
)

data class SystemHealthMetrics(
    val firebaseAuthStatus: String = "OPERATIONAL",
    val firestoreStatus: String = "OPERATIONAL",
    val firebaseStorageStatus: String = "OPERATIONAL",
    val adMobStatus: String = "CONNECTED",
    val adsterraStatus: String = "ACTIVE",
    val activeWebSocketConnections: Int = 142,
    val averageLatencyMs: Long = 48L,
    val uptimePercent: Double = 99.98,
    val lastErrorLog: String = "None. All realm security services reporting nominal."
)

data class CustomizationSettings(
    val appName: String = "Magical World",
    val appTagline: String = "Explore the Lands • Earn • Play • Complete Quests",
    val logoUrl: String = "",
    val bannerUrl: String = "",
    val announcementText: String = "🌟 Welcome Adventurers! Double rewards active on Quest Board this week! ✨",
    val supportEmail: String = "support@magicalworld.game",
    val supportPhone: String = "+880 1800-MAGICW",
    val telegramChannel: String = "https://t.me/magicalworldgame",
    val discordCommunity: String = "https://discord.gg/magicalworld",
    val privacyPolicyText: String = "Magical World values your privacy. User data is encrypted and protected by strict realm policies.",
    val termsOfServiceText: String = "Fair play is strictly enforced. Automated bots, multiple accounts, and fraudulent activity are prohibited."
)

data class SystemSettings(
    val isMaintenanceMode: Boolean = false,
    val isRegistrationEnabled: Boolean = true,
    val isLoginEnabled: Boolean = true,
    val isTasksEnabled: Boolean = true,
    val isGamesEnabled: Boolean = true,
    val isWithdrawalsEnabled: Boolean = true,
    val isReferralEnabled: Boolean = true,
    val isAdsEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    // Rewards
    val dailyLoginBonusPoints: Long = 50L,
    val referralBonusPoints: Long = 100L,
    val referredUserBonusPoints: Long = 50L,
    // Currency conversion: 100 points = 1.0 currency unit (BDT / USD)
    val pointsPerCurrencyUnit: Long = 100L,
    val minWithdrawalAmount: Double = 50.0,
    val maxWithdrawalAmount: Double = 5000.0,
    val dailyWithdrawalLimit: Double = 10000.0,
    val currencySymbol: String = "৳"
)

data class AuditLog(
    val id: String = "",
    val adminId: String = "",
    val adminRole: String = "SUPER_ADMIN",
    val adminName: String = "",
    val action: String = "",
    val targetId: String = "",
    val details: String = "",
    val reason: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
