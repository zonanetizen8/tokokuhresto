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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QuestionMark
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
import com.example.ui.theme.TokokuhBlack
import com.example.ui.theme.TokokuhMaroonDark
import com.example.ui.theme.TokokuhMaroonPrimary
import com.example.ui.theme.TokokuhTextMuted
import com.example.ui.theme.TokokuhWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterOwnerScreen(
    viewModel: RegistrationViewModel,
    onNavigateBack: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.registerState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daftar Akun Pemilik (Owner)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhWhite
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("register_back_button")
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
                        Text(
                            text = "Hak Akses Penuh Restoran",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhMaroonDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Akun Pemilik (Owner) memiliki akses penuh ke laporan omzet, pengaturan pajak, manajemen karyawan, inventaris, dan backup data lokal.",
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
                    value = state.fullName,
                    onValueChange = { viewModel.updateRegisterForm(fullName = it) },
                    label = { Text("Nama Lengkap Pemilik") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TokokuhMaroonPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("register_fullname_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                )

                OutlinedTextField(
                    value = state.username,
                    onValueChange = { viewModel.updateRegisterForm(username = it) },
                    label = { Text("Username") },
                    placeholder = { Text("contoh: boss_resto") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TokokuhMaroonPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("register_username_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                )

                OutlinedTextField(
                    value = state.phone,
                    onValueChange = { viewModel.updateRegisterForm(phone = it) },
                    label = { Text("Nomor Telepon / WhatsApp") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TokokuhMaroonPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("register_phone_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.updateRegisterForm(password = it) },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("register_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                    )
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.updateRegisterForm(confirmPassword = it) },
                        label = { Text("Ulangi Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("register_confirm_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = state.pin,
                        onValueChange = { if (it.length <= 6) viewModel.updateRegisterForm(pin = it) },
                        label = { Text("PIN Kasir (6 Angka)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("register_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                    )
                    OutlinedTextField(
                        value = state.confirmPin,
                        onValueChange = { if (it.length <= 6) viewModel.updateRegisterForm(confirmPin = it) },
                        label = { Text("Ulangi PIN") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("register_confirm_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                    )
                }

                Text(
                    text = "PEMULIHAN KEAMANAN LOKAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TokokuhMaroonDark,
                    letterSpacing = 0.5.sp
                )

                OutlinedTextField(
                    value = state.securityQuestion,
                    onValueChange = { viewModel.updateRegisterForm(securityQuestion = it) },
                    label = { Text("Pertanyaan Keamanan") },
                    leadingIcon = { Icon(Icons.Default.QuestionMark, contentDescription = null, tint = TokokuhMaroonPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("register_question_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                )

                OutlinedTextField(
                    value = state.securityAnswer,
                    onValueChange = { viewModel.updateRegisterForm(securityAnswer = it) },
                    label = { Text("Jawaban Keamanan (Disimpan Terenkripsi)") },
                    leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = TokokuhMaroonPrimary) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("register_answer_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TokokuhMaroonPrimary)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.registerOwner() },
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_register_button"),
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
                        Text("Simpan Akun Pemilik", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
