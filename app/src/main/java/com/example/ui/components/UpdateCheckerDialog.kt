package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppVersionInfo
import com.example.ui.theme.*

/**
 * Dialog presented to user when an APK update is detected via Remote Configuration.
 * If forceUpdate is true, dismissal is blocked until the user updates.
 */
@Composable
fun UpdateCheckerDialog(
    versionInfo: AppVersionInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isForced = versionInfo.forceUpdate || versionInfo.currentVersionCode < versionInfo.minSupportedVersionCode

    Dialog(
        onDismissRequest = {
            if (!isForced) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isForced,
            dismissOnClickOutside = !isForced
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MagicSurface)
                .border(2.dp, Brush.horizontalGradient(listOf(MagicCyan, MagicPurple, MagicGold)), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(listOf(MagicCyan.copy(alpha = 0.2f), MagicPurple.copy(alpha = 0.2f)))
                        )
                        .border(1.dp, MagicCyan, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = "Update Icon",
                        tint = MagicCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = versionInfo.updateTitle,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = MagicTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Current: v${versionInfo.currentVersionName}",
                        fontSize = 12.sp,
                        color = MagicTextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("→", fontSize = 12.sp, color = MagicCyan, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New: v${versionInfo.latestVersionName}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MagicGold
                    )
                }

                if (isForced) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MagicCrimson.copy(alpha = 0.15f))
                            .border(1.dp, MagicCrimson.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mandatory Realm Update Required to Continue",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagicCrimson
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Release Notes Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F1530))
                        .border(1.dp, MagicCardBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.NewReleases,
                                contentDescription = null,
                                tint = MagicGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "What's New in This Release",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MagicGold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = versionInfo.releaseNotes.ifBlank { "• Bug fixes, performance improvements, and updated quest rewards." },
                            fontSize = 12.sp,
                            color = MagicTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.downloadUrl))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // fallback
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = MagicDeepNavy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download & Install Update",
                        color = MagicDeepNavy,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!isForced) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Remind Me Later", color = MagicTextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
