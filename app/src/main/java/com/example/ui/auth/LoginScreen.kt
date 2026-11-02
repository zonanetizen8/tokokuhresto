package com.example.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.ui.components.OfflineSecurityBadge
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhBlack
import com.example.ui.theme.TokokuhBorder
import com.example.ui.theme.TokokuhMaroonDark
import com.example.ui.theme.TokokuhMaroonPrimary
import com.example.ui.theme.TokokuhTextMuted
import com.example.ui.theme.TokokuhWhite

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (UserEntity) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.loggedInUser) {
        uiState.loggedInUser?.let { user ->
            onLoginSuccess(user)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = TokokuhBackground
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Brand Header with deep maroon card & logo (ensures white & yellow text is clear)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = TokokuhMaroonPrimary),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_tokokuh_resto),
                            contentDescription = "TOKOKUH RESTO",
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                            modifier = Modifier
                                .height(52.dp)
                                .fillMaxWidth(0.9f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aplikasi Kasir & Resto Profesional 100% Offline",
                            fontSize = 13.sp,
                            color = TokokuhWhite.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OfflineSecurityBadge()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Account Selectors (Owner / Kasir / Dapur)
                if (allUsers.isNotEmpty()) {
                    Text(
                        text = "PILIH AKUN LOGIN CEPAT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhMaroonDark,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allUsers.take(3).forEach { user ->
                            val isSelected = uiState.usernameInput.equals(user.username, ignoreCase = true)
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.selectUserToLogin(user)
                                    }
                                    .testTag("user_chip_${user.username}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) TokokuhMaroonPrimary else TokokuhWhite
                                ),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = when (user.role) {
                                            UserRole.OWNER -> Icons.Default.Store
                                            UserRole.CASHIER -> Icons.Default.Person
                                            UserRole.KITCHEN -> Icons.Default.Restaurant
                                            UserRole.ADMIN -> Icons.Default.AccountCircle
                                        },
                                        contentDescription = user.role.name,
                                        tint = if (isSelected) TokokuhWhite else TokokuhMaroonPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = user.fullName.split(" ").firstOrNull() ?: user.username,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) TokokuhWhite else TokokuhBlack
                                    )
                                    Text(
                                        text = user.role.name,
                                        fontSize = 10.sp,
                                        color = if (isSelected) TokokuhWhite.copy(alpha = 0.8f) else TokokuhTextMuted
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Login Mode Tabs: PIN Cepat vs Username & Password
                TabRow(
                    selectedTabIndex = if (uiState.loginMode == LoginMode.PIN) 0 else 1,
                    containerColor = TokokuhWhite,
                    contentColor = TokokuhMaroonPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[if (uiState.loginMode == LoginMode.PIN) 0 else 1]),
                            color = TokokuhMaroonPrimary
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, TokokuhBorder, RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = uiState.loginMode == LoginMode.PIN,
                        onClick = { viewModel.switchLoginMode(LoginMode.PIN) },
                        text = {
                            Text(
                                "PIN Kasir (6 Angka)",
                                fontWeight = if (uiState.loginMode == LoginMode.PIN) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.loginMode == LoginMode.PIN) TokokuhMaroonPrimary else TokokuhTextMuted
                            )
                        }
                    )
                    Tab(
                        selected = uiState.loginMode == LoginMode.PASSWORD,
                        onClick = { viewModel.switchLoginMode(LoginMode.PASSWORD) },
                        text = {
                            Text(
                                "Password",
                                fontWeight = if (uiState.loginMode == LoginMode.PASSWORD) FontWeight.Bold else FontWeight.Normal,
                                color = if (uiState.loginMode == LoginMode.PASSWORD) TokokuhMaroonPrimary else TokokuhTextMuted
                            )
                        }
                    )
                }

                // Error Message Display
                uiState.errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFB91C1C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Content Based on Mode
                if (uiState.loginMode == LoginMode.PIN) {
                    PinPadSection(
                        pin = uiState.pinInput,
                        isLoading = uiState.isLoading,
                        onDigitClick = { viewModel.onPinDigit(it) },
                        onBackspaceClick = { viewModel.onPinBackspace() },
                        onClearClick = { viewModel.clearPin() }
                    )
                } else {
                    PasswordLoginSection(
                        username = uiState.usernameInput,
                        password = uiState.passwordInput,
                        isLoading = uiState.isLoading,
                        onUsernameChange = { viewModel.onUsernameChange(it) },
                        onPasswordChange = { viewModel.onPasswordChange(it) },
                        onSubmit = { viewModel.loginWithPassword() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer Links: Forgot PIN & Register Owner
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onNavigateToForgotPin,
                        modifier = Modifier.testTag("forgot_pin_button")
                    ) {
                        Text(
                            "Lupa PIN?",
                            color = TokokuhMaroonPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.testTag("register_owner_button")
                    ) {
                        Text(
                            "+ Buat Akun Pemilik",
                            color = TokokuhMaroonDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PinPadSection(
    pin: String,
    isLoading: Boolean,
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // PIN Visual Dots (6 circles)
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            repeat(6) { index ->
                val isFilled = index < pin.length
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            if (isFilled) TokokuhMaroonPrimary else Color.Transparent
                        )
                        .border(
                            width = 2.dp,
                            color = if (isFilled) TokokuhMaroonPrimary else TokokuhBorder,
                            shape = CircleShape
                        )
                )
            }
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .padding(vertical = 8.dp),
                color = TokokuhMaroonPrimary,
                strokeWidth = 3.dp
            )
        } else {
            Text(
                text = "Masukkan 6 Digit PIN (Default Owner: 123456 / Kasir: 112233)",
                fontSize = 12.sp,
                color = TokokuhTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // Numeric Keypad (1-9, C, 0, Backspace)
        val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "DEL")
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            keys.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    row.forEach { key ->
                        Surface(
                            onClick = {
                                when (key) {
                                    "C" -> onClearClick()
                                    "DEL" -> onBackspaceClick()
                                    else -> onDigitClick(key)
                                }
                            },
                            shape = CircleShape,
                            color = when (key) {
                                "C", "DEL" -> TokokuhBackground
                                else -> TokokuhWhite
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder),
                            modifier = Modifier
                                .size(64.dp)
                                .testTag("pin_key_$key")
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                when (key) {
                                    "DEL" -> Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Hapus",
                                        tint = TokokuhMaroonDark,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    "C" -> Text(
                                        text = "C",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TokokuhMaroonDark
                                    )
                                    else -> Text(
                                        text = key,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TokokuhBlack
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

@Composable
private fun PasswordLoginSection(
    username: String,
    password: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            placeholder = { Text("Contoh: owner atau kasir") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null, tint = TokokuhMaroonPrimary)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("username_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TokokuhMaroonPrimary,
                focusedLabelColor = TokokuhMaroonPrimary
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = TokokuhMaroonPrimary)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = TokokuhTextMuted
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("password_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TokokuhMaroonPrimary,
                focusedLabelColor = TokokuhMaroonPrimary
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onSubmit() })
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onSubmit,
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("login_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = TokokuhMaroonPrimary,
                contentColor = TokokuhWhite
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = TokokuhWhite,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Masuk ke Sistem",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
