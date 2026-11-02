package com.example.ui.tables

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.TableEntity
import com.example.data.local.entities.TableStatus
import com.example.ui.theme.TokokuhAccentYellow
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhBlack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableManagementScreen(
    viewModel: TableViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val tables by viewModel.tables.collectAsStateWithLifecycle()
    val allTables by viewModel.allTablesRaw.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.statusFilter.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var tableToEdit by remember { mutableStateOf<TableEntity?>(null) }
    var tableToMove by remember { mutableStateOf<TableEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Pengelolaan Meja Restoran", fontWeight = FontWeight.Bold, color = TokokuhWhite, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("table_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali ke Dashboard", tint = TokokuhWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TokokuhMaroonPrimary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TokokuhMaroonPrimary,
                contentColor = TokokuhWhite,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tambah Meja", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = TokokuhBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Status Filter Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { viewModel.setFilter(null) },
                        label = { Text("Semua (${allTables.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                }
                items(TableStatus.values()) { status ->
                    val count = allTables.count { it.status == status }
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { viewModel.setFilter(status) },
                        label = { Text("${status.displayName} ($count)", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                }
            }

            // Tables Grid
            if (tables.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TableBar, contentDescription = null, tint = TokokuhBorder, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Tidak ada meja ditemukan", color = TokokuhTextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    items(tables, key = { it.id }) { table ->
                        TableGridCard(
                            table = table,
                            onClear = { viewModel.clearTable(table.id) },
                            onEdit = { tableToEdit = table },
                            onMove = { tableToMove = table },
                            onStatusChange = { newStatus -> viewModel.updateStatus(table.id, newStatus) },
                            onDelete = { viewModel.deleteTable(table.id) }
                        )
                    }
                }
            }
        }
    }

    // Add Table Dialog
    if (showAddDialog) {
        var numStr by remember { mutableStateOf("") }
        var capStr by remember { mutableStateOf("4") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Meja Restoran", fontWeight = FontWeight.Bold, color = TokokuhMaroonDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = numStr,
                        onValueChange = { numStr = it },
                        label = { Text("Nomor Meja (Contoh: 11 atau Meja 11)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = capStr,
                        onValueChange = { capStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Kapasitas Kursi") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (numStr.isNotBlank()) {
                            viewModel.addTable(numStr, capStr.toIntOrNull() ?: 4)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary)
                ) {
                    Text("Simpan Meja", color = TokokuhWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
            }
        )
    }

    // Move Table Dialog
    tableToMove?.let { fromTable ->
        val candidateTables = allTables.filter { it.id != fromTable.id && it.status == TableStatus.VACANT }
        AlertDialog(
            onDismissRequest = { tableToMove = null },
            title = { Text("Pindahkan ${fromTable.tableNumber}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pilih meja kosong tujuan:", fontSize = 13.sp, color = TokokuhTextSecondary)
                    if (candidateTables.isEmpty()) {
                        Text("Tidak ada meja lain yang berstatus Kosong.", color = TokokuhError, fontSize = 12.sp)
                    } else {
                        candidateTables.forEach { candidate ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.moveTable(fromTable.id, candidate.id)
                                        tableToMove = null
                                    },
                                shape = RoundedCornerShape(8.dp),
                                color = TokokuhSurface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(candidate.tableNumber, fontWeight = FontWeight.Bold)
                                    Text("${candidate.capacity} Kursi", fontSize = 12.sp, color = TokokuhTextSecondary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { tableToMove = null }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun TableGridCard(
    table: TableEntity,
    onClear: () -> Unit,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onStatusChange: (TableStatus) -> Unit,
    onDelete: () -> Unit
) {
    val statusBg = when (table.status) {
        TableStatus.VACANT -> TokokuhSuccessBg
        TableStatus.OCCUPIED -> TokokuhWarningBg
        TableStatus.WAITING_ORDER -> Color(0xFFE0F2FE)
        TableStatus.SERVING -> Color(0xFFF3E8FF)
        TableStatus.WAITING_PAYMENT -> TokokuhErrorBg
    }
    val statusColor = when (table.status) {
        TableStatus.VACANT -> TokokuhSuccess
        TableStatus.OCCUPIED -> TokokuhWarning
        TableStatus.WAITING_ORDER -> Color(0xFF0369A1)
        TableStatus.SERVING -> Color(0xFF7E22CE)
        TableStatus.WAITING_PAYMENT -> TokokuhError
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, statusColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(table.tableNumber, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TokokuhBlack)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusBg
                ) {
                    Text(
                        table.status.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TokokuhTextSecondary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Kapasitas: ${table.capacity} Orang", fontSize = 11.sp, color = TokokuhTextSecondary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (table.status != TableStatus.VACANT) {
                    TextButton(onClick = onClear) {
                        Text("Kosongkan", color = TokokuhSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onMove, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.MoveDown, contentDescription = "Pindah Meja", tint = TokokuhMaroonPrimary, modifier = Modifier.size(16.dp))
                    }
                } else {
                    TextButton(onClick = { onStatusChange(TableStatus.OCCUPIED) }) {
                        Text("+ Isi Meja", color = TokokuhMaroonPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = TokokuhTextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
