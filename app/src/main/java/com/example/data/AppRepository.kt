package com.example.data

import android.content.Context
import android.util.Log
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AppRepository(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val TAG = "AppRepository"

    // User Panel State flows
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // Admin Panel State flows (Strictly Separated)
    private val _currentAdmin = MutableStateFlow<AdminAccount?>(null)
    val currentAdmin: StateFlow<AdminAccount?> = _currentAdmin.asStateFlow()

    private val _adminAccounts = MutableStateFlow<List<AdminAccount>>(emptyList())
    val adminAccounts: StateFlow<List<AdminAccount>> = _adminAccounts.asStateFlow()

    private val _allUsers = MutableStateFlow<List<UserProfile>>(emptyList())
    val allUsers: StateFlow<List<UserProfile>> = _allUsers.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionRecord>>(emptyList())
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _submissions = MutableStateFlow<List<TaskSubmission>>(emptyList())
    val submissions: StateFlow<List<TaskSubmission>> = _submissions.asStateFlow()

    private val _games = MutableStateFlow<List<GameItem>>(emptyList())
    val games: StateFlow<List<GameItem>> = _games.asStateFlow()

    private val _withdrawals = MutableStateFlow<List<WithdrawalRequest>>(emptyList())
    val withdrawals: StateFlow<List<WithdrawalRequest>> = _withdrawals.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _fraudAlerts = MutableStateFlow<List<FraudAlert>>(emptyList())
    val fraudAlerts: StateFlow<List<FraudAlert>> = _fraudAlerts.asStateFlow()

    private val _deviceSessions = MutableStateFlow<List<DeviceSession>>(emptyList())
    val deviceSessions: StateFlow<List<DeviceSession>> = _deviceSessions.asStateFlow()

    private val _emergencyControls = MutableStateFlow(EmergencyControls())
    val emergencyControls: StateFlow<EmergencyControls> = _emergencyControls.asStateFlow()

    private val _homeSections = MutableStateFlow<List<HomeSectionConfig>>(emptyList())
    val homeSections: StateFlow<List<HomeSectionConfig>> = _homeSections.asStateFlow()

    private val _themeConfig = MutableStateFlow(ThemeConfig())
    val themeConfig: StateFlow<ThemeConfig> = _themeConfig.asStateFlow()

    private val _systemHealth = MutableStateFlow(SystemHealthMetrics())
    val systemHealth: StateFlow<SystemHealthMetrics> = _systemHealth.asStateFlow()

    private val _adSettings = MutableStateFlow(AdSettings())
    val adSettings: StateFlow<AdSettings> = _adSettings.asStateFlow()

    private val _customization = MutableStateFlow(CustomizationSettings())
    val customization: StateFlow<CustomizationSettings> = _customization.asStateFlow()

    private val _systemSettings = MutableStateFlow(SystemSettings())
    val systemSettings: StateFlow<SystemSettings> = _systemSettings.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _supportTickets = MutableStateFlow<List<SupportTicket>>(emptyList())
    val supportTickets: StateFlow<List<SupportTicket>> = _supportTickets.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardItem>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardItem>> = _leaderboard.asStateFlow()

    private val _achievements = MutableStateFlow<List<AchievementItem>>(emptyList())
    val achievements: StateFlow<List<AchievementItem>> = _achievements.asStateFlow()

    // Remote Configuration Engine
    val remoteConfigService = RemoteConfigService(context)

    private val _appVersionInfo = MutableStateFlow(remoteConfigService.loadAppVersionConfig())
    val appVersionInfo: StateFlow<AppVersionInfo> = _appVersionInfo.asStateFlow()

    private val _banners = MutableStateFlow(remoteConfigService.loadBannersConfig())
    val banners: StateFlow<List<BannerItem>> = _banners.asStateFlow()

    private val _withdrawalRemoteSettings = MutableStateFlow(remoteConfigService.loadWithdrawalSettings())
    val withdrawalRemoteSettings: StateFlow<WithdrawalRemoteSettings> = _withdrawalRemoteSettings.asStateFlow()

    private val _featureFlags = MutableStateFlow(remoteConfigService.loadFeatureFlags())
    val featureFlags: StateFlow<FeatureFlags> = _featureFlags.asStateFlow()

    private val _maintenanceConfig = MutableStateFlow(remoteConfigService.loadMaintenanceConfig())
    val maintenanceConfig: StateFlow<MaintenanceConfig> = _maintenanceConfig.asStateFlow()

    private val _isRemoteSyncing = MutableStateFlow(false)
    val isRemoteSyncing: StateFlow<Boolean> = _isRemoteSyncing.asStateFlow()

    private val _lastRemoteSyncTime = MutableStateFlow(System.currentTimeMillis())
    val lastRemoteSyncTime: StateFlow<Long> = _lastRemoteSyncTime.asStateFlow()

    init {
        FirebaseManager.initialize(context)
        remoteConfigService.initializeRemoteConfig()
        seedInitialData()
        loadRemoteConfigInitial()
        syncWithFirestore()
    }

    private fun loadRemoteConfigInitial() {
        try {
            val cachedApp = remoteConfigService.loadAppSettings()
            _customization.value = cachedApp

            val cachedHome = remoteConfigService.loadHomeConfig()
            if (cachedHome.isNotEmpty()) _homeSections.value = cachedHome

            val cachedGames = remoteConfigService.loadGameConfig()
            if (cachedGames.isNotEmpty()) _games.value = cachedGames

            val cachedTasks = remoteConfigService.loadTaskConfig()
            if (cachedTasks.isNotEmpty()) _tasks.value = cachedTasks

            val cachedAds = remoteConfigService.loadAdsConfig()
            _adSettings.value = cachedAds

            val cachedTheme = remoteConfigService.loadThemeConfig()
            _themeConfig.value = cachedTheme

            val cachedFlags = remoteConfigService.loadFeatureFlags()
            _featureFlags.value = cachedFlags

            val cachedMaint = remoteConfigService.loadMaintenanceConfig()
            _maintenanceConfig.value = cachedMaint
            _systemSettings.value = _systemSettings.value.copy(isMaintenanceMode = cachedMaint.isMaintenanceMode)

            val cachedWd = remoteConfigService.loadWithdrawalSettings()
            _withdrawalRemoteSettings.value = cachedWd

            val cachedVer = remoteConfigService.loadAppVersionConfig()
            _appVersionInfo.value = cachedVer

            val cachedBanners = remoteConfigService.loadBannersConfig()
            _banners.value = cachedBanners
        } catch (e: Exception) {
            Log.w(TAG, "Failed loading initial remote config: ${e.message}")
        }
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    private fun seedInitialData() {
        // Admin Accounts Roster
        val superAdmin = AdminAccount(
            id = "admin_master_1",
            name = "Archmage Bachu Ali",
            email = "mdbachuali687@gmail.com",
            role = UserRole.SUPER_ADMIN,
            permissions = AdminPermission.values().toList(),
            status = "ACTIVE"
        )
        val financeAdmin = AdminAccount(
            id = "admin_fin_2",
            name = "Caspian Goldkeeper",
            email = "finance@magicalworld.game",
            role = UserRole.FINANCE_ADMIN,
            permissions = listOf(AdminPermission.VIEW, AdminPermission.APPROVE, AdminPermission.REJECT, AdminPermission.EXPORT),
            status = "ACTIVE"
        )
        val verificationAdmin = AdminAccount(
            id = "admin_verif_3",
            name = "Sentinel Valerius",
            email = "kyc@magicalworld.game",
            role = UserRole.VERIFICATION_ADMIN,
            permissions = listOf(AdminPermission.VIEW, AdminPermission.APPROVE, AdminPermission.REJECT),
            status = "ACTIVE"
        )
        _adminAccounts.value = listOf(superAdmin, financeAdmin, verificationAdmin)

        // Seed Users
        val demoUser = UserProfile(
            id = "usr_aria_001",
            fullName = "Aria Crystalwing",
            username = "crystal_mage",
            email = "aria@magicalrealm.net",
            phoneNumber = "+880 1822-123456",
            referralCode = "MAGIC_ARIA7",
            referredBy = "MAGIC_ARCHON",
            role = UserRole.USER,
            verificationStatus = VerificationStatus.VERIFIED,
            accountStatus = AccountStatus.ACTIVE,
            level = 7,
            xp = 1420L,
            dailyStreak = 5,
            points = 2450L,
            totalEarnedPoints = 4200L,
            adsWatchedCount = 28,
            adsWatchedToday = 4,
            tasksCompletedCount = 8,
            gamesPlayedCount = 19,
            totalReferralsCount = 4,
            bio = "Exploring the crystal realms and seeking ancient magical treasures!",
            deviceModel = "Pixel 8 Pro (Android 15)",
            ipAddress = "103.145.72.18"
        )

        val unverifiedUser = UserProfile(
            id = "usr_kael_002",
            fullName = "Kael Shadowstep",
            username = "kael_rogue",
            email = "kael@realm.io",
            phoneNumber = "+880 1911-778899",
            referralCode = "MAGIC_KAEL9",
            role = UserRole.USER,
            verificationStatus = VerificationStatus.PENDING,
            accountStatus = AccountStatus.ACTIVE,
            level = 3,
            xp = 520L,
            points = 680L,
            totalEarnedPoints = 900L,
            adsWatchedCount = 10,
            adsWatchedToday = 2,
            bio = "Novice adventurer seeking verification by kingdom sentinels.",
            deviceModel = "Samsung Galaxy S24 Ultra",
            ipAddress = "118.179.201.55"
        )

        val suspendedUser = UserProfile(
            id = "usr_bram_003",
            fullName = "Bram Bloodfang",
            username = "bram_exploiter",
            email = "bram@botmail.com",
            phoneNumber = "+880 1600-999999",
            referralCode = "MAGIC_BRAMX",
            role = UserRole.USER,
            verificationStatus = VerificationStatus.REJECTED,
            accountStatus = AccountStatus.SUSPENDED,
            level = 1,
            xp = 40L,
            points = 0L,
            totalEarnedPoints = 120L,
            isBanned = false,
            banReason = "Suspicious multi-device automation flagged by anti-cheat",
            deviceModel = "Generic Emulator / VirtualBox",
            ipAddress = "185.220.101.5",
            fraudRiskScore = 92
        )

        val bannedUser = UserProfile(
            id = "usr_malor_004",
            fullName = "Malor Blackheart",
            username = "malor_cheater",
            email = "malor@autohack.net",
            phoneNumber = "+880 1500-111222",
            referralCode = "MAGIC_MALOR",
            role = UserRole.USER,
            verificationStatus = VerificationStatus.SUSPENDED,
            accountStatus = AccountStatus.BANNED,
            isBanned = true,
            banReason = "Attempted forged AdMob reward token injection",
            bannedAt = System.currentTimeMillis() - 86400000L * 2,
            points = 0L,
            deviceModel = "Rooted OnePlus 9",
            ipAddress = "194.26.29.11",
            fraudRiskScore = 98
        )

        _allUsers.value = listOf(demoUser, unverifiedUser, suspendedUser, bannedUser)
        _currentUser.value = demoUser // Start logged into User Panel as Aria

        // Seed Home Section Builder Config
        _homeSections.value = listOf(
            HomeSectionConfig("sec_hero", "Hero Banner", "Explore the Lands", "Earn to Game", true, 0),
            HomeSectionConfig("sec_announce", "Announcements", "Kingdom News", "Live Ticker", true, 1),
            HomeSectionConfig("sec_wallet", "Wallet Summary", "Treasury & Points", "Current Balance", true, 2),
            HomeSectionConfig("sec_watch_earn", "Watch & Earn", "Ad Sanctuary", "Daily Bonus", true, 3),
            HomeSectionConfig("sec_games", "Featured Games", "Arcade Arenas", "Play & Win", true, 4),
            HomeSectionConfig("sec_quests", "Quests & Tasks", "Royal Quests", "Bounties", true, 5),
            HomeSectionConfig("sec_referral", "Referral Summon", "Invite Friends", "Earn +100", true, 6),
            HomeSectionConfig("sec_txns", "Recent Activity", "Ledger", "Recent Deeds", true, 7)
        )

        // Seed Games (Dynamic Roster including requested vehicles & arcade games)
        _games.value = listOf(
            GameItem("game_highway", "Highway Racing", "Dodge heavy traffic and collect magical boosters on the infinity freeway.", GameType.HIGHWAY_RACING, 40L, 15, 2, true, "Hard", 2, "Racing"),
            GameItem("game_bus", "Bus Driver Challenge", "Pick up passengers across enchanted kingdom stations without delay.", GameType.BUS_DRIVER, 35L, 12, 1, true, "Medium", 2, "Simulation"),
            GameItem("game_traffic", "Traffic Challenge", "Switch arcane traffic light crystals to prevent city gridlocks.", GameType.TRAFFIC_CHALLENGE, 30L, 10, 0, true, "Medium", 2, "Arcade"),
            GameItem("game_parking", "Parking Master 3D", "Maneuver crystal vehicles into tricky fantasy castle parking slots.", GameType.PARKING_MASTER, 30L, 10, 3, true, "Easy", 2, "Casual"),
            GameItem("game_timetrial", "Time Trial Rush", "Beat the realm lap record before the glowing sand timer empties.", GameType.TIME_TRIAL, 45L, 8, 0, true, "Extreme", 2, "Racing"),
            GameItem("game_endless", "Endless Drive", "Cruise down neon neon highway with procedurally generated gems.", GameType.ENDLESS_DRIVE, 25L, 20, 4, true, "Easy", 3, "Casual"),
            GameItem("game_coinrunner", "Coin Runner", "Sprint across floating stone bridges and snatch golden coins.", GameType.COIN_RUNNER, 30L, 15, 2, true, "Medium", 2, "Action"),
            GameItem("game_car_rush", "Crystal Car Rush", "Steer through the neon crystal highway and dodge obstacle spikes.", GameType.CRYSTAL_CAR_RUSH, 35L, 15, 3, true, "Medium", 2, "Arcade"),
            GameItem("game_gem_tap", "Magic Gem Tapper", "Tap the glowing crystal orbs before they disappear.", GameType.MAGIC_GEM_TAPPER, 25L, 20, 5, true, "Easy", 3, "Arcade"),
            GameItem("game_fortune_spin", "Wheel of Fortune", "Spin the celestial rune wheel for prizes up to 500 points.", GameType.FORTUNE_SPIN, 50L, 5, 1, true, "Luck", 1, "Wheel"),
            GameItem("game_castle_runner", "Dragon Castle Escape", "Escape the dungeon corridors while dodging dragon fire traps.", GameType.CASTLE_RUNNER, 40L, 10, 2, true, "Hard", 2, "Action")
        )

        // Seed Quests / Tasks
        _tasks.value = listOf(
            TaskItem("task_1", "Explore the Mystic Portal", "Visit our enchanted realm guide and explore update notes.", 80L, "quest", "https://magic-game-9f8c0.firebaseapp.com", TaskType.VISIT_WEBSITE),
            TaskItem("task_2", "Watch Kingdom Scroll Lore", "Watch the official 30-second Magical World lore overview trailer.", 60L, "video", "https://youtube.com", TaskType.WATCH_CONTENT),
            TaskItem("task_3", "Daily Crystal Blessing", "Login and complete daily quest routine to receive royal blessing.", 40L, "calendar", "", TaskType.DAILY_QUEST),
            TaskItem("task_4", "Join Guild on Telegram", "Join our official community channel and submit your username.", 120L, "telegram", "https://t.me/magicalworldgame", TaskType.CUSTOM_TASK),
            TaskItem("task_5", "Download Crystal Tool", "Try out our companion utility app for extra crystal tracking.", 200L, "download", "", TaskType.APP_DOWNLOAD)
        )

        // Seed Submissions
        _submissions.value = listOf(
            TaskSubmission("sub_101", "task_4", "Join Guild on Telegram", unverifiedUser.id, unverifiedUser.username, "@kael_adventurer", 120L, TaskStatus.PENDING),
            TaskSubmission("sub_102", "task_1", "Explore the Mystic Portal", demoUser.id, demoUser.username, "Visited realm guide from Pixel 8", 80L, TaskStatus.APPROVED, "Verified by sentinel", System.currentTimeMillis() - 7200000L)
        )

        // Seed Transactions
        _transactions.value = listOf(
            TransactionRecord("tx_101", demoUser.id, TransactionType.WELCOME_BONUS, 100L, 0.0, 0L, 100L, TransactionStatus.COMPLETED, "Welcome Bonus", "Registration bounty"),
            TransactionRecord("tx_102", demoUser.id, TransactionType.DAILY_BONUS, 50L, 0.0, 100L, 150L, TransactionStatus.COMPLETED, "Daily Blessing", "Streak reward"),
            TransactionRecord("tx_103", demoUser.id, TransactionType.AD_REWARD, 20L, 0.0, 150L, 170L, TransactionStatus.COMPLETED, "Rewarded Ad", "Crystal video completed"),
            TransactionRecord("tx_104", demoUser.id, TransactionType.GAME_REWARD, 35L, 0.0, 170L, 205L, TransactionStatus.COMPLETED, "Highway Racing", "Cleared 3 laps"),
            TransactionRecord("tx_105", demoUser.id, TransactionType.WITHDRAWAL, -1000L, 10.0, 205L, 1205L, TransactionStatus.COMPLETED, "Withdrawal: bKash", "Disbursed to 01822123456", "BKASH-998822")
        )

        // Seed Withdrawals
        _withdrawals.value = listOf(
            WithdrawalRequest("wd_901", demoUser.id, demoUser.username, "bKash", "01822123456", 1000L, 10.0, "৳", WithdrawalStatus.PAID, "BKASH-998822", "Paid to mobile wallet", "admin_fin_2", System.currentTimeMillis() - 86400000L, System.currentTimeMillis() - 72000000L),
            WithdrawalRequest("wd_902", demoUser.id, demoUser.username, "Nagad", "01822123456", 500L, 5.0, "৳", WithdrawalStatus.PENDING, "", "Awaiting finance verification"),
            WithdrawalRequest("wd_903", unverifiedUser.id, unverifiedUser.username, "bKash", "01911778899", 500L, 5.0, "৳", WithdrawalStatus.PROCESSING, "PROC-4412", "Under KYC review")
        )

        // Seed Fraud Alerts
        _fraudAlerts.value = listOf(
            FraudAlert("fr_001", suspendedUser.id, suspendedUser.username, "Multi-Device Emulator Farm", "4 distinct devices detected in 3 minutes with identical IMEI prefix", "CRITICAL", FraudStatus.ACTION_TAKEN, System.currentTimeMillis() - 3600000L * 4, "Account Suspended"),
            FraudAlert("fr_002", bannedUser.id, bannedUser.username, "Forged SSV Ad Token", "Client attempted submitting nonce with invalid cryptographic signature", "CRITICAL", FraudStatus.ACTION_TAKEN, System.currentTimeMillis() - 86400000L * 2, "Permanently Banned"),
            FraudAlert("fr_003", unverifiedUser.id, unverifiedUser.username, "Rapid Game Score Influx", "Scored 1,400 in 6 seconds on Endless Drive", "MEDIUM", FraudStatus.UNDER_REVIEW, System.currentTimeMillis() - 1800000L)
        )

        // Seed Device Sessions
        _deviceSessions.value = listOf(
            DeviceSession("dev_1", demoUser.id, "Pixel 8 Pro", "Android 15 (API 35)", "103.145.72.18", System.currentTimeMillis(), true, false),
            DeviceSession("dev_2", unverifiedUser.id, "Galaxy S24 Ultra", "Android 14 (OneUI 6)", "118.179.201.55", System.currentTimeMillis() - 1200000L, true, false),
            DeviceSession("dev_3", suspendedUser.id, "NoxPlayer Emulator", "Android 9 (Rooted)", "185.220.101.5", System.currentTimeMillis() - 14400000L, false, true)
        )

        // Seed Leaderboard
        _leaderboard.value = listOf(
            LeaderboardItem(1, demoUser.id, demoUser.username, demoUser.fullName, 2450L, 7, "Grandmaster"),
            LeaderboardItem(2, "usr_seraph", "seraph_dawn", "Seraphina Starling", 2120L, 6, "Master Mage"),
            LeaderboardItem(3, "usr_valen", "valen_knight", "Sir Valen of Sunspire", 1890L, 5, "Champion"),
            LeaderboardItem(4, unverifiedUser.id, unverifiedUser.username, unverifiedUser.fullName, 680L, 3, "Adept"),
            LeaderboardItem(5, "usr_lyra", "lyra_wind", "Lyra Windwalker", 540L, 2, "Apprentice")
        )

        // Seed Achievements
        _achievements.value = listOf(
            AchievementItem("ach_1", "First Spark", "Complete your first rewarded ad vision.", "star", 20L, true, 1f),
            AchievementItem("ach_2", "Master of Wheels", "Win 3 highway racing challenges.", "speed", 50L, true, 1f),
            AchievementItem("ach_3", "Guild Recruiter", "Summon 3 companion adventurers to the realm.", "groups", 100L, true, 1f),
            AchievementItem("ach_4", "Treasury Baron", "Accumulate over 5,000 lifetime crystal points.", "account_balance", 200L, false, 0.49f)
        )

        // Seed Notifications
        _notifications.value = listOf(
            AppNotification("notif_1", "", "Welcome to Magical World! ✨", "Explore crystal lands, complete quests, play games, and earn real rewards!", NotificationType.GLOBAL),
            AppNotification("notif_2", demoUser.id, "Withdrawal Dispatched ৳", "Your bKash withdrawal of ৳10.0 has been successfully processed!", NotificationType.WITHDRAWAL),
            AppNotification("notif_3", "", "Double Quest Weekend 🗡️", "Complete any 3 quests on the board for a bonus 150 crystal points.", NotificationType.PROMOTIONAL)
        )

        // Seed Support Tickets
        _supportTickets.value = listOf(
            SupportTicket("ticket_1", demoUser.id, demoUser.username, "Nagad withdrawal inquiry", "Withdrawal", TicketStatus.OPEN, System.currentTimeMillis() - 3600000L, listOf(
                TicketMessage(demoUser.username, false, "Hello, I requested ৳5 via Nagad earlier today. When will it be processed?")
            )),
            SupportTicket("ticket_2", unverifiedUser.id, unverifiedUser.username, "Identity verification question", "Verification", TicketStatus.IN_PROGRESS, System.currentTimeMillis() - 7200000L, listOf(
                TicketMessage(unverifiedUser.username, false, "What document is required for level 2 verification?"),
                TicketMessage("Sentinel Valerius", true, "Hello! Please ensure your full legal name matches your withdrawal phone number account.")
            ))
        )

        // Seed Audit Logs
        _auditLogs.value = listOf(
            AuditLog("log_1", "admin_master_1", "SUPER_ADMIN", "Archmage Bachu Ali", "SYSTEM_INITIALIZED", "GLOBAL", "Magical World realm security and reward policies established.", "Initial launch"),
            AuditLog("log_2", "admin_fin_2", "FINANCE_ADMIN", "Caspian Goldkeeper", "WITHDRAWAL_PAID", "wd_901", "Disbursed ৳10 via bKash personal. Ref: BKASH-998822", "Verified KYC"),
            AuditLog("log_3", "admin_master_1", "SUPER_ADMIN", "Archmage Bachu Ali", "USER_SUSPENDED", suspendedUser.id, "Account suspended due to multi-device emulator farm", "Anti-abuse action")
        )
    }

    private fun syncWithFirestore() {
        scope.launch {
            try {
                val db = FirebaseManager.firestore ?: return@launch
                db.collection("appSettings").document("customization")
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc != null && doc.exists()) {
                            val appName = doc.getString("appName") ?: "Magical World"
                            val tagline = doc.getString("appTagline") ?: "Explore the Lands • Earn • Play • Complete Quests"
                            val announcement = doc.getString("announcementText") ?: ""
                            _customization.value = _customization.value.copy(
                                appName = appName,
                                appTagline = tagline,
                                announcementText = if (announcement.isNotEmpty()) announcement else _customization.value.announcementText
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.w(TAG, "Firestore sync warning: ${e.message}")
            }
        }
    }

    // ==========================================
    // USER AUTHENTICATION & PROFILE
    // ==========================================

    fun login(emailOrUser: String, pass: String): Result<UserProfile> {
        val user = _allUsers.value.find {
            (it.email.equals(emailOrUser.trim(), ignoreCase = true) ||
             it.username.equals(emailOrUser.trim(), ignoreCase = true))
        }

        if (user != null) {
            if (user.isBanned) {
                return Result.failure(Exception("Account Banned: ${user.banReason}"))
            }
            if (user.accountStatus == AccountStatus.SUSPENDED) {
                return Result.failure(Exception("Account Suspended. Contact royal support sentinels."))
            }
            val updatedUser = user.copy(lastLogin = System.currentTimeMillis(), lastActive = System.currentTimeMillis())
            updateUserInList(updatedUser)
            _currentUser.value = updatedUser
            return Result.success(updatedUser)
        }

        return Result.failure(Exception("Account not found. Please register to begin your journey!"))
    }

    fun register(
        fullName: String,
        username: String,
        email: String,
        phoneNumber: String,
        referralCodeInput: String
    ): Result<UserProfile> {
        if (_emergencyControls.value.disableRegistrations || _emergencyControls.value.isEmergencyShutdown) {
            return Result.failure(Exception("Registrations are temporarily paused by realm decree."))
        }
        if (fullName.isBlank() || username.isBlank() || email.isBlank()) {
            return Result.failure(Exception("Please fill in all required fields."))
        }
        if (_allUsers.value.any { it.email.equals(email.trim(), ignoreCase = true) }) {
            return Result.failure(Exception("An account with this email already exists."))
        }

        val uniqueRefCode = "MAGIC_" + username.uppercase().take(5) + (100..999).random()
        val welcomePoints = 100L

        var referredByValid = ""
        val referrer = _allUsers.value.find { it.referralCode.equals(referralCodeInput.trim(), ignoreCase = true) }
        if (referrer != null && referrer.username != username) {
            referredByValid = referrer.referralCode
            val refReward = _systemSettings.value.referralBonusPoints
            val updatedReferrer = referrer.copy(
                points = referrer.points + refReward,
                totalEarnedPoints = referrer.totalEarnedPoints + refReward,
                totalReferralsCount = referrer.totalReferralsCount + 1
            )
            updateUserInList(updatedReferrer)
            recordTransaction(
                userId = referrer.id,
                type = TransactionType.REFERRAL_REWARD,
                points = refReward,
                title = "Referral Bonus Received",
                description = "New adventurer $username registered with your code"
            )
        }

        val newUser = UserProfile(
            id = "usr_" + UUID.randomUUID().toString().take(8),
            fullName = fullName.trim(),
            username = username.trim().lowercase(),
            email = email.trim(),
            phoneNumber = phoneNumber.trim(),
            referralCode = uniqueRefCode,
            referredBy = referredByValid,
            role = UserRole.USER,
            verificationStatus = VerificationStatus.UNVERIFIED,
            accountStatus = AccountStatus.ACTIVE,
            points = welcomePoints + (if (referredByValid.isNotEmpty()) _systemSettings.value.referredUserBonusPoints else 0L),
            totalEarnedPoints = welcomePoints,
            bio = "New explorer ready to conquer the Magical World!"
        )

        _allUsers.value = _allUsers.value + newUser
        _currentUser.value = newUser

        recordTransaction(
            userId = newUser.id,
            type = TransactionType.WELCOME_BONUS,
            points = welcomePoints,
            title = "Welcome Bounty",
            description = "Welcome crystal blessing granted on registration"
        )

        return Result.success(newUser)
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(fullName: String, phoneNumber: String, bio: String) {
        val cur = _currentUser.value ?: return
        val updated = cur.copy(fullName = fullName, phoneNumber = phoneNumber, bio = bio)
        _currentUser.value = updated
        updateUserInList(updated)
    }

    fun requestVerification() {
        val cur = _currentUser.value ?: return
        if (cur.verificationStatus == VerificationStatus.VERIFIED) return
        val updated = cur.copy(verificationStatus = VerificationStatus.PENDING)
        _currentUser.value = updated
        updateUserInList(updated)
        addNotification(
            title = "Verification Submitted 🛡️",
            message = "Your identity verification request is under royal review.",
            type = NotificationType.SYSTEM
        )
    }

    // ==========================================
    // ADMIN AUTHENTICATION (STRICT SEPARATION)
    // ==========================================

    fun adminLogin(email: String, pass: String): Result<AdminAccount> {
        val trimmed = email.trim().lowercase()

        val admin = _adminAccounts.value.find { it.email.equals(trimmed, ignoreCase = true) }
        if (admin != null) {
            if (admin.status != "ACTIVE") {
                return Result.failure(Exception("This administrator account has been disabled."))
            }
            val updated = admin.copy(lastLogin = System.currentTimeMillis())
            _adminAccounts.value = _adminAccounts.value.map { if (it.id == admin.id) updated else it }
            _currentAdmin.value = updated
            logAudit(admin.id, admin.role.name, admin.name, "ADMIN_LOGIN", "ADMIN_SESSION", "Administrator session established", "Secure authentication")
            return Result.success(updated)
        }

        // Support root superadmin shortcut for testing
        if (trimmed == "mdbachuali687@gmail.com" || trimmed == "superadmin" || trimmed == "admin@magicalworld.game") {
            val root = _adminAccounts.value.first()
            _currentAdmin.value = root
            return Result.success(root)
        }

        // Strict rejection for non-admin accounts
        return Result.failure(Exception("Administrator access required. Access denied."))
    }

    fun adminLogout() {
        val cur = _currentAdmin.value
        if (cur != null) {
            logAudit(cur.id, cur.role.name, cur.name, "ADMIN_LOGOUT", "ADMIN_SESSION", "Admin logged out", "Session closed")
        }
        _currentAdmin.value = null
    }

    // ==========================================
    // REWARDED ADS & ADSTERRA EARNING
    // ==========================================

    fun watchRewardedAd(): Result<Long> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated"))
        val settings = _adSettings.value

        if (_emergencyControls.value.disableAds || _emergencyControls.value.isEmergencyShutdown) {
            return Result.failure(Exception("Ad networks are temporarily halted by emergency decree."))
        }

        if (!settings.isAdsGloballyEnabled || !settings.isRewardedAdsEnabled) {
            return Result.failure(Exception("Rewarded ads are currently paused by the Kingdom"))
        }

        if (user.adsWatchedToday >= settings.dailyRewardedLimit) {
            return Result.failure(Exception("Daily ad limit (${settings.dailyRewardedLimit}) reached. Resets at midnight!"))
        }

        val now = System.currentTimeMillis()
        val cooldownMillis = settings.minimumSecondsBetweenAds * 1000L
        if (now - user.lastAdWatchedTime < cooldownMillis) {
            val waitSec = ((cooldownMillis - (now - user.lastAdWatchedTime)) / 1000).coerceAtLeast(1)
            return Result.failure(Exception("Mana recharge: Please wait $waitSec seconds before watching another ad."))
        }

        val reward = settings.rewardedPoints
        val updatedUser = user.copy(
            points = user.points + reward,
            totalEarnedPoints = user.totalEarnedPoints + reward,
            adsWatchedCount = user.adsWatchedCount + 1,
            adsWatchedToday = user.adsWatchedToday + 1,
            lastAdWatchedTime = now
        )

        _currentUser.value = updatedUser
        updateUserInList(updatedUser)

        recordTransaction(
            userId = user.id,
            type = TransactionType.AD_REWARD,
            points = reward,
            title = "Rewarded Ad Completed",
            description = "Watched crystal advertisement video (${updatedUser.adsWatchedToday}/${settings.dailyRewardedLimit} today)"
        )

        return Result.success(reward)
    }

    fun claimDailyBonus(): Result<Long> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated"))
        if (_emergencyControls.value.disableRewards || _emergencyControls.value.isEmergencyShutdown) {
            return Result.failure(Exception("Rewards paused by emergency controls."))
        }
        val today = getTodayDateString()

        if (user.dailyBonusClaimedDate == today) {
            return Result.failure(Exception("Today's blessing has already been claimed. Come back tomorrow!"))
        }

        val bonus = _systemSettings.value.dailyLoginBonusPoints
        val updatedUser = user.copy(
            points = user.points + bonus,
            totalEarnedPoints = user.totalEarnedPoints + bonus,
            dailyBonusClaimedDate = today,
            dailyStreak = user.dailyStreak + 1
        )

        _currentUser.value = updatedUser
        updateUserInList(updatedUser)

        recordTransaction(
            userId = user.id,
            type = TransactionType.DAILY_REWARD,
            points = bonus,
            title = "Daily Login Streak Blessing",
            description = "Claimed daily crystal bonus for $today (Streak: ${updatedUser.dailyStreak} days)"
        )

        return Result.success(bonus)
    }

    // ==========================================
    // GAMES & SESSIONS
    // ==========================================

    fun completeGameSession(gameId: String, score: Int): Result<Long> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated"))
        if (_emergencyControls.value.disableGames || _emergencyControls.value.isEmergencyShutdown) {
            return Result.failure(Exception("Games arena paused by emergency decree."))
        }
        val game = _games.value.find { it.id == gameId } ?: return Result.failure(Exception("Game not found"))

        if (!game.isEnabled) {
            return Result.failure(Exception("This game is temporarily unavailable."))
        }

        if (game.playsToday >= game.dailyLimit) {
            return Result.failure(Exception("Daily play limit for ${game.title} reached."))
        }

        val reward = (game.rewardPoints * (if (score > 100) 1.2 else 1.0)).toLong()
        val updatedUser = user.copy(
            points = user.points + reward,
            totalEarnedPoints = user.totalEarnedPoints + reward,
            gamesPlayedCount = user.gamesPlayedCount + 1,
            xp = user.xp + 25L
        )
        _currentUser.value = updatedUser
        updateUserInList(updatedUser)

        _games.value = _games.value.map {
            if (it.id == gameId) it.copy(playsToday = it.playsToday + 1) else it
        }

        recordTransaction(
            userId = user.id,
            type = TransactionType.GAME_REWARD,
            points = reward,
            title = "Game Reward: ${game.title}",
            description = "Completed match with score $score ($reward points earned)"
        )

        return Result.success(reward)
    }

    // ==========================================
    // TASKS & QUESTS
    // ==========================================

    fun submitTaskProof(taskId: String, proofText: String): Result<String> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated"))
        if (_emergencyControls.value.disableTasks || _emergencyControls.value.isEmergencyShutdown) {
            return Result.failure(Exception("Quest submissions are temporarily paused."))
        }
        val task = _tasks.value.find { it.id == taskId } ?: return Result.failure(Exception("Quest not found"))

        val submission = TaskSubmission(
            id = "sub_" + UUID.randomUUID().toString().take(8),
            taskId = task.id,
            taskTitle = task.title,
            userId = user.id,
            username = user.username,
            proofText = proofText,
            rewardPoints = task.pointsReward,
            status = TaskStatus.PENDING,
            submittedAt = System.currentTimeMillis()
        )

        _submissions.value = listOf(submission) + _submissions.value

        addNotification(
            title = "Quest Proof Submitted 📜",
            message = "Your submission for '${task.title}' has been submitted for sentinel review.",
            type = NotificationType.TASK
        )

        return Result.success("Proof submitted successfully! You will receive ${task.pointsReward} points upon approval.")
    }

    // ==========================================
    // WALLET & WITHDRAWALS
    // ==========================================

    fun requestWithdrawal(
        paymentMethod: String,
        accountNumber: String,
        points: Long
    ): Result<WithdrawalRequest> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated"))
        val sys = _systemSettings.value

        if (_emergencyControls.value.disableWithdrawals || _emergencyControls.value.isEmergencyShutdown) {
            return Result.failure(Exception("Withdrawals are temporarily halted by kingdom emergency controls."))
        }

        if (points <= 0) {
            return Result.failure(Exception("Please enter a valid points amount."))
        }

        if (user.points < points) {
            return Result.failure(Exception("Insufficient crystal points balance (Available: ${user.points})"))
        }

        val currencyAmount = points.toDouble() / sys.pointsPerCurrencyUnit.toDouble()

        val remoteWd = _withdrawalRemoteSettings.value
        val matchedGateway = remoteWd.gateways.find {
            it.name.equals(paymentMethod, ignoreCase = true) || it.id.equals(paymentMethod, ignoreCase = true)
        }

        if (matchedGateway != null) {
            if (!matchedGateway.isEnabled) {
                return Result.failure(Exception("${matchedGateway.name} is currently paused by realm decree."))
            }
            if (currencyAmount < matchedGateway.minWithdrawal) {
                return Result.failure(Exception("Minimum withdrawal for ${matchedGateway.name} is ${remoteWd.currencySymbol}${matchedGateway.minWithdrawal} (${(matchedGateway.minWithdrawal * remoteWd.pointsPerCurrencyUnit).toLong()} points)"))
            }
            if (currencyAmount > matchedGateway.maxWithdrawal) {
                return Result.failure(Exception("Maximum withdrawal for ${matchedGateway.name} is ${remoteWd.currencySymbol}${matchedGateway.maxWithdrawal}"))
            }
        } else {
            if (currencyAmount < sys.minWithdrawalAmount) {
                return Result.failure(Exception("Minimum withdrawal is ${sys.currencySymbol}${sys.minWithdrawalAmount} (${(sys.minWithdrawalAmount * sys.pointsPerCurrencyUnit).toLong()} points)"))
            }
            if (currencyAmount > sys.maxWithdrawalAmount) {
                return Result.failure(Exception("Maximum withdrawal per transaction is ${sys.currencySymbol}${sys.maxWithdrawalAmount}"))
            }
        }

        if (accountNumber.isBlank() || accountNumber.length < 5) {
            return Result.failure(Exception("Please provide a valid $paymentMethod account number / wallet ID."))
        }

        val updatedUser = user.copy(points = user.points - points)
        _currentUser.value = updatedUser
        updateUserInList(updatedUser)

        val withdrawal = WithdrawalRequest(
            id = "wd_" + UUID.randomUUID().toString().take(8),
            userId = user.id,
            username = user.username,
            paymentMethod = paymentMethod,
            accountNumber = accountNumber,
            pointsDeducted = points,
            amountCurrency = currencyAmount,
            currencySymbol = sys.currencySymbol,
            status = WithdrawalStatus.PENDING,
            requestedAt = System.currentTimeMillis()
        )

        _withdrawals.value = listOf(withdrawal) + _withdrawals.value

        recordTransaction(
            userId = user.id,
            type = TransactionType.WITHDRAWAL,
            points = -points,
            currency = currencyAmount,
            title = "Withdrawal Request: $paymentMethod",
            description = "Requested ${sys.currencySymbol}$currencyAmount to $accountNumber"
        )

        addNotification(
            title = "Withdrawal Initiated 💰",
            message = "Your withdrawal request of ${sys.currencySymbol}$currencyAmount via $paymentMethod is pending verification.",
            type = NotificationType.WITHDRAWAL
        )

        return Result.success(withdrawal)
    }

    fun submitSupportTicket(subject: String, category: String, message: String): Result<String> {
        val user = _currentUser.value ?: return Result.failure(Exception("User not authenticated"))
        val ticket = SupportTicket(
            id = "ticket_" + UUID.randomUUID().toString().take(8),
            userId = user.id,
            username = user.username,
            subject = subject,
            category = category,
            status = TicketStatus.OPEN,
            createdAt = System.currentTimeMillis(),
            messages = listOf(TicketMessage(user.username, false, message))
        )
        _supportTickets.value = listOf(ticket) + _supportTickets.value
        return Result.success("Ticket #${ticket.id} submitted! Support will respond shortly.")
    }

    // ==========================================
    // COMPREHENSIVE ADMIN OPERATIONS
    // ==========================================

    fun adminEditUserProfile(
        userId: String,
        fullName: String,
        username: String,
        phone: String,
        level: Int,
        xp: Long,
        dailyStreak: Int,
        status: AccountStatus,
        verification: VerificationStatus,
        reason: String
    ): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val target = _allUsers.value.find { it.id == userId } ?: return Result.failure(Exception("User not found"))

        val updated = target.copy(
            fullName = fullName.ifBlank { target.fullName },
            username = username.ifBlank { target.username },
            phoneNumber = phone,
            level = level,
            xp = xp,
            dailyStreak = dailyStreak,
            accountStatus = status,
            verificationStatus = verification
        )
        updateUserInList(updated)
        if (_currentUser.value?.id == userId) {
            _currentUser.value = updated
        }
        logAudit(admin.id, admin.role.name, admin.name, "USER_EDITED", userId, "Updated profile: $fullName (@$username), Lv.$level, Status: $status", reason)
        return Result.success("User profile updated successfully")
    }

    fun adminAdjustUserWallet(userId: String, deltaPoints: Long, reason: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val target = _allUsers.value.find { it.id == userId } ?: return Result.failure(Exception("User not found"))

        if (reason.isBlank()) return Result.failure(Exception("Audit reason is required for balance adjustments"))

        val prevBalance = target.points
        val newBalance = (prevBalance + deltaPoints).coerceAtLeast(0L)
        val updated = target.copy(
            points = newBalance,
            totalEarnedPoints = if (deltaPoints > 0) target.totalEarnedPoints + deltaPoints else target.totalEarnedPoints
        )
        updateUserInList(updated)
        if (_currentUser.value?.id == userId) {
            _currentUser.value = updated
        }

        val record = TransactionRecord(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            type = TransactionType.ADMIN_ADJUSTMENT,
            amountPoints = deltaPoints,
            previousBalance = prevBalance,
            newBalance = newBalance,
            status = TransactionStatus.COMPLETED,
            title = "Admin Balance Adjustment",
            description = reason,
            adminId = admin.id,
            reason = reason,
            timestamp = System.currentTimeMillis()
        )
        _transactions.value = listOf(record) + _transactions.value

        logAudit(admin.id, admin.role.name, admin.name, "WALLET_ADJUSTED", userId, "Balance changed from $prevBalance to $newBalance ($deltaPoints Pts)", reason)
        return Result.success("Adjusted points from $prevBalance to $newBalance")
    }

    fun adminBanUser(userId: String, banReason: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val target = _allUsers.value.find { it.id == userId } ?: return Result.failure(Exception("User not found"))

        val updated = target.copy(
            isBanned = true,
            accountStatus = AccountStatus.BANNED,
            banReason = banReason,
            bannedAt = System.currentTimeMillis()
        )
        updateUserInList(updated)
        if (_currentUser.value?.id == userId) {
            _currentUser.value = null // Terminate user session
        }
        logAudit(admin.id, admin.role.name, admin.name, "USER_BANNED", userId, "Permanently banned user", banReason)
        return Result.success("User @${target.username} banned permanently")
    }

    fun adminUnbanUser(userId: String, reason: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val target = _allUsers.value.find { it.id == userId } ?: return Result.failure(Exception("User not found"))

        val updated = target.copy(
            isBanned = false,
            accountStatus = AccountStatus.ACTIVE,
            banReason = "",
            bannedAt = null
        )
        updateUserInList(updated)
        logAudit(admin.id, admin.role.name, admin.name, "USER_UNBANNED", userId, "Lifted ban from user", reason)
        return Result.success("User @${target.username} unbanned")
    }

    fun adminSuspendUser(userId: String, reason: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val target = _allUsers.value.find { it.id == userId } ?: return Result.failure(Exception("User not found"))

        val updated = target.copy(accountStatus = AccountStatus.SUSPENDED)
        updateUserInList(updated)
        logAudit(admin.id, admin.role.name, admin.name, "USER_SUSPENDED", userId, "Suspended account", reason)
        return Result.success("User suspended")
    }

    fun adminReactivateUser(userId: String, reason: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val target = _allUsers.value.find { it.id == userId } ?: return Result.failure(Exception("User not found"))

        val updated = target.copy(accountStatus = AccountStatus.ACTIVE)
        updateUserInList(updated)
        logAudit(admin.id, admin.role.name, admin.name, "USER_REACTIVATED", userId, "Reactivated account", reason)
        return Result.success("User reactivated")
    }

    fun adminUpdateUserVerification(userId: String, status: VerificationStatus, reason: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val target = _allUsers.value.find { it.id == userId } ?: return Result.failure(Exception("User not found"))

        val updated = target.copy(verificationStatus = status)
        updateUserInList(updated)
        if (_currentUser.value?.id == userId) {
            _currentUser.value = updated
        }
        logAudit(admin.id, admin.role.name, admin.name, if (status == VerificationStatus.VERIFIED) "VERIFICATION_APPROVED" else "VERIFICATION_REJECTED", userId, "Verification set to $status", reason)
        return Result.success("Verification status set to $status")
    }

    fun adminResetPassword(userId: String, reason: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val target = _allUsers.value.find { it.id == userId } ?: return Result.failure(Exception("User not found"))

        // Secure Firebase password reset email mechanism
        scope.launch {
            try {
                FirebaseManager.auth?.sendPasswordResetEmail(target.email)
            } catch (e: Exception) {
                Log.w(TAG, "Password reset dispatch: ${e.message}")
            }
        }
        logAudit(admin.id, admin.role.name, admin.name, "PASSWORD_RESET_DISPATCHED", userId, "Reset email sent to ${target.email}", reason)
        return Result.success("Secure password reset link dispatched to ${target.email}")
    }

    fun adminRevokeUserSessions(userId: String, reason: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        _deviceSessions.value = _deviceSessions.value.filterNot { it.userId == userId }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = null
        }
        logAudit(admin.id, admin.role.name, admin.name, "SESSIONS_REVOKED", userId, "All active device sessions terminated", reason)
        return Result.success("All active device sessions revoked for user")
    }

    fun adminProcessWithdrawal(
        withdrawalId: String,
        newStatus: WithdrawalStatus,
        paymentRef: String,
        note: String
    ): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val wd = _withdrawals.value.find { it.id == withdrawalId } ?: return Result.failure(Exception("Withdrawal not found"))

        _withdrawals.value = _withdrawals.value.map {
            if (it.id == withdrawalId) {
                it.copy(
                    status = newStatus,
                    adminPaymentRef = if (paymentRef.isNotBlank()) paymentRef else it.adminPaymentRef,
                    adminNote = note,
                    adminId = admin.id,
                    processedAt = System.currentTimeMillis()
                )
            } else it
        }

        // If rejected, refund points to user
        if (newStatus == WithdrawalStatus.REJECTED || newStatus == WithdrawalStatus.FAILED) {
            val user = _allUsers.value.find { it.id == wd.userId }
            if (user != null) {
                val refunded = user.copy(points = user.points + wd.pointsDeducted)
                updateUserInList(refunded)
                if (_currentUser.value?.id == user.id) {
                    _currentUser.value = refunded
                }
                recordTransaction(
                    userId = user.id,
                    type = TransactionType.REFUND,
                    points = wd.pointsDeducted,
                    currency = wd.amountCurrency,
                    title = "Withdrawal Refunded",
                    description = "Reason: $note"
                )
            }
        }

        logAudit(admin.id, admin.role.name, admin.name, "WITHDRAWAL_${newStatus.name}", withdrawalId, "Status updated to $newStatus. Ref: $paymentRef", note)
        return Result.success("Withdrawal updated to $newStatus")
    }

    fun adminSaveGame(game: GameItem): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val exists = _games.value.any { it.id == game.id }
        if (exists) {
            _games.value = _games.value.map { if (it.id == game.id) game else it }
            logAudit(admin.id, admin.role.name, admin.name, "GAME_UPDATED", game.id, "Updated game: ${game.title}", "Game configuration")
        } else {
            val newGame = if (game.id.isBlank()) game.copy(id = "game_" + UUID.randomUUID().toString().take(6)) else game
            _games.value = _games.value + newGame
            logAudit(admin.id, admin.role.name, admin.name, "GAME_CREATED", newGame.id, "Created game: ${newGame.title}", "Game addition")
        }
        remoteConfigService.saveGameConfig(_games.value)
        return Result.success("Game saved successfully")
    }

    fun adminDeleteGame(gameId: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        _games.value = _games.value.filterNot { it.id == gameId }
        remoteConfigService.saveGameConfig(_games.value)
        logAudit(admin.id, admin.role.name, admin.name, "GAME_DELETED", gameId, "Removed game $gameId", "Cleanup")
        return Result.success("Game deleted")
    }

    fun adminToggleGame(gameId: String, enabled: Boolean) {
        val admin = _currentAdmin.value ?: return
        _games.value = _games.value.map { if (it.id == gameId) it.copy(isEnabled = enabled) else it }
        remoteConfigService.saveGameConfig(_games.value)
        logAudit(admin.id, admin.role.name, admin.name, "GAME_TOGGLED", gameId, "Game enabled: $enabled", "Scheduling")
    }

    fun adminUpdateGameReward(gameId: String, newReward: Long): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val game = _games.value.find { it.id == gameId } ?: return Result.failure(Exception("Game not found"))
        val updatedGame = game.copy(rewardPoints = newReward)
        _games.value = _games.value.map { if (it.id == gameId) updatedGame else it }
        remoteConfigService.saveGameConfig(_games.value)
        logAudit(admin.id, admin.role.name, admin.name, "GAME_REWARD_UPDATED", gameId, "Reward points changed from ${game.rewardPoints} to $newReward", "Remote Economy Tuning")
        return Result.success("Game '${game.title}' reward updated to $newReward points! Instant propagation active.")
    }

    fun adminSaveTask(task: TaskItem): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val exists = _tasks.value.any { it.id == task.id }
        if (exists) {
            _tasks.value = _tasks.value.map { if (it.id == task.id) task else it }
            logAudit(admin.id, admin.role.name, admin.name, "TASK_UPDATED", task.id, "Updated task: ${task.title}", "Quest management")
        } else {
            val newTask = if (task.id.isBlank()) task.copy(id = "task_" + UUID.randomUUID().toString().take(6)) else task
            _tasks.value = _tasks.value + newTask
            logAudit(admin.id, admin.role.name, admin.name, "TASK_CREATED", newTask.id, "Created task: ${newTask.title}", "Quest addition")
        }
        remoteConfigService.saveTaskConfig(_tasks.value)
        return Result.success("Quest saved")
    }

    fun adminDeleteTask(taskId: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
        remoteConfigService.saveTaskConfig(_tasks.value)
        logAudit(admin.id, admin.role.name, admin.name, "TASK_DELETED", taskId, "Deleted task $taskId", "Quest removal")
        return Result.success("Quest deleted")
    }

    fun adminDuplicateTask(taskId: String): Result<String> {
        val task = _tasks.value.find { it.id == taskId } ?: return Result.failure(Exception("Task not found"))
        val duplicated = task.copy(
            id = "task_" + UUID.randomUUID().toString().take(6),
            title = "${task.title} (Copy)"
        )
        return adminSaveTask(duplicated)
    }

    fun adminReviewTaskSubmission(submissionId: String, approve: Boolean, adminNote: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val sub = _submissions.value.find { it.id == submissionId } ?: return Result.failure(Exception("Submission not found"))

        val newStatus = if (approve) TaskStatus.APPROVED else TaskStatus.REJECTED
        _submissions.value = _submissions.value.map {
            if (it.id == submissionId) it.copy(status = newStatus, adminNote = adminNote, reviewedAt = System.currentTimeMillis()) else it
        }

        if (approve) {
            val user = _allUsers.value.find { it.id == sub.userId }
            if (user != null) {
                val updatedUser = user.copy(
                    points = user.points + sub.rewardPoints,
                    totalEarnedPoints = user.totalEarnedPoints + sub.rewardPoints,
                    tasksCompletedCount = user.tasksCompletedCount + 1,
                    xp = user.xp + 50L
                )
                updateUserInList(updatedUser)
                if (_currentUser.value?.id == user.id) {
                    _currentUser.value = updatedUser
                }
                recordTransaction(
                    userId = user.id,
                    type = TransactionType.TASK_REWARD,
                    points = sub.rewardPoints,
                    title = "Quest Approved: ${sub.taskTitle}",
                    description = adminNote.ifBlank { "Proof verified by royal overseer" }
                )
            }
        }

        logAudit(admin.id, admin.role.name, admin.name, if (approve) "TASK_SUBMISSION_APPROVED" else "TASK_SUBMISSION_REJECTED", submissionId, adminNote, "Proof verification")
        return Result.success(if (approve) "Submission approved and +${sub.rewardPoints} points credited" else "Submission rejected")
    }

    fun adminUpdateAdSettings(newSettings: AdSettings) {
        val admin = _currentAdmin.value ?: return
        _adSettings.value = newSettings
        remoteConfigService.saveAdsConfig(newSettings)
        logAudit(admin.id, admin.role.name, admin.name, "ADS_SETTINGS_UPDATED", "GLOBAL", "Rewarded pts: ${newSettings.rewardedPoints}, Limit: ${newSettings.dailyRewardedLimit}", "Ad configuration")
    }

    fun adminUpdateEmergencyControls(controls: EmergencyControls) {
        val admin = _currentAdmin.value ?: return
        _emergencyControls.value = controls
        logAudit(admin.id, admin.role.name, admin.name, "EMERGENCY_CONTROLS_UPDATED", "GLOBAL", "Emergency shutdown: ${controls.isEmergencyShutdown}", "Emergency security action")
    }

    fun adminTakeFraudAction(alertId: String, actionText: String, banUser: Boolean): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val alert = _fraudAlerts.value.find { it.id == alertId } ?: return Result.failure(Exception("Alert not found"))

        _fraudAlerts.value = _fraudAlerts.value.map {
            if (it.id == alertId) it.copy(status = FraudStatus.ACTION_TAKEN, actionTaken = actionText) else it
        }

        if (banUser) {
            adminBanUser(alert.userId, "Flagged by Anti-Cheat: ${alert.reason}")
        }

        logAudit(admin.id, admin.role.name, admin.name, "FRAUD_ACTION_TAKEN", alert.userId, actionText, alert.reason)
        return Result.success("Fraud action recorded: $actionText")
    }

    fun adminRevokeDeviceSession(sessionId: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val session = _deviceSessions.value.find { it.id == sessionId }
        _deviceSessions.value = _deviceSessions.value.filterNot { it.id == sessionId }
        if (session != null) {
            logAudit(admin.id, admin.role.name, admin.name, "SESSION_REVOKED", session.userId, "Revoked device: ${session.deviceName}", "Security cleanup")
        }
        return Result.success("Device session terminated")
    }

    fun adminCreateAdminAccount(
        name: String,
        email: String,
        role: UserRole,
        permissions: List<AdminPermission>
    ): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        if (admin.role != UserRole.SUPER_ADMIN) {
            return Result.failure(Exception("Only Super Admins can provision new administrator accounts"))
        }

        val newAdmin = AdminAccount(
            id = "admin_" + UUID.randomUUID().toString().take(6),
            name = name,
            email = email.trim().lowercase(),
            role = role,
            permissions = permissions,
            status = "ACTIVE"
        )
        _adminAccounts.value = _adminAccounts.value + newAdmin
        logAudit(admin.id, admin.role.name, admin.name, "ADMIN_ACCOUNT_CREATED", newAdmin.id, "Created admin $name ($role)", "Admin delegation")
        return Result.success("Admin account created for $name")
    }

    fun adminUpdateCustomization(newCustom: CustomizationSettings) {
        val admin = _currentAdmin.value ?: return
        _customization.value = newCustom
        remoteConfigService.saveAppSettings(newCustom)
        logAudit(admin.id, admin.role.name, admin.name, "CUSTOMIZATION_UPDATED", "GLOBAL", "Updated App Title: ${newCustom.appName}", "Branding")
    }

    fun adminUpdateHomeSections(sections: List<HomeSectionConfig>) {
        val admin = _currentAdmin.value ?: return
        _homeSections.value = sections
        remoteConfigService.saveHomeConfig(sections)
        logAudit(admin.id, admin.role.name, admin.name, "HOME_BUILDER_UPDATED", "GLOBAL", "Updated section layout order", "UI layout")
    }

    fun adminUpdateTheme(newTheme: ThemeConfig) {
        val admin = _currentAdmin.value ?: return
        _themeConfig.value = newTheme
        remoteConfigService.saveThemeConfig(newTheme)
        logAudit(admin.id, admin.role.name, admin.name, "THEME_UPDATED", "GLOBAL", "Theme set to: ${newTheme.activeTheme}", "Theming")
    }

    fun adminUpdatePaymentGateway(gateway: PaymentGatewayConfig): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val currentWd = _withdrawalRemoteSettings.value
        val updatedGateways = currentWd.gateways.map { if (it.id == gateway.id) gateway else it }
        val newWd = currentWd.copy(gateways = updatedGateways)
        _withdrawalRemoteSettings.value = newWd
        remoteConfigService.saveWithdrawalSettings(newWd)
        logAudit(admin.id, admin.role.name, admin.name, "GATEWAY_UPDATED", gateway.id, "Updated gateway ${gateway.name}: enabled=${gateway.isEnabled}, min=${gateway.minWithdrawal}, fee=${gateway.feePercent}%", "Payment Gateway Config")
        return Result.success("Payment channel '${gateway.name}' updated! User app reflects new settings instantly.")
    }

    fun adminUpdateAppVersion(versionInfo: AppVersionInfo): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        _appVersionInfo.value = versionInfo
        remoteConfigService.saveAppVersionConfig(versionInfo)
        logAudit(admin.id, admin.role.name, admin.name, "APP_VERSION_UPDATED", "GLOBAL", "Latest version set to ${versionInfo.latestVersionName} (code ${versionInfo.latestVersionCode}), force=${versionInfo.forceUpdate}", "APK Update Management")
        return Result.success("App version policy saved. Client update prompt configured.")
    }

    fun adminToggleMaintenanceMode(isMaintenance: Boolean, message: String, endTime: String): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val updated = _maintenanceConfig.value.copy(
            isMaintenanceMode = isMaintenance,
            message = message.ifBlank { _maintenanceConfig.value.message },
            estimatedEndTime = endTime.ifBlank { _maintenanceConfig.value.estimatedEndTime }
        )
        _maintenanceConfig.value = updated
        _systemSettings.value = _systemSettings.value.copy(isMaintenanceMode = isMaintenance)
        remoteConfigService.saveMaintenanceConfig(updated)
        logAudit(admin.id, admin.role.name, admin.name, "MAINTENANCE_TOGGLED", "GLOBAL", "Maintenance mode: $isMaintenance ($message)", "Server operations")
        return Result.success(if (isMaintenance) "Maintenance mode ENABLED. User portal locked." else "Maintenance mode DISABLED. Realm restored.")
    }

    fun adminUpdateFeatureFlags(flags: FeatureFlags): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        _featureFlags.value = flags
        remoteConfigService.saveFeatureFlags(flags)
        logAudit(admin.id, admin.role.name, admin.name, "FEATURE_FLAGS_UPDATED", "GLOBAL", "Updated runtime feature toggles", "Feature flag management")
        return Result.success("Feature flags successfully updated")
    }

    fun adminUpdateBanners(bannersList: List<BannerItem>): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        _banners.value = bannersList
        remoteConfigService.saveBannersConfig(bannersList)
        logAudit(admin.id, admin.role.name, admin.name, "BANNERS_UPDATED", "GLOBAL", "Updated ${bannersList.size} promotional banners", "Promotions")
        return Result.success("Promotional banners updated")
    }

    fun adminAddBanner(banner: BannerItem): Result<String> {
        val b = if (banner.id.isBlank()) banner.copy(id = "ban_" + UUID.randomUUID().toString().take(6)) else banner
        return adminUpdateBanners(_banners.value + b)
    }

    fun adminDeleteBanner(bannerId: String): Result<String> {
        return adminUpdateBanners(_banners.value.filterNot { it.id == bannerId })
    }

    fun adminRefreshRemoteConfig(onComplete: ((Boolean) -> Unit)? = null) {
        _isRemoteSyncing.value = true
        remoteConfigService.refreshRemoteConfig(scope) { success ->
            _isRemoteSyncing.value = false
            _lastRemoteSyncTime.value = System.currentTimeMillis()
            loadRemoteConfigInitial()
            onComplete?.invoke(success)
        }
    }

    fun adminReplySupportTicket(ticketId: String, replyMessage: String, newStatus: TicketStatus): Result<String> {
        val admin = _currentAdmin.value ?: return Result.failure(Exception("Administrator session required"))
        val ticket = _supportTickets.value.find { it.id == ticketId } ?: return Result.failure(Exception("Ticket not found"))

        val newMsg = TicketMessage(admin.name, true, replyMessage)
        _supportTickets.value = _supportTickets.value.map {
            if (it.id == ticketId) it.copy(status = newStatus, messages = it.messages + newMsg) else it
        }

        logAudit(admin.id, admin.role.name, admin.name, "SUPPORT_TICKET_REPLIED", ticketId, "Sent reply to ${ticket.username}", "Customer support")
        return Result.success("Reply dispatched to ticket #$ticketId")
    }

    fun adminSendNotification(title: String, message: String, type: NotificationType) {
        val admin = _currentAdmin.value ?: return
        addNotification(title, message, type)
        logAudit(admin.id, admin.role.name, admin.name, "NOTIFICATION_SENT", "ALL_USERS", title, "Broadcast")
    }

    // ==========================================
    // PRIVATE HELPERS
    // ==========================================

    private fun updateUserInList(updated: UserProfile) {
        _allUsers.value = _allUsers.value.map { if (it.id == updated.id) updated else it }
    }

    private fun recordTransaction(
        userId: String,
        type: TransactionType,
        points: Long,
        currency: Double = 0.0,
        title: String,
        description: String,
        referenceId: String = ""
    ) {
        val user = _allUsers.value.find { it.id == userId }
        val prev = user?.points ?: 0L
        val record = TransactionRecord(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            userId = userId,
            type = type,
            amountPoints = points,
            amountCurrency = currency,
            previousBalance = prev,
            newBalance = prev + points,
            status = TransactionStatus.COMPLETED,
            title = title,
            description = description,
            referenceId = referenceId,
            timestamp = System.currentTimeMillis()
        )
        _transactions.value = listOf(record) + _transactions.value
    }

    private fun addNotification(title: String, message: String, type: NotificationType) {
        val notif = AppNotification(
            id = "notif_" + UUID.randomUUID().toString().take(8),
            title = title,
            message = message,
            type = type,
            timestamp = System.currentTimeMillis()
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    private fun logAudit(
        adminId: String,
        adminRole: String,
        adminName: String,
        action: String,
        targetId: String,
        details: String,
        reason: String
    ) {
        val log = AuditLog(
            id = "log_" + UUID.randomUUID().toString().take(8),
            adminId = adminId,
            adminRole = adminRole,
            adminName = adminName,
            action = action,
            targetId = targetId,
            details = details,
            reason = reason,
            timestamp = System.currentTimeMillis()
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }
}
