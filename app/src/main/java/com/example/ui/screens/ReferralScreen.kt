package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ReferralScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val systemSettings by repository.systemSettings.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var copyAlert by remember { mutableStateOf<String?>(null) }

    val user = currentUser ?: return
    val referralLink = "https://magic-game-9f8c0.firebaseapp.com/join?ref=${user.referralCode}"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            FantasySectionHeader(
                title = "Guild Recruitment Portal",
                subtitle = "Summon companion adventurers and reap eternal crystal bounties"
            )
        }

        // Hero Card
        item {
            MagicalCard(
                borderGlow = MagicGold
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MagicGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = MagicGold,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "INVITE & EARN +${systemSettings.referralBonusPoints} PTS",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = MagicGold
                    )
                    Text(
                        text = "Your friends receive +${systemSettings.referredUserBonusPoints} bonus points instantly upon registration!",
                        fontSize = 12.sp,
                        color = MagicTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Referral Code Display & Copy
        item {
            MagicalCard {
                Text("Your Unique Referral Code", fontSize = 12.sp, color = MagicTextSecondary)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0D142E))
                        .border(1.dp, MagicCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.referralCode,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MagicCyan,
                        letterSpacing = 2.sp
                    )

                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(user.referralCode))
                            copyAlert = "Referral code copied to clipboard!"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("copy_ref_code_btn")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = MagicDeepNavy, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy", color = MagicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Share Button with Intent
                MagicalButton(
                    text = "Share Recruitment Link",
                    onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Join me in Magical World: Explore the Lands, Earn & Play! Use my invite code ${user.referralCode} to get 150 free points: $referralLink"
                            )
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Summon Friends to Magical World")
                        context.startActivity(shareIntent)
                    },
                    icon = Icons.Default.Share,
                    modifier = Modifier.fillMaxWidth(),
                    gradient = Brush.horizontalGradient(listOf(MagicPurple, MagicPink)),
                    testTag = "share_referral_link_btn"
                )
            }
        }

        // Stats Overview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MagicCardBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Total Recruits", fontSize = 11.sp, color = MagicTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${user.totalReferralsCount}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MagicCyan)
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MagicCardBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = MagicCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Referral Points", fontSize = 11.sp, color = MagicTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "+${user.totalReferralsCount * systemSettings.referralBonusPoints}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MagicGold
                        )
                    }
                }
            }
        }

        // Guild Rules
        item {
            MagicalCard {
                Text("Recruitment Rules & Fair Play", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MagicTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Self-referrals and creating multiple fake accounts on the same device are detected and permanently banned.\n• Recruits must be active adventurers.\n• Bonus points are credited immediately when your recruit enters your referral code during sign-up.",
                    fontSize = 11.sp,
                    color = MagicTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }

    if (copyAlert != null) {
        AlertDialog(
            onDismissRequest = { copyAlert = null },
            title = { Text("Guild Herald", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = { Text(copyAlert ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { copyAlert = null }) {
                    Text("OK", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}
