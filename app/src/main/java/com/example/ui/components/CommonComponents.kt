package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun MagicalCard(
    modifier: Modifier = Modifier,
    borderGlow: Color = MagicCyan.copy(alpha = 0.4f),
    cornerRadius: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(cornerRadius), ambientColor = MagicPurple, spotColor = MagicCyan)
            .border(
                BorderStroke(1.dp, Brush.linearGradient(listOf(borderGlow, MagicPurple.copy(alpha = 0.3f), Color.Transparent))),
                shape = RoundedCornerShape(cornerRadius)
            ),
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = MagicGlassBg
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

@Composable
fun MagicalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    gradient: Brush = Brush.horizontalGradient(listOf(MagicCyan, MagicPurple)),
    testTag: String = "magical_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .shadow(if (enabled) 6.dp else 0.dp, RoundedCornerShape(14.dp), ambientColor = MagicCyan),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = MagicSurface.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (enabled) gradient else Brush.linearGradient(listOf(Color(0xFF242C4C), Color(0xFF1B213B))))
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) Color.White else MagicTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (enabled) Color.White else MagicTextMuted
                )
            }
        }
    }
}

@Composable
fun PointsBadge(
    points: Long,
    modifier: Modifier = Modifier,
    prefix: String = "+",
    fontSize: Int = 14
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MagicGold.copy(alpha = 0.18f))
            .border(1.dp, MagicGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.MonetizationOn,
            contentDescription = "Points",
            tint = MagicGold,
            modifier = Modifier.size((fontSize + 2).dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$prefix$points",
            color = MagicGold,
            fontWeight = FontWeight.ExtraBold,
            fontSize = fontSize.sp
        )
    }
}

@Composable
fun StatusPill(
    status: String,
    modifier: Modifier = Modifier,
    isSuccess: Boolean = false,
    isWarning: Boolean = false,
    isDanger: Boolean = false
) {
    val (bgColor, textColor) = when {
        isSuccess -> Pair(MagicEmerald.copy(alpha = 0.2f), MagicEmerald)
        isWarning -> Pair(MagicGold.copy(alpha = 0.2f), MagicGold)
        isDanger -> Pair(MagicCrimson.copy(alpha = 0.2f), MagicCrimson)
        else -> Pair(MagicCyan.copy(alpha = 0.2f), MagicCyan)
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun FantasySectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp, 20.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Brush.verticalGradient(listOf(MagicCyan, MagicPurple)))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MagicTextPrimary
                )
            }
            if (actionText != null && onActionClick != null) {
                Text(
                    text = actionText,
                    color = MagicCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onActionClick() }
                )
            }
        }
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MagicTextSecondary,
                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
            )
        }
    }
}
