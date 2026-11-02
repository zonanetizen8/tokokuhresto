package com.example.ui.auth

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhMaroonDark
import com.example.ui.theme.TokokuhMaroonPrimary
import com.example.ui.theme.TokokuhTextMuted
import com.example.ui.theme.TokokuhWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPinScreen(
    viewModel: RegistrationViewModel,
    onNavigateBack: () -> Unit,
    onResetSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.forgotPinState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onResetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pemulihan PIN Lokal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhWhite
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("forgot_pin_back")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TokokuhWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TokokuhMaroonPrimary
                )
            )
        },
        containerColor = TokokuhBackground
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LockReset,
                                contentDescription = null,
                                tint = TokokuhMaroonPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "Reset PIN Tanpa Koneksi Internet",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TokokuhMaroonDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gunakan pertanyaan keamanan yang telah Anda atur saat pendaftaran untuk mereset PIN kasir Anda.",
                            fontSize = 12.sp,
                            color = TokokuhTextMuted
                        )
                    }
                }

                state.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            color = Color(0xFFB91C1C),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = state.username,
                    onValueChange = { viewModel.updateForgotPinForm(username = it) },
                    label = { Text("Username") },
                    placeholder = { Text("Contoh: owner") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TokokuhMaroonPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("forgot_pin_username"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                )

                OutlinedTextField(
                    value = state.securityAnswer,
                    onValueChange = { viewModel.updateForgotPinForm(securityAnswer = it) },
                    label = { Text("Jawaban Keamanan") },
                    placeholder = { Text("Masukkan jawaban saat registrasi") },
                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = TokokuhMaroonPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("forgot_pin_answer"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = state.newPin,
                        onValueChange = { if (it.length <= 6) viewModel.updateForgotPinForm(newPin = it) },
                        label = { Text("PIN Baru (6 Angka)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("forgot_new_pin"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                    )
                    OutlinedTextField(
                        value = state.confirmNewPin,
                        onValueChange = { if (it.length <= 6) viewModel.updateForgotPinForm(confirmNewPin = it) },
                        label = { Text("Ulangi PIN Baru") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("forgot_confirm_pin"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.resetPin() },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_reset_pin"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TokokuhMaroonPrimary,
                        contentColor = TokokuhWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = TokokuhWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Perbarui PIN Sekarang", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
