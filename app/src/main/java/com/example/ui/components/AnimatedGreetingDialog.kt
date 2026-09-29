package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.*

@Composable
fun AnimatedGreetingDialog(
    userName: String,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(24.dp, RoundedCornerShape(24.dp), ambientColor = MagicCyan, spotColor = MagicPurple)
                .border(
                    2.dp,
                    Brush.sweepGradient(listOf(MagicCyan, MagicPurple, MagicGold, MagicCyan)),
                    RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MagicDeepNavy)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            listOf(MagicCardBg, MagicDeepNavy),
                            radius = 600f
                        )
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated Glowing Emblem
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .scale(pulse),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer spinning star aura
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(MagicCyan.copy(alpha = 0.4f), MagicPurple.copy(alpha = 0.5f), Color.Transparent)
                                )
                            )
                    )
                    // Logo Image
                    Image(
                        painter = painterResource(id = R.drawable.magical_world_logo),
                        contentDescription = "Magical World Logo",
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .border(2.dp, MagicGold, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MagicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ROYAL SUMMONS",
                        color = MagicGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MagicGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome, Adventurer\n$userName! ✨",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MagicTextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "The realm gates have opened. Watch enchanted visions, conquer quests, and claim gold bounties!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MagicTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                MagicalButton(
                    text = "Enter the Realm",
                    onClick = onDismiss,
                    icon = Icons.Default.Check,
                    modifier = Modifier.fillMaxWidth(),
                    gradient = Brush.horizontalGradient(listOf(MagicGoldDark, MagicGold))
                )
            }
        }
    }
}
