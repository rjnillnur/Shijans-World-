package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.MaintenanceConfig
import com.example.ui.theme.*

/**
 * Maintenance Screen shown when the Realm is under remote maintenance mode.
 * Blocks standard user navigation while allowing Admin portal access to authorized personnel.
 */
@Composable
fun MaintenanceScreen(
    config: MaintenanceConfig,
    onAdminLoginClick: () -> Unit,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Magical Logo
            Image(
                painter = painterResource(id = R.drawable.magical_world_logo),
                contentDescription = "Magical World Logo",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(2.dp, MagicGold, CircleShape)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Maintenance Rune Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MagicGold.copy(alpha = 0.15f))
                    .border(1.dp, MagicGold, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        tint = MagicGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REALM SANCTUARY UNDER TUNING",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MagicGold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = config.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MagicTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = config.message,
                fontSize = 14.sp,
                color = MagicTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ETA Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MagicSurface)
                    .border(1.dp, MagicCardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Estimated Downtime", fontSize = 12.sp, color = MagicTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = config.estimatedEndTime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MagicCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All user points and transaction records remain safe and encrypted in the royal database.",
                        fontSize = 11.sp,
                        color = MagicTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Refresh Status Button
            OutlinedButton(
                onClick = onRefreshClick,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MagicCyan),
                border = androidx.compose.foundation.BorderStroke(1.dp, MagicCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Check If Realm Reopened", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Admin Portal Access Button
            if (config.allowAdminBypass) {
                TextButton(
                    onClick = onAdminLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = MagicPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Administrator Bypass Portal",
                        color = MagicPurple,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Email, contentDescription = null, tint = MagicTextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Contact Sentinels: ${config.supportEmail}",
                    fontSize = 11.sp,
                    color = MagicTextSecondary
                )
            }
        }
    }
}
