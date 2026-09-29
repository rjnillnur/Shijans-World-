package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.UserProfile
import com.example.ui.theme.*

enum class AppScreen(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Castle),
    EARN("Earn", Icons.Default.SlowMotionVideo),
    TASKS("Tasks", Icons.Default.Assignment),
    GAMES("Games", Icons.Default.SportsEsports),
    REFERRAL("Referral", Icons.Default.GroupAdd),
    WALLET("Wallet", Icons.Default.AccountBalanceWallet),
    LEADERBOARD("Ranks", Icons.Default.Leaderboard),
    ACHIEVEMENTS("Badges", Icons.Default.EmojiEvents),
    PROFILE("Profile", Icons.Default.Person),
    NOTIFICATIONS("Inbox", Icons.Default.Notifications),
    SUPPORT("Support", Icons.Default.HelpCenter),
    SECURITY("Security", Icons.Default.Security)
}

@Composable
fun MagicalTopAppBar(
    appName: String,
    currentUser: UserProfile?,
    unreadNotificationsCount: Int,
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MagicSurface)
            .border(0.dp, Color.Transparent, RoundedCornerShape(0.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Brand with Logo (User Portal)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigate(AppScreen.HOME) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.magical_world_logo),
                    contentDescription = "Magical World Logo",
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MagicCyan, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = appName,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = MagicTextPrimary
                    )
                    Text(
                        text = "EXPLORE • EARN • PLAY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = MagicCyan,
                        letterSpacing = 1.sp
                    )
                }
            }

            // User Actions: Points balance & Notifications ONLY (Strictly NO Admin options here)
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentUser != null) {
                    PointsBadge(
                        points = currentUser.points,
                        prefix = "",
                        modifier = Modifier.clickable { onNavigate(AppScreen.WALLET) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = { onNavigate(AppScreen.NOTIFICATIONS) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MagicCardBg)
                            .testTag("nav_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = MagicCrimson,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadNotificationsCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MagicCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MagicalBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        AppScreen.HOME,
        AppScreen.EARN,
        AppScreen.TASKS,
        AppScreen.GAMES,
        AppScreen.WALLET,
        AppScreen.PROFILE
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .border(1.dp, MagicCardBorder, RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
        containerColor = MagicSurface,
        tonalElevation = 12.dp
    ) {
        items.forEach { screen ->
            val isSelected = currentScreen == screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MagicCyan,
                    selectedTextColor = MagicCyan,
                    unselectedIconColor = MagicTextMuted,
                    unselectedTextColor = MagicTextMuted,
                    indicatorColor = MagicCardBg
                ),
                modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
            )
        }
    }
}
