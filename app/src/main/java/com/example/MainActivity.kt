package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.AppRepository
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MagicCrimson
import com.example.ui.theme.MagicCyan
import com.example.ui.theme.MagicDeepNavy
import com.example.ui.theme.MagicTextPrimary
import com.example.ui.theme.MyApplicationTheme

enum class AppPortal {
    USER_PORTAL,
    ADMIN_LOGIN,
    ADMIN_PORTAL
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = AppRepository(applicationContext)

        setContent {
            MyApplicationTheme {
                MainContent(repository)
            }
        }
    }
}

@Composable
fun MainContent(repository: AppRepository) {
    val currentUser by repository.currentUser.collectAsState()
    val currentAdmin by repository.currentAdmin.collectAsState()
    val customization by repository.customization.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val maintenanceConfig by repository.maintenanceConfig.collectAsState()
    val appVersionInfo by repository.appVersionInfo.collectAsState()

    var activePortal by remember { mutableStateOf(AppPortal.USER_PORTAL) }
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var showGreetingDialog by remember { mutableStateOf(true) }
    var showUpdateDialog by remember { mutableStateOf(true) }
    var accessDeniedAlert by remember { mutableStateOf<String?>(null) }

    // Guard: Prevent normal users from ever entering admin portal
    fun navigateToAdminPortal() {
        if (currentAdmin != null) {
            activePortal = AppPortal.ADMIN_PORTAL
        } else {
            activePortal = AppPortal.ADMIN_LOGIN
        }
    }

    // Portal Switching Router
    when (activePortal) {
        AppPortal.ADMIN_LOGIN -> {
            BackHandler {
                activePortal = AppPortal.USER_PORTAL
            }
            AdminLoginScreen(
                repository = repository,
                onAdminAuthSuccess = {
                    activePortal = AppPortal.ADMIN_PORTAL
                },
                onReturnToUserPortal = {
                    activePortal = AppPortal.USER_PORTAL
                }
            )
        }

        AppPortal.ADMIN_PORTAL -> {
            // Strict Protected Route Check: Admin session must be present
            if (currentAdmin == null) {
                activePortal = AppPortal.USER_PORTAL
                currentScreen = AppScreen.HOME
                accessDeniedAlert = "Access denied: Administrator privileges required."
            } else {
                BackHandler {
                    repository.adminLogout()
                    activePortal = AppPortal.USER_PORTAL
                }
                AdminScreen(
                    repository = repository,
                    onExitAdmin = {
                        activePortal = AppPortal.USER_PORTAL
                    }
                )
            }
        }

        AppPortal.USER_PORTAL -> {
            // Check Remote Maintenance Mode
            if (maintenanceConfig.isMaintenanceMode && currentAdmin == null) {
                MaintenanceScreen(
                    config = maintenanceConfig,
                    onAdminLoginClick = {
                        activePortal = AppPortal.ADMIN_LOGIN
                    },
                    onRefreshClick = {
                        repository.adminRefreshRemoteConfig()
                    }
                )
            } else {
                // USER PANEL (Standard Adventurer Experience)
                val user = currentUser

                if (user == null) {
                    AuthScreen(
                        repository = repository,
                        onAuthSuccess = {
                            currentScreen = AppScreen.HOME
                            showGreetingDialog = true
                        },
                        onNavigateToAdminLogin = {
                            activePortal = AppPortal.ADMIN_LOGIN
                        }
                    )
                } else {
                    // BackHandler for User sub-screens
                    if (currentScreen != AppScreen.HOME) {
                        BackHandler {
                            currentScreen = AppScreen.HOME
                        }
                    }

                    val unreadCount = notifications.count { !it.isRead }

                    Scaffold(
                        topBar = {
                            if (currentScreen != AppScreen.SUPPORT) {
                                MagicalTopAppBar(
                                    appName = customization.appName,
                                    currentUser = user,
                                    unreadNotificationsCount = unreadCount,
                                    currentScreen = currentScreen,
                                    onNavigate = { currentScreen = it }
                                )
                            }
                        },
                        bottomBar = {
                            if (currentScreen != AppScreen.SUPPORT) {
                                MagicalBottomNavigation(
                                    currentScreen = currentScreen,
                                    onNavigate = { currentScreen = it }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MagicDeepNavy)
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(MagicDeepNavy)
                        ) {
                            when (currentScreen) {
                                AppScreen.HOME -> HomeScreen(repository = repository, onNavigate = { currentScreen = it })
                                AppScreen.EARN -> EarnScreen(repository = repository)
                                AppScreen.TASKS -> TasksScreen(repository = repository)
                                AppScreen.GAMES -> GamesScreen(repository = repository)
                                AppScreen.REFERRAL -> ReferralScreen(repository = repository)
                                AppScreen.WALLET -> WalletScreen(repository = repository)
                                AppScreen.LEADERBOARD -> LeaderboardScreen(
                                    repository = repository,
                                    onBack = { currentScreen = AppScreen.HOME }
                                )
                                AppScreen.ACHIEVEMENTS -> AchievementsScreen(
                                    repository = repository,
                                    onBack = { currentScreen = AppScreen.HOME }
                                )
                                AppScreen.PROFILE -> ProfileScreen(
                                    repository = repository,
                                    onLogout = { repository.logout() },
                                    onOpenSupport = { currentScreen = AppScreen.SUPPORT }
                                )
                                AppScreen.NOTIFICATIONS -> NotificationsScreen(repository = repository)
                                AppScreen.SUPPORT -> SupportScreen(
                                    repository = repository,
                                    onBack = { currentScreen = AppScreen.PROFILE }
                                )
                                else -> HomeScreen(repository = repository, onNavigate = { currentScreen = it })
                            }
                        }
                    }

                    // Welcome Greeting on User Login
                    if (showGreetingDialog) {
                        AnimatedGreetingDialog(
                            userName = user.fullName.ifBlank { user.username },
                            onDismiss = { showGreetingDialog = false }
                        )
                    }
                }
            }
        }
    }

    // Remote APK Update Checker Dialog
    if (showUpdateDialog && appVersionInfo.latestVersionCode > appVersionInfo.currentVersionCode) {
        UpdateCheckerDialog(
            versionInfo = appVersionInfo,
            onDismiss = { showUpdateDialog = false }
        )
    }

    // Access Denied Security Dialog
    if (accessDeniedAlert != null) {
        AlertDialog(
            onDismissRequest = { accessDeniedAlert = null },
            title = {
                Text(
                    text = "Security Sentinel: Access Denied",
                    color = MagicCrimson,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = accessDeniedAlert ?: "Administrator access is strictly restricted.",
                    color = MagicTextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = { accessDeniedAlert = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MagicCyan)
                ) {
                    Text("Return to Realm", color = MagicDeepNavy, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF140F22)
        )
    }
}
