package com.example.ui.expense

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.ExpenseEntity
import com.example.ui.theme.TokokuhAccentYellow
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhBorder
import com.example.ui.theme.TokokuhError
import com.example.ui.theme.TokokuhErrorBg
import com.example.ui.theme.TokokuhMaroonDark
import com.example.ui.theme.TokokuhMaroonPrimary
import com.example.ui.theme.TokokuhSurface
import com.example.ui.theme.TokokuhTextPrimary
import com.example.ui.theme.TokokuhTextSecondary
import com.example.ui.theme.TokokuhWhite
import com.example.data.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var showAddDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    val categories = listOf(
        "Bahan Baku",
        "Operasional Toko",
        "Listrik & Air",
        "Gaji Karyawan",
        "Sewa & Kebersihan",
        "Lain-lain"
    )

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
                            text = "Pengeluaran Operasional",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite
                        )
                        Text(
                            text = "Pencatatan Biaya Restoran & Arus Kas",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = TokokuhWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("expense_back_button")
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = TokokuhAccentYellow,
                contentColor = TokokuhMaroonDark,
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Catat Biaya")
            }
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
            // Summary banner
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
                                text = "TOTAL PENGELUARAN RESTO",
                                color = TokokuhAccentYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Default.MoneyOff,
                                contentDescription = null,
                                tint = TokokuhAccentYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = CurrencyFormatter.formatRupiah(totalExpense),
                            color = TokokuhWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${expenses.size} transaksi pengeluaran tercatat",
                            color = TokokuhWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Category filter chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { viewModel.setCategoryFilter(null) },
                            label = { Text("Semua") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TokokuhMaroonPrimary,
                                selectedLabelColor = TokokuhWhite
                            )
                        )
                    }
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.setCategoryFilter(cat) },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TokokuhMaroonPrimary,
                                selectedLabelColor = TokokuhWhite
                            )
                        )
                    }
                }
            }

            // List of expenses
            if (expenses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = null,
                                tint = TokokuhTextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Belum Ada Catatan Biaya",
                                fontWeight = FontWeight.Bold,
                                color = TokokuhTextPrimary
                            )
                            Text(
                                text = "Gunakan tombol + di sudut kanan bawah untuk mencatat pengeluaran restoran.",
                                color = TokokuhTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(expenses, key = { it.id }) { item ->
                    ExpenseItemCard(
                        expense = item,
                        onDelete = { expenseToDelete = item }
                    )
                }
            }
        }
    }

    // Add Expense Dialog
    if (showAddDialog) {
        AddExpenseDialog(
            categories = categories,
            onDismiss = { showAddDialog = false },
            onConfirm = { cat, amt, note ->
                viewModel.addExpense(cat, amt, note)
                showAddDialog = false
            }
        )
    }

    // Delete Confirmation Dialog
    expenseToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Hapus Catatan Pengeluaran?", fontWeight = FontWeight.Bold, color = TokokuhError) },
            text = {
                Text("Pengeluaran ${item.category} sebesar ${CurrencyFormatter.formatRupiah(item.amount)} akan dihapus permanen.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(item.id)
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhError)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun ExpenseItemCard(
    expense: ExpenseEntity,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TokokuhSurface
                ) {
                    Text(
                        text = expense.category,
                        color = TokokuhMaroonPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (expense.note.isNotBlank()) expense.note else "Pengeluaran ${expense.category}",
                    fontWeight = FontWeight.SemiBold,
                    color = TokokuhTextPrimary,
                    fontSize = 13.sp
                )

                Text(
                    text = CurrencyFormatter.formatDateTime(expense.date),
                    color = TokokuhTextSecondary,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "-${CurrencyFormatter.formatRupiah(expense.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = TokokuhError,
                    fontSize = 14.sp
                )

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = TokokuhTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddExpenseDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String, Long, String) -> Unit
) {
    var selectedCat by remember { mutableStateOf(categories.first()) }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    val quickAmounts = listOf(25000L, 50000L, 100000L, 250000L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Catat Pengeluaran Baru", fontWeight = FontWeight.Bold, color = TokokuhMaroonPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Kategori Pengeluaran:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TokokuhTextPrimary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCat == cat,
                            onClick = { selectedCat = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TokokuhMaroonPrimary,
                                selectedLabelColor = TokokuhWhite
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Nominal Biaya (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("expense_amount_input"),
                    singleLine = true
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickAmounts) { amt ->
                        FilterChip(
                            selected = amountText == amt.toString(),
                            onClick = { amountText = amt.toString() },
                            label = { Text(CurrencyFormatter.formatRupiah(amt), fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TokokuhMaroonPrimary,
                                selectedLabelColor = TokokuhWhite
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Keterangan / Rincian Biaya") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toLongOrNull() ?: 0L
                    onConfirm(selectedCat, amount, noteText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary),
                modifier = Modifier.testTag("confirm_add_expense")
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
