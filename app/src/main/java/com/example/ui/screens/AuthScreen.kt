package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppRepository
import com.example.model.UserProfile
import com.example.ui.components.MagicalButton
import com.example.ui.components.MagicalCard
import com.example.ui.theme.*

@Composable
fun AuthScreen(
    repository: AppRepository,
    onAuthSuccess: (UserProfile) -> Unit,
    onNavigateToAdminLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isRegisterTab by remember { mutableStateOf(false) }

    // Form fields
    var emailOrUser by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var referralCode by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MagicDeepNavy)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo & Title
            Image(
                painter = painterResource(id = R.drawable.magical_world_logo),
                contentDescription = "Magical World Logo",
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, MagicCyan, CircleShape)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "MAGICAL WORLD",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
                color = MagicTextPrimary
            )
            Text(
                text = "Explore the Lands • Earn • Play • Complete Quests",
                style = MaterialTheme.typography.bodySmall,
                color = MagicCyan,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Auth Tab Switcher
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
                        .background(if (!isRegisterTab) MagicCyan else Color.Transparent)
                        .clickable {
                            isRegisterTab = false
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sign In",
                        fontWeight = FontWeight.Bold,
                        color = if (!isRegisterTab) MagicDeepNavy else MagicTextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isRegisterTab) MagicPurple else Color.Transparent)
                        .clickable {
                            isRegisterTab = true
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Register",
                        fontWeight = FontWeight.Bold,
                        color = if (isRegisterTab) Color.White else MagicTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MagicalCard {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MagicCrimson,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                if (successMessage != null) {
                    Text(
                        text = successMessage!!,
                        color = MagicEmerald,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (isRegisterTab) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MagicCyan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_fullname_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = MagicCyan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_username_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MagicCyan) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_phone_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = emailOrUser,
                        onValueChange = { emailOrUser = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MagicCyan) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_email_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = referralCode,
                        onValueChange = { referralCode = it },
                        label = { Text("Referral Code (Optional)") },
                        leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = MagicGold) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reg_referral_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicGold,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MagicCyan) },
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
                            .testTag("reg_password_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    MagicalButton(
                        text = "Create Adventurer Account (+100 Pts)",
                        onClick = {
                            val res = repository.register(fullName, username, emailOrUser, phoneNumber, referralCode)
                            if (res.isSuccess) {
                                onAuthSuccess(res.getOrThrow())
                            } else {
                                errorMessage = res.exceptionOrNull()?.message
                            }
                        },
                        icon = Icons.Default.HowToReg,
                        modifier = Modifier.fillMaxWidth(),
                        gradient = Brush.horizontalGradient(listOf(MagicPurple, MagicPink)),
                        testTag = "register_submit_button"
                    )
                } else {
                    OutlinedTextField(
                        value = emailOrUser,
                        onValueChange = { emailOrUser = it },
                        label = { Text("Email or Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MagicCyan) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MagicCyan) },
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
                            .testTag("login_password_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MagicCyan,
                            unfocusedBorderColor = MagicCardBorder,
                            focusedTextColor = MagicTextPrimary,
                            unfocusedTextColor = MagicTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    MagicalButton(
                        text = "Sign In to Realm",
                        onClick = {
                            val res = repository.login(emailOrUser, password)
                            if (res.isSuccess) {
                                onAuthSuccess(res.getOrThrow())
                            } else {
                                errorMessage = res.exceptionOrNull()?.message
                            }
                        },
                        icon = Icons.Default.Login,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "login_submit_button"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Demo / Quick Sign-In Buttons for quick testing
                    Text(
                        text = "Quick Demo Access:",
                        fontSize = 12.sp,
                        color = MagicTextMuted,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val res = repository.login("aria@magicalrealm.net", "123456")
                                if (res.isSuccess) onAuthSuccess(res.getOrThrow())
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MagicCyan)
                        ) {
                            Text("User: Aria", fontSize = 11.sp, color = MagicCyan)
                        }
                        OutlinedButton(
                            onClick = {
                                val res = repository.login("mdbachuali687@gmail.com", "admin123")
                                if (res.isSuccess) onAuthSuccess(res.getOrThrow())
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MagicPurple)
                        ) {
                            Text("Super Admin", fontSize = 11.sp, color = MagicPurple)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dedicated Administration Chamber Access
            OutlinedButton(
                onClick = onNavigateToAdminLogin,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MagicCrimson.copy(alpha = 0.6f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MagicCrimson),
                modifier = Modifier.fillMaxWidth().testTag("auth_admin_portal_button")
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = MagicCrimson, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Administrator Portal (/admin/login)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MagicCrimson)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
