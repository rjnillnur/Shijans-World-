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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.ui.components.FantasySectionHeader
import com.example.ui.components.MagicalCard
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    repository: AppRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val leaderboard by repository.leaderboard.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

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
                    title = "Realm Hall of Champions",
                    subtitle = "Supreme adventurers ranked by lifetime crystal honors"
                )
            }
        }

        // Top 3 Podium Card
        item {
            MagicalCard(borderGlow = MagicGold) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    leaderboard.take(3).forEach { item ->
                        val isFirst = item.rank == 1
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isFirst) 56.dp else 46.dp)
                                    .clip(CircleShape)
                                    .background(if (isFirst) MagicGold.copy(alpha = 0.25f) else MagicPurple.copy(alpha = 0.25f))
                                    .border(2.dp, if (isFirst) MagicGold else MagicCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${item.rank}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = if (isFirst) 18.sp else 14.sp,
                                    color = if (isFirst) MagicGold else MagicCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(item.fullName.split(" ").first(), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MagicTextPrimary)
                            Text("${item.points} Pts", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MagicGold)
                            Text(item.badge, fontSize = 9.sp, color = MagicTextSecondary)
                        }
                    }
                }
            }
        }

        items(leaderboard) { entry ->
            val isCurrent = entry.userId == currentUser?.id
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCurrent) MagicPurple.copy(alpha = 0.25f) else MagicSurface)
                    .border(1.dp, if (isCurrent) MagicCyan else Color.Transparent, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${entry.rank}",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = when (entry.rank) {
                            1 -> MagicGold
                            2 -> Color(0xFFCBD5E1)
                            3 -> Color(0xFFD97706)
                            else -> MagicTextMuted
                        }
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(entry.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagicTextPrimary)
                            if (isCurrent) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("(You)", fontSize = 11.sp, color = MagicCyan, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("@${entry.username} • Level ${entry.level}", fontSize = 11.sp, color = MagicTextSecondary)
                    }
                }

                Text(
                    text = "${entry.points} Pts",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = MagicGold
                )
            }
        }
    }
}
