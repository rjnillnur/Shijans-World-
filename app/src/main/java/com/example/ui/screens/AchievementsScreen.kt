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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.ui.components.FantasySectionHeader
import com.example.ui.components.MagicalCard
import com.example.ui.components.PointsBadge
import com.example.ui.components.StatusPill
import com.example.ui.theme.*

@Composable
fun AchievementsScreen(
    repository: AppRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val achievements by repository.achievements.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MagicCyan)
                }
                Spacer(modifier = Modifier.width(6.dp))
                FantasySectionHeader(
                    title = "Royal Feats & Achievements",
                    subtitle = "Complete milestones to earn eternal prestige and bonus points"
                )
            }
        }

        items(achievements) { ach ->
            MagicalCard(
                borderGlow = if (ach.isUnlocked) MagicGold.copy(alpha = 0.5f) else MagicCardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (ach.isUnlocked) MagicGold.copy(alpha = 0.2f) else MagicSurface)
                                .border(1.dp, if (ach.isUnlocked) MagicGold else MagicCardBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (ach.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (ach.isUnlocked) MagicGold else MagicTextMuted,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(ach.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Text(ach.description, fontSize = 11.sp, color = MagicTextSecondary)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        PointsBadge(points = ach.rewardPoints, prefix = "+")
                        Spacer(modifier = Modifier.height(6.dp))
                        StatusPill(if (ach.isUnlocked) "UNLOCKED" else "LOCKED", isSuccess = ach.isUnlocked)
                    }
                }
            }
        }
    }
}
