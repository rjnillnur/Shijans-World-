package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val withdrawals by repository.withdrawals.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val systemSettings by repository.systemSettings.collectAsState()
    val withdrawalRemoteSettings by repository.withdrawalRemoteSettings.collectAsState()

    var showWithdrawDialog by remember { mutableStateOf(false) }
    var selectedMethod by remember { mutableStateOf("bKash") }
    var accountNumberInput by remember { mutableStateOf("") }
    var pointsWithdrawInput by remember { mutableStateOf("") }
    var withdrawalFeedback by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Transactions, 1 = Withdrawals

    val user = currentUser ?: return
    val userWithdrawals = withdrawals.filter { it.userId == user.id }
    val userTransactions = transactions.filter { it.userId == user.id }

    val currencyEquivalent = String.format(
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
        item {
            FantasySectionHeader(
                title = "Treasure Vault & Wallet",
                subtitle = "Convert your magical points into real worldly wealth"
            )
        }

        // Wallet Balance Card
        item {
            MagicalCard(
                borderGlow = MagicGold
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Available Treasury Balance", fontSize = 12.sp, color = MagicTextSecondary)
                        StatusPill(status = "ACTIVE", isSuccess = true)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${systemSettings.currencySymbol}$currencyEquivalent",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MagicEmerald
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "(${user.points} Points)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MagicGold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Rate: ${systemSettings.pointsPerCurrencyUnit} Points = ${systemSettings.currencySymbol}1.00 • Min: ${systemSettings.currencySymbol}${systemSettings.minWithdrawalAmount}",
                        fontSize = 11.sp,
                        color = MagicTextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MagicalButton(
                        text = "Request Withdrawal",
                        onClick = {
                            pointsWithdrawInput = (systemSettings.minWithdrawalAmount * systemSettings.pointsPerCurrencyUnit).toLong().toString()
                            showWithdrawDialog = true
                        },
                        icon = Icons.Default.Payments,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "wallet_request_withdrawal_btn"
                    )
                }
            }
        }

        // Vault Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MagicCardBg)
                        .border(1.dp, MagicCardBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Earned", fontSize = 11.sp, color = MagicTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "+${user.totalEarnedPoints}",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MagicGold
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MagicCardBg)
                        .border(1.dp, MagicCardBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Payouts", fontSize = 11.sp, color = MagicTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        val paidAmount = userWithdrawals.filter { it.status == WithdrawalStatus.PAID }.sumOf { it.amountCurrency }
                        Text(
                            "${systemSettings.currencySymbol}${String.format(java.util.Locale.US, "%.1f", paidAmount)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MagicCyan
                        )
                    }
                }
            }
        }

        // Ledger Switcher Tab
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MagicCardBg)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedTab == 0) MagicCyan else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Transactions (${userTransactions.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedTab == 0) MagicDeepNavy else MagicTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selectedTab == 1) MagicPurple else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Withdrawal History (${userWithdrawals.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedTab == 1) Color.White else MagicTextSecondary
                    )
                }
            }
        }

        if (selectedTab == 0) {
            items(userTransactions) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MagicSurface)
                        .border(1.dp, MagicCardBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(tx.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MagicTextPrimary)
                        Text(tx.description, fontSize = 11.sp, color = MagicTextSecondary)
                        Text(
                            text = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.US).format(java.util.Date(tx.timestamp)),
                            fontSize = 10.sp,
                            color = MagicTextMuted
                        )
                    }

                    Text(
                        text = if (tx.amountPoints >= 0) "+${tx.amountPoints} Pts" else "${tx.amountPoints} Pts",
                        fontWeight = FontWeight.Black,
                        color = if (tx.amountPoints >= 0) MagicGold else MagicCrimson,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            if (userWithdrawals.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No withdrawal requests submitted yet.", color = MagicTextSecondary, fontSize = 12.sp)
                    }
                }
            } else {
                items(userWithdrawals) { wd ->
                    MagicalCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text(
                                    "${wd.paymentMethod}: ${wd.accountNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MagicTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Amount: ${wd.currencySymbol}${wd.amountCurrency} (${wd.pointsDeducted} Pts)",
                                    fontSize = 12.sp,
                                    color = MagicGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (wd.adminPaymentRef.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Ref ID: ${wd.adminPaymentRef}", fontSize = 11.sp, color = MagicCyan)
                                }
                                if (wd.adminNote.isNotBlank()) {
                                    Text("Note: ${wd.adminNote}", fontSize = 11.sp, color = MagicTextSecondary)
                                }
                            }

                            StatusPill(
                                status = wd.status.name,
                                isSuccess = wd.status == WithdrawalStatus.PAID || wd.status == WithdrawalStatus.APPROVED,
                                isWarning = wd.status == WithdrawalStatus.PENDING || wd.status == WithdrawalStatus.PROCESSING,
                                isDanger = wd.status == WithdrawalStatus.REJECTED
                            )
                        }
                    }
                }
            }
        }
    }

    // Withdrawal Request Modal Dialog
    if (showWithdrawDialog) {
        val calculatedPoints = pointsWithdrawInput.toLongOrNull() ?: 0L
        val calculatedMoney = calculatedPoints.toDouble() / systemSettings.pointsPerCurrencyUnit.toDouble()

        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            title = {
                Text(
                    text = "Request Worldly Payout 💰",
                    fontWeight = FontWeight.Bold,
                    color = MagicCyan
                )
            },
            text = {
                Column {
                    Text("Select Payment Channel (Remotely Configured):", fontSize = 12.sp, color = MagicTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    val gateways = withdrawalRemoteSettings.gateways.filter { it.isEnabled }
                    val currentGateway = gateways.find { it.name == selectedMethod || it.id == selectedMethod }
                        ?: gateways.firstOrNull()

                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(gateways) { g ->
                            val isSel = selectedMethod == g.name
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MagicCyan else MagicSurface)
                                    .border(1.dp, if (isSel) MagicCyan else MagicCardBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedMethod = g.name }
                                    .padding(horizontal = 10.dp, vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = g.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) MagicDeepNavy else MagicTextPrimary
                                    )
                                    if (g.badge.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isSel) MagicDeepNavy.copy(alpha = 0.2f) else MagicGold.copy(alpha = 0.2f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = g.badge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (isSel) MagicDeepNavy else MagicGold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (currentGateway != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Min: ${withdrawalRemoteSettings.currencySymbol}${currentGateway.minWithdrawal.toInt()} • Max: ${withdrawalRemoteSettings.currencySymbol}${currentGateway.maxWithdrawal.toInt()}",
                                fontSize = 10.sp,
                                color = MagicGold
                            )
                            if (currentGateway.feePercent > 0.0) {
                                Text(
                                    text = "Fee: ${currentGateway.feePercent}%",
                                    fontSize = 10.sp,
                                    color = MagicCrimson
                                )
                            } else {
                                Text(
                                    text = "Zero Fee",
                                    fontSize = 10.sp,
                                    color = MagicEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = accountNumberInput,
                        onValueChange = { accountNumberInput = it },
                        label = { Text("${currentGateway?.name ?: selectedMethod} Account / ID") },
                        placeholder = { Text(currentGateway?.accountPlaceholder ?: "Account Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_account_number_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pointsWithdrawInput,
                        onValueChange = { pointsWithdrawInput = it },
                        label = { Text("Points to Withdraw") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_points_amount_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicGold,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val feeAmount = calculatedMoney * ((currentGateway?.feePercent ?: 0.0) / 100.0)
                    val netPayout = (calculatedMoney - feeAmount).coerceAtLeast(0.0)
                    Text(
                        text = "Net Payout: ${withdrawalRemoteSettings.currencySymbol}${String.format(java.util.Locale.US, "%.2f", netPayout)}" +
                               (if (currentGateway?.currencyCode != "BDT" && currentGateway?.currencyCode?.isNotBlank() == true) " (${currentGateway.currencyCode})" else ""),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MagicEmerald
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pts = pointsWithdrawInput.toLongOrNull() ?: 0L
                        val res = repository.requestWithdrawal(selectedMethod, accountNumberInput, pts)
                        if (res.isSuccess) {
                            showWithdrawDialog = false
                            withdrawalFeedback = "Withdrawal request submitted! Sentinels will review and dispatch payment."
                        } else {
                            withdrawalFeedback = res.exceptionOrNull()?.message
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                    modifier = Modifier.testTag("submit_withdrawal_button")
                ) {
                    Text("Confirm", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = MagicTextSecondary)
                }
            },
            containerColor = MagicCardBg
        )
    }

    if (withdrawalFeedback != null) {
        AlertDialog(
            onDismissRequest = { withdrawalFeedback = null },
            title = { Text("Treasury Notification", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = { Text(withdrawalFeedback ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { withdrawalFeedback = null }) {
                    Text("Understood", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}
