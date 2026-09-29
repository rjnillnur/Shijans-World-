package com.example.ui.screens

import androidx.compose.animation.core.*
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
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.AppRepository
import com.example.model.GameItem
import com.example.model.GameType
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GamesScreen(
    repository: AppRepository,
    modifier: Modifier = Modifier
) {
    val games by repository.games.collectAsState()
    val currentUser by repository.currentUser.collectAsState()
    var activeGamePlaying by remember { mutableStateOf<GameItem?>(null) }
    var gameCompletedAlert by remember { mutableStateOf<String?>(null) }

    val user = currentUser ?: return

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
                title = "Arcane Game Arenas",
                subtitle = "Test your reflexes, skill, and fortune for instant points"
            )
        }

        items(games) { game ->
            val playsRemaining = (game.dailyLimit - game.playsToday).coerceAtLeast(0)

            MagicalCard(
                borderGlow = if (game.isEnabled && playsRemaining > 0) MagicCyan else MagicTextMuted.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(listOf(MagicPurple.copy(alpha = 0.4f), MagicCardBg))
                                )
                                .border(1.dp, MagicCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (game.type) {
                                    GameType.CRYSTAL_CAR_RUSH -> Icons.Default.DirectionsCar
                                    GameType.MAGIC_GEM_TAPPER -> Icons.Default.TouchApp
                                    GameType.FORTUNE_SPIN -> Icons.Default.Casino
                                    GameType.CASTLE_RUNNER -> Icons.Default.DirectionsRun
                                    else -> Icons.Default.SportsEsports
                                },
                                contentDescription = null,
                                tint = MagicCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = game.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MagicTextPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = game.description,
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
                                    Text("Diff: ${game.difficulty}", fontSize = 10.sp, color = MagicGold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$playsRemaining/${game.dailyLimit} Plays left today",
                                    fontSize = 11.sp,
                                    color = if (playsRemaining > 0) MagicCyan else MagicCrimson
                                )
                            }
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        PointsBadge(points = game.rewardPoints, prefix = "+")
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { activeGamePlaying = game },
                            enabled = game.isEnabled && playsRemaining > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = MagicCyan),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("play_game_${game.id}")
                        ) {
                            Text(
                                text = if (playsRemaining > 0) "Play" else "Maxed",
                                color = MagicDeepNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Mini-Game Player Dialog
    if (activeGamePlaying != null) {
        val game = activeGamePlaying!!
        when (game.type) {
            GameType.CRYSTAL_CAR_RUSH -> {
                CrystalCarRushDialog(
                    game = game,
                    onFinish = { score ->
                        val res = repository.completeGameSession(game.id, score)
                        activeGamePlaying = null
                        gameCompletedAlert = if (res.isSuccess) {
                            "Victory! Scored $score in Crystal Car Rush. Awarded +${res.getOrThrow()} Points! 🏎️💎"
                        } else {
                            res.exceptionOrNull()?.message
                        }
                    },
                    onDismiss = { activeGamePlaying = null }
                )
            }
            GameType.MAGIC_GEM_TAPPER -> {
                MagicGemTapperDialog(
                    game = game,
                    onFinish = { score ->
                        val res = repository.completeGameSession(game.id, score)
                        activeGamePlaying = null
                        gameCompletedAlert = if (res.isSuccess) {
                            "Glorious! Tapped $score magical gems! Awarded +${res.getOrThrow()} Points! ✨"
                        } else {
                            res.exceptionOrNull()?.message
                        }
                    },
                    onDismiss = { activeGamePlaying = null }
                )
            }
            GameType.FORTUNE_SPIN -> {
                FortuneSpinWheelDialog(
                    game = game,
                    onFinish = { score ->
                        val res = repository.completeGameSession(game.id, score)
                        activeGamePlaying = null
                        gameCompletedAlert = if (res.isSuccess) {
                            "Fortune favored you! Wheel landed on $score! Received +${res.getOrThrow()} Points! 🎡"
                        } else {
                            res.exceptionOrNull()?.message
                        }
                    },
                    onDismiss = { activeGamePlaying = null }
                )
            }
            else -> {
                CastleRunnerDialog(
                    game = game,
                    onFinish = { score ->
                        val res = repository.completeGameSession(game.id, score)
                        activeGamePlaying = null
                        gameCompletedAlert = if (res.isSuccess) {
                            "Escaped the dungeon! Scored $score. Received +${res.getOrThrow()} Points! 🏰"
                        } else {
                            res.exceptionOrNull()?.message
                        }
                    },
                    onDismiss = { activeGamePlaying = null }
                )
            }
        }
    }

    if (gameCompletedAlert != null) {
        AlertDialog(
            onDismissRequest = { gameCompletedAlert = null },
            title = { Text("Game Arena Result 🏆", color = MagicGold, fontWeight = FontWeight.Bold) },
            text = { Text(gameCompletedAlert ?: "", color = MagicTextPrimary) },
            confirmButton = {
                TextButton(onClick = { gameCompletedAlert = null }) {
                    Text("Collect", color = MagicCyan, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MagicCardBg
        )
    }
}

// -------------------------------------------------------------
// Interactive Mini-Game 1: Crystal Car Rush
// -------------------------------------------------------------
@Composable
fun CrystalCarRushDialog(
    game: GameItem,
    onFinish: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var playerLane by remember { mutableIntStateOf(1) } // 0=Left, 1=Center, 2=Right
    var score by remember { mutableIntStateOf(0) }
    var obstacleLane by remember { mutableIntStateOf(0) }
    var obstacleY by remember { mutableFloatStateOf(0f) }
    var isGameOver by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableIntStateOf(15) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0 && !isGameOver) {
            delay(1000L)
            timeLeft--
            score += 15
        }
        if (!isGameOver) {
            onFinish(score + 100)
        }
    }

    LaunchedEffect(Unit) {
        while (!isGameOver && timeLeft > 0) {
            delay(50L)
            obstacleY += 0.08f
            if (obstacleY >= 0.85f && obstacleY <= 1.0f && obstacleLane == playerLane) {
                // Crash
                isGameOver = true
                break
            }
            if (obstacleY > 1.1f) {
                obstacleY = 0f
                obstacleLane = Random.nextInt(3)
                score += 20
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(520.dp)
                .border(2.dp, MagicCyan, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MagicDeepNavy),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏎️ Crystal Car Rush", fontWeight = FontWeight.Bold, color = MagicCyan, fontSize = 16.sp)
                    Text("Time: ${timeLeft}s | Score: $score", fontWeight = FontWeight.Bold, color = MagicGold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Road Area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B1028))
                        .border(1.dp, MagicCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                ) {
                    // 3 Lanes dividing lines
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color(0xFF1B2348)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color(0xFF1B2348)))
                        Box(modifier = Modifier.weight(1f).fillMaxHeight().border(1.dp, Color(0xFF1B2348)))
                    }

                    // Obstacle Crystal
                    val obstacleXAlign = when (obstacleLane) {
                        0 -> -0.66f
                        1 -> 0f
                        else -> 0.66f
                    }
                    Box(
                        modifier = Modifier
                            .align(BiasAlignment(obstacleXAlign, (obstacleY * 2f) - 1f))
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MagicCrimson),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Dangerous, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }

                    // Player Car
                    val playerXAlign = when (playerLane) {
                        0 -> -0.66f
                        1 -> 0f
                        else -> 0.66f
                    }
                    Box(
                        modifier = Modifier
                            .align(BiasAlignment(playerXAlign, 0.85f))
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Brush.verticalGradient(listOf(MagicCyan, MagicPurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    if (isGameOver) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MagicDeepNavy.copy(alpha = 0.85f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("CRASHED!", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MagicCrimson)
                                Text("Final Score: $score", fontSize = 15.sp, color = MagicTextPrimary)
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { onFinish(score.coerceAtLeast(30)) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                                ) {
                                    Text("Claim Points", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Steering Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { if (playerLane > 0) playerLane-- },
                        colors = ButtonDefaults.buttonColors(containerColor = MagicCardBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(60.dp, 48.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Left", tint = MagicCyan)
                    }

                    Button(
                        onClick = { if (playerLane < 2) playerLane++ },
                        colors = ButtonDefaults.buttonColors(containerColor = MagicCardBg),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(60.dp, 48.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Right", tint = MagicCyan)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Interactive Mini-Game 2: Magic Gem Tapper
// -------------------------------------------------------------
@Composable
fun MagicGemTapperDialog(
    game: GameItem,
    onFinish: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var activeTile by remember { mutableIntStateOf(Random.nextInt(9)) }
    var timeLeft by remember { mutableIntStateOf(10) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        onFinish(score)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(2.dp, MagicPurple, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MagicDeepNavy)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("✨ Gem Tapper", fontWeight = FontWeight.Bold, color = MagicPurple, fontSize = 16.sp)
                    Text("Time: ${timeLeft}s | Score: $score", fontWeight = FontWeight.Bold, color = MagicGold)
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text("Tap glowing crystal gems quickly!", fontSize = 12.sp, color = MagicTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))

                // 3x3 Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (row in 0 until 3) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                val isActive = index == activeTile

                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isActive) MagicCyan else MagicCardBg)
                                        .border(1.dp, if (isActive) MagicGold else MagicCardBorder, RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (isActive) {
                                                score += 10
                                                activeTile = Random.nextInt(9)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isActive) {
                                        Icon(
                                            Icons.Default.Diamond,
                                            contentDescription = "Gem",
                                            tint = MagicDeepNavy,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Interactive Mini-Game 3: Fortune Spin Wheel
// -------------------------------------------------------------
@Composable
fun FortuneSpinWheelDialog(
    game: GameItem,
    onFinish: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isSpinning by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var selectedPrize by remember { mutableStateOf<Int?>(null) }

    val prizes = listOf(20, 50, 100, 30, 200, 40)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(2.dp, MagicGold, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MagicDeepNavy)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🎡 Wheel of Fortune", fontWeight = FontWeight.Bold, color = MagicGold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Spin the celestial rune wheel for points!", fontSize = 12.sp, color = MagicTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(MagicCardBg)
                        .border(3.dp, MagicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotationAngle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Stars,
                            contentDescription = "Wheel",
                            tint = MagicCyan,
                            modifier = Modifier.size(130.dp)
                        )
                    }

                    // Indicator Needle
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Needle",
                        tint = MagicCrimson,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.TopCenter)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (selectedPrize != null) {
                    Text("Won: +$selectedPrize Points! 🎉", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MagicGold)
                    Spacer(modifier = Modifier.height(10.dp))
                    MagicalButton(
                        text = "Claim Prize",
                        onClick = { onFinish(selectedPrize!!) },
                        icon = Icons.Default.Check
                    )
                } else {
                    MagicalButton(
                        text = if (isSpinning) "Spinning..." else "SPIN NOW",
                        onClick = {
                            if (!isSpinning) {
                                isSpinning = true
                                coroutineScope.launch {
                                    val spins = Random.nextInt(5, 10) * 360f + Random.nextInt(360)
                                    val startAngle = rotationAngle
                                    val targetAngle = startAngle + spins

                                    val steps = 40
                                    for (i in 0..steps) {
                                        delay(40L)
                                        val fraction = i.toFloat() / steps.toFloat()
                                        rotationAngle = startAngle + (spins * fraction)
                                    }
                                    val prize = prizes.random()
                                    selectedPrize = prize
                                    isSpinning = false
                                }
                            }
                        },
                        enabled = !isSpinning,
                        icon = Icons.Default.RotateRight
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Interactive Mini-Game 4: Castle Runner
// -------------------------------------------------------------
@Composable
fun CastleRunnerDialog(
    game: GameItem,
    onFinish: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var jumps by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(2.dp, MagicCyan, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = MagicDeepNavy)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🏰 Dragon Castle Escape", fontWeight = FontWeight.Bold, color = MagicCyan, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Jump over castle fire traps! (Jumps: $jumps/10)", fontSize = 12.sp, color = MagicTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F1530)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = MagicCyan, modifier = Modifier.size(42.dp))
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = MagicCrimson, modifier = Modifier.size(36.dp))
                        Icon(Icons.Default.Castle, contentDescription = null, tint = MagicGold, modifier = Modifier.size(42.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                MagicalButton(
                    text = "JUMP! (+15 Pts)",
                    onClick = {
                        jumps++
                        score += 15
                        if (jumps >= 10) {
                            onFinish(score)
                        }
                    },
                    icon = Icons.Default.Upgrade
                )
            }
        }
    }
}
