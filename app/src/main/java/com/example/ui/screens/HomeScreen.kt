package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: AppRepository,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val tasks by repository.tasks.collectAsState()
    val games by repository.games.collectAsState()
    val adSettings by repository.adSettings.collectAsState()
    val customization by repository.customization.collectAsState()
    val systemSettings by repository.systemSettings.collectAsState()
    val homeSections by repository.homeSections.collectAsState()
    val banners by repository.banners.collectAsState()
    val featureFlags by repository.featureFlags.collectAsState()

    var showRewardedAdDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    val user = currentUser ?: return

    val currencyAmount = String.format(java.util.Locale.US, "%.2f", user.points.toDouble() / systemSettings.pointsPerCurrencyUnit.toDouble())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Banner (Uploaded Magical World Banner)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .shadow(12.dp, RoundedCornerShape(18.dp), ambientColor = MagicCyan, spotColor = MagicPurple)
                    .border(1.5.dp, Brush.horizontalGradient(listOf(MagicCyan, MagicPurple, MagicGold)), RoundedCornerShape(18.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.magical_world_banner),
                    contentDescription = "Magical World Hero Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark vignette overlay at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, MagicDeepNavy.copy(alpha = 0.85f)),
                                startY = 160f
                            )
                        )
                )

                // Banner slogan bar
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = customization.appName,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = customization.appTagline,
                            fontSize = 11.sp,
                            color = MagicGold,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { onNavigate(AppScreen.EARN) },
                        colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("hero_explore_button")
                    ) {
                        Icon(Icons.Default.FlashOn, contentDescription = null, tint = MagicDeepNavy, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Earn Now", color = MagicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Remote Promotional Banners Carousel (Configured remotely via Firebase/Admin)
        val activeBanners = banners.filter { it.isEnabled }.sortedBy { it.orderIndex }
        if (activeBanners.isNotEmpty()) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(activeBanners) { b ->
                        Card(
                            modifier = Modifier
                                .width(280.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, MagicCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                .clickable {
                                    when (b.actionType) {
                                        "NAVIGATE_GAMES" -> onNavigate(AppScreen.GAMES)
                                        "NAVIGATE_TASKS" -> onNavigate(AppScreen.TASKS)
                                        "NAVIGATE_REFERRAL" -> onNavigate(AppScreen.REFERRAL)
                                        else -> onNavigate(AppScreen.EARN)
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MagicCyan.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("EVENT", fontSize = 9.sp, fontWeight = FontWeight.Black, color = MagicCyan)
                                    }
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = MagicCyan, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(b.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagicTextPrimary, maxLines = 1)
                                Text(b.subtitle, fontSize = 11.sp, color = MagicTextSecondary, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }

        // 2. Announcement Ticker
        if (customization.announcementText.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MagicSurface)
                        .border(1.dp, MagicGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MagicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = customization.announcementText,
                        color = MagicTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // 3. Welcome & Balance Cards
        item {
            MagicalCard(
                borderGlow = MagicCyan
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.magical_world_logo),
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, MagicCyan, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Welcome, ${user.fullName.split(" ").firstOrNull() ?: user.username}!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MagicTextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                StatusPill(
                                    status = user.verificationStatus.name,
                                    isSuccess = user.verificationStatus == VerificationStatus.VERIFIED,
                                    isWarning = user.verificationStatus == VerificationStatus.PENDING
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "@${user.username}",
                                    fontSize = 12.sp,
                                    color = MagicTextSecondary
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { onNavigate(AppScreen.PROFILE) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MagicCardBg)
                    ) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Profile", tint = MagicCyan)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Dashboard Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F1530))
                        .border(1.dp, MagicCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Current Points", fontSize = 11.sp, color = MagicTextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MagicGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${user.points}", fontSize = 17.sp, fontWeight = FontWeight.Black, color = MagicGold)
                        }
                    }

                    Box(modifier = Modifier.size(1.dp, 36.dp).background(MagicCardBorder))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Wallet Balance", fontSize = 11.sp, color = MagicTextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "${systemSettings.currencySymbol}$currencyAmount",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MagicEmerald
                        )
                    }

                    Box(modifier = Modifier.size(1.dp, 36.dp).background(MagicCardBorder))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ads Today", fontSize = 11.sp, color = MagicTextSecondary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "${user.adsWatchedToday}/${adSettings.dailyRewardedLimit}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = MagicCyan
                        )
                    }
                }
            }
        }

        // 4. Quick Action Buttons: Watch Ad & Earn + Daily Bonus
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Watch Ad Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showRewardedAdDialog = true }
                        .border(1.dp, MagicCyan.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .testTag("home_watch_ad_button"),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MagicCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = null, tint = MagicCyan, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Watch Ad & Earn", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagicTextPrimary)
                        Text("+${adSettings.rewardedPoints} Points", fontSize = 11.sp, color = MagicGold, fontWeight = FontWeight.Bold)
                    }
                }

                // Daily Bonus Card
                val isClaimedToday = user.dailyBonusClaimedDate.isNotEmpty()
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable(enabled = !isClaimedToday) {
                            val res = repository.claimDailyBonus()
                            snackbarMessage = if (res.isSuccess) "Claimed +${res.getOrThrow()} Daily Points! ✨" else res.exceptionOrNull()?.message
                        }
                        .border(1.dp, MagicGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                        .testTag("home_daily_bonus_button"),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MagicGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = MagicGold, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(if (isClaimedToday) "Bonus Claimed" else "Daily Bonus", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagicTextPrimary)
                        Text(if (isClaimedToday) "Done Today" else "+${systemSettings.dailyLoginBonusPoints} Points", fontSize = 11.sp, color = if (isClaimedToday) MagicTextMuted else MagicEmerald, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Quick Navigation to Leaderboard & Achievements
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(AppScreen.LEADERBOARD) }
                        .border(1.dp, MagicPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .testTag("home_leaderboard_btn"),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MagicPurple.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Leaderboard, contentDescription = null, tint = MagicPurple, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Leaderboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                            Text("Top Adventurers", fontSize = 10.sp, color = MagicTextSecondary)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigate(AppScreen.ACHIEVEMENTS) }
                        .border(1.dp, MagicGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .testTag("home_achievements_btn"),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MagicGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = MagicGold, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Achievements", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                            Text("Trophies & Feats", fontSize = 10.sp, color = MagicTextSecondary)
                        }
                    }
                }
            }
        }

        // 5. AdMob Banner Ad
        item {
            AdMobBannerCard(adSettings = adSettings, placement = "Home Feed")
        }

        // 6. Featured Games Section
        item {
            FantasySectionHeader(
                title = "Play & Win Realms",
                subtitle = "Skill & arcade mini-games with instant points",
                actionText = "View All",
                onActionClick = { onNavigate(AppScreen.GAMES) }
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(games) { game ->
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onNavigate(AppScreen.GAMES) }
                            .border(1.dp, MagicPurple.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                        colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(75.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.verticalGradient(listOf(MagicPurple.copy(alpha = 0.3f), MagicCardBg))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (game.type) {
                                        GameType.CRYSTAL_CAR_RUSH -> Icons.Default.DirectionsCar
                                        GameType.MAGIC_GEM_TAPPER -> Icons.Default.TouchApp
                                        GameType.FORTUNE_SPIN -> Icons.Default.Casino
                                        GameType.CASTLE_RUNNER -> Icons.Default.DirectionsRun
                                        else -> Icons.Default.SportsEsports
                                    },
                                    contentDescription = null,
                                    tint = MagicCyan,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(game.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("+${game.rewardPoints} Pts", color = MagicGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("${game.playsToday}/${game.dailyLimit}", color = MagicTextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // 7. Active Quests / Tasks Section
        item {
            FantasySectionHeader(
                title = "Royal Quests",
                subtitle = "Complete tasks to earn bounties",
                actionText = "See All (${tasks.size})",
                onActionClick = { onNavigate(AppScreen.TASKS) }
            )
        }

        items(tasks.take(3)) { task ->
            MagicalCard(
                borderGlow = MagicPurple.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                        Text(task.description, fontSize = 12.sp, color = MagicTextSecondary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { onNavigate(AppScreen.TASKS) },
                        colors = ButtonDefaults.buttonColors(containerColor = MagicPurple),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("+${task.pointsReward}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 8. Adsterra Web Ad Card
        item {
            AdsterraWebAdCard(adSettings = adSettings)
        }

        // 9. Recent Transactions Section
        item {
            FantasySectionHeader(
                title = "Recent Magical Deeds",
                subtitle = "Immutable ledger records",
                actionText = "Wallet",
                onActionClick = { onNavigate(AppScreen.WALLET) }
            )
        }

        items(transactions.take(4)) { tx ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MagicSurface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (tx.type) {
                                    TransactionType.AD_REWARD -> MagicCyan.copy(alpha = 0.2f)
                                    TransactionType.DAILY_BONUS -> MagicGold.copy(alpha = 0.2f)
                                    TransactionType.WITHDRAWAL -> MagicCrimson.copy(alpha = 0.2f)
                                    else -> MagicPurple.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (tx.type) {
                                TransactionType.AD_REWARD -> Icons.Default.PlayCircle
                                TransactionType.DAILY_BONUS -> Icons.Default.Star
                                TransactionType.WITHDRAWAL -> Icons.Default.ArrowOutward
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = when (tx.type) {
                                TransactionType.WITHDRAWAL -> MagicCrimson
                                TransactionType.DAILY_BONUS -> MagicGold
                                else -> MagicCyan
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(tx.title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MagicTextPrimary)
                        Text(tx.description, fontSize = 11.sp, color = MagicTextSecondary, maxLines = 1)
                    }
                }

                Text(
                    text = if (tx.amountPoints >= 0) "+${tx.amountPoints}" else "${tx.amountPoints}",
                    fontWeight = FontWeight.Bold,
                    color = if (tx.amountPoints >= 0) MagicGold else MagicCrimson,
                    fontSize = 13.sp
                )
            }
        }
    }

    // Rewarded Ad Modal
    if (showRewardedAdDialog) {
        RewardedVideoModal(
            adSettings = adSettings,
            rewardPoints = adSettings.rewardedPoints,
            onRewardEarned = {
                val res = repository.watchRewardedAd()
                snackbarMessage = if (res.isSuccess) "Ad Reward Claimed: +${res.getOrThrow()} Points! 💎" else res.exceptionOrNull()?.message
            },
            onDismiss = { showRewardedAdDialog = false }
        )
    }

    // Toast Snackbar
    if (snackbarMessage != null) {
        AlertDialog(
            onDismissRequest = { snackbarMessage = null },
            title = { Text("Realm Notification ✨", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = { Text(snackbarMessage ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { snackbarMessage = null }) {
                    Text("OK", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}
