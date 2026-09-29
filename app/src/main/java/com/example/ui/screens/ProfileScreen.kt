package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.model.AccountStatus
import com.example.model.UserRole
import com.example.model.VerificationStatus
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    repository: AppRepository,
    onLogout: () -> Unit,
    onOpenSupport: () -> Unit,
    onOpenAdmin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val systemSettings by repository.systemSettings.collectAsState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editFullName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }
    var verificationAlert by remember { mutableStateOf<String?>(null) }

    val user = currentUser ?: return

    val currencyAmount = String.format(
        java.util.Locale.US,
        "%.2f",
        user.points.toDouble() / systemSettings.pointsPerCurrencyUnit.toDouble()
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Profile Card
        item {
            MagicalCard(
                borderGlow = MagicCyan
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(MagicCardBg)
                            .border(2.dp, Brush.sweepGradient(listOf(MagicCyan, MagicPurple, MagicGold)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.magical_world_logo),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = user.fullName,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MagicTextPrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "@${user.username}",
                            fontSize = 13.sp,
                            color = MagicCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusPill(
                            status = user.role.name,
                            isSuccess = user.role == UserRole.SUPER_ADMIN || user.role == UserRole.ADMIN
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.bio,
                        fontSize = 12.sp,
                        color = MagicTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Verification Pill
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StatusPill(
                            status = "VERIFICATION: ${user.verificationStatus.name}",
                            isSuccess = user.verificationStatus == VerificationStatus.VERIFIED,
                            isWarning = user.verificationStatus == VerificationStatus.PENDING,
                            isDanger = user.verificationStatus == VerificationStatus.REJECTED || user.verificationStatus == VerificationStatus.SUSPENDED
                        )

                        if (user.verificationStatus == VerificationStatus.UNVERIFIED || user.verificationStatus == VerificationStatus.REJECTED) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    repository.requestVerification()
                                    verificationAlert = "Verification request submitted! Sentinels will review your profile."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("request_verification_btn")
                            ) {
                                Text("Verify", color = MagicDeepNavy, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Stats Matrix
        item {
            FantasySectionHeader(title = "Adventurer Statistics")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(title = "Crystal Points", value = "${user.points}", modifier = Modifier.weight(1f), color = MagicGold)
                    StatBox(title = "Wallet Balance", value = "${systemSettings.currencySymbol}$currencyAmount", modifier = Modifier.weight(1f), color = MagicEmerald)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(title = "Ads Watched", value = "${user.adsWatchedCount}", modifier = Modifier.weight(1f), color = MagicCyan)
                    StatBox(title = "Quests Solved", value = "${user.tasksCompletedCount}", modifier = Modifier.weight(1f), color = MagicPurple)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatBox(title = "Games Played", value = "${user.gamesPlayedCount}", modifier = Modifier.weight(1f), color = MagicCyan)
                    StatBox(title = "Referrals", value = "${user.totalReferralsCount}", modifier = Modifier.weight(1f), color = MagicGold)
                }
            }
        }

        // Account Details & Security
        item {
            MagicalCard {
                Text("Account & Identity Information", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                Spacer(modifier = Modifier.height(8.dp))

                InfoRow(label = "Email Address", value = user.email)
                InfoRow(label = "Phone Number", value = user.phoneNumber.ifBlank { "Not provided" })
                InfoRow(label = "Adventurer ID", value = user.id)
                InfoRow(label = "Referral Code", value = user.referralCode)
                InfoRow(label = "Account Status", value = user.accountStatus.name)
                InfoRow(
                    label = "Joined Realm",
                    value = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US).format(java.util.Date(user.joinDate))
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            editFullName = user.fullName
                            editPhone = user.phoneNumber
                            editBio = user.bio
                            showEditProfileDialog = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("edit_profile_btn"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MagicCyan)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = MagicCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Profile", color = MagicCyan, fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = onOpenSupport,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("profile_support_btn"),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MagicPurple)
                    ) {
                        Icon(Icons.Default.HelpCenter, contentDescription = null, tint = MagicPurple, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Support", color = MagicPurple, fontSize = 12.sp)
                    }
                }
            }
        }

        // Security & Device Center
        item {
            MagicalCard(borderGlow = MagicCyan.copy(alpha = 0.3f)) {
                Text("Device Security & Session", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))
                InfoRow(label = "Primary Device", value = user.deviceModel)
                InfoRow(label = "Network Node", value = user.ipAddress)
                InfoRow(label = "Security Protocol", value = "AES-256 Cloud Vault")
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "🔒 Your adventurer identity and wallet keys are protected by cryptographically signed Firebase sessions.",
                    fontSize = 11.sp,
                    color = MagicTextMuted,
                    lineHeight = 15.sp
                )
            }
        }

        // Logout Button
        item {
            MagicalButton(
                text = "Sign Out of Realm",
                onClick = onLogout,
                icon = Icons.Default.Logout,
                modifier = Modifier.fillMaxWidth(),
                gradient = Brush.horizontalGradient(listOf(MagicCrimson, Color(0xFF9F1239))),
                testTag = "profile_logout_btn"
            )
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Realm Profile", fontWeight = FontWeight.Bold, color = MagicCyan) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editFullName,
                        onValueChange = { editFullName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Adventurer Bio") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.updateProfile(editFullName, editPhone, editBio)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) {
                    Text("Save Changes", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = MagicTextSecondary)
                }
            },
            containerColor = MagicCardBg
        )
    }

    if (verificationAlert != null) {
        AlertDialog(
            onDismissRequest = { verificationAlert = null },
            title = { Text("Identity Sentinel", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = { Text(verificationAlert ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { verificationAlert = null }) {
                    Text("OK", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}

@Composable
private fun StatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MagicCardBg)
            .border(1.dp, MagicCardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MagicCardBg)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 11.sp, color = MagicTextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = MagicTextSecondary)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MagicTextPrimary)
    }
}
