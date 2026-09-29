package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
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

/**
 * Admin Panel - Remote Configuration & APK Update Architecture Section
 * Remotely controls Payment Gateways (bKash, Nagad, Rocket, Upay, Binance, UPI, Bank),
 * Game Rewards (e.g. 100 -> 150), Maintenance Mode, Feature Flags, and APK Updates.
 */
fun LazyListScope.adminRemoteConfigSection(
    repository: AppRepository,
    withdrawalRemoteSettings: WithdrawalRemoteSettings,
    games: List<GameItem>,
    appVersionInfo: AppVersionInfo,
    banners: List<BannerItem>,
    maintenanceConfig: MaintenanceConfig,
    featureFlags: FeatureFlags,
    isRemoteSyncing: Boolean,
    lastRemoteSyncTime: Long,
    onEditGateway: (PaymentGatewayConfig) -> Unit,
    onEditGameReward: (GameItem) -> Unit,
    onOpenVersionDialog: () -> Unit,
    onOpenBannerDialog: () -> Unit,
    onOpenMaintenanceDialog: () -> Unit
) {
    // 1. Cloud Sync Engine Status Card
    item {
        MagicalCard(borderGlow = MagicCyan) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MagicCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = MagicCyan, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Firebase Remote Config Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MagicTextPrimary
                            )
                            Text(
                                text = "Target: magic-game-9f8c0 (Firestore OTA)",
                                fontSize = 11.sp,
                                color = MagicTextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = { repository.adminRefreshRemoteConfig() },
                        enabled = !isRemoteSyncing,
                        colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isRemoteSyncing) {
                            CircularProgressIndicator(color = MagicDeepNavy, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Syncing...", fontSize = 11.sp, color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = MagicDeepNavy, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Cloud", fontSize = 11.sp, color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F1530))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Last Synced: ${SimpleDateFormat("MMM dd, HH:mm:ss", Locale.US).format(Date(lastRemoteSyncTime))}",
                        fontSize = 11.sp,
                        color = MagicTextSecondary
                    )
                    Text(
                        text = "BUILD ONCE • REMOTE CONTROL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = MagicGold
                    )
                }
            }
        }
    }

    // 2. Remote Payment Gateways Manager
    item {
        FantasySectionHeader(
            title = "Payment Gateways (Remote Control)",
            subtitle = "bKash, Nagad, Rocket, Upay, Binance, UPI, Bank"
        )
    }

    item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            withdrawalRemoteSettings.gateways.forEach { gw ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, if (gw.isEnabled) MagicCardBorder else MagicCrimson.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = gw.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MagicTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MagicCyan.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(gw.badge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MagicCyan)
                                }
                                if (gw.currencyCode != "BDT") {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("(${gw.currencyCode})", fontSize = 10.sp, color = MagicGold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Min: ${withdrawalRemoteSettings.currencySymbol}${gw.minWithdrawal.toInt()}  •  Max: ${withdrawalRemoteSettings.currencySymbol}${gw.maxWithdrawal.toInt()}  •  Fee: ${gw.feePercent}%",
                                fontSize = 11.sp,
                                color = MagicTextSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onEditGateway(gw) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Gateway", tint = MagicGold, modifier = Modifier.size(16.dp))
                            }

                            Spacer(modifier = Modifier.width(6.dp))

                            Switch(
                                checked = gw.isEnabled,
                                onCheckedChange = { isChecked ->
                                    repository.adminUpdatePaymentGateway(gw.copy(isEnabled = isChecked))
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // 3. Remote Game Rewards Tuning
    item {
        FantasySectionHeader(
            title = "Game Rewards Matrix (Remote Tuning)",
            subtitle = "Instantly change rewards (e.g. 50 → 150) without rebuilding APK"
        )
    }

    item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            games.forEach { g ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MagicPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(g.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                Text("${g.category} • ${g.difficulty} • Daily limit: ${g.dailyLimit}", fontSize = 11.sp, color = MagicTextSecondary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${g.rewardPoints} Pts",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MagicGold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { onEditGameReward(g) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = "Tune Reward", tint = MagicCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Reward Presets Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(35L, 50L, 100L, 150L, 200L).forEach { pts ->
                                val isCurrent = g.rewardPoints == pts
                                OutlinedButton(
                                    onClick = {
                                        repository.adminUpdateGameReward(g.id, pts)
                                    },
                                    modifier = Modifier.weight(1f),
                                    contentPadding = PaddingValues(2.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isCurrent) MagicGold else MagicCardBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isCurrent) MagicGold.copy(alpha = 0.15f) else Color.Transparent
                                    )
                                ) {
                                    Text(
                                        text = "$pts",
                                        fontSize = 11.sp,
                                        fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Normal,
                                        color = if (isCurrent) MagicGold else MagicTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 4. Realm Maintenance Mode Remote Switch
    item {
        FantasySectionHeader(
            title = "Realm Maintenance Controller",
            subtitle = "Lock user portal with custom downtime announcement"
        )
    }

    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, if (maintenanceConfig.isMaintenanceMode) MagicCrimson else MagicCardBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = MagicCardBg)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Maintenance Mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MagicTextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusPill(
                                status = if (maintenanceConfig.isMaintenanceMode) "PORTAL LOCKED" else "ACTIVE",
                                isSuccess = !maintenanceConfig.isMaintenanceMode,
                                isWarning = maintenanceConfig.isMaintenanceMode
                            )
                        }
                        Text(
                            text = if (maintenanceConfig.isMaintenanceMode) "ETA: ${maintenanceConfig.estimatedEndTime}" else "All realm portals open for adventurers",
                            fontSize = 11.sp,
                            color = MagicTextSecondary
                        )
                    }

                    Button(
                        onClick = onOpenMaintenanceDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (maintenanceConfig.isMaintenanceMode) MagicEmerald else MagicCrimson
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (maintenanceConfig.isMaintenanceMode) "Deactivate" else "Configure",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MagicDeepNavy
                        )
                    }
                }

                if (maintenanceConfig.isMaintenanceMode) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Active Message: \"${maintenanceConfig.message}\"",
                        fontSize = 12.sp,
                        color = MagicGold,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }

    // 5. APK Version & OTA Update Manager
    item {
        FantasySectionHeader(
            title = "APK Version & OTA Update Control",
            subtitle = "Push version updates, release notes, and force update policies"
        )
    }

    item {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MagicCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = MagicCardBg)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current APK: v${appVersionInfo.currentVersionName} (Code ${appVersionInfo.currentVersionCode})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                        Text("Remote Latest: v${appVersionInfo.latestVersionName} (Code ${appVersionInfo.latestVersionCode})", fontSize = 12.sp, color = MagicGold)
                    }

                    Button(
                        onClick = onOpenVersionDialog,
                        colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = MagicDeepNavy, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Update Policy", color = MagicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Enforce Update: ${if (appVersionInfo.forceUpdate) "MANDATORY" else "OPTIONAL"}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (appVersionInfo.forceUpdate) MagicCrimson else MagicEmerald
                    )
                    Text(
                        text = "Min Supported: Code ${appVersionInfo.minSupportedVersionCode}",
                        fontSize = 11.sp,
                        color = MagicTextSecondary
                    )
                }
            }
        }
    }

    // 6. Promotional Banners Manager
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FantasySectionHeader(
                title = "Promotional Banners (${banners.size})",
                subtitle = "Manage event banners and quick destination shortcuts"
            )
            IconButton(
                onClick = onOpenBannerDialog,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MagicCyan)
                    .size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Banner", tint = MagicDeepNavy, modifier = Modifier.size(18.dp))
            }
        }
    }

    item {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            banners.forEach { b ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MagicCardBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(b.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagicTextPrimary)
                            Text(b.subtitle, fontSize = 11.sp, color = MagicTextSecondary)
                            Text("Action: ${b.actionType}", fontSize = 10.sp, color = MagicCyan)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = b.isEnabled,
                                onCheckedChange = { isChecked ->
                                    val updated = banners.map { if (it.id == b.id) it.copy(isEnabled = isChecked) else it }
                                    repository.adminUpdateBanners(updated)
                                }
                            )

                            IconButton(
                                onClick = { repository.adminDeleteBanner(b.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MagicCrimson, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // 7. Runtime Feature Flags
    item {
        FantasySectionHeader(
            title = "Runtime Feature Flags",
            subtitle = "Instantly toggle modules without app reinstall"
        )
    }

    item {
        MagicalCard(borderGlow = MagicPurple) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    Triple("Arcade Games Engine", featureFlags.enableGames) { checked: Boolean ->
                        repository.adminUpdateFeatureFlags(featureFlags.copy(enableGames = checked))
                        Unit
                    },
                    Triple("Quests & Tasks System", featureFlags.enableTasks) { checked: Boolean ->
                        repository.adminUpdateFeatureFlags(featureFlags.copy(enableTasks = checked))
                        Unit
                    },
                    Triple("Ad Monetization & Rewards", featureFlags.enableAds) { checked: Boolean ->
                        repository.adminUpdateFeatureFlags(featureFlags.copy(enableAds = checked))
                        Unit
                    },
                    Triple("Withdrawals & Payouts", featureFlags.enableWithdrawals) { checked: Boolean ->
                        repository.adminUpdateFeatureFlags(featureFlags.copy(enableWithdrawals = checked))
                        Unit
                    },
                    Triple("Referral Summon System", featureFlags.enableReferrals) { checked: Boolean ->
                        repository.adminUpdateFeatureFlags(featureFlags.copy(enableReferrals = checked))
                        Unit
                    },
                    Triple("New User Registrations", featureFlags.enableRegistration) { checked: Boolean ->
                        repository.adminUpdateFeatureFlags(featureFlags.copy(enableRegistration = checked))
                        Unit
                    },
                    Triple("Leaderboard & Ranks", featureFlags.enableLeaderboard) { checked: Boolean ->
                        repository.adminUpdateFeatureFlags(featureFlags.copy(enableLeaderboard = checked))
                        Unit
                    }
                ).forEach { (label, isEnabled, onToggle) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, fontSize = 12.sp, color = MagicTextPrimary)
                        Switch(checked = isEnabled, onCheckedChange = onToggle)
                    }
                }
            }
        }
    }
}
