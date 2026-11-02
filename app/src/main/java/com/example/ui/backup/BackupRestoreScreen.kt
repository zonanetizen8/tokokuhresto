package com.example.ui.backup

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.TokokuhAccentYellow
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhBorder
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
fun BackupRestoreScreen(
    viewModel: BackupViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val lastBackup by viewModel.lastBackup.collectAsStateWithLifecycle()
    val isBackingUp by viewModel.isBackingUp.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showRestoreConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Backup dan Restore",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite
                        )
                        Text(
                            text = "Cadangan Database Lokal & File Penyimpanan",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = TokokuhWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("backup_back_button")
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
            // Hero Status
            item {
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
                            Text(
                                text = "RINGKASAN DATABASE LOKAL",
                                color = TokokuhAccentYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = TokokuhAccentYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatBox(label = "Menu Makanan", value = "${stats.menuCount}")
                            StatBox(label = "Meja Resto", value = "${stats.tableCount}")
                            StatBox(label = "Bahan Baku", value = "${stats.ingredientCount}")
                            StatBox(label = "Transaksi", value = "${stats.orderCount}")
                        }
                    }
                }
            }

            // Offline Privacy Guarantee Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhSuccessBg),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhSuccess.copy(alpha = 0.3f)))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = TokokuhSuccess,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "100% Offline & Privasi Terjamin",
                                fontWeight = FontWeight.Bold,
                                color = TokokuhSuccess,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Seluruh database tersimpan di memori internal ponsel. Tidak ada server cloud yang dapat mengakses resep, harga, atau omzet restoran Anda.",
                                color = TokokuhTextPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // Backup Action Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Backup,
                                contentDescription = null,
                                tint = TokokuhMaroonPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Buat Cadangan Baru (Export)",
                                fontWeight = FontWeight.Bold,
                                color = TokokuhTextPrimary,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Ekspor seluruh struktur database, menu, meja, dan riwayat transaksi ke berkas cadangan lokal di memori HP.",
                            color = TokokuhTextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (lastBackup != null) {
                            Surface(
                                color = TokokuhSurface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = "Cadangan Terakhir:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = TokokuhTextSecondary
                                    )
                                    Text(
                                        text = lastBackup!!.fileName,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp,
                                        color = TokokuhTextPrimary
                                    )
                                    Text(
                                        text = "Waktu: ${CurrencyFormatter.formatDateTime(lastBackup!!.timestamp)} • Ukuran: ${lastBackup!!.fileSizeFormatted}",
                                        fontSize = 11.sp,
                                        color = TokokuhTextSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        Button(
                            onClick = { viewModel.createBackup() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("create_backup_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !isBackingUp
                        ) {
                            if (isBackingUp) {
                                CircularProgressIndicator(color = TokokuhWhite, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sedang Mencadangkan...")
                            } else {
                                Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cadangkan Database Sekarang")
                            }
                        }
                    }
                }
            }

            // Restore / Integrity Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = null,
                                tint = TokokuhMaroonPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pulihkan & Verifikasi Integritas",
                                fontWeight = FontWeight.Bold,
                                color = TokokuhTextPrimary,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Periksa keutuhan relasi database SQLite lokal serta pulihkan cadangan jika mengganti perangkat HP kasir.",
                            color = TokokuhTextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = { showRestoreConfirmDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verify_restore_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TokokuhMaroonPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verifikasi & Uji Integritas Data", color = TokokuhMaroonPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tips Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = TokokuhTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Saran Keamanan Data Offline:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TokokuhTextPrimary)
                        }
                        Text(
                            text = "Salin berkas cadangan dari memori HP ke flashdisk USB OTG atau laptop pemilik restoran secara berkala untuk perlindungan dari kerusakan fisik ponsel.",
                            fontSize = 11.sp,
                            color = TokokuhTextSecondary
                        )
                    }
                }
            }
        }
    }

    if (showRestoreConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirmDialog = false },
            title = { Text("Verifikasi Database SQLite", fontWeight = FontWeight.Bold, color = TokokuhMaroonPrimary) },
            text = {
                Text(
                    "Sistem akan memverifikasi konsistensi relasi menu, meja, dan riwayat pesanan dalam penyimpanan lokal.",
                    fontSize = 13.sp,
                    color = TokokuhTextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.verifyRestore()
                        showRestoreConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary)
                ) {
                    Text("Jalankan Verifikasi")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreConfirmDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
private fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = TokokuhWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = TokokuhWhite.copy(alpha = 0.75f), fontSize = 10.sp)
    }
}
