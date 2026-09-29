package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class AdminRoute(val title: String, val icon: ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    REMOTE_CONFIG("Remote & APK", Icons.Default.CloudSync),
    USERS("User Control", Icons.Default.People),
    WITHDRAWALS("Withdrawals", Icons.Default.Payments),
    TASKS("Quests & Tasks", Icons.Default.Assignment),
    GAMES("Games Manager", Icons.Default.SportsEsports),
    ADS("Ads & Monetization", Icons.Default.Campaign),
    REWARDS("Rewards Matrix", Icons.Default.MonetizationOn),
    FRAUD("Fraud & Anti-Cheat", Icons.Default.GppBad),
    DEVICES("Sessions & Devices", Icons.Default.Devices),
    SUPPORT("Support Tickets", Icons.Default.HelpCenter),
    EMERGENCY("Emergency Controls", Icons.Default.WarningAmber),
    HOME_BUILDER("Home Builder", Icons.Default.ViewQuilt),
    THEME("Theme & Content", Icons.Default.Palette),
    ADMIN_USERS("Admin Roster", Icons.Default.AdminPanelSettings),
    SYSTEM_HEALTH("System Health", Icons.Default.HealthAndSafety),
    AUDIT_LOGS("Audit Logs", Icons.Default.ReceiptLong)
}

@Composable
fun AdminScreen(
    repository: AppRepository,
    onExitAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentAdmin by repository.currentAdmin.collectAsState()
    val allUsers by repository.allUsers.collectAsState()
    val withdrawals by repository.withdrawals.collectAsState()
    val tasks by repository.tasks.collectAsState()
    val submissions by repository.submissions.collectAsState()
    val games by repository.games.collectAsState()
    val adSettings by repository.adSettings.collectAsState()
    val customization by repository.customization.collectAsState()
    val systemSettings by repository.systemSettings.collectAsState()
    val emergencyControls by repository.emergencyControls.collectAsState()
    val homeSections by repository.homeSections.collectAsState()
    val themeConfig by repository.themeConfig.collectAsState()
    val systemHealth by repository.systemHealth.collectAsState()
    val fraudAlerts by repository.fraudAlerts.collectAsState()
    val deviceSessions by repository.deviceSessions.collectAsState()
    val auditLogs by repository.auditLogs.collectAsState()
    val supportTickets by repository.supportTickets.collectAsState()
    val adminAccounts by repository.adminAccounts.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val appVersionInfo by repository.appVersionInfo.collectAsState()
    val withdrawalRemoteSettings by repository.withdrawalRemoteSettings.collectAsState()
    val banners by repository.banners.collectAsState()
    val featureFlags by repository.featureFlags.collectAsState()
    val maintenanceConfig by repository.maintenanceConfig.collectAsState()
    val isRemoteSyncing by repository.isRemoteSyncing.collectAsState()
    val lastRemoteSyncTime by repository.lastRemoteSyncTime.collectAsState()

    var activeRoute by remember { mutableStateOf(AdminRoute.DASHBOARD) }

    // Remote Config Dialog States
    var editingGateway by remember { mutableStateOf<PaymentGatewayConfig?>(null) }
    var editGatewayMin by remember { mutableStateOf("") }
    var editGatewayMax by remember { mutableStateOf("") }
    var editGatewayFee by remember { mutableStateOf("") }

    var showVersionDialog by remember { mutableStateOf(false) }
    var editLatestVersionCode by remember { mutableStateOf("1") }
    var editLatestVersionName by remember { mutableStateOf("1.0.0") }
    var editForceUpdate by remember { mutableStateOf(false) }
    var editDownloadUrl by remember { mutableStateOf("") }
    var editReleaseNotes by remember { mutableStateOf("") }

    var showBannerDialog by remember { mutableStateOf(false) }
    var editBannerTitle by remember { mutableStateOf("") }
    var editBannerSubtitle by remember { mutableStateOf("") }
    var editBannerAction by remember { mutableStateOf("NAVIGATE_EARN") }

    var showMaintenanceDialog by remember { mutableStateOf(false) }
    var editMaintenanceMessage by remember { mutableStateOf("") }
    var editMaintenanceEndTime by remember { mutableStateOf("") }

    var editingGameReward by remember { mutableStateOf<GameItem?>(null) }
    var editRewardAmount by remember { mutableStateOf("") }

    // Date Filter State
    var dateFilter by remember { mutableStateOf("Today") } // Today, 7 Days, 30 Days, 90 Days

    // User Detail Full View Modal State (/admin/users/:uid)
    var inspectingUser by remember { mutableStateOf<UserProfile?>(null) }
    var userDetailTab by remember { mutableIntStateOf(0) } // 0=Account, 1=Wallet, 2=Transactions, 3=Activity, 4=Security

    // Edit User Dialog
    var editingUser by remember { mutableStateOf<UserProfile?>(null) }
    var editFullName by remember { mutableStateOf("") }
    var editUsername by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editLevel by remember { mutableStateOf("1") }
    var editXp by remember { mutableStateOf("0") }
    var editStreak by remember { mutableStateOf("0") }
    var editAccountStatus by remember { mutableStateOf(AccountStatus.ACTIVE) }
    var editVerificationStatus by remember { mutableStateOf(VerificationStatus.VERIFIED) }
    var editReason by remember { mutableStateOf("") }

    // Wallet Adjust Dialog
    var adjustingWalletUser by remember { mutableStateOf<UserProfile?>(null) }
    var adjustPointsAmount by remember { mutableStateOf("") }
    var adjustPointsReason by remember { mutableStateOf("") }

    // Ban User Dialog
    var banningUser by remember { mutableStateOf<UserProfile?>(null) }
    var banReasonInput by remember { mutableStateOf("") }

    // Withdrawal Process Dialog
    var reviewingWithdrawal by remember { mutableStateOf<WithdrawalRequest?>(null) }
    var withdrawalActionType by remember { mutableStateOf(WithdrawalStatus.APPROVED) }
    var paymentRefInput by remember { mutableStateOf("") }
    var adminNoteInput by remember { mutableStateOf("") }

    // Game Create/Edit Dialog
    var editingGame by remember { mutableStateOf<GameItem?>(null) }
    var gameTitleInput by remember { mutableStateOf("") }
    var gameDescInput by remember { mutableStateOf("") }
    var gameRewardInput by remember { mutableStateOf("35") }
    var gameLimitInput by remember { mutableStateOf("15") }
    var gameDiffInput by remember { mutableStateOf("Medium") }
    var gameTypeInput by remember { mutableStateOf(GameType.CRYSTAL_CAR_RUSH) }

    // Task Create Dialog
    var showCreateTaskModal by remember { mutableStateOf(false) }
    var taskTitleInput by remember { mutableStateOf("") }
    var taskDescInput by remember { mutableStateOf("") }
    var taskRewardInput by remember { mutableStateOf("80") }
    var taskTypeInput by remember { mutableStateOf(TaskType.CUSTOM_TASK) }

    // Provision Admin Modal
    var showAddAdminModal by remember { mutableStateOf(false) }
    var newAdminName by remember { mutableStateOf("") }
    var newAdminEmail by remember { mutableStateOf("") }
    var newAdminRole by remember { mutableStateOf(UserRole.ADMIN) }

    // Support Reply Dialog
    var replyingTicket by remember { mutableStateOf<SupportTicket?>(null) }
    var ticketReplyInput by remember { mutableStateOf("") }

    // Global Alert Feedback
    var adminToast by remember { mutableStateOf<String?>(null) }
    var userSearch by remember { mutableStateOf("") }
    var userFilterStatus by remember { mutableStateOf("ALL") }
    var wdFilter by remember { mutableStateOf("ALL") }

    val admin = currentAdmin

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D1C))
    ) {
        // Master Admin Top Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF10152B))
                .border(1.dp, Color(0xFF232D56), RoundedCornerShape(0.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MagicCrimson.copy(alpha = 0.2f))
                            .border(1.dp, MagicCrimson, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = MagicCrimson, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("MAGICAL WORLD MISSION CONTROL", fontWeight = FontWeight.Black, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            if (emergencyControls.isEmergencyShutdown) {
                                StatusPill("LOCKDOWN", isDanger = true)
                            }
                        }
                        Text(
                            "Admin: ${admin?.name ?: "Bachu Ali"} • Role: ${admin?.role?.name ?: "SUPER_ADMIN"}",
                            fontSize = 11.sp,
                            color = MagicCyan,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            repository.adminLogout()
                            onExitAdmin()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MagicCardBg),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MagicCrimson.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, tint = MagicCrimson, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Exit", color = MagicCrimson, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Horizontal Route Navigation Bar
        val navScrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D1224))
                .horizontalScroll(navScrollState)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AdminRoute.values().forEach { route ->
                val isSelected = activeRoute == route
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MagicCyan else Color.Transparent)
                        .border(1.dp, if (isSelected) MagicCyan else Color(0xFF202A4F), RoundedCornerShape(8.dp))
                        .clickable { activeRoute = route }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = route.icon,
                            contentDescription = null,
                            tint = if (isSelected) MagicDeepNavy else MagicTextSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = route.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MagicDeepNavy else MagicTextSecondary
                        )
                    }
                }
            }
        }

        // Active Section Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            contentPadding = PaddingValues(bottom = 60.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (activeRoute) {
                // ==========================================
                // 1. DASHBOARD
                // ==========================================
                AdminRoute.DASHBOARD -> {
                    item {
                        // Date Filter Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Executive Realm Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MagicTextPrimary)
                                Text("Real-time telemetry, engagement & financial metrics", fontSize = 11.sp, color = MagicTextSecondary)
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MagicCardBg)
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                listOf("Today", "7D", "30D", "90D").forEach { d ->
                                    val isSel = dateFilter == d
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSel) MagicCyan else Color.Transparent)
                                            .clickable { dateFilter = d }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(d, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) MagicDeepNavy else MagicTextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    // Key Metric Cards
                    item {
                        val totalPoints = allUsers.sumOf { it.points }
                        val activeUsersCount = allUsers.count { it.accountStatus == AccountStatus.ACTIVE }
                        val verifiedCount = allUsers.count { it.verificationStatus == VerificationStatus.VERIFIED }
                        val suspendedCount = allUsers.count { it.accountStatus == AccountStatus.SUSPENDED }
                        val bannedCount = allUsers.count { it.isBanned }
                        val pendingWdSum = withdrawals.filter { it.status == WithdrawalStatus.PENDING }.sumOf { it.amountCurrency }
                        val paidWdSum = withdrawals.filter { it.status == WithdrawalStatus.PAID }.sumOf { it.amountCurrency }
                        val gamesPlayedSum = allUsers.sumOf { it.gamesPlayedCount }
                        val adsWatchedSum = allUsers.sumOf { it.adsWatchedCount }
                        val tasksCompletedSum = allUsers.sumOf { it.tasksCompletedCount }

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AdminMetricCard("Total Users", "${allUsers.size}", "Active: $activeUsersCount | Verified: $verifiedCount", MagicCyan, Modifier.weight(1f))
                                AdminMetricCard("Suspended / Banned", "$suspendedCount / $bannedCount", "High security standing", MagicCrimson, Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AdminMetricCard("Total Points Issued", "$totalPoints Pts", "Today: +${totalPoints / 10}", MagicGold, Modifier.weight(1f))
                                AdminMetricCard("Paid Withdrawals", "${systemSettings.currencySymbol}$paidWdSum", "Pending: ${systemSettings.currencySymbol}$pendingWdSum", MagicEmerald, Modifier.weight(1f))
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                AdminMetricCard("Games Played", "$gamesPlayedSum matches", "11 Active Mini-Games", MagicPurple, Modifier.weight(1f))
                                AdminMetricCard("Ads Watched", "$adsWatchedSum visions", "AdMob + Adsterra CPM", MagicCyan, Modifier.weight(1f))
                            }
                        }
                    }

                    // Activity & Engagement Bars
                    item {
                        MagicalCard {
                            Text("Engagement Breakdown ($dateFilter)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))

                            MetricProgressBar(label = "Rewarded Ad Views", count = "1,840 views", progress = 0.85f, color = MagicCyan)
                            Spacer(modifier = Modifier.height(8.dp))
                            MetricProgressBar(label = "Arcade Game Sessions", count = "1,220 sessions", progress = 0.68f, color = MagicPurple)
                            Spacer(modifier = Modifier.height(8.dp))
                            MetricProgressBar(label = "Quest Completions", count = "640 quests", progress = 0.45f, color = MagicGold)
                            Spacer(modifier = Modifier.height(8.dp))
                            MetricProgressBar(label = "Referral Recruits", count = "210 recruits", progress = 0.32f, color = MagicEmerald)
                        }
                    }

                    // System Sentinel Quick Status
                    item {
                        MagicalCard {
                            Text("Realm Sentinel Status", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Anti-Cheat Shield", fontSize = 12.sp, color = MagicTextSecondary)
                                StatusPill("${fraudAlerts.count { it.status == FraudStatus.FLAGGED }} FLAGGED", isWarning = true)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pending Proofs", fontSize = 12.sp, color = MagicTextSecondary)
                                StatusPill("${submissions.count { it.status == TaskStatus.PENDING }} Awaiting Review", isSuccess = false)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pending Payouts", fontSize = 12.sp, color = MagicTextSecondary)
                                StatusPill("${withdrawals.count { it.status == WithdrawalStatus.PENDING }} Requests", isWarning = true)
                            }
                        }
                    }
                }

                // ==========================================
                // 1.5. REMOTE CONFIG & APK UPDATES (OTA)
                // ==========================================
                AdminRoute.REMOTE_CONFIG -> {
                    adminRemoteConfigSection(
                        repository = repository,
                        withdrawalRemoteSettings = withdrawalRemoteSettings,
                        games = games,
                        appVersionInfo = appVersionInfo,
                        banners = banners,
                        maintenanceConfig = maintenanceConfig,
                        featureFlags = featureFlags,
                        isRemoteSyncing = isRemoteSyncing,
                        lastRemoteSyncTime = lastRemoteSyncTime,
                        onEditGateway = { gw ->
                            editingGateway = gw
                            editGatewayMin = gw.minWithdrawal.toInt().toString()
                            editGatewayMax = gw.maxWithdrawal.toInt().toString()
                            editGatewayFee = gw.feePercent.toString()
                        },
                        onEditGameReward = { gm ->
                            editingGameReward = gm
                            editRewardAmount = gm.rewardPoints.toString()
                        },
                        onOpenVersionDialog = {
                            editLatestVersionCode = appVersionInfo.latestVersionCode.toString()
                            editLatestVersionName = appVersionInfo.latestVersionName
                            editForceUpdate = appVersionInfo.forceUpdate
                            editDownloadUrl = appVersionInfo.downloadUrl
                            editReleaseNotes = appVersionInfo.releaseNotes
                            showVersionDialog = true
                        },
                        onOpenBannerDialog = {
                            editBannerTitle = ""
                            editBannerSubtitle = ""
                            editBannerAction = "NAVIGATE_EARN"
                            showBannerDialog = true
                        },
                        onOpenMaintenanceDialog = {
                            editMaintenanceMessage = maintenanceConfig.message
                            editMaintenanceEndTime = maintenanceConfig.estimatedEndTime
                            showMaintenanceDialog = true
                        }
                    )
                }

                // ==========================================
                // 2. USER CONTROL CENTER (/admin/users)
                // ==========================================
                AdminRoute.USERS -> {
                    item {
                        OutlinedTextField(
                            value = userSearch,
                            onValueChange = { userSearch = it },
                            label = { Text("Search UID, Name, Email, Phone, Referral...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MagicCyan) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MagicCyan,
                                unfocusedBorderColor = MagicCardBorder,
                                focusedTextColor = MagicTextPrimary,
                                unfocusedTextColor = MagicTextPrimary
                            )
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("ALL", "ACTIVE", "SUSPENDED", "BANNED", "VERIFIED", "PENDING", "HIGH EARNERS").forEach { filter ->
                                val isSel = userFilterStatus == filter
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) MagicPurple else MagicCardBg)
                                        .clickable { userFilterStatus = filter }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(filter, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else MagicTextSecondary)
                                }
                            }
                        }
                    }

                    val filteredUsers = allUsers.filter { u ->
                        val matchesSearch = u.id.contains(userSearch, true) ||
                                u.fullName.contains(userSearch, true) ||
                                u.username.contains(userSearch, true) ||
                                u.email.contains(userSearch, true) ||
                                u.phoneNumber.contains(userSearch, true) ||
                                u.referralCode.contains(userSearch, true)
                        val matchesFilter = when (userFilterStatus) {
                            "ACTIVE" -> u.accountStatus == AccountStatus.ACTIVE && !u.isBanned
                            "SUSPENDED" -> u.accountStatus == AccountStatus.SUSPENDED
                            "BANNED" -> u.isBanned
                            "VERIFIED" -> u.verificationStatus == VerificationStatus.VERIFIED
                            "PENDING" -> u.verificationStatus == VerificationStatus.PENDING
                            "HIGH EARNERS" -> u.points > 1000L
                            else -> true
                        }
                        matchesSearch && matchesFilter
                    }

                    items(filteredUsers) { userItem ->
                        MagicalCard(
                            borderGlow = if (userItem.isBanned) MagicCrimson else if (userItem.accountStatus == AccountStatus.SUSPENDED) MagicGold else MagicCardBorder
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF1B2348))
                                                .border(1.dp, MagicCyan, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(userItem.fullName.take(1), fontWeight = FontWeight.Bold, color = MagicCyan, fontSize = 16.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(userItem.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Lv.${userItem.level}", fontSize = 10.sp, color = MagicGold, fontWeight = FontWeight.Bold)
                                            }
                                            Text("@${userItem.username} • ${userItem.email}", fontSize = 11.sp, color = MagicTextSecondary)
                                            Text("UID: ${userItem.id} • Ref: ${userItem.referralCode}", fontSize = 10.sp, color = MagicTextMuted)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        StatusPill(
                                            status = if (userItem.isBanned) "BANNED" else userItem.accountStatus.name,
                                            isDanger = userItem.isBanned || userItem.accountStatus == AccountStatus.SUSPENDED,
                                            isSuccess = userItem.accountStatus == AccountStatus.ACTIVE && !userItem.isBanned
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("${userItem.points} Pts", fontSize = 13.sp, fontWeight = FontWeight.Black, color = MagicGold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action Buttons Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            inspectingUser = userItem
                                            userDetailTab = 0
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text("Full Profile", color = MagicDeepNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            editingUser = userItem
                                            editFullName = userItem.fullName
                                            editUsername = userItem.username
                                            editPhone = userItem.phoneNumber
                                            editLevel = userItem.level.toString()
                                            editXp = userItem.xp.toString()
                                            editStreak = userItem.dailyStreak.toString()
                                            editAccountStatus = userItem.accountStatus
                                            editVerificationStatus = userItem.verificationStatus
                                            editReason = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MagicPurple),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text("Edit User", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            adjustingWalletUser = userItem
                                            adjustPointsAmount = ""
                                            adjustPointsReason = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MagicGoldDark),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(1f).height(30.dp)
                                    ) {
                                        Text("Wallet ±", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    if (userItem.isBanned) {
                                        Button(
                                            onClick = {
                                                val res = repository.adminUnbanUser(userItem.id, "Administrative pardon")
                                                adminToast = res.getOrNull()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MagicEmerald),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.weight(1f).height(30.dp)
                                        ) {
                                            Text("Unban", color = MagicDeepNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                banningUser = userItem
                                                banReasonInput = ""
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MagicCrimson),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.weight(1f).height(30.dp)
                                        ) {
                                            Text("Ban", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 3. WITHDRAWALS CENTER
                // ==========================================
                AdminRoute.WITHDRAWALS -> {
                    item {
                        FantasySectionHeader(
                            title = "Withdrawal Dispatches & Verification",
                            subtitle = "Disburse, process, or reject pending payout requests"
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("ALL", "PENDING", "PROCESSING", "APPROVED", "PAID", "REJECTED").forEach { f ->
                                val isSel = wdFilter == f
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSel) MagicCyan else MagicCardBg)
                                        .clickable { wdFilter = f }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(f, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) MagicDeepNavy else MagicTextSecondary)
                                }
                            }
                        }
                    }

                    val filteredWd = withdrawals.filter {
                        if (wdFilter == "ALL") true else it.status.name == wdFilter
                    }

                    items(filteredWd) { wd ->
                        MagicalCard(
                            borderGlow = when (wd.status) {
                                WithdrawalStatus.PENDING -> MagicCrimson
                                WithdrawalStatus.PAID -> MagicEmerald
                                WithdrawalStatus.PROCESSING -> MagicGold
                                else -> MagicCardBorder
                            }
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text("${wd.paymentMethod}: ${wd.accountNumber}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MagicTextPrimary)
                                        Text("User: @${wd.username} (UID: ${wd.userId})", fontSize = 11.sp, color = MagicCyan)
                                        Text(
                                            "Requested: ${wd.currencySymbol}${wd.amountCurrency} (${wd.pointsDeducted} Pts)",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MagicGold
                                        )
                                        Text(
                                            "Date: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(Date(wd.requestedAt))}",
                                            fontSize = 10.sp,
                                            color = MagicTextMuted
                                        )
                                    }

                                    StatusPill(
                                        status = wd.status.name,
                                        isSuccess = wd.status == WithdrawalStatus.PAID,
                                        isWarning = wd.status == WithdrawalStatus.PENDING || wd.status == WithdrawalStatus.PROCESSING,
                                        isDanger = wd.status == WithdrawalStatus.REJECTED || wd.status == WithdrawalStatus.FAILED
                                    )
                                }

                                if (wd.adminPaymentRef.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Txn Ref: ${wd.adminPaymentRef}", fontSize = 11.sp, color = MagicEmerald, fontWeight = FontWeight.Bold)
                                }
                                if (wd.adminNote.isNotBlank()) {
                                    Text("Admin Note: ${wd.adminNote}", fontSize = 11.sp, color = MagicTextSecondary)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (wd.status != WithdrawalStatus.PAID) {
                                        Button(
                                            onClick = {
                                                reviewingWithdrawal = wd
                                                withdrawalActionType = WithdrawalStatus.PAID
                                                paymentRefInput = "TXN-${(100000..999999).random()}"
                                                adminNoteInput = "Disbursed via ${wd.paymentMethod} gateway"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MagicEmerald),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Text("Mark Paid", color = MagicDeepNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                reviewingWithdrawal = wd
                                                withdrawalActionType = WithdrawalStatus.PROCESSING
                                                paymentRefInput = ""
                                                adminNoteInput = "Sent to finance batch processing"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MagicGoldDark),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Text("Processing", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                reviewingWithdrawal = wd
                                                withdrawalActionType = WithdrawalStatus.REJECTED
                                                paymentRefInput = ""
                                                adminNoteInput = "Account verification failed / invalid number"
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MagicCrimson),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Text("Reject & Refund", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 4. QUESTS & TASKS
                // ==========================================
                AdminRoute.TASKS -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Quest Board Master Setup", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MagicTextPrimary)
                            Button(
                                onClick = { showCreateTaskModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = MagicDeepNavy, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Quest", color = MagicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    // Submissions Review Queue
                    val pendingSubs = submissions.filter { it.status == TaskStatus.PENDING }
                    item {
                        Text("Submissions Needing Review (${pendingSubs.size}):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MagicGold)
                    }

                    items(pendingSubs) { sub ->
                        MagicalCard(borderGlow = MagicGold) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(sub.taskTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagicTextPrimary)
                                Text("User: @${sub.username} (UID: ${sub.userId}) • Reward: +${sub.rewardPoints} Pts", fontSize = 11.sp, color = MagicCyan)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Submitted Proof: ${sub.proofText}", fontSize = 12.sp, color = MagicTextPrimary)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            val res = repository.adminReviewTaskSubmission(sub.id, true, "Verified by Sentinel")
                                            adminToast = res.getOrNull()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MagicEmerald),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text("Approve (+${sub.rewardPoints})", color = MagicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val res = repository.adminReviewTaskSubmission(sub.id, false, "Proof verification failed")
                                            adminToast = res.getOrNull()
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MagicCrimson)
                                    ) {
                                        Text("Reject", color = MagicCrimson, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text("All Active Quests (${tasks.size}):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                    }

                    items(tasks) { task ->
                        MagicalCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                    Text(task.description, fontSize = 11.sp, color = MagicTextSecondary)
                                    Text("+${task.pointsReward} Pts • ${task.type.name}", fontSize = 11.sp, color = MagicGold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            val res = repository.adminDuplicateTask(task.id)
                                            adminToast = res.getOrNull()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = MagicCyan, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val res = repository.adminDeleteTask(task.id)
                                            adminToast = res.getOrNull()
                                        }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MagicCrimson, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 5. GAMES MANAGER
                // ==========================================
                AdminRoute.GAMES -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Game Roster & Arcades (${games.size})", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MagicTextPrimary)
                            Button(
                                onClick = {
                                    editingGame = GameItem()
                                    gameTitleInput = ""
                                    gameDescInput = ""
                                    gameRewardInput = "35"
                                    gameLimitInput = "15"
                                    gameDiffInput = "Medium"
                                    gameTypeInput = GameType.HIGHWAY_RACING
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = MagicDeepNavy, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Game", color = MagicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    items(games) { g ->
                        MagicalCard(
                            borderGlow = if (g.isEnabled) MagicCyan.copy(alpha = 0.4f) else MagicCrimson.copy(alpha = 0.4f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(g.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        StatusPill(if (g.isEnabled) "ACTIVE" else "DISABLED", isSuccess = g.isEnabled, isDanger = !g.isEnabled)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(g.description, fontSize = 11.sp, color = MagicTextSecondary)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Reward: +${g.rewardPoints} Pts | Limit: ${g.dailyLimit}/day | Diff: ${g.difficulty}", fontSize = 11.sp, color = MagicGold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = g.isEnabled,
                                        onCheckedChange = { repository.adminToggleGame(g.id, it) }
                                    )
                                    IconButton(
                                        onClick = {
                                            editingGame = g
                                            gameTitleInput = g.title
                                            gameDescInput = g.description
                                            gameRewardInput = g.rewardPoints.toString()
                                            gameLimitInput = g.dailyLimit.toString()
                                            gameDiffInput = g.difficulty
                                            gameTypeInput = g.type
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MagicCyan, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = { repository.adminDeleteGame(g.id) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MagicCrimson, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 6. ADS & MONETIZATION
                // ==========================================
                AdminRoute.ADS -> {
                    item {
                        FantasySectionHeader(
                            title = "Ad Network & Placement Manager",
                            subtitle = "AdMob units, Adsterra scripts, cooldowns, and yield analytics"
                        )
                    }

                    item {
                        MagicalCard {
                            Text("Master Ad Switch & Environments", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("Master Ads Network", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                                    Text("Globally enable/disable all ads", fontSize = 11.sp, color = MagicTextSecondary)
                                }
                                Switch(
                                    checked = adSettings.isAdsGloballyEnabled,
                                    onCheckedChange = { repository.adminUpdateAdSettings(adSettings.copy(isAdsGloballyEnabled = it)) }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("AdMob Test Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                                    Text("Use test units during development", fontSize = 11.sp, color = MagicTextSecondary)
                                }
                                Switch(
                                    checked = adSettings.isTestMode,
                                    onCheckedChange = { repository.adminUpdateAdSettings(adSettings.copy(isTestMode = it)) }
                                )
                            }
                        }
                    }

                    item {
                        MagicalCard {
                            Text("Rewarded Video Parameters", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Reward per Ad", fontSize = 12.sp, color = MagicTextSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(onClick = { repository.adminUpdateAdSettings(adSettings.copy(rewardedPoints = (adSettings.rewardedPoints - 5).coerceAtLeast(5))) }) { Text("-") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${adSettings.rewardedPoints} Pts", fontWeight = FontWeight.Bold, color = MagicGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { repository.adminUpdateAdSettings(adSettings.copy(rewardedPoints = adSettings.rewardedPoints + 5)) }) { Text("+") }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Daily Limit per User", fontSize = 12.sp, color = MagicTextSecondary)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(onClick = { repository.adminUpdateAdSettings(adSettings.copy(dailyRewardedLimit = (adSettings.dailyRewardedLimit - 5).coerceAtLeast(5))) }) { Text("-") }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${adSettings.dailyRewardedLimit} Ads", fontWeight = FontWeight.Bold, color = MagicCyan)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = { repository.adminUpdateAdSettings(adSettings.copy(dailyRewardedLimit = adSettings.dailyRewardedLimit + 5)) }) { Text("+") }
                                }
                            }
                        }
                    }

                    item {
                        MagicalCard {
                            Text("Configured AdMob IDs", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("• Rewarded: ${adSettings.adMobUnits.rewardedId}", fontSize = 11.sp, color = MagicTextMuted)
                            Text("• Rewarded Interstitial: ${adSettings.adMobUnits.rewardedInterstitialId}", fontSize = 11.sp, color = MagicTextMuted)
                            Text("• Interstitial: ${adSettings.adMobUnits.interstitialId}", fontSize = 11.sp, color = MagicTextMuted)
                            Text("• Banner: ${adSettings.adMobUnits.bannerId}", fontSize = 11.sp, color = MagicTextMuted)
                            Text("• Native: ${adSettings.adMobUnits.nativeId}", fontSize = 11.sp, color = MagicTextMuted)
                            Text("• App Open: ${adSettings.adMobUnits.appOpenId}", fontSize = 11.sp, color = MagicTextMuted)
                        }
                    }
                }

                // ==========================================
                // 7. FRAUD & ANTI-CHEAT CENTER
                // ==========================================
                AdminRoute.FRAUD -> {
                    item {
                        FantasySectionHeader(
                            title = "Sentinel Fraud & Risk Center",
                            subtitle = "Automatic detection of multi-account emulators, token replay, and score spikes"
                        )
                    }

                    items(fraudAlerts) { alert ->
                        MagicalCard(
                            borderGlow = when (alert.riskLevel) {
                                "CRITICAL" -> MagicCrimson
                                "HIGH" -> MagicCrimson.copy(alpha = 0.6f)
                                else -> MagicGold
                            }
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(alert.reason, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                        Text("User: @${alert.username} (UID: ${alert.userId})", fontSize = 11.sp, color = MagicCyan)
                                        Text("Evidence: ${alert.evidence}", fontSize = 11.sp, color = MagicTextSecondary)
                                    }

                                    StatusPill(alert.riskLevel, isDanger = alert.riskLevel == "CRITICAL" || alert.riskLevel == "HIGH", isWarning = alert.riskLevel == "MEDIUM")
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Status: ${alert.status.name} ${if (alert.actionTaken.isNotBlank()) "• Action: ${alert.actionTaken}" else ""}", fontSize = 10.sp, color = MagicTextMuted)

                                if (alert.status == FraudStatus.FLAGGED || alert.status == FraudStatus.UNDER_REVIEW) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                val res = repository.adminTakeFraudAction(alert.id, "Account permanently banned for cheating", true)
                                                adminToast = res.getOrNull()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MagicCrimson),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Ban Account", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                val res = repository.adminTakeFraudAction(alert.id, "Cleared after telemetry inspection", false)
                                                adminToast = res.getOrNull()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = MagicEmerald),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Clear Flag", color = MagicDeepNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 8. SESSIONS & DEVICES
                // ==========================================
                AdminRoute.DEVICES -> {
                    item {
                        FantasySectionHeader(
                            title = "Active Devices & Telemetry Sessions",
                            subtitle = "Inspect and revoke unauthorized user hardware sessions"
                        )
                    }

                    items(deviceSessions) { sess ->
                        MagicalCard(
                            borderGlow = if (sess.isSuspicious) MagicCrimson else MagicCardBorder
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(sess.deviceName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagicTextPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        if (sess.isSuspicious) {
                                            StatusPill("SUSPICIOUS", isDanger = true)
                                        }
                                    }
                                    Text("OS: ${sess.osVersion} • IP: ${sess.ipAddress}", fontSize = 11.sp, color = MagicTextSecondary)
                                    Text("User UID: ${sess.userId}", fontSize = 10.sp, color = MagicTextMuted)
                                }

                                Button(
                                    onClick = {
                                        val res = repository.adminRevokeDeviceSession(sess.id)
                                        adminToast = res.getOrNull()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MagicCrimson),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Revoke", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 9. SUPPORT TICKETS
                // ==========================================
                AdminRoute.SUPPORT -> {
                    item {
                        FantasySectionHeader(
                            title = "Player Support Chamber",
                            subtitle = "Review tickets, answer inquiries, and resolve disputes"
                        )
                    }

                    items(supportTickets) { ticket ->
                        MagicalCard {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {
                                        Text(ticket.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                        Text("By @${ticket.username} • Category: ${ticket.category}", fontSize = 11.sp, color = MagicCyan)
                                    }
                                    StatusPill(ticket.status.name, isSuccess = ticket.status == TicketStatus.RESOLVED)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                ticket.messages.forEach { m ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (m.isAdmin) MagicPurple.copy(alpha = 0.2f) else MagicCardBg)
                                            .padding(8.dp)
                                    ) {
                                        Column {
                                            Text(if (m.isAdmin) "🛡️ ${m.senderName}:" else "Adventurer:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (m.isAdmin) MagicPurple else MagicCyan)
                                            Text(m.message, fontSize = 12.sp, color = MagicTextPrimary)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        replyingTicket = ticket
                                        ticketReplyInput = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth().height(32.dp)
                                ) {
                                    Text("Reply & Resolve", color = MagicDeepNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 10. EMERGENCY CONTROLS
                // ==========================================
                AdminRoute.EMERGENCY -> {
                    item {
                        FantasySectionHeader(
                            title = "Emergency Protocol Chamber ⚠️",
                            subtitle = "Immediate kill-switches to protect kingdom assets and database integrity"
                        )
                    }

                    item {
                        MagicalCard(borderGlow = MagicCrimson) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text("MASTER EMERGENCY LOCKDOWN", fontWeight = FontWeight.Black, fontSize = 14.sp, color = MagicCrimson)
                                    Text("Immediately halt all financial and reward operations", fontSize = 11.sp, color = MagicTextSecondary)
                                }
                                Switch(
                                    checked = emergencyControls.isEmergencyShutdown,
                                    onCheckedChange = {
                                        repository.adminUpdateEmergencyControls(emergencyControls.copy(isEmergencyShutdown = it))
                                    }
                                )
                            }
                        }
                    }

                    item {
                        MagicalCard {
                            Text("Granular Module Kill-Switches", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(10.dp))

                            EmergencySwitchRow("Halt All Withdrawals", emergencyControls.disableWithdrawals) {
                                repository.adminUpdateEmergencyControls(emergencyControls.copy(disableWithdrawals = it))
                            }
                            EmergencySwitchRow("Pause Rewarded Ads", emergencyControls.disableAds) {
                                repository.adminUpdateEmergencyControls(emergencyControls.copy(disableAds = it))
                            }
                            EmergencySwitchRow("Disable Games Arena", emergencyControls.disableGames) {
                                repository.adminUpdateEmergencyControls(emergencyControls.copy(disableGames = it))
                            }
                            EmergencySwitchRow("Disable Quest Submissions", emergencyControls.disableTasks) {
                                repository.adminUpdateEmergencyControls(emergencyControls.copy(disableTasks = it))
                            }
                            EmergencySwitchRow("Block New Registrations", emergencyControls.disableRegistrations) {
                                repository.adminUpdateEmergencyControls(emergencyControls.copy(disableRegistrations = it))
                            }
                        }
                    }
                }

                // ==========================================
                // 11. HOME BUILDER
                // ==========================================
                AdminRoute.HOME_BUILDER -> {
                    item {
                        FantasySectionHeader(
                            title = "Dynamic Home Page Builder",
                            subtitle = "Reorder and toggle live sections shown on the Adventurer Home feed"
                        )
                    }

                    items(homeSections) { sec ->
                        MagicalCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(sec.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                    Text("${sec.title} • ${sec.subtitle}", fontSize = 11.sp, color = MagicTextSecondary)
                                }

                                Switch(
                                    checked = sec.isEnabled,
                                    onCheckedChange = { isChecked ->
                                        val updated = homeSections.map {
                                            if (it.id == sec.id) it.copy(isEnabled = isChecked) else it
                                        }
                                        repository.adminUpdateHomeSections(updated)
                                    }
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // 12. THEME & CONTENT
                // ==========================================
                AdminRoute.THEME -> {
                    item {
                        FantasySectionHeader(
                            title = "Theme & Realm Customization",
                            subtitle = "Change app branding, theme styles, and announcement text without code changes"
                        )
                    }

                    item {
                        var appNameInput by remember { mutableStateOf(customization.appName) }
                        var taglineInput by remember { mutableStateOf(customization.appTagline) }
                        var announceInput by remember { mutableStateOf(customization.announcementText) }

                        MagicalCard {
                            OutlinedTextField(
                                value = appNameInput,
                                onValueChange = { appNameInput = it },
                                label = { Text("Realm Application Name") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MagicCyan, unfocusedBorderColor = MagicCardBorder, focusedTextColor = MagicTextPrimary, unfocusedTextColor = MagicTextPrimary)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = taglineInput,
                                onValueChange = { taglineInput = it },
                                label = { Text("Realm Tagline") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MagicCyan, unfocusedBorderColor = MagicCardBorder, focusedTextColor = MagicTextPrimary, unfocusedTextColor = MagicTextPrimary)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = announceInput,
                                onValueChange = { announceInput = it },
                                label = { Text("Global Announcement Ticker") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MagicGold, unfocusedBorderColor = MagicCardBorder, focusedTextColor = MagicTextPrimary, unfocusedTextColor = MagicTextPrimary)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            MagicalButton(
                                text = "Save Realm Identity",
                                onClick = {
                                    repository.adminUpdateCustomization(
                                        customization.copy(
                                            appName = appNameInput,
                                            appTagline = taglineInput,
                                            announcementText = announceInput
                                        )
                                    )
                                    adminToast = "Realm branding saved!"
                                },
                                icon = Icons.Default.Save,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    item {
                        MagicalCard {
                            Text("Active Theme Preset", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))
                            val themes = listOf("Neon Fantasy", "Obsidian Dark", "Royal Gold", "Mystic Violet")
                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                themes.forEach { t ->
                                    val isSel = themeConfig.activeTheme == t
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) MagicPurple else MagicCardBg)
                                            .border(1.dp, if (isSel) MagicPurple else Color(0xFF283669), RoundedCornerShape(8.dp))
                                            .clickable { repository.adminUpdateTheme(themeConfig.copy(activeTheme = t)) }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Text(t, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if (isSel) Color.White else MagicTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // ==========================================
                // 13. ADMIN ROSTER
                // ==========================================
                AdminRoute.ADMIN_USERS -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Administration Staff Roster", fontWeight = FontWeight.Black, fontSize = 15.sp, color = MagicTextPrimary)
                            Button(
                                onClick = { showAddAdminModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MagicDeepNavy, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Admin", color = MagicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    items(adminAccounts) { acc ->
                        MagicalCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(acc.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                    Text(acc.email, fontSize = 11.sp, color = MagicTextSecondary)
                                    Text("Role: ${acc.role.name} • Status: ${acc.status}", fontSize = 11.sp, color = MagicCyan)
                                }
                                StatusPill(acc.role.name, isSuccess = acc.role == UserRole.SUPER_ADMIN)
                            }
                        }
                    }
                }

                // ==========================================
                // 14. SYSTEM HEALTH
                // ==========================================
                AdminRoute.SYSTEM_HEALTH -> {
                    item {
                        FantasySectionHeader(
                            title = "Infrastructure Health Monitor",
                            subtitle = "Firebase connectivity, AdMob provider health, and server latency"
                        )
                    }

                    item {
                        MagicalCard {
                            SystemHealthRow("Firebase Authentication", systemHealth.firebaseAuthStatus, isSuccess = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            SystemHealthRow("Firestore Database", systemHealth.firestoreStatus, isSuccess = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            SystemHealthRow("Firebase Storage", systemHealth.firebaseStorageStatus, isSuccess = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            SystemHealthRow("Google AdMob Provider", systemHealth.adMobStatus, isSuccess = true)
                            Spacer(modifier = Modifier.height(6.dp))
                            SystemHealthRow("Adsterra CPM Network", systemHealth.adsterraStatus, isSuccess = true)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Average Latency", fontSize = 12.sp, color = MagicTextSecondary)
                                Text("${systemHealth.averageLatencyMs} ms", fontWeight = FontWeight.Bold, color = MagicEmerald)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Uptime SLA", fontSize = 12.sp, color = MagicTextSecondary)
                                Text("${systemHealth.uptimePercent}%", fontWeight = FontWeight.Bold, color = MagicCyan)
                            }
                        }
                    }
                }

                // ==========================================
                // 15. AUDIT LOGS
                // ==========================================
                AdminRoute.AUDIT_LOGS -> {
                    item {
                        FantasySectionHeader(
                            title = "Immutable Sentinel Audit Logs",
                            subtitle = "Tamper-proof record of every administrative action, user adjustment, and security event"
                        )
                    }

                    items(auditLogs) { log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MagicSurface)
                                .border(1.dp, Color(0xFF1F2B52), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagicCyan)
                                Text(log.details, fontSize = 11.sp, color = MagicTextPrimary)
                                Text("By: ${log.adminName} (${log.adminRole}) • Target: ${log.targetId}", fontSize = 10.sp, color = MagicTextMuted)
                                if (log.reason.isNotBlank()) {
                                    Text("Reason: ${log.reason}", fontSize = 10.sp, color = MagicGold)
                                }
                            }
                            Text(
                                text = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(log.timestamp)),
                                fontSize = 10.sp,
                                color = MagicTextMuted
                            )
                        }
                    }
                }

                else -> {
                    item {
                        Text("Section active.", color = MagicTextSecondary)
                    }
                }
            }
        }
    }

    // ========================================================
    // MODAL: USER DETAIL FULL VIEW (/admin/users/:uid)
    // ========================================================
    if (inspectingUser != null) {
        val u = inspectingUser!!
        val userTxns = transactions.filter { it.userId == u.id }
        val userWds = withdrawals.filter { it.userId == u.id }

        AlertDialog(
            onDismissRequest = { inspectingUser = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(u.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MagicTextPrimary)
                        Text("@${u.username} • UID: ${u.id}", fontSize = 11.sp, color = MagicCyan)
                    }
                    StatusPill(if (u.isBanned) "BANNED" else u.accountStatus.name, isDanger = u.isBanned)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 460.dp)) {
                    // Sub-Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F152D))
                            .padding(2.dp)
                    ) {
                        listOf("Account", "Wallet", "Ledger", "Security").forEachIndexed { index, tabName ->
                            val isSel = userDetailTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) MagicCyan else Color.Transparent)
                                    .clickable { userDetailTab = index }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(tabName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) MagicDeepNavy else MagicTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    when (userDetailTab) {
                        0 -> { // Account
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                item { AdminInfoLine("Email", u.email) }
                                item { AdminInfoLine("Phone", u.phoneNumber) }
                                item { AdminInfoLine("Referral Code", u.referralCode) }
                                item { AdminInfoLine("Referred By", u.referredBy.ifBlank { "None" }) }
                                item { AdminInfoLine("Level & XP", "Level ${u.level} (${u.xp} XP)") }
                                item { AdminInfoLine("Daily Streak", "${u.dailyStreak} Days") }
                                item { AdminInfoLine("Verification", u.verificationStatus.name) }
                                item { AdminInfoLine("Ads Watched", "${u.adsWatchedCount} (${u.adsWatchedToday} today)") }
                                item { AdminInfoLine("Quests Completed", "${u.tasksCompletedCount}") }
                                item { AdminInfoLine("Games Played", "${u.gamesPlayedCount}") }
                                item { AdminInfoLine("Total Referrals", "${u.totalReferralsCount}") }
                                item { AdminInfoLine("Joined Date", SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date(u.joinDate))) }
                            }
                        }
                        1 -> { // Wallet
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AdminInfoLine("Current Balance", "${u.points} Points")
                                AdminInfoLine("Fiat Equivalent", "${systemSettings.currencySymbol}${String.format(Locale.US, "%.2f", u.points.toDouble() / systemSettings.pointsPerCurrencyUnit.toDouble())}")
                                AdminInfoLine("Total Earned", "${u.totalEarnedPoints} Points")
                                AdminInfoLine("Total Withdrawn", "${systemSettings.currencySymbol}${u.totalWithdrawnAmount}")
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Withdrawal Requests (${userWds.size}):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagicTextPrimary)
                                userWds.forEach { w ->
                                    Text("• ${w.paymentMethod} ${w.currencySymbol}${w.amountCurrency} [${w.status.name}]", fontSize = 11.sp, color = MagicGold)
                                }
                            }
                        }
                        2 -> { // Transactions
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (userTxns.isEmpty()) {
                                    item { Text("No transactions recorded.", fontSize = 11.sp, color = MagicTextSecondary) }
                                } else {
                                    items(userTxns) { tx ->
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(tx.title, fontSize = 11.sp, color = MagicTextPrimary)
                                            Text(if (tx.amountPoints >= 0) "+${tx.amountPoints}" else "${tx.amountPoints}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (tx.amountPoints >= 0) MagicGold else MagicCrimson)
                                        }
                                    }
                                }
                            }
                        }
                        3 -> { // Security & Devices
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AdminInfoLine("Device Hardware", u.deviceModel)
                                AdminInfoLine("Last Known IP", u.ipAddress)
                                AdminInfoLine("Fraud Risk Score", "${u.fraudRiskScore}/100")
                                if (u.isBanned) {
                                    AdminInfoLine("Ban Reason", u.banReason)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = {
                                            val res = repository.adminResetPassword(u.id, "Admin initiated password reset")
                                            adminToast = res.getOrNull()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Send Reset Email", color = MagicDeepNavy, fontSize = 10.sp) }

                                    Button(
                                        onClick = {
                                            val res = repository.adminRevokeUserSessions(u.id, "Admin terminated all sessions")
                                            adminToast = res.getOrNull()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MagicCrimson),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Revoke Sessions", color = Color.White, fontSize = 10.sp) }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { inspectingUser = null }) {
                    Text("Close", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }

    // ========================================================
    // MODAL: EDIT USER PROFILE
    // ========================================================
    if (editingUser != null) {
        val u = editingUser!!
        AlertDialog(
            onDismissRequest = { editingUser = null },
            title = { Text("Edit User Profile: @${u.username}", fontWeight = FontWeight.Bold, color = MagicCyan) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp)) {
                    OutlinedTextField(
                        value = editFullName,
                        onValueChange = { editFullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = editUsername,
                        onValueChange = { editUsername = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = editLevel,
                            onValueChange = { editLevel = it },
                            label = { Text("Level") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = editStreak,
                            onValueChange = { editStreak = it },
                            label = { Text("Streak") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = editReason,
                        onValueChange = { editReason = it },
                        label = { Text("Audit Reason (Mandatory)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val lvl = editLevel.toIntOrNull() ?: u.level
                        val xpVal = editXp.toLongOrNull() ?: u.xp
                        val strk = editStreak.toIntOrNull() ?: u.dailyStreak
                        val res = repository.adminEditUserProfile(
                            u.id, editFullName, editUsername, editPhone, lvl, xpVal, strk, editAccountStatus, editVerificationStatus, editReason.ifBlank { "Admin profile edit" }
                        )
                        editingUser = null
                        adminToast = res.getOrNull()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) { Text("Save & Log", color = MagicDeepNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { editingUser = null }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // ========================================================
    // MODAL: ADJUST WALLET BALANCE
    // ========================================================
    if (adjustingWalletUser != null) {
        val u = adjustingWalletUser!!
        AlertDialog(
            onDismissRequest = { adjustingWalletUser = null },
            title = { Text("Adjust Wallet: @${u.username}", fontWeight = FontWeight.Bold, color = MagicGold) },
            text = {
                Column {
                    Text("Current Balance: ${u.points} Points", fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adjustPointsAmount,
                        onValueChange = { adjustPointsAmount = it },
                        label = { Text("+/- Points (e.g. 500 or -250)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adjustPointsReason,
                        onValueChange = { adjustPointsReason = it },
                        label = { Text("Reason for Adjustment (Mandatory)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val delta = adjustPointsAmount.toLongOrNull()
                        if (delta != null && delta != 0L) {
                            val res = repository.adminAdjustUserWallet(u.id, delta, adjustPointsReason)
                            adminToast = res.getOrNull() ?: res.exceptionOrNull()?.message
                        }
                        adjustingWalletUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicGold)
                ) { Text("Confirm Adjustment", color = MagicDeepNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { adjustingWalletUser = null }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // ========================================================
    // MODAL: BAN USER
    // ========================================================
    if (banningUser != null) {
        val u = banningUser!!
        AlertDialog(
            onDismissRequest = { banningUser = null },
            title = { Text("Permanent Ban: @${u.username}", fontWeight = FontWeight.Bold, color = MagicCrimson) },
            text = {
                Column {
                    Text("This action will immediately terminate all active sessions and block login.", fontSize = 12.sp, color = MagicTextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = banReasonInput,
                        onValueChange = { banReasonInput = it },
                        label = { Text("Violation Reason") },
                        placeholder = { Text("e.g. Script botting, forged ad tokens") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val res = repository.adminBanUser(u.id, banReasonInput.ifBlank { "Terms of Service violation" })
                        banningUser = null
                        adminToast = res.getOrNull()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCrimson)
                ) { Text("Confirm Ban", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { banningUser = null }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // ========================================================
    // MODAL: PROCESS WITHDRAWAL
    // ========================================================
    if (reviewingWithdrawal != null) {
        val wd = reviewingWithdrawal!!
        AlertDialog(
            onDismissRequest = { reviewingWithdrawal = null },
            title = { Text("Process Payout: ${wd.paymentMethod}", fontWeight = FontWeight.Bold, color = MagicEmerald) },
            text = {
                Column {
                    Text("User: @${wd.username} • Account: ${wd.accountNumber}", fontSize = 12.sp, color = MagicTextPrimary)
                    Text("Amount: ${wd.currencySymbol}${wd.amountCurrency} (${wd.pointsDeducted} Pts)", fontWeight = FontWeight.Bold, color = MagicGold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = paymentRefInput,
                        onValueChange = { paymentRefInput = it },
                        label = { Text("Payment Ref / Bank Transaction ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = adminNoteInput,
                        onValueChange = { adminNoteInput = it },
                        label = { Text("Audit Note / Disburse Details") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val res = repository.adminProcessWithdrawal(wd.id, withdrawalActionType, paymentRefInput, adminNoteInput)
                        reviewingWithdrawal = null
                        adminToast = res.getOrNull()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicEmerald)
                ) { Text("Execute Disburse", color = MagicDeepNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { reviewingWithdrawal = null }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // ========================================================
    // MODAL: CREATE / EDIT GAME
    // ========================================================
    if (editingGame != null) {
        AlertDialog(
            onDismissRequest = { editingGame = null },
            title = { Text("Configure Game Item", fontWeight = FontWeight.Bold, color = MagicCyan) },
            text = {
                Column {
                    OutlinedTextField(
                        value = gameTitleInput,
                        onValueChange = { gameTitleInput = it },
                        label = { Text("Game Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = gameDescInput,
                        onValueChange = { gameDescInput = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = gameRewardInput,
                            onValueChange = { gameRewardInput = it },
                            label = { Text("Reward Pts") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = gameLimitInput,
                            onValueChange = { gameLimitInput = it },
                            label = { Text("Daily Limit") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (gameTitleInput.isNotBlank()) {
                            val r = gameRewardInput.toLongOrNull() ?: 35L
                            val l = gameLimitInput.toIntOrNull() ?: 15
                            val game = editingGame!!.copy(
                                title = gameTitleInput,
                                description = gameDescInput,
                                rewardPoints = r,
                                dailyLimit = l,
                                difficulty = gameDiffInput,
                                type = gameTypeInput
                            )
                            val res = repository.adminSaveGame(game)
                            editingGame = null
                            adminToast = res.getOrNull()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) { Text("Save Game", color = MagicDeepNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { editingGame = null }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // ========================================================
    // MODAL: CREATE QUEST / TASK
    // ========================================================
    if (showCreateTaskModal) {
        AlertDialog(
            onDismissRequest = { showCreateTaskModal = false },
            title = { Text("Create Realm Quest", fontWeight = FontWeight.Bold, color = MagicCyan) },
            text = {
                Column {
                    OutlinedTextField(
                        value = taskTitleInput,
                        onValueChange = { taskTitleInput = it },
                        label = { Text("Quest Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = taskDescInput,
                        onValueChange = { taskDescInput = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = taskRewardInput,
                        onValueChange = { taskRewardInput = it },
                        label = { Text("Reward Points") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskTitleInput.isNotBlank()) {
                            val pts = taskRewardInput.toLongOrNull() ?: 80L
                            val task = TaskItem(
                                title = taskTitleInput,
                                description = taskDescInput,
                                pointsReward = pts,
                                type = taskTypeInput
                            )
                            val res = repository.adminSaveTask(task)
                            showCreateTaskModal = false
                            adminToast = res.getOrNull()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) { Text("Publish", color = MagicDeepNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTaskModal = false }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // ========================================================
    // MODAL: PROVISION NEW ADMIN
    // ========================================================
    if (showAddAdminModal) {
        AlertDialog(
            onDismissRequest = { showAddAdminModal = false },
            title = { Text("Provision Administrator Account", fontWeight = FontWeight.Bold, color = MagicCyan) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newAdminName,
                        onValueChange = { newAdminName = it },
                        label = { Text("Full Legal Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = newAdminEmail,
                        onValueChange = { newAdminEmail = it },
                        label = { Text("Administrative Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAdminName.isNotBlank() && newAdminEmail.isNotBlank()) {
                            val res = repository.adminCreateAdminAccount(
                                newAdminName,
                                newAdminEmail,
                                newAdminRole,
                                AdminPermission.values().toList()
                            )
                            showAddAdminModal = false
                            adminToast = res.getOrNull() ?: res.exceptionOrNull()?.message
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) { Text("Provision Admin", color = MagicDeepNavy, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showAddAdminModal = false }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // Remote Payment Gateway Edit Dialog
    if (editingGateway != null) {
        val gw = editingGateway!!
        AlertDialog(
            onDismissRequest = { editingGateway = null },
            title = { Text("Edit Payment Gateway: ${gw.name}", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Currency: ${gw.currencyCode} • ID: ${gw.id}", fontSize = 12.sp, color = MagicTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editGatewayMin,
                        onValueChange = { editGatewayMin = it },
                        label = { Text("Minimum Withdrawal (${gw.currencyCode})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editGatewayMax,
                        onValueChange = { editGatewayMax = it },
                        label = { Text("Maximum Withdrawal (${gw.currencyCode})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editGatewayFee,
                        onValueChange = { editGatewayFee = it },
                        label = { Text("Gateway Fee Percentage (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val minVal = editGatewayMin.toDoubleOrNull() ?: gw.minWithdrawal
                        val maxVal = editGatewayMax.toDoubleOrNull() ?: gw.maxWithdrawal
                        val feeVal = editGatewayFee.toDoubleOrNull() ?: gw.feePercent
                        val updated = gw.copy(
                            minWithdrawal = minVal,
                            maxWithdrawal = maxVal,
                            feePercent = feeVal
                        )
                        val res = repository.adminUpdatePaymentGateway(updated)
                        editingGateway = null
                        adminToast = res.getOrNull() ?: res.exceptionOrNull()?.message
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) {
                    Text("Save Gateway", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingGateway = null }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // Remote Game Reward Edit Dialog
    if (editingGameReward != null) {
        val gm = editingGameReward!!
        AlertDialog(
            onDismissRequest = { editingGameReward = null },
            title = { Text("Tune Game Reward: ${gm.title}", color = MagicGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Change points reward for this game without rebuilding APK.", fontSize = 12.sp, color = MagicTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editRewardAmount,
                        onValueChange = { editRewardAmount = it },
                        label = { Text("Points Reward per Victory") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pts = editRewardAmount.toLongOrNull() ?: gm.rewardPoints
                        val res = repository.adminUpdateGameReward(gm.id, pts)
                        editingGameReward = null
                        adminToast = res.getOrNull() ?: res.exceptionOrNull()?.message
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicGold)
                ) {
                    Text("Save & Propagate", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingGameReward = null }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // Remote APK Version Policy Dialog
    if (showVersionDialog) {
        AlertDialog(
            onDismissRequest = { showVersionDialog = false },
            title = { Text("Deploy APK Version Policy", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editLatestVersionCode,
                            onValueChange = { editLatestVersionCode = it },
                            label = { Text("Latest Code") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editLatestVersionName,
                            onValueChange = { editLatestVersionName = it },
                            label = { Text("Version Name") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mandatory Force Update", fontSize = 12.sp, color = MagicTextPrimary)
                        Switch(checked = editForceUpdate, onCheckedChange = { editForceUpdate = it })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editDownloadUrl,
                        onValueChange = { editDownloadUrl = it },
                        label = { Text("Download / Store URL") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editReleaseNotes,
                        onValueChange = { editReleaseNotes = it },
                        label = { Text("What's New / Changelog") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val code = editLatestVersionCode.toIntOrNull() ?: 1
                        val updated = appVersionInfo.copy(
                            latestVersionCode = code,
                            latestVersionName = editLatestVersionName,
                            forceUpdate = editForceUpdate,
                            downloadUrl = editDownloadUrl,
                            releaseNotes = editReleaseNotes
                        )
                        val res = repository.adminUpdateAppVersion(updated)
                        showVersionDialog = false
                        adminToast = res.getOrNull() ?: res.exceptionOrNull()?.message
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) {
                    Text("Publish Policy", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showVersionDialog = false }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // Remote Maintenance Mode Dialog
    if (showMaintenanceDialog) {
        var isMaintActive by remember { mutableStateOf(maintenanceConfig.isMaintenanceMode) }
        AlertDialog(
            onDismissRequest = { showMaintenanceDialog = false },
            title = { Text("Realm Maintenance Configuration", color = MagicGold, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Lock User Portal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                        Switch(checked = isMaintActive, onCheckedChange = { isMaintActive = it })
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editMaintenanceMessage,
                        onValueChange = { editMaintenanceMessage = it },
                        label = { Text("Maintenance Announcement Message") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editMaintenanceEndTime,
                        onValueChange = { editMaintenanceEndTime = it },
                        label = { Text("Estimated End Time / ETA") },
                        placeholder = { Text("e.g. 1 - 2 hours") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val res = repository.adminToggleMaintenanceMode(
                            isMaintenance = isMaintActive,
                            message = editMaintenanceMessage,
                            endTime = editMaintenanceEndTime
                        )
                        showMaintenanceDialog = false
                        adminToast = res.getOrNull() ?: res.exceptionOrNull()?.message
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMaintActive) MagicCrimson else MagicEmerald
                    )
                ) {
                    Text(if (isMaintActive) "Activate Maintenance" else "End Maintenance", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showMaintenanceDialog = false }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // Remote Banner Add Dialog
    if (showBannerDialog) {
        AlertDialog(
            onDismissRequest = { showBannerDialog = false },
            title = { Text("Create Promotional Banner", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editBannerTitle,
                        onValueChange = { editBannerTitle = it },
                        label = { Text("Banner Headline") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editBannerSubtitle,
                        onValueChange = { editBannerSubtitle = it },
                        label = { Text("Subtitle / Details") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select Click Destination:", fontSize = 11.sp, color = MagicTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("NAVIGATE_EARN" to "Earn", "NAVIGATE_GAMES" to "Games", "NAVIGATE_TASKS" to "Quests", "NAVIGATE_REFERRAL" to "Invite").forEach { (action, label) ->
                            val isSel = editBannerAction == action
                            OutlinedButton(
                                onClick = { editBannerAction = action },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(2.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MagicCyan else MagicCardBorder),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (isSel) MagicCyan.copy(alpha = 0.2f) else Color.Transparent)
                            ) {
                                Text(label, fontSize = 10.sp, color = if (isSel) MagicCyan else MagicTextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editBannerTitle.isNotBlank()) {
                            val newBanner = BannerItem(
                                title = editBannerTitle,
                                subtitle = editBannerSubtitle,
                                actionType = editBannerAction,
                                isEnabled = true
                            )
                            val res = repository.adminAddBanner(newBanner)
                            showBannerDialog = false
                            adminToast = res.getOrNull() ?: res.exceptionOrNull()?.message
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) {
                    Text("Add Banner", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBannerDialog = false }) { Text("Cancel", color = MagicTextSecondary) }
            },
            containerColor = MagicCardBg
        )
    }

    // Toast Alert
    if (adminToast != null) {
        AlertDialog(
            onDismissRequest = { adminToast = null },
            title = { Text("Mission Control Notice", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = { Text(adminToast ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { adminToast = null }) {
                    Text("OK", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}

// -------------------------------------------------------------
// Helper UI Components for Admin Panel
// -------------------------------------------------------------
@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF111730))
            .border(1.dp, Color(0xFF222C52), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111730))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = MagicTextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 17.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = MagicTextMuted)
        }
    }
}

@Composable
private fun MetricProgressBar(label: String, count: String, progress: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontSize = 12.sp, color = MagicTextPrimary)
            Text(count, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = color,
            trackColor = Color(0xFF1E284A)
        )
    }
}

@Composable
private fun EmergencySwitchRow(label: String, isHalted: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = MagicTextPrimary)
        Switch(checked = isHalted, onCheckedChange = onToggle)
    }
}

@Composable
private fun SystemHealthRow(service: String, status: String, isSuccess: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(service, fontSize = 12.sp, color = MagicTextPrimary)
        StatusPill(status, isSuccess = isSuccess)
    }
}

@Composable
private fun AdminInfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 11.sp, color = MagicTextSecondary)
        Text(value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = MagicTextPrimary)
    }
}
