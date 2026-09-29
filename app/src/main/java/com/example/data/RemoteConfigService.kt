package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * RemoteConfigService
 * Central remote configuration engine for Magical World.
 * Enables live OTA configuration updates from Firebase Firestore with local fallback caching.
 */
class RemoteConfigService(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("magical_world_remote_config_cache", Context.MODE_PRIVATE)

    private val TAG = "RemoteConfigService"

    // Firestore Collections
    companion object {
        const val COL_APP_SETTINGS = "app_settings"
        const val COL_HOME_SECTIONS = "home_sections"
        const val COL_GAMES = "games"
        const val COL_TASKS = "tasks"
        const val COL_ADS_CONFIG = "ads_config"
        const val COL_AD_PLACEMENTS = "ad_placements"
        const val COL_REWARDS = "rewards"
        const val COL_REFERRAL_SETTINGS = "referral_settings"
        const val COL_WITHDRAWAL_SETTINGS = "withdrawal_settings"
        const val COL_THEMES = "themes"
        const val COL_MEDIA = "media"
        const val COL_BANNERS = "banners"
        const val COL_CONTENT = "content"
        const val COL_EVENTS = "events"
        const val COL_FEATURE_FLAGS = "feature_flags"
        const val COL_APP_VERSIONS = "app_versions"
    }

    private fun getDb(): FirebaseFirestore? = FirebaseManager.firestore

    fun initializeRemoteConfig() {
        Log.d(TAG, "Initializing RemoteConfigService with local cache fallback.")
    }

    // ==========================================
    // 1. APP SETTINGS & BRANDING
    // ==========================================

    fun loadAppSettings(): CustomizationSettings {
        val cachedJson = prefs.getString("cache_app_settings", null)
        if (cachedJson != null) {
            try {
                val json = JSONObject(cachedJson)
                return CustomizationSettings(
                    appName = json.optString("appName", "Magical World"),
                    appTagline = json.optString("appTagline", "Explore the Lands • Earn • Play • Complete Quests"),
                    logoUrl = json.optString("logoUrl", ""),
                    bannerUrl = json.optString("bannerUrl", ""),
                    announcementText = json.optString("announcementText", "🌟 Welcome Adventurers! Double rewards active on Quest Board this week! ✨"),
                    supportEmail = json.optString("supportEmail", "support@magicalworld.game"),
                    supportPhone = json.optString("supportPhone", "+880 1800-MAGICW"),
                    telegramChannel = json.optString("telegramChannel", "https://t.me/magicalworldgame"),
                    discordCommunity = json.optString("discordCommunity", "https://discord.gg/magicalworld"),
                    privacyPolicyText = json.optString("privacyPolicyText", "Magical World values your privacy. User data is encrypted."),
                    termsOfServiceText = json.optString("termsOfServiceText", "Fair play is strictly enforced.")
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed to parse cached app settings: ${e.message}")
            }
        }
        return CustomizationSettings()
    }

    fun saveAppSettings(settings: CustomizationSettings, onComplete: ((Boolean) -> Unit)? = null) {
        // 1. Save to local cache
        try {
            val json = JSONObject().apply {
                put("appName", settings.appName)
                put("appTagline", settings.appTagline)
                put("logoUrl", settings.logoUrl)
                put("bannerUrl", settings.bannerUrl)
                put("announcementText", settings.announcementText)
                put("supportEmail", settings.supportEmail)
                put("supportPhone", settings.supportPhone)
                put("telegramChannel", settings.telegramChannel)
                put("discordCommunity", settings.discordCommunity)
                put("privacyPolicyText", settings.privacyPolicyText)
                put("termsOfServiceText", settings.termsOfServiceText)
            }
            prefs.edit().putString("cache_app_settings", json.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Cache error saving app settings", e)
        }

        // 2. Push to Firestore
        val db = getDb()
        if (db == null) {
            onComplete?.invoke(false)
            return
        }

        val map = hashMapOf(
            "appName" to settings.appName,
            "appTagline" to settings.appTagline,
            "logoUrl" to settings.logoUrl,
            "bannerUrl" to settings.bannerUrl,
            "announcementText" to settings.announcementText,
            "supportEmail" to settings.supportEmail,
            "supportPhone" to settings.supportPhone,
            "telegramChannel" to settings.telegramChannel,
            "discordCommunity" to settings.discordCommunity,
            "privacyPolicyText" to settings.privacyPolicyText,
            "termsOfServiceText" to settings.termsOfServiceText,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection(COL_APP_SETTINGS).document("customization")
            .set(map)
            .addOnSuccessListener {
                Log.d(TAG, "Saved remote app_settings successfully")
                onComplete?.invoke(true)
            }
            .addOnFailureListener {
                Log.w(TAG, "Error saving remote app_settings", it)
                onComplete?.invoke(false)
            }
    }

    // ==========================================
    // 2. HOME SECTIONS (DYNAMIC HOME BUILDER)
    // ==========================================

    fun loadHomeConfig(): List<HomeSectionConfig> {
        val cached = prefs.getString("cache_home_sections", null)
        if (cached != null) {
            try {
                val array = JSONArray(cached)
                val list = mutableListOf<HomeSectionConfig>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        HomeSectionConfig(
                            id = obj.optString("id", "sec_$i"),
                            name = obj.optString("name", ""),
                            title = obj.optString("title", ""),
                            subtitle = obj.optString("subtitle", ""),
                            isEnabled = obj.optBoolean("isEnabled", true),
                            orderIndex = obj.optInt("orderIndex", i),
                            layoutStyle = obj.optString("layoutStyle", "DEFAULT"),
                            customActionUrl = obj.optString("customActionUrl", "")
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                Log.w(TAG, "Error parsing cached home sections: ${e.message}")
            }
        }

        // Default home layout
        return listOf(
            HomeSectionConfig("sec_hero", "Hero Banner", "Explore the Realm", "Earn to Game", true, 0, "HERO_BANNER"),
            HomeSectionConfig("sec_announce", "Announcements", "Kingdom News", "Live Ticker", true, 1, "ANNOUNCEMENT"),
            HomeSectionConfig("sec_wallet", "Wallet Summary", "Treasury & Points", "Current Balance", true, 2, "STATS_WALLET"),
            HomeSectionConfig("sec_watch_earn", "Watch & Earn", "Ad Sanctuary", "Daily Bonus", true, 3, "ACTION_GRID"),
            HomeSectionConfig("sec_games", "Featured Games", "Arcade Arenas", "Play & Win", true, 4, "FEATURED_GAMES"),
            HomeSectionConfig("sec_quests", "Quests & Tasks", "Royal Quests", "Bounties", true, 5, "TOP_QUESTS"),
            HomeSectionConfig("sec_referral", "Referral Summon", "Invite Friends", "Earn +100", true, 6, "PROMO_BANNER"),
            HomeSectionConfig("sec_txns", "Recent Activity", "Ledger", "Recent Deeds", true, 7, "LIST")
        )
    }

    fun saveHomeConfig(sections: List<HomeSectionConfig>, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val array = JSONArray()
            sections.forEach { s ->
                val obj = JSONObject().apply {
                    put("id", s.id)
                    put("name", s.name)
                    put("title", s.title)
                    put("subtitle", s.subtitle)
                    put("isEnabled", s.isEnabled)
                    put("orderIndex", s.orderIndex)
                    put("layoutStyle", s.layoutStyle)
                    put("customActionUrl", s.customActionUrl)
                }
                array.put(obj)
            }
            prefs.edit().putString("cache_home_sections", array.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching home config", e)
        }

        val db = getDb()
        if (db == null) {
            onComplete?.invoke(false)
            return
        }

        val batch = db.batch()
        sections.forEach { s ->
            val docRef = db.collection(COL_HOME_SECTIONS).document(s.id)
            val data = hashMapOf(
                "id" to s.id,
                "name" to s.name,
                "title" to s.title,
                "subtitle" to s.subtitle,
                "isEnabled" to s.isEnabled,
                "orderIndex" to s.orderIndex,
                "layoutStyle" to s.layoutStyle,
                "customActionUrl" to s.customActionUrl,
                "updatedAt" to System.currentTimeMillis()
            )
            batch.set(docRef, data)
        }

        batch.commit()
            .addOnSuccessListener {
                Log.d(TAG, "Synced remote home_sections batch")
                onComplete?.invoke(true)
            }
            .addOnFailureListener {
                Log.w(TAG, "Failed syncing home_sections batch", it)
                onComplete?.invoke(false)
            }
    }

    // ==========================================
    // 3. REMOTE BANNERS
    // ==========================================

    fun loadBannersConfig(): List<BannerItem> {
        val cached = prefs.getString("cache_banners", null)
        if (cached != null) {
            try {
                val arr = JSONArray(cached)
                val list = mutableListOf<BannerItem>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    list.add(
                        BannerItem(
                            id = o.optString("id", "ban_$i"),
                            title = o.optString("title", ""),
                            subtitle = o.optString("subtitle", ""),
                            imageUrl = o.optString("imageUrl", ""),
                            actionType = o.optString("actionType", "NAVIGATE_EARN"),
                            actionTarget = o.optString("actionTarget", ""),
                            isEnabled = o.optBoolean("isEnabled", true),
                            orderIndex = o.optInt("orderIndex", i)
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached banners: ${e.message}")
            }
        }

        return listOf(
            BannerItem(
                id = "ban_main_1",
                title = "Magical World Grand Championship",
                subtitle = "Race in Crystal Car Rush and climb the leaderboard!",
                imageUrl = "",
                actionType = "NAVIGATE_GAMES",
                actionTarget = "crystal_rush",
                isEnabled = true,
                orderIndex = 0
            ),
            BannerItem(
                id = "ban_quest_2",
                title = "Double Quest Rewards",
                subtitle = "Complete royal tasks for up to +200 bonus crystals!",
                imageUrl = "",
                actionType = "NAVIGATE_TASKS",
                actionTarget = "tasks",
                isEnabled = true,
                orderIndex = 1
            ),
            BannerItem(
                id = "ban_refer_3",
                title = "Summon Companions",
                subtitle = "Earn 100 crystals for every explorer who registers.",
                imageUrl = "",
                actionType = "NAVIGATE_REFERRAL",
                actionTarget = "referrals",
                isEnabled = true,
                orderIndex = 2
            )
        )
    }

    fun saveBannersConfig(banners: List<BannerItem>, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val arr = JSONArray()
            banners.forEach { b ->
                val obj = JSONObject().apply {
                    put("id", b.id)
                    put("title", b.title)
                    put("subtitle", b.subtitle)
                    put("imageUrl", b.imageUrl)
                    put("actionType", b.actionType)
                    put("actionTarget", b.actionTarget)
                    put("isEnabled", b.isEnabled)
                    put("orderIndex", b.orderIndex)
                }
                arr.put(obj)
            }
            prefs.edit().putString("cache_banners", arr.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching banners", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val batch = db.batch()
        banners.forEach { b ->
            val docRef = db.collection(COL_BANNERS).document(b.id)
            val data = hashMapOf(
                "id" to b.id,
                "title" to b.title,
                "subtitle" to b.subtitle,
                "imageUrl" to b.imageUrl,
                "actionType" to b.actionType,
                "actionTarget" to b.actionTarget,
                "isEnabled" to b.isEnabled,
                "orderIndex" to b.orderIndex,
                "updatedAt" to System.currentTimeMillis()
            )
            batch.set(docRef, data)
        }
        batch.commit()
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 4. REMOTE GAME CONFIGURATION & REWARDS
    // ==========================================

    fun loadGameConfig(): List<GameItem> {
        val cached = prefs.getString("cache_games", null)
        if (cached != null) {
            try {
                val arr = JSONArray(cached)
                val list = mutableListOf<GameItem>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    list.add(
                        GameItem(
                            id = o.optString("id", "game_$i"),
                            title = o.optString("title", ""),
                            description = o.optString("description", ""),
                            type = try { GameType.valueOf(o.optString("type", "CRYSTAL_CAR_RUSH")) } catch(e: Exception) { GameType.CRYSTAL_CAR_RUSH },
                            rewardPoints = o.optLong("rewardPoints", 35L),
                            dailyLimit = o.optInt("dailyLimit", 10),
                            playsToday = o.optInt("playsToday", 0),
                            isEnabled = o.optBoolean("isEnabled", true),
                            difficulty = o.optString("difficulty", "Medium"),
                            interstitialAfterPlays = o.optInt("interstitialAfterPlays", 2),
                            category = o.optString("category", "Arcade")
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached games: ${e.message}")
            }
        }

        return listOf(
            GameItem("game_rush", "Crystal Car Rush", "Dodge enchanted road obstacles and collect floating crystals.", GameType.CRYSTAL_CAR_RUSH, 50L, 10, 0, true, "Easy", 2, "Racing"),
            GameItem("game_tap", "Magic Gem Tapper", "Tap glowing crystal runes in rhythm before they vanish into ether.", GameType.MAGIC_GEM_TAPPER, 35L, 15, 0, true, "Medium", 3, "Arcade"),
            GameItem("game_spin", "Wheel of Fortune", "Spin the celestial zodiac wheel for guaranteed bounty rewards.", GameType.FORTUNE_SPIN, 25L, 5, 0, true, "Luck", 1, "Chance"),
            GameItem("game_runner", "Castle Runner", "Sprint across misty castle ramparts and leap over mystical gaps.", GameType.CASTLE_RUNNER, 40L, 10, 0, true, "Hard", 2, "Platformer"),
            GameItem("game_puzzle", "Rune Matcher", "Align 3 celestial gemstones of identical colors to unlock chests.", GameType.PUZZLE_MATCH, 45L, 12, 0, true, "Medium", 2, "Puzzle")
        )
    }

    fun saveGameConfig(games: List<GameItem>, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val arr = JSONArray()
            games.forEach { g ->
                val o = JSONObject().apply {
                    put("id", g.id)
                    put("title", g.title)
                    put("description", g.description)
                    put("type", g.type.name)
                    put("rewardPoints", g.rewardPoints)
                    put("dailyLimit", g.dailyLimit)
                    put("isEnabled", g.isEnabled)
                    put("difficulty", g.difficulty)
                    put("interstitialAfterPlays", g.interstitialAfterPlays)
                    put("category", g.category)
                }
                arr.put(o)
            }
            prefs.edit().putString("cache_games", arr.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching games", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val batch = db.batch()
        games.forEach { g ->
            val ref = db.collection(COL_GAMES).document(g.id)
            val data = hashMapOf(
                "id" to g.id,
                "title" to g.title,
                "description" to g.description,
                "type" to g.type.name,
                "rewardPoints" to g.rewardPoints,
                "dailyLimit" to g.dailyLimit,
                "isEnabled" to g.isEnabled,
                "difficulty" to g.difficulty,
                "interstitialAfterPlays" to g.interstitialAfterPlays,
                "category" to g.category,
                "updatedAt" to System.currentTimeMillis()
            )
            batch.set(ref, data)
        }
        batch.commit()
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 5. REMOTE TASKS CONFIGURATION
    // ==========================================

    fun loadTaskConfig(): List<TaskItem> {
        val cached = prefs.getString("cache_tasks", null)
        if (cached != null) {
            try {
                val arr = JSONArray(cached)
                val list = mutableListOf<TaskItem>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    list.add(
                        TaskItem(
                            id = o.optString("id", "task_$i"),
                            title = o.optString("title", ""),
                            description = o.optString("description", ""),
                            pointsReward = o.optLong("pointsReward", 50L),
                            iconName = o.optString("iconName", "quest"),
                            targetUrl = o.optString("targetUrl", ""),
                            type = try { TaskType.valueOf(o.optString("type", "VISIT_WEBSITE")) } catch(e: Exception) { TaskType.VISIT_WEBSITE },
                            verificationInstructions = o.optString("verificationInstructions", "Complete the quest and submit proof."),
                            isPaused = o.optBoolean("isPaused", false),
                            dailyLimit = o.optInt("dailyLimit", 1),
                            userLimit = o.optInt("userLimit", 1)
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached tasks: ${e.message}")
            }
        }

        return listOf(
            TaskItem("task_1", "Visit Archmage News Portal", "Browse the community updates portal for 30 seconds.", 60L, "explore", "https://magicalworld.game/news", TaskType.VISIT_WEBSITE, "Stay on the page until progress completes.", false, 3, 3),
            TaskItem("task_2", "Subscribe to Telegram Realm", "Join the official announcement channel for real-time giveaways.", 120L, "send", "https://t.me/magicalworldgame", TaskType.CUSTOM_TASK, "Submit a screenshot of the joined group or your username.", false, 1, 1),
            TaskItem("task_3", "Watch Realm Cinematic Reel", "Watch the fantasy trailer video to earn gold crystals.", 80L, "play_arrow", "https://youtube.com/shorts/sample", TaskType.WATCH_CONTENT, "Watch the full 45-second cinematic presentation.", false, 5, 5),
            TaskItem("task_4", "Rate Realm on Play Store", "Leave honest feedback and rating for Magical World.", 150L, "star", "market://details?id=com.aistudio.magicalworld.qptxz", TaskType.APP_DOWNLOAD, "Submit proof of review.", false, 1, 1),
            TaskItem("task_5", "Daily Adventurer Survey", "Answer 3 quick questions about today's gameplay experience.", 75L, "assignment", "https://magicalworld.game/survey", TaskType.SURVEY, "Select all answers truthfully.", false, 1, 1)
        )
    }

    fun saveTaskConfig(tasks: List<TaskItem>, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val arr = JSONArray()
            tasks.forEach { t ->
                val o = JSONObject().apply {
                    put("id", t.id)
                    put("title", t.title)
                    put("description", t.description)
                    put("pointsReward", t.pointsReward)
                    put("iconName", t.iconName)
                    put("targetUrl", t.targetUrl)
                    put("type", t.type.name)
                    put("verificationInstructions", t.verificationInstructions)
                    put("isPaused", t.isPaused)
                    put("dailyLimit", t.dailyLimit)
                    put("userLimit", t.userLimit)
                }
                arr.put(o)
            }
            prefs.edit().putString("cache_tasks", arr.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching tasks", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val batch = db.batch()
        tasks.forEach { t ->
            val ref = db.collection(COL_TASKS).document(t.id)
            val data = hashMapOf(
                "id" to t.id,
                "title" to t.title,
                "description" to t.description,
                "pointsReward" to t.pointsReward,
                "iconName" to t.iconName,
                "targetUrl" to t.targetUrl,
                "type" to t.type.name,
                "verificationInstructions" to t.verificationInstructions,
                "isPaused" to t.isPaused,
                "dailyLimit" to t.dailyLimit,
                "userLimit" to t.userLimit,
                "updatedAt" to System.currentTimeMillis()
            )
            batch.set(ref, data)
        }
        batch.commit()
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 6. REMOTE WITHDRAWAL & GATEWAYS CONFIGURATION
    // (bkash, nagad, rocket, upay, binance, upi, bank)
    // ==========================================

    fun loadWithdrawalSettings(): WithdrawalRemoteSettings {
        val cached = prefs.getString("cache_withdrawal_settings", null)
        if (cached != null) {
            try {
                val json = JSONObject(cached)
                val ptsPerCurr = json.optLong("pointsPerCurrencyUnit", 100L)
                val currSym = json.optString("currencySymbol", "৳")
                val dailyLim = json.optDouble("dailyWithdrawalLimit", 10000.0)
                val procText = json.optString("processingTimeText", "Payouts processed within 1 - 24 hours")

                val gArray = json.optJSONArray("gateways")
                val gatewaysList = mutableListOf<PaymentGatewayConfig>()
                if (gArray != null) {
                    for (i in 0 until gArray.length()) {
                        val g = gArray.getJSONObject(i)
                        gatewaysList.add(
                            PaymentGatewayConfig(
                                id = g.optString("id", "gw_$i"),
                                name = g.optString("name", ""),
                                isEnabled = g.optBoolean("isEnabled", true),
                                minWithdrawal = g.optDouble("minWithdrawal", 50.0),
                                maxWithdrawal = g.optDouble("maxWithdrawal", 5000.0),
                                feePercent = g.optDouble("feePercent", 0.0),
                                accountPlaceholder = g.optString("accountPlaceholder", "01XXXXXXXXX"),
                                instructionNote = g.optString("instructionNote", ""),
                                badge = g.optString("badge", "Instant"),
                                currencyCode = g.optString("currencyCode", "BDT")
                            )
                        )
                    }
                }

                if (gatewaysList.isNotEmpty()) {
                    return WithdrawalRemoteSettings(
                        pointsPerCurrencyUnit = ptsPerCurr,
                        currencySymbol = currSym,
                        dailyWithdrawalLimit = dailyLim,
                        processingTimeText = procText,
                        gateways = gatewaysList
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached withdrawal settings: ${e.message}")
            }
        }
        return WithdrawalRemoteSettings()
    }

    fun saveWithdrawalSettings(settings: WithdrawalRemoteSettings, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val json = JSONObject().apply {
                put("pointsPerCurrencyUnit", settings.pointsPerCurrencyUnit)
                put("currencySymbol", settings.currencySymbol)
                put("dailyWithdrawalLimit", settings.dailyWithdrawalLimit)
                put("processingTimeText", settings.processingTimeText)

                val gArr = JSONArray()
                settings.gateways.forEach { g ->
                    val o = JSONObject().apply {
                        put("id", g.id)
                        put("name", g.name)
                        put("isEnabled", g.isEnabled)
                        put("minWithdrawal", g.minWithdrawal)
                        put("maxWithdrawal", g.maxWithdrawal)
                        put("feePercent", g.feePercent)
                        put("accountPlaceholder", g.accountPlaceholder)
                        put("instructionNote", g.instructionNote)
                        put("badge", g.badge)
                        put("currencyCode", g.currencyCode)
                    }
                    gArr.put(o)
                }
                put("gateways", gArr)
            }
            prefs.edit().putString("cache_withdrawal_settings", json.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching withdrawal settings", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val map = hashMapOf(
            "pointsPerCurrencyUnit" to settings.pointsPerCurrencyUnit,
            "currencySymbol" to settings.currencySymbol,
            "dailyWithdrawalLimit" to settings.dailyWithdrawalLimit,
            "processingTimeText" to settings.processingTimeText,
            "gateways" to settings.gateways.map { g ->
                mapOf(
                    "id" to g.id,
                    "name" to g.name,
                    "isEnabled" to g.isEnabled,
                    "minWithdrawal" to g.minWithdrawal,
                    "maxWithdrawal" to g.maxWithdrawal,
                    "feePercent" to g.feePercent,
                    "accountPlaceholder" to g.accountPlaceholder,
                    "instructionNote" to g.instructionNote,
                    "badge" to g.badge,
                    "currencyCode" to g.currencyCode
                )
            },
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection(COL_WITHDRAWAL_SETTINGS).document("payout_config")
            .set(map)
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 7. REMOTE ADS CONFIGURATION
    // ==========================================

    fun loadAdsConfig(): AdSettings {
        val cached = prefs.getString("cache_ads_config", null)
        if (cached != null) {
            try {
                val j = JSONObject(cached)
                return AdSettings(
                    isTestMode = j.optBoolean("isTestMode", true),
                    isAdsGloballyEnabled = j.optBoolean("isAdsGloballyEnabled", true),
                    isRewardedAdsEnabled = j.optBoolean("isRewardedAdsEnabled", true),
                    isBannerEnabled = j.optBoolean("isBannerEnabled", true),
                    isInterstitialEnabled = j.optBoolean("isInterstitialEnabled", true),
                    rewardedPoints = j.optLong("rewardedPoints", 25L),
                    dailyRewardedLimit = j.optInt("dailyRewardedLimit", 20),
                    minimumSecondsBetweenAds = j.optInt("minimumSecondsBetweenAds", 15)
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached ads config: ${e.message}")
            }
        }
        return AdSettings()
    }

    fun saveAdsConfig(ads: AdSettings, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val j = JSONObject().apply {
                put("isTestMode", ads.isTestMode)
                put("isAdsGloballyEnabled", ads.isAdsGloballyEnabled)
                put("isRewardedAdsEnabled", ads.isRewardedAdsEnabled)
                put("isBannerEnabled", ads.isBannerEnabled)
                put("isInterstitialEnabled", ads.isInterstitialEnabled)
                put("rewardedPoints", ads.rewardedPoints)
                put("dailyRewardedLimit", ads.dailyRewardedLimit)
                put("minimumSecondsBetweenAds", ads.minimumSecondsBetweenAds)
            }
            prefs.edit().putString("cache_ads_config", j.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching ads config", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val map = hashMapOf(
            "isTestMode" to ads.isTestMode,
            "isAdsGloballyEnabled" to ads.isAdsGloballyEnabled,
            "isRewardedAdsEnabled" to ads.isRewardedAdsEnabled,
            "isBannerEnabled" to ads.isBannerEnabled,
            "isInterstitialEnabled" to ads.isInterstitialEnabled,
            "rewardedPoints" to ads.rewardedPoints,
            "dailyRewardedLimit" to ads.dailyRewardedLimit,
            "minimumSecondsBetweenAds" to ads.minimumSecondsBetweenAds,
            "updatedAt" to System.currentTimeMillis()
        )

        db.collection(COL_ADS_CONFIG).document("main_config")
            .set(map)
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 8. THEME & COLORS CONFIGURATION
    // ==========================================

    fun loadThemeConfig(): ThemeConfig {
        val cached = prefs.getString("cache_theme_config", null)
        if (cached != null) {
            try {
                val j = JSONObject(cached)
                return ThemeConfig(
                    activeTheme = j.optString("activeTheme", "Neon Fantasy"),
                    primaryColorHex = j.optString("primaryColorHex", "#38BDF8"),
                    secondaryColorHex = j.optString("secondaryColorHex", "#A855F7"),
                    accentGoldHex = j.optString("accentGoldHex", "#FBBF24"),
                    backgroundDarkHex = j.optString("backgroundDarkHex", "#090D1E"),
                    isDarkMode = j.optBoolean("isDarkMode", true)
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached theme: ${e.message}")
            }
        }
        return ThemeConfig()
    }

    fun saveThemeConfig(theme: ThemeConfig, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val j = JSONObject().apply {
                put("activeTheme", theme.activeTheme)
                put("primaryColorHex", theme.primaryColorHex)
                put("secondaryColorHex", theme.secondaryColorHex)
                put("accentGoldHex", theme.accentGoldHex)
                put("backgroundDarkHex", theme.backgroundDarkHex)
                put("isDarkMode", theme.isDarkMode)
            }
            prefs.edit().putString("cache_theme_config", j.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching theme", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val map = hashMapOf(
            "activeTheme" to theme.activeTheme,
            "primaryColorHex" to theme.primaryColorHex,
            "secondaryColorHex" to theme.secondaryColorHex,
            "accentGoldHex" to theme.accentGoldHex,
            "backgroundDarkHex" to theme.backgroundDarkHex,
            "isDarkMode" to theme.isDarkMode,
            "updatedAt" to System.currentTimeMillis()
        )
        db.collection(COL_THEMES).document("active_theme")
            .set(map)
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 9. FEATURE FLAGS & MODULE TOGGLES
    // ==========================================

    fun loadFeatureFlags(): FeatureFlags {
        val cached = prefs.getString("cache_feature_flags", null)
        if (cached != null) {
            try {
                val j = JSONObject(cached)
                return FeatureFlags(
                    enableGames = j.optBoolean("enableGames", true),
                    enableTasks = j.optBoolean("enableTasks", true),
                    enableAds = j.optBoolean("enableAds", true),
                    enableWithdrawals = j.optBoolean("enableWithdrawals", true),
                    enableReferrals = j.optBoolean("enableReferrals", true),
                    enableRegistration = j.optBoolean("enableRegistration", true),
                    enableLeaderboard = j.optBoolean("enableLeaderboard", true),
                    enableSupportChat = j.optBoolean("enableSupportChat", true),
                    enableDynamicTheming = j.optBoolean("enableDynamicTheming", true),
                    enableLiveNotifications = j.optBoolean("enableLiveNotifications", true)
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached feature flags: ${e.message}")
            }
        }
        return FeatureFlags()
    }

    fun saveFeatureFlags(flags: FeatureFlags, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val j = JSONObject().apply {
                put("enableGames", flags.enableGames)
                put("enableTasks", flags.enableTasks)
                put("enableAds", flags.enableAds)
                put("enableWithdrawals", flags.enableWithdrawals)
                put("enableReferrals", flags.enableReferrals)
                put("enableRegistration", flags.enableRegistration)
                put("enableLeaderboard", flags.enableLeaderboard)
                put("enableSupportChat", flags.enableSupportChat)
                put("enableDynamicTheming", flags.enableDynamicTheming)
                put("enableLiveNotifications", flags.enableLiveNotifications)
            }
            prefs.edit().putString("cache_feature_flags", j.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching flags", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val map = hashMapOf(
            "enableGames" to flags.enableGames,
            "enableTasks" to flags.enableTasks,
            "enableAds" to flags.enableAds,
            "enableWithdrawals" to flags.enableWithdrawals,
            "enableReferrals" to flags.enableReferrals,
            "enableRegistration" to flags.enableRegistration,
            "enableLeaderboard" to flags.enableLeaderboard,
            "enableSupportChat" to flags.enableSupportChat,
            "enableDynamicTheming" to flags.enableDynamicTheming,
            "enableLiveNotifications" to flags.enableLiveNotifications,
            "updatedAt" to System.currentTimeMillis()
        )
        db.collection(COL_FEATURE_FLAGS).document("runtime_flags")
            .set(map)
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 10. MAINTENANCE MODE SETTINGS
    // ==========================================

    fun loadMaintenanceConfig(): MaintenanceConfig {
        val cached = prefs.getString("cache_maintenance_config", null)
        if (cached != null) {
            try {
                val j = JSONObject(cached)
                return MaintenanceConfig(
                    isMaintenanceMode = j.optBoolean("isMaintenanceMode", false),
                    title = j.optString("title", "Magical World Realm Maintenance"),
                    message = j.optString("message", "The Grand Archmages are tuning the crystal servers and enhancing security runes. We will return shortly with higher rewards!"),
                    estimatedEndTime = j.optString("estimatedEndTime", "1 - 2 hours"),
                    supportEmail = j.optString("supportEmail", "support@magicalworld.game"),
                    allowAdminBypass = j.optBoolean("allowAdminBypass", true)
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached maintenance config: ${e.message}")
            }
        }
        return MaintenanceConfig()
    }

    fun saveMaintenanceConfig(config: MaintenanceConfig, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val j = JSONObject().apply {
                put("isMaintenanceMode", config.isMaintenanceMode)
                put("title", config.title)
                put("message", config.message)
                put("estimatedEndTime", config.estimatedEndTime)
                put("supportEmail", config.supportEmail)
                put("allowAdminBypass", config.allowAdminBypass)
            }
            prefs.edit().putString("cache_maintenance_config", j.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching maintenance config", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val map = hashMapOf(
            "isMaintenanceMode" to config.isMaintenanceMode,
            "title" to config.title,
            "message" to config.message,
            "estimatedEndTime" to config.estimatedEndTime,
            "supportEmail" to config.supportEmail,
            "allowAdminBypass" to config.allowAdminBypass,
            "updatedAt" to System.currentTimeMillis()
        )
        db.collection(COL_APP_SETTINGS).document("maintenance")
            .set(map)
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 11. APK VERSION & UPDATE CHECKER
    // ==========================================

    fun loadAppVersionConfig(): AppVersionInfo {
        val cached = prefs.getString("cache_app_version", null)
        if (cached != null) {
            try {
                val j = JSONObject(cached)
                return AppVersionInfo(
                    currentVersionCode = 1,
                    currentVersionName = "1.0.0",
                    latestVersionCode = j.optInt("latestVersionCode", 1),
                    latestVersionName = j.optString("latestVersionName", "1.0.0"),
                    minSupportedVersionCode = j.optInt("minSupportedVersionCode", 1),
                    forceUpdate = j.optBoolean("forceUpdate", false),
                    downloadUrl = j.optString("downloadUrl", "https://play.google.com/store/apps/details?id=com.aistudio.magicalworld.qptxz"),
                    releaseNotes = j.optString("releaseNotes", "• Welcome to Magical World!\n• Complete quests, play arcade challenges, and win real rewards."),
                    updateTitle = j.optString("updateTitle", "Magical Realm Update Available! ✨"),
                    updatedAt = j.optLong("updatedAt", System.currentTimeMillis())
                )
            } catch (e: Exception) {
                Log.w(TAG, "Failed reading cached version: ${e.message}")
            }
        }
        return AppVersionInfo()
    }

    fun saveAppVersionConfig(info: AppVersionInfo, onComplete: ((Boolean) -> Unit)? = null) {
        try {
            val j = JSONObject().apply {
                put("latestVersionCode", info.latestVersionCode)
                put("latestVersionName", info.latestVersionName)
                put("minSupportedVersionCode", info.minSupportedVersionCode)
                put("forceUpdate", info.forceUpdate)
                put("downloadUrl", info.downloadUrl)
                put("releaseNotes", info.releaseNotes)
                put("updateTitle", info.updateTitle)
                put("updatedAt", System.currentTimeMillis())
            }
            prefs.edit().putString("cache_app_version", j.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Error caching version config", e)
        }

        val db = getDb() ?: run { onComplete?.invoke(false); return }
        val map = hashMapOf(
            "latestVersionCode" to info.latestVersionCode,
            "latestVersionName" to info.latestVersionName,
            "minSupportedVersionCode" to info.minSupportedVersionCode,
            "forceUpdate" to info.forceUpdate,
            "downloadUrl" to info.downloadUrl,
            "releaseNotes" to info.releaseNotes,
            "updateTitle" to info.updateTitle,
            "updatedAt" to System.currentTimeMillis()
        )
        db.collection(COL_APP_VERSIONS).document("android_release")
            .set(map)
            .addOnSuccessListener { onComplete?.invoke(true) }
            .addOnFailureListener { onComplete?.invoke(false) }
    }

    // ==========================================
    // 12. FULL SYNC / REFRESH FROM CLOUD
    // ==========================================

    fun refreshRemoteConfig(scope: CoroutineScope, onFinished: (Boolean) -> Unit) {
        val db = getDb()
        if (db == null) {
            onFinished(false)
            return
        }

        scope.launch(Dispatchers.IO) {
            var allSuccess = true
            try {
                // Fetch app_settings
                db.collection(COL_APP_SETTINGS).document("customization").get()
                    .addOnSuccessListener { doc ->
                        if (doc != null && doc.exists()) {
                            val settings = CustomizationSettings(
                                appName = doc.getString("appName") ?: "Magical World",
                                appTagline = doc.getString("appTagline") ?: "Explore the Lands • Earn • Play • Complete Quests",
                                logoUrl = doc.getString("logoUrl") ?: "",
                                bannerUrl = doc.getString("bannerUrl") ?: "",
                                announcementText = doc.getString("announcementText") ?: "",
                                supportEmail = doc.getString("supportEmail") ?: "support@magicalworld.game",
                                supportPhone = doc.getString("supportPhone") ?: "+880 1800-MAGICW"
                            )
                            saveAppSettings(settings)
                        }
                    }

                // Fetch app_versions
                db.collection(COL_APP_VERSIONS).document("android_release").get()
                    .addOnSuccessListener { doc ->
                        if (doc != null && doc.exists()) {
                            val ver = AppVersionInfo(
                                currentVersionCode = 1,
                                currentVersionName = "1.0.0",
                                latestVersionCode = (doc.getLong("latestVersionCode") ?: 1L).toInt(),
                                latestVersionName = doc.getString("latestVersionName") ?: "1.0.0",
                                minSupportedVersionCode = (doc.getLong("minSupportedVersionCode") ?: 1L).toInt(),
                                forceUpdate = doc.getBoolean("forceUpdate") ?: false,
                                downloadUrl = doc.getString("downloadUrl") ?: "https://play.google.com/store/apps/details?id=com.aistudio.magicalworld.qptxz",
                                releaseNotes = doc.getString("releaseNotes") ?: "",
                                updateTitle = doc.getString("updateTitle") ?: "Magical Realm Update Available! ✨"
                            )
                            saveAppVersionConfig(ver)
                        }
                    }

                // Fetch maintenance
                db.collection(COL_APP_SETTINGS).document("maintenance").get()
                    .addOnSuccessListener { doc ->
                        if (doc != null && doc.exists()) {
                            val maint = MaintenanceConfig(
                                isMaintenanceMode = doc.getBoolean("isMaintenanceMode") ?: false,
                                title = doc.getString("title") ?: "Magical World Realm Maintenance",
                                message = doc.getString("message") ?: "",
                                estimatedEndTime = doc.getString("estimatedEndTime") ?: "1 - 2 hours",
                                supportEmail = doc.getString("supportEmail") ?: "support@magicalworld.game",
                                allowAdminBypass = doc.getBoolean("allowAdminBypass") ?: true
                            )
                            saveMaintenanceConfig(maint)
                        }
                    }

                // Fetch feature flags
                db.collection(COL_FEATURE_FLAGS).document("runtime_flags").get()
                    .addOnSuccessListener { doc ->
                        if (doc != null && doc.exists()) {
                            val flags = FeatureFlags(
                                enableGames = doc.getBoolean("enableGames") ?: true,
                                enableTasks = doc.getBoolean("enableTasks") ?: true,
                                enableAds = doc.getBoolean("enableAds") ?: true,
                                enableWithdrawals = doc.getBoolean("enableWithdrawals") ?: true,
                                enableReferrals = doc.getBoolean("enableReferrals") ?: true,
                                enableRegistration = doc.getBoolean("enableRegistration") ?: true,
                                enableLeaderboard = doc.getBoolean("enableLeaderboard") ?: true,
                                enableSupportChat = doc.getBoolean("enableSupportChat") ?: true,
                                enableDynamicTheming = doc.getBoolean("enableDynamicTheming") ?: true,
                                enableLiveNotifications = doc.getBoolean("enableLiveNotifications") ?: true
                            )
                            saveFeatureFlags(flags)
                        }
                    }

                // Fetch withdrawal settings
                db.collection(COL_WITHDRAWAL_SETTINGS).document("payout_config").get()
                    .addOnSuccessListener { doc ->
                        if (doc != null && doc.exists()) {
                            val pts = doc.getLong("pointsPerCurrencyUnit") ?: 100L
                            val sym = doc.getString("currencySymbol") ?: "৳"
                            val lim = doc.getDouble("dailyWithdrawalLimit") ?: 10000.0
                            val proc = doc.getString("processingTimeText") ?: "Payouts processed within 1 - 24 hours"
                            val rawList = doc.get("gateways") as? List<Map<String, Any>>
                            val gateways = rawList?.map { m ->
                                PaymentGatewayConfig(
                                    id = m["id"] as? String ?: "",
                                    name = m["name"] as? String ?: "",
                                    isEnabled = m["isEnabled"] as? Boolean ?: true,
                                    minWithdrawal = (m["minWithdrawal"] as? Number)?.toDouble() ?: 50.0,
                                    maxWithdrawal = (m["maxWithdrawal"] as? Number)?.toDouble() ?: 5000.0,
                                    feePercent = (m["feePercent"] as? Number)?.toDouble() ?: 0.0,
                                    accountPlaceholder = m["accountPlaceholder"] as? String ?: "",
                                    instructionNote = m["instructionNote"] as? String ?: "",
                                    badge = m["badge"] as? String ?: "Fast",
                                    currencyCode = m["currencyCode"] as? String ?: "BDT"
                                )
                            } ?: emptyList()

                            if (gateways.isNotEmpty()) {
                                saveWithdrawalSettings(WithdrawalRemoteSettings(pts, sym, lim, proc, gateways))
                            }
                        }
                    }

                onFinished(true)
            } catch (e: Exception) {
                Log.w(TAG, "Refresh remote config error: ${e.message}")
                onFinished(false)
            }
        }
    }
}
