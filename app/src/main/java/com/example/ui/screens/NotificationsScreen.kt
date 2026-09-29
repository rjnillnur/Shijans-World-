package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.NotificationType
import com.example.ui.components.FantasySectionHeader
import com.example.ui.components.MagicalCard
import com.example.ui.theme.*

@Composable
fun NotificationsScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val notifications by repository.notifications.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            FantasySectionHeader(
                title = "Realm Dispatches & Alerts",
                subtitle = "Royal decrees, quest approvals, and payout notices"
            )
        }

        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notifications at this time.", color = MagicTextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            items(notifications) { notif ->
                MagicalCard(
                    borderGlow = when (notif.type) {
                        NotificationType.WITHDRAWAL -> MagicEmerald.copy(alpha = 0.4f)
                        NotificationType.TASK -> MagicCyan.copy(alpha = 0.4f)
                        NotificationType.PROMOTIONAL -> MagicGold.copy(alpha = 0.4f)
                        else -> MagicPurple.copy(alpha = 0.3f)
                    }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when (notif.type) {
                                        NotificationType.WITHDRAWAL -> MagicEmerald.copy(alpha = 0.2f)
                                        NotificationType.TASK -> MagicCyan.copy(alpha = 0.2f)
                                        NotificationType.PROMOTIONAL -> MagicGold.copy(alpha = 0.2f)
                                        else -> MagicPurple.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (notif.type) {
                                    NotificationType.WITHDRAWAL -> Icons.Default.Payments
                                    NotificationType.TASK -> Icons.Default.AssignmentTurnedIn
                                    NotificationType.PROMOTIONAL -> Icons.Default.LocalOffer
                                    else -> Icons.Default.Campaign
                                },
                                contentDescription = null,
                                tint = when (notif.type) {
                                    NotificationType.WITHDRAWAL -> MagicEmerald
                                    NotificationType.TASK -> MagicCyan
                                    NotificationType.PROMOTIONAL -> MagicGold
                                    else -> MagicPurple
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = notif.title,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MagicTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notif.message,
                                fontSize = 12.sp,
                                color = MagicTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.US).format(java.util.Date(notif.timestamp)),
                                fontSize = 10.sp,
                                color = MagicTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
