package com.example.ui.category

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.CategoryEntity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var categoryToEdit by remember { mutableStateOf<CategoryEntity?>(null) }
    var categoryToDelete by remember { mutableStateOf<CategoryEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Kategori Menu", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TokokuhWhite)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("category_back_btn")) {
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
                    Text("Tambah Kategori", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = TokokuhBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = TokokuhErrorBg),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(error, color = TokokuhError, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Tutup", color = TokokuhError)
                        }
                    }
                }
            }

            if (categories.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Category, contentDescription = null, tint = TokokuhBorder, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Belum ada kategori", color = TokokuhTextSecondary)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(categories, key = { it.id }) { cat ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = TokokuhWhite,
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.Category, contentDescription = null, tint = TokokuhMaroonPrimary)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(cat.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TokokuhTextPrimary)
                                }

                                Row {
                                    IconButton(onClick = { categoryToEdit = cat }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = TokokuhMaroonPrimary)
                                    }
                                    IconButton(onClick = { categoryToDelete = cat }) {
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

    // Add Category Dialog
    if (showAddDialog) {
        var catName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Kategori Menu", fontWeight = FontWeight.Bold, color = TokokuhMaroonDark) },
            text = {
                OutlinedTextField(
                    value = catName,
                    onValueChange = { catName = it },
                    label = { Text("Nama Kategori *") },
                    placeholder = { Text("Contoh: Kopi & Minuman Panas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            viewModel.addCategory(catName)
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

    // Edit Category Dialog
    categoryToEdit?.let { cat ->
        var catName by remember { mutableStateOf(cat.name) }
        AlertDialog(
            onDismissRequest = { categoryToEdit = null },
            title = { Text("Edit Kategori", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = catName,
                    onValueChange = { catName = it },
                    label = { Text("Nama Kategori") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            viewModel.updateCategory(cat.copy(name = catName.trim()))
                            categoryToEdit = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary)
                ) {
                    Text("Simpan Perubahan", color = TokokuhWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToEdit = null }) { Text("Batal") }
            }
        )
    }

    // Delete Confirmation Dialog
    categoryToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Hapus Kategori?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus '${cat.name}'? Kategori yang masih berisi produk tidak dapat dihapus.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCategory(cat.id)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhError)
                ) {
                    Text("Hapus", color = TokokuhWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) { Text("Batal") }
            }
        )
    }
}
