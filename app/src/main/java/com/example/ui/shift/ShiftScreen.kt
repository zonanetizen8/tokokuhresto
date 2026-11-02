package com.example.ui.shift

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.ui.components.TokokuhEmptyState
import com.example.ui.components.TokokuhPrimaryButton
import com.example.ui.components.tokokuhTextFieldColors
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ShiftEntity
import com.example.data.local.entities.ShiftStatus
import com.example.data.local.entities.UserEntity
import com.example.ui.theme.TokokuhAccentYellow
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhBorder
import com.example.ui.theme.TokokuhError
import com.example.ui.theme.TokokuhErrorBg
import com.example.ui.theme.TokokuhMaroonDark
import com.example.ui.theme.TokokuhMaroonPrimary
import com.example.ui.theme.TokokuhSuccess
import com.example.ui.theme.TokokuhSuccessBg
import com.example.ui.theme.TokokuhSurface
import com.example.ui.theme.TokokuhTextPrimary
import com.example.ui.theme.TokokuhTextSecondary
import com.example.ui.theme.TokokuhWarning
import com.example.ui.theme.TokokuhWarningBg
import com.example.ui.theme.TokokuhWhite
import com.example.data.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftScreen(
    viewModel: ShiftViewModel,
    currentUser: UserEntity? = null,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val activeShift by viewModel.activeShift.collectAsStateWithLifecycle()
    val allShifts by viewModel.allShifts.collectAsStateWithLifecycle()
    val shiftSummary by viewModel.shiftSummary.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    var showOpenShiftDialog by remember { mutableStateOf(false) }
    var showCloseShiftDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Shift dan Kas Harian",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite
                        )
                        Text(
                            text = "Rekonsiliasi Modal Awal & Uang Fisik Laci Kasir",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = TokokuhWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("shift_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Dashboard",
                            tint = TokokuhWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TokokuhMaroonPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = TokokuhBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status Card (Active / Inactive)
            item {
                if (activeShift != null) {
                    ActiveShiftCard(
                        summary = shiftSummary,
                        onCloseShift = { showCloseShiftDialog = true }
                    )
                } else {
                    NoActiveShiftCard(
                        onOpenShift = { showOpenShiftDialog = true }
                    )
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Riwayat Shift Kasir",
                        fontWeight = FontWeight.Bold,
                        color = TokokuhTextPrimary,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "${allShifts.size} shift tercatat",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = TokokuhTextSecondary
                    )
                }
            }

            if (allShifts.isEmpty()) {
                item {
                    TokokuhEmptyState(
                        icon = Icons.Default.Schedule,
                        title = "Belum Ada Riwayat Shift",
                        message = "Buka shift kasir di atas untuk memulai pencatatan uang modal awal, penjualan tunai, dan rekonsiliasi kas harian.",
                        actionText = "Buka Shift Kasir",
                        onActionClick = { showOpenShiftDialog = true },
                        actionTestTag = "empty_open_shift_btn"
                    )
                }
            } else {
                items(allShifts, key = { it.id }) { shift ->
                    ShiftHistoryCard(shift = shift)
                }
            }
        }
    }

    if (showOpenShiftDialog) {
        OpenShiftDialog(
            defaultCashierName = currentUser?.fullName ?: "Kasir Utama",
            onDismiss = { showOpenShiftDialog = false },
            onConfirm = { amount ->
                viewModel.openShift(
                    userId = currentUser?.id ?: 1L,
                    userName = currentUser?.fullName ?: "Kasir Tokokuh",
                    startingCash = amount
                )
                showOpenShiftDialog = false
            }
        )
    }

    if (showCloseShiftDialog && activeShift != null) {
        CloseShiftDialog(
            summary = shiftSummary,
            onDismiss = { showCloseShiftDialog = false },
            onConfirm = { actualCash, note ->
                viewModel.closeShift(
                    shiftId = activeShift!!.id,
                    actualCash = actualCash,
                    note = note
                )
                showCloseShiftDialog = false
            }
        )
    }
}

@Composable
private fun ActiveShiftCard(
    summary: ShiftSummaryData,
    onCloseShift: () -> Unit
) {
    val shift = summary.activeShift ?: return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TokokuhMaroonDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(TokokuhSuccess)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SHIFT SEDANG AKTIF",
                        color = TokokuhAccentYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TokokuhWhite.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = CurrencyFormatter.formatDateTime(shift.startTime),
                        color = TokokuhWhite,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Kasir Bertugas: ${shift.userName}",
                color = TokokuhWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Breakdown box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.25f)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Modal Awal Kas:", color = TokokuhWhite.copy(alpha = 0.8f), fontSize = 13.sp)
                        Text(CurrencyFormatter.formatRupiah(shift.startingCash), color = TokokuhWhite, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("+ Penjualan Tunai Shift:", color = TokokuhWhite.copy(alpha = 0.8f), fontSize = 13.sp)
                        Text(CurrencyFormatter.formatRupiah(summary.cashSales), color = TokokuhSuccess, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("- Pengeluaran Kas Shift:", color = TokokuhWhite.copy(alpha = 0.8f), fontSize = 13.sp)
                        Text(CurrencyFormatter.formatRupiah(summary.cashExpenses), color = TokokuhError, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(TokokuhWhite.copy(alpha = 0.2f)))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Estimasi Uang Fisik di Laci:", color = TokokuhAccentYellow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            CurrencyFormatter.formatRupiah(summary.expectedCash),
                            color = TokokuhAccentYellow,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCloseShift,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("close_shift_button"),
                colors = ButtonDefaults.buttonColors(containerColor = TokokuhAccentYellow, contentColor = TokokuhMaroonDark),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tutup Shift & Rekonsiliasi Kas", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NoActiveShiftCard(
    onOpenShift: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(TokokuhWarningBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = TokokuhWarning,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Belum Ada Shift Kasir yang Dibuka",
                fontWeight = FontWeight.Bold,
                color = TokokuhTextPrimary,
                fontSize = 16.sp
            )

            Text(
                text = "Buka shift dengan modal awal agar uang tunai kasir dapat dipantau dan direkonsiliasi dengan akurat.",
                color = TokokuhTextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TokokuhPrimaryButton(
                text = "Buka Shift Baru Sekarang",
                icon = Icons.Default.AccountBalanceWallet,
                onClick = onOpenShift,
                modifier = Modifier.fillMaxWidth(),
                testTag = "open_shift_button"
            )
        }
    }
}

@Composable
private fun ShiftHistoryCard(shift: ShiftEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhBorder))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TokokuhMaroonPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = shift.userName,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhTextPrimary,
                        fontSize = 14.sp
                    )
                }

                val isClosed = shift.status == ShiftStatus.CLOSED
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isClosed) TokokuhSuccessBg else TokokuhWarningBg
                ) {
                    Text(
                        text = if (isClosed) "SELESAI" else "BERJALAN",
                        color = if (isClosed) TokokuhSuccess else TokokuhWarning,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Mulai: ${CurrencyFormatter.formatDateTime(shift.startTime)}",
                color = TokokuhTextSecondary,
                fontSize = 11.sp
            )
            shift.endTime?.let { end ->
                Text(
                    text = "Selesai: ${CurrencyFormatter.formatDateTime(end)}",
                    color = TokokuhTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Modal Awal:", color = TokokuhTextSecondary, fontSize = 11.sp)
                    Text(
                        CurrencyFormatter.formatRupiah(shift.startingCash),
                        fontWeight = FontWeight.SemiBold,
                        color = TokokuhTextPrimary,
                        fontSize = 13.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Uang Fisik Kasir:", color = TokokuhTextSecondary, fontSize = 11.sp)
                    Text(
                        CurrencyFormatter.formatRupiah(shift.actualCash),
                        fontWeight = FontWeight.Bold,
                        color = TokokuhMaroonPrimary,
                        fontSize = 13.sp
                    )
                }
            }

            if (shift.status == ShiftStatus.CLOSED) {
                Spacer(modifier = Modifier.height(8.dp))
                val diff = shift.difference
                val diffColor = if (diff >= 0) TokokuhSuccess else TokokuhError
                val diffPrefix = if (diff > 0) "+" else ""
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (diff >= 0) TokokuhSuccessBg else TokokuhErrorBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Selisih Rekonsiliasi:", fontSize = 11.sp, color = diffColor, fontWeight = FontWeight.Medium)
                    Text("$diffPrefix${CurrencyFormatter.formatRupiah(diff)}", fontSize = 11.sp, color = diffColor, fontWeight = FontWeight.Bold)
                }
            }

            if (!shift.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Catatan: ${shift.note}",
                    color = TokokuhTextSecondary,
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun OpenShiftDialog(
    defaultCashierName: String,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    var cashText by remember { mutableStateOf("200000") }
    val quickAmounts = listOf(100000L, 200000L, 300000L, 500000L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buka Shift Kasir Baru", fontWeight = FontWeight.Bold, color = TokokuhMaroonPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Petugas: $defaultCashierName",
                    fontWeight = FontWeight.SemiBold,
                    color = TokokuhTextPrimary,
                    fontSize = 13.sp
                )
                Text(
                    text = "Masukkan jumlah modal uang kembalian kasir awal:",
                    color = TokokuhTextSecondary,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = cashText,
                    onValueChange = { cashText = it.filter { char -> char.isDigit() } },
                    label = { Text("Modal Awal Kas (Rp)") },
                    placeholder = { Text("0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("starting_cash_input"),
                    singleLine = true,
                    colors = tokokuhTextFieldColors()
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(quickAmounts) { amt ->
                        FilterChip(
                            selected = cashText == amt.toString(),
                            onClick = { cashText = amt.toString() },
                            label = {
                                Text(
                                    CurrencyFormatter.formatRupiah(amt),
                                    fontSize = 11.sp,
                                    color = if (cashText == amt.toString()) TokokuhWhite else TokokuhTextPrimary
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TokokuhMaroonPrimary,
                                selectedLabelColor = TokokuhWhite,
                                containerColor = TokokuhSurface,
                                labelColor = TokokuhTextPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = cashText.toLongOrNull() ?: 0L
                    onConfirm(amount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary, contentColor = TokokuhWhite),
                modifier = Modifier.testTag("confirm_open_shift")
            ) {
                Text("Buka Shift", color = TokokuhWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TokokuhTextSecondary)
            }
        }
    )
}

@Composable
private fun CloseShiftDialog(
    summary: ShiftSummaryData,
    onDismiss: () -> Unit,
    onConfirm: (Long, String) -> Unit
) {
    var actualCashText by remember { mutableStateOf(summary.expectedCash.toString()) }
    var noteText by remember { mutableStateOf("") }

    val actualCash = actualCashText.toLongOrNull() ?: 0L
    val diff = actualCash - summary.expectedCash

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tutup Shift & Hitung Kasir", fontWeight = FontWeight.Bold, color = TokokuhMaroonPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = TokokuhSurface,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Estimasi Sistem: ${CurrencyFormatter.formatRupiah(summary.expectedCash)}",
                            fontWeight = FontWeight.Bold,
                            color = TokokuhTextPrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "(Modal Awal + Penjualan Tunai - Pengeluaran)",
                            color = TokokuhTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = actualCashText,
                    onValueChange = { actualCashText = it.filter { char -> char.isDigit() } },
                    label = { Text("Uang Fisik Kasir (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("actual_cash_input"),
                    singleLine = true,
                    colors = tokokuhTextFieldColors()
                )

                // Difference indicator
                val diffColor = if (diff >= 0) TokokuhSuccess else TokokuhError
                val diffLabel = if (diff == 0L) "Pas (Sesuai Sistem)" else if (diff > 0) "Kelebihan Uang Fisik" else "Kekurangan Uang Fisik"
                Surface(
                    color = if (diff >= 0) TokokuhSuccessBg else TokokuhErrorBg,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(diffLabel, color = diffColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Text(CurrencyFormatter.formatRupiah(diff), color = diffColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Catatan Penutupan Shift (Opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    colors = tokokuhTextFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(actualCash, noteText) },
                colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary, contentColor = TokokuhWhite),
                modifier = Modifier.testTag("confirm_close_shift")
            ) {
                Text("Simpan & Tutup", color = TokokuhWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TokokuhTextSecondary)
            }
        }
    )
}
