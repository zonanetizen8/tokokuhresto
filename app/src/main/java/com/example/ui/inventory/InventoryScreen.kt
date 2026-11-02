package com.example.ui.inventory

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.IngredientEntity
import com.example.data.local.entities.MovementType
import com.example.data.util.CurrencyFormatter
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    viewModel: InventoryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val ingredients by viewModel.ingredients.collectAsStateWithLifecycle()
    val lowCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val onlyLow by viewModel.onlyLowStock.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var adjustIng by remember { mutableStateOf<IngredientEntity?>(null) }
    var deleteIng by remember { mutableStateOf<IngredientEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Inventaris & Bahan Baku", fontWeight = FontWeight.Bold, color = TokokuhWhite, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("inventory_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TokokuhWhite)
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
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambah Bahan", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = TokokuhBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp)
        ) {
            // Low Stock Warning Banner if any
            if (lowCount > 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWarningBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = TokokuhWarning, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$lowCount Bahan Baku Menipis! Segera lakukan pembelian stok.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhMaroonDark
                        )
                    }
                }
            }

            // Search Bar & Filters
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearch(it) },
                placeholder = { Text("Cari nama atau kode bahan...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TokokuhMaroonPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TokokuhMaroonPrimary,
                    unfocusedBorderColor = TokokuhBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !onlyLow,
                    onClick = { viewModel.setLowOnly(false) },
                    label = { Text("Semua Bahan") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TokokuhMaroonPrimary, selectedLabelColor = TokokuhWhite)
                )
                FilterChip(
                    selected = onlyLow,
                    onClick = { viewModel.setLowOnly(true) },
                    label = { Text("Hanya Stok Menipis ($lowCount)") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TokokuhWarning, selectedLabelColor = TokokuhWhite)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ingredients List
            if (ingredients.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Inventory, contentDescription = null, tint = TokokuhBorder, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tidak ada bahan baku tercatat", color = TokokuhTextSecondary)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(ingredients, key = { it.id }) { ing ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (ing.isLowStock) TokokuhWarning else TokokuhBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(ing.code, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TokokuhMaroonPrimary)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        if (ing.isLowStock) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = TokokuhWarningBg) {
                                                Text("Menipis", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TokokuhWarning, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                            }
                                        }
                                    }
                                    Text(ing.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TokokuhTextPrimary)
                                    Text("Harga Beli: ${CurrencyFormatter.formatRupiah(ing.buyPrice)} / ${ing.unit}", fontSize = 11.sp, color = TokokuhTextSecondary)
                                    Text("Batas Minimum: ${ing.minStock} ${ing.unit}", fontSize = 11.sp, color = TokokuhTextSecondary)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${ing.stock} ${ing.unit}",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (ing.isLowStock) TokokuhWarning else TokokuhSuccess
                                    )
                                    Row {
                                        IconButton(onClick = { adjustIng = ing }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Tune, contentDescription = "Sesuaikan Stok", tint = TokokuhMaroonPrimary)
                                        }
                                        IconButton(onClick = { deleteIng = ing }, modifier = Modifier.size(32.dp)) {
                                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = TokokuhError)
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

    // Add Ingredient Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var code by remember { mutableStateOf("") }
        var stockStr by remember { mutableStateOf("10") }
        var unit by remember { mutableStateOf("Kg") }
        var buyPriceStr by remember { mutableStateOf("15000") }
        var minStockStr by remember { mutableStateOf("3") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Bahan Baku", fontWeight = FontWeight.Bold, color = TokokuhMaroonDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Bahan *") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Kode") }, placeholder = { Text("Auto") }, singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Satuan (Kg/Liter)") }, singleLine = true, modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = stockStr, onValueChange = { stockStr = it }, label = { Text("Stok Awal") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = minStockStr, onValueChange = { minStockStr = it }, label = { Text("Batas Min") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.weight(1f))
                    }
                    OutlinedTextField(value = buyPriceStr, onValueChange = { buyPriceStr = it.filter { ch -> ch.isDigit() } }, label = { Text("Harga Beli (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addIngredient(
                                name = name,
                                code = code,
                                stock = stockStr.toDoubleOrNull() ?: 0.0,
                                unit = unit,
                                buyPrice = buyPriceStr.toLongOrNull() ?: 0L,
                                minStock = minStockStr.toDoubleOrNull() ?: 2.0
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary)
                ) {
                    Text("Simpan", color = TokokuhWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
            }
        )
    }

    // Adjust Stock Dialog
    adjustIng?.let { ing ->
        var qtyStr by remember { mutableStateOf("") }
        var isAdd by remember { mutableStateOf(true) }
        var note by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { adjustIng = null },
            title = { Text("Penyesuaian Stok: ${ing.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Stok Saat Ini: ${ing.stock} ${ing.unit}", fontWeight = FontWeight.Bold, color = TokokuhMaroonDark)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = isAdd, onClick = { isAdd = true }, label = { Text("+ Stok Masuk") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TokokuhSuccess, selectedLabelColor = TokokuhWhite))
                        FilterChip(selected = !isAdd, onClick = { isAdd = false }, label = { Text("- Stok Keluar/Basi") }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = TokokuhError, selectedLabelColor = TokokuhWhite))
                    }
                    OutlinedTextField(value = qtyStr, onValueChange = { qtyStr = it }, label = { Text("Jumlah (${ing.unit})") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("Catatan (Contoh: Kulakan Pasar / Basi)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val qty = qtyStr.toDoubleOrNull() ?: 0.0
                        if (qty > 0) {
                            val type = if (isAdd) MovementType.IN else MovementType.OUT
                            viewModel.adjustStock(ing.id, qty, type, note)
                            adjustIng = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary)
                ) {
                    Text("Simpan Penyesuaian", color = TokokuhWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { adjustIng = null }) { Text("Batal") }
            }
        )
    }

    // Delete Confirmation
    deleteIng?.let { ing ->
        AlertDialog(
            onDismissRequest = { deleteIng = null },
            title = { Text("Hapus Bahan Baku?", fontWeight = FontWeight.Bold) },
            text = { Text("Hapus '${ing.name}' dari inventaris?") },
            confirmButton = {
                Button(onClick = { viewModel.deleteIngredient(ing.id); deleteIng = null }, colors = ButtonDefaults.buttonColors(containerColor = TokokuhError)) {
                    Text("Hapus", color = TokokuhWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteIng = null }) { Text("Batal") }
            }
        )
    }
}
