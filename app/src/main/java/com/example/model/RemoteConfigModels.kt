package com.example.model

/**
 * Remote Configuration Models for Magical World
 * Enables remote control of content, gateways, game rewards, and APK version management.
 */

data class AppVersionInfo(
    val currentVersionCode: Int = 1,
    val currentVersionName: String = "1.0.0",
    val latestVersionCode: Int = 1,
    val latestVersionName: String = "1.0.0",
    val minSupportedVersionCode: Int = 1,
    val forceUpdate: Boolean = false,
    val downloadUrl: String = "https://play.google.com/store/apps/details?id=com.aistudio.magicalworld.qptxz",
    val releaseNotes: String = "• Welcome to Magical World!\n• Crystal Car Rush and Gem Tapper arcade arenas\n• Daily quest rewards and instant payouts\n• Remote configuration engine activated",
    val updateTitle: String = "Magical Realm Update Available! ✨",
    val updatedAt: Long = System.currentTimeMillis()
)

data class PaymentGatewayConfig(
    val id: String = "bkash",
    val name: String = "bKash",
    val isEnabled: Boolean = true,
    val minWithdrawal: Double = 50.0,
    val maxWithdrawal: Double = 5000.0,
    val feePercent: Double = 0.0,
    val accountPlaceholder: String = "01XXXXXXXXX",
    val instructionNote: String = "Enter personal bKash mobile number.",
    val badge: String = "Fast",
    val currencyCode: String = "BDT"
)

data class WithdrawalRemoteSettings(
    val pointsPerCurrencyUnit: Long = 100L,
    val currencySymbol: String = "৳",
    val dailyWithdrawalLimit: Double = 10000.0,
    val processingTimeText: String = "Payouts processed within 1 - 24 hours",
    val gateways: List<PaymentGatewayConfig> = listOf(
        PaymentGatewayConfig(
            id = "bkash",
            name = "bKash",
            isEnabled = true,
            minWithdrawal = 50.0,
            maxWithdrawal = 5000.0,
            feePercent = 0.0,
            accountPlaceholder = "01XXXXXXXXX",
            instructionNote = "Personal bKash mobile account number",
            badge = "Instant",
            currencyCode = "BDT"
        ),
        PaymentGatewayConfig(
            id = "nagad",
            name = "Nagad",
            isEnabled = true,
            minWithdrawal = 50.0,
            maxWithdrawal = 5000.0,
            feePercent = 0.0,
            accountPlaceholder = "01XXXXXXXXX",
            instructionNote = "Personal Nagad mobile number",
            badge = "Fast",
            currencyCode = "BDT"
        ),
        PaymentGatewayConfig(
            id = "rocket",
            name = "Rocket (DBBL)",
            isEnabled = true,
            minWithdrawal = 50.0,
            maxWithdrawal = 3000.0,
            feePercent = 1.0,
            accountPlaceholder = "01XXXXXXXXX-X (12 digits)",
            instructionNote = "Rocket account with check digit",
            badge = "Low Fee",
            currencyCode = "BDT"
        ),
        PaymentGatewayConfig(
            id = "upay",
            name = "Upay",
            isEnabled = true,
            minWithdrawal = 50.0,
            maxWithdrawal = 3000.0,
            feePercent = 0.0,
            accountPlaceholder = "01XXXXXXXXX",
            instructionNote = "Upay registered wallet number",
            badge = "Zero Fee",
            currencyCode = "BDT"
        ),
        PaymentGatewayConfig(
            id = "binance",
            name = "Binance (USDT)",
            isEnabled = true,
            minWithdrawal = 100.0,
            maxWithdrawal = 10000.0,
            feePercent = 0.5,
            accountPlaceholder = "Binance Pay ID / USDT TRC20 Address",
            instructionNote = "Global crypto payout in USDT",
            badge = "Global",
            currencyCode = "USDT"
        ),
        PaymentGatewayConfig(
            id = "upi",
            name = "UPI (India)",
            isEnabled = true,
            minWithdrawal = 50.0,
            maxWithdrawal = 5000.0,
            feePercent = 0.0,
            accountPlaceholder = "username@okhdfcbank / PhonePe UPI ID",
            instructionNote = "Instant UPI VPA address",
            badge = "Fast",
            currencyCode = "INR"
        ),
        PaymentGatewayConfig(
            id = "bank",
            name = "Bank Transfer",
            isEnabled = true,
            minWithdrawal = 500.0,
            maxWithdrawal = 25000.0,
            feePercent = 2.0,
            accountPlaceholder = "Account No, Bank Name, Branch, Routing",
            instructionNote = "Direct electronic wire to local bank",
            badge = "High Limits",
            currencyCode = "BDT"
        )
    )
)

data class BannerItem(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val actionType: String = "NAVIGATE_EARN", // NAVIGATE_EARN, NAVIGATE_GAMES, NAVIGATE_TASKS, NAVIGATE_REFERRAL, EXTERNAL_URL
    val actionTarget: String = "",
    val isEnabled: Boolean = true,
    val orderIndex: Int = 0
)

data class MaintenanceConfig(
    val isMaintenanceMode: Boolean = false,
    val title: String = "Magical World Realm Maintenance",
    val message: String = "The Grand Archmages are tuning the crystal servers and enhancing security runes. We will return shortly with higher rewards!",
    val estimatedEndTime: String = "1 - 2 hours",
    val supportEmail: String = "support@magicalworld.game",
    val allowAdminBypass: Boolean = true
)

data class FeatureFlags(
    val enableGames: Boolean = true,
    val enableTasks: Boolean = true,
    val enableAds: Boolean = true,
    val enableWithdrawals: Boolean = true,
    val enableReferrals: Boolean = true,
    val enableRegistration: Boolean = true,
    val enableLeaderboard: Boolean = true,
    val enableSupportChat: Boolean = true,
    val enableDynamicTheming: Boolean = true,
    val enableLiveNotifications: Boolean = true
)
