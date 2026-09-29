package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun TasksScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val tasks by repository.tasks.collectAsState()
    val submissions by repository.submissions.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var selectedTaskForSubmission by remember { mutableStateOf<TaskItem?>(null) }
    var proofTextInput by remember { mutableStateOf("") }
    var feedbackDialogMessage by remember { mutableStateOf<String?>(null) }
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0 = Available, 1 = Submissions

    val user = currentUser ?: return
    val userSubmissions = submissions.filter { it.userId == user.id }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            FantasySectionHeader(
                title = "Kingdom Quest Board",
                subtitle = "Complete heroic missions to unlock gold and points"
            )
        }

        // Tab Filter
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MagicCardBg)
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedFilterTab == 0) MagicCyan else Color.Transparent)
                        .clickable { selectedFilterTab = 0 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Available Quests (${tasks.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedFilterTab == 0) MagicDeepNavy else MagicTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selectedFilterTab == 1) MagicPurple else Color.Transparent)
                        .clickable { selectedFilterTab = 1 }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "My Submissions (${userSubmissions.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (selectedFilterTab == 1) Color.White else MagicTextSecondary
                    )
                }
            }
        }

        if (selectedFilterTab == 0) {
            items(tasks) { task ->
                val alreadySubmitted = userSubmissions.any { it.taskId == task.id }

                MagicalCard(
                    borderGlow = if (alreadySubmitted) MagicEmerald.copy(alpha = 0.4f) else MagicPurple.copy(alpha = 0.3f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(MagicPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (task.type) {
                                        TaskType.VISIT_WEBSITE -> Icons.Default.Language
                                        TaskType.WATCH_CONTENT -> Icons.Default.SmartDisplay
                                        TaskType.APP_DOWNLOAD -> Icons.Default.Download
                                        TaskType.DAILY_QUEST -> Icons.Default.CalendarToday
                                        else -> Icons.Default.Task
                                    },
                                    contentDescription = null,
                                    tint = MagicCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MagicTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.description,
                                    fontSize = 12.sp,
                                    color = MagicTextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MagicSurface)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(task.type.name.replace("_", " "), fontSize = 10.sp, color = MagicCyan)
                                    }
                                    if (task.targetUrl.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("URL Quest", fontSize = 10.sp, color = MagicTextMuted)
                                    }
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            PointsBadge(points = task.pointsReward, prefix = "+")
                            Spacer(modifier = Modifier.height(10.dp))
                            if (alreadySubmitted) {
                                StatusPill(status = "Submitted", isSuccess = true)
                            } else {
                                Button(
                                    onClick = {
                                        selectedTaskForSubmission = task
                                        proofTextInput = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("task_start_${task.id}")
                                ) {
                                    Text("Claim", color = MagicDeepNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (userSubmissions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No quest submissions yet. Pick an available quest above!", color = MagicTextSecondary, fontSize = 13.sp)
                    }
                }
            } else {
                items(userSubmissions) { sub ->
                    MagicalCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sub.taskTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MagicTextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Proof: ${sub.proofText}", fontSize = 12.sp, color = MagicTextSecondary)
                                if (sub.adminNote.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Sentinel Note: ${sub.adminNote}", fontSize = 11.sp, color = MagicGold)
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                StatusPill(
                                    status = sub.status.name,
                                    isSuccess = sub.status == TaskStatus.APPROVED,
                                    isWarning = sub.status == TaskStatus.PENDING,
                                    isDanger = sub.status == TaskStatus.REJECTED
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("+${sub.rewardPoints} Pts", color = MagicGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Task Proof Submission Dialog
    if (selectedTaskForSubmission != null) {
        val task = selectedTaskForSubmission!!
        AlertDialog(
            onDismissRequest = { selectedTaskForSubmission = null },
            title = {
                Text(
                    text = "Submit Quest: ${task.title}",
                    fontWeight = FontWeight.Bold,
                    color = MagicCyan
                )
            },
            text = {
                Column {
                    Text(
                        text = task.description,
                        fontSize = 12.sp,
                        color = MagicTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Instructions: ${task.verificationInstructions}",
                        fontSize = 12.sp,
                        color = MagicGold
                    )
                    if (task.targetUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Target Link: ${task.targetUrl}",
                            fontSize = 11.sp,
                            color = MagicCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = proofTextInput,
                        onValueChange = { proofTextInput = it },
                        label = { Text("Proof / Verification ID / Username") },
                        placeholder = { Text("e.g. Joined channel with @handle") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_proof_input"),
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
                        val proof = if (proofTextInput.isBlank()) "Task completed by ${user.username}" else proofTextInput
                        val res = repository.submitTaskProof(task.id, proof)
                        selectedTaskForSubmission = null
                        feedbackDialogMessage = if (res.isSuccess) res.getOrThrow() else res.exceptionOrNull()?.message
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                    modifier = Modifier.testTag("submit_task_proof_btn")
                ) {
                    Text("Submit for Review", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedTaskForSubmission = null }) {
                    Text("Cancel", color = MagicTextSecondary)
                }
            },
            containerColor = MagicCardBg
        )
    }

    if (feedbackDialogMessage != null) {
        AlertDialog(
            onDismissRequest = { feedbackDialogMessage = null },
            title = { Text("Quest Update 📜", color = MagicCyan, fontWeight = FontWeight.Bold) },
            text = { Text(feedbackDialogMessage ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { feedbackDialogMessage = null }) {
                    Text("Awesome", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}
