package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun EarnScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val adSettings by repository.adSettings.collectAsState()

    var showRewardedAdDialog by remember { mutableStateOf(false) }
    var resultAlert by remember { mutableStateOf<String?>(null) }

    val user = currentUser ?: return
    val remainingAds = (adSettings.dailyRewardedLimit - user.adsWatchedToday).coerceAtLeast(0)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            FantasySectionHeader(
                title = "Watch & Earn Sanctuary",
                subtitle = "Empower your journey with magical visions & ad rewards"
            )
        }

        // Daily Ad Limit Tracker Card
        item {
            MagicalCard(
                borderGlow = MagicCyan
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Daily Vision Limit", color = MagicTextSecondary, fontSize = 12.sp)
                        Text(
                            "$remainingAds / ${adSettings.dailyRewardedLimit} Remaining",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (remainingAds > 0) MagicCyan else MagicCrimson
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MagicCyan.copy(alpha = 0.2f))
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SlowMotionVideo,
                            contentDescription = null,
                            tint = MagicCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val progress = if (adSettings.dailyRewardedLimit > 0) {
                    user.adsWatchedToday.toFloat() / adSettings.dailyRewardedLimit.toFloat()
                } else 0f

                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = MagicCyan,
                    trackColor = Color(0xFF1E284A)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Watched Today: ${user.adsWatchedToday}", fontSize = 11.sp, color = MagicTextMuted)
                    Text("Reward: +${adSettings.rewardedPoints} Pts / ad", fontSize = 11.sp, color = MagicGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Main Watch Ad Action Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        2.dp,
                        Brush.horizontalGradient(listOf(MagicCyan, MagicPurple, MagicGold)),
                        RoundedCornerShape(18.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = MagicCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(MagicCyan.copy(alpha = 0.3f), MagicPurple.copy(alpha = 0.3f)))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Video Ad",
                            tint = MagicCyan,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "WATCH REWARDED AD",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MagicTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Watch the full sponsored vision without skipping to receive +${adSettings.rewardedPoints} crystal points directly to your wallet.",
                        fontSize = 12.sp,
                        color = MagicTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    MagicalButton(
                        text = if (remainingAds > 0) "Watch Video (+${adSettings.rewardedPoints} Pts)" else "Daily Limit Reached",
                        onClick = {
                            if (remainingAds > 0) {
                                showRewardedAdDialog = true
                            } else {
                                resultAlert = "You have exhausted your daily vision limit of ${adSettings.dailyRewardedLimit} ads. Resets at midnight!"
                            }
                        },
                        icon = Icons.Default.PlayCircle,
                        enabled = remainingAds > 0,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "watch_rewarded_ad_main_button"
                    )
                }
            }
        }

        // Anti-Fraud & Security Notice Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F1630))
                    .border(1.dp, MagicPurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Security",
                    tint = MagicPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Anti-Abuse Verification Active",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MagicTextPrimary
                    )
                    Text(
                        text = "Ad rewards are verified with server tokens. Fast forwarding, bots, or duplicate requests are rejected automatically.",
                        fontSize = 11.sp,
                        color = MagicTextSecondary
                    )
                }
            }
        }

        // AdMob Banner Placement
        item {
            AdMobBannerCard(adSettings = adSettings, placement = "Earn Page")
        }

        // Adsterra Network Section
        item {
            FantasySectionHeader(
                title = "Web Partner Campaigns (Adsterra)",
                subtitle = "Supported high-CPM multi-channel ad formats"
            )
        }

        item {
            AdsterraWebAdCard(adSettings = adSettings)
        }

        // Ad Units Specs (AdMob Central Service transparency)
        item {
            MagicalCard(
                borderGlow = MagicCardBorder
            ) {
                Text(
                    text = "Centralized Ad Network Configuration",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MagicTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Rewarded Ad Unit: ${adSettings.adMobUnits.rewardedId}\n• Interstitial Unit: ${adSettings.adMobUnits.interstitialId}\n• Banner Unit: ${adSettings.adMobUnits.bannerId}\n• Mode: ${if (adSettings.isTestMode) "Development Test Ads" else "Production Ads"}",
                    fontSize = 11.sp,
                    color = MagicTextMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }

    if (showRewardedAdDialog) {
        RewardedVideoModal(
            adSettings = adSettings,
            rewardPoints = adSettings.rewardedPoints,
            onRewardEarned = {
                val res = repository.watchRewardedAd()
                resultAlert = if (res.isSuccess) {
                    "Splendid! Validated completion. Awarded +${res.getOrThrow()} Points to your wallet! 💎"
                } else {
                    res.exceptionOrNull()?.message
                }
            },
            onDismiss = { showRewardedAdDialog = false }
        )
    }

    if (resultAlert != null) {
        AlertDialog(
            onDismissRequest = { resultAlert = null },
            title = { Text("Realm Verification", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = { Text(resultAlert ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { resultAlert = null }) {
                    Text("OK", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}
