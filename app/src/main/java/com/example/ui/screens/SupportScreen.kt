package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.model.TicketStatus
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun SupportScreen(
    repository: AppRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val supportTickets by repository.supportTickets.collectAsState()

    var showCreateTicketDialog by remember { mutableStateOf(false) }
    var ticketSubject by remember { mutableStateOf("") }
    var ticketCategory by remember { mutableStateOf("Withdrawal") }
    var ticketMessage by remember { mutableStateOf("") }
    var alertNotice by remember { mutableStateOf<String?>(null) }

    val user = currentUser ?: return
    val userTickets = supportTickets.filter { it.userId == user.id }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                    title = "Royal Guild Help Center",
                    subtitle = "Direct communication with high sentinels and administrators"
                )
            }
        }

        // New Ticket Button
        item {
            MagicalButton(
                text = "Open New Support Ticket",
                onClick = { showCreateTicketDialog = true },
                icon = Icons.Default.AddComment,
                modifier = Modifier.fillMaxWidth(),
                testTag = "open_new_ticket_btn"
            )
        }

        item {
            Text("Your Inquiries (${userTickets.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
        }

        if (userTickets.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No open support tickets. Everything is peaceful in the realm!", color = MagicTextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            items(userTickets) { ticket ->
                MagicalCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(ticket.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Category: ${ticket.category}", fontSize = 11.sp, color = MagicCyan)
                            Spacer(modifier = Modifier.height(6.dp))
                            ticket.messages.forEach { msg ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (msg.isAdmin) MagicPurple.copy(alpha = 0.2f) else MagicSurface)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(
                                            if (msg.isAdmin) "🛡️ Support Sentinel:" else "Adventurer:",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (msg.isAdmin) MagicPurple else MagicCyan
                                        )
                                        Text(msg.message, fontSize = 12.sp, color = MagicTextPrimary)
                                    }
                                }
                            }
                        }

                        StatusPill(
                            status = ticket.status.name,
                            isSuccess = ticket.status == TicketStatus.RESOLVED || ticket.status == TicketStatus.CLOSED,
                            isWarning = ticket.status == TicketStatus.OPEN || ticket.status == TicketStatus.IN_PROGRESS
                        )
                    }
                }
            }
        }
    }

    if (showCreateTicketDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTicketDialog = false },
            title = { Text("Summon Support Sentinel", fontWeight = FontWeight.Bold, color = MagicCyan) },
            text = {
                Column {
                    OutlinedTextField(
                        value = ticketSubject,
                        onValueChange = { ticketSubject = it },
                        label = { Text("Subject") },
                        placeholder = { Text("e.g. bKash payment verification") },
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
                        value = ticketMessage,
                        onValueChange = { ticketMessage = it },
                        label = { Text("Describe your issue") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
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
                        if (ticketSubject.isNotBlank() && ticketMessage.isNotBlank()) {
                            val res = repository.submitSupportTicket(ticketSubject, ticketCategory, ticketMessage)
                            showCreateTicketDialog = false
                            alertNotice = if (res.isSuccess) res.getOrThrow() else res.exceptionOrNull()?.message
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) {
                    Text("Submit Ticket", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTicketDialog = false }) {
                    Text("Cancel", color = MagicTextSecondary)
                }
            },
            containerColor = MagicCardBg
        )
    }

    if (alertNotice != null) {
        AlertDialog(
            onDismissRequest = { alertNotice = null },
            title = { Text("Support Dispatch", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = { Text(alertNotice ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { alertNotice = null }) {
                    Text("OK", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}
