package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.model.AdminAccount
import com.example.ui.components.MagicalButton
import com.example.ui.components.MagicalCard
import com.example.ui.theme.*

@Composable
fun AdminLoginScreen(
    repository: AppRepository,
    onAdminAuthSuccess: (AdminAccount) -> Unit,
    onReturnToUserPortal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var adminEmailInput by remember { mutableStateOf("") }
    var adminPasswordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070A14))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Return to User Realm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                OutlinedButton(
                    onClick = onReturnToUserPortal,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MagicCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MagicCyan.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Return to User Realm", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Shield Logo
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF140B22))
                    .border(2.dp, Brush.sweepGradient(listOf(MagicCrimson, MagicPurple, MagicGold)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Admin Shield",
                    tint = MagicCrimson,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MAGICAL WORLD",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MagicTextPrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = "RESTRICTED ADMINISTRATION CONTROL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MagicCrimson,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            MagicalCard(
                borderGlow = MagicCrimson.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "Sign in with an authorized administrative account to access the mission control chamber.",
                    fontSize = 12.sp,
                    color = MagicTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MagicCrimson.copy(alpha = 0.15f))
                            .border(1.dp, MagicCrimson, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MagicCrimson, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(errorMessage!!, color = MagicCrimson, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedTextField(
                    value = adminEmailInput,
                    onValueChange = { adminEmailInput = it },
                    label = { Text("Admin Email / Identity") },
                    leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MagicCrimson) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_login_email_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MagicCrimson,
                        unfocusedBorderColor = MagicCardBorder,
                        focusedTextColor = MagicTextPrimary,
                        unfocusedTextColor = MagicTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = adminPasswordInput,
                    onValueChange = { adminPasswordInput = it },
                    label = { Text("Admin Passkey") },
                    leadingIcon = { Icon(Icons.Default.VpnKey, contentDescription = null, tint = MagicPurple) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = MagicTextSecondary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_login_password_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MagicCrimson,
                        unfocusedBorderColor = MagicCardBorder,
                        focusedTextColor = MagicTextPrimary,
                        unfocusedTextColor = MagicTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                MagicalButton(
                    text = "Authenticate Admin Portal",
                    onClick = {
                        val res = repository.adminLogin(adminEmailInput, adminPasswordInput)
                        if (res.isSuccess) {
                            onAdminAuthSuccess(res.getOrThrow())
                        } else {
                            errorMessage = res.exceptionOrNull()?.message
                        }
                    },
                    icon = Icons.Default.LockOpen,
                    modifier = Modifier.fillMaxWidth(),
                    gradient = Brush.horizontalGradient(listOf(MagicCrimson, Color(0xFF7C3AED))),
                    testTag = "admin_login_submit_btn"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fast test credentials buttons
                Text(
                    text = "Quick Demo Admin Credentials:",
                    fontSize = 11.sp,
                    color = MagicTextMuted,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val res = repository.adminLogin("mdbachuali687@gmail.com", "superadmin")
                            if (res.isSuccess) onAdminAuthSuccess(res.getOrThrow())
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MagicGold)
                    ) {
                        Text("Super Admin", fontSize = 11.sp, color = MagicGold, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            val res = repository.adminLogin("finance@magicalworld.game", "finance123")
                            if (res.isSuccess) onAdminAuthSuccess(res.getOrThrow())
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MagicCyan)
                    ) {
                        Text("Finance Admin", fontSize = 11.sp, color = MagicCyan, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security Disclaimer
            Text(
                text = "⚠️ Notice: All administrative sessions are cryptographically signed and logged with timestamp and network telemetry.",
                fontSize = 10.sp,
                color = MagicTextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
        }
    }
}
