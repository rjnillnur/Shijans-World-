package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AdSettings
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AdMobBannerCard(
    adSettings: AdSettings,
    placement: String = "Home",
    modifier: Modifier = Modifier
) {
    if (!adSettings.isAdsGloballyEnabled || !adSettings.isBannerEnabled) return

    val currentAdId = if (adSettings.isTestMode) {
        adSettings.adMobUnits.testBannerId
    } else {
        adSettings.adMobUnits.bannerId
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F152E))
            .border(1.dp, MagicCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF26335B))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Ad • Google AdMob",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagicCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    if (adSettings.isTestMode) {
                        Text(
                            text = "[TEST MODE]",
                            fontSize = 10.sp,
                            color = MagicGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = placement,
                    fontSize = 10.sp,
                    color = MagicTextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Simulated AdMob Adaptive Banner Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF161F42))
                    .border(1.dp, Color(0xFF283669), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Ad",
                        tint = MagicGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Play Crystal Legends 3D",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagicTextPrimary
                        )
                        Text(
                            text = "Unit: ${currentAdId.takeLast(10)}...",
                            fontSize = 10.sp,
                            color = MagicTextMuted
                        )
                    }
                }

                Button(
                    onClick = { /* External ad link */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Install", color = MagicDeepNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdsterraWebAdCard(
    adSettings: AdSettings,
    modifier: Modifier = Modifier
) {
    if (!adSettings.isAdsGloballyEnabled || !adSettings.adsterra.isEnabled) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF120E24))
            .border(1.dp, MagicPurple.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(12.dp)
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
                            .clip(RoundedCornerShape(4.dp))
                            .background(MagicPurple.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Sponsored • Adsterra CPM Network",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagicPurple
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = null,
                    tint = MagicTextMuted,
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "⚡ High-Yield Web Partner Campaign Zone",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MagicTextPrimary
            )
            Text(
                text = "Script: pl31578566.profitableratecpmnetwork.com/d7/8d/4c...",
                fontSize = 10.sp,
                color = MagicTextMuted
            )
        }
    }
}

@Composable
fun RewardedVideoModal(
    adSettings: AdSettings,
    rewardPoints: Long,
    onRewardEarned: () -> Unit,
    onDismiss: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(5) }
    var isCompleted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
        isCompleted = true
    }

    Dialog(
        onDismissRequest = {
            if (isCompleted) onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = isCompleted, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, MagicCyan, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MagicDeepNavy)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with countdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SlowMotionVideo,
                            contentDescription = null,
                            tint = MagicCyan
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Rewarded Ad",
                            fontWeight = FontWeight.Bold,
                            color = MagicTextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isCompleted) MagicEmerald.copy(alpha = 0.2f) else MagicGold.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isCompleted) "VERIFIED" else "Reward in ${countdown}s",
                            color = if (isCompleted) MagicEmerald else MagicGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ad Showcase Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(listOf(Color(0xFF1E143E), Color(0xFF0F1836)))
                        )
                        .border(1.dp, MagicPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = MagicCyan,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Mystic Blades of Arcana",
                            color = MagicTextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Epic 3D Multiplayer Fantasy RPG",
                            color = MagicTextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Unit: ${adSettings.adMobUnits.rewardedId}",
                            fontSize = 9.sp,
                            color = MagicTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { if (isCompleted) 1f else (5 - countdown) / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MagicCyan,
                    trackColor = Color(0xFF212C52)
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (isCompleted) {
                    MagicalButton(
                        text = "Claim +$rewardPoints Points!",
                        onClick = {
                            onRewardEarned()
                            onDismiss()
                        },
                        icon = Icons.Default.MonetizationOn,
                        modifier = Modifier.fillMaxWidth(),
                        gradient = Brush.horizontalGradient(listOf(MagicGoldDark, MagicGold)),
                        testTag = "claim_ad_reward_button"
                    )
                } else {
                    Text(
                        text = "Do not close early to earn full reward...",
                        color = MagicTextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
