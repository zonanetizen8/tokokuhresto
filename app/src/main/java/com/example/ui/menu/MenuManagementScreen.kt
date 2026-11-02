package com.example.ui.menu

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.MenuEntity
import com.example.data.util.CurrencyFormatter
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
import com.example.ui.theme.TokokuhWhite
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuManagementScreen(
    viewModel: MenuViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val menus by viewModel.filteredMenus.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryId.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var menuToEdit by remember { mutableStateOf<MenuEntity?>(null) }
    var menuToDelete by remember { mutableStateOf<MenuEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kelola Menu Restoran",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhWhite
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("menu_back_btn")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                onClick = {
                    menuToEdit = null
                    showDialog = true
                },
                containerColor = TokokuhMaroonPrimary,
                contentColor = TokokuhWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_menu_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Menu")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tambah Menu", fontWeight = FontWeight.Bold)
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
            // Search Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Cari nama menu atau kode...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TokokuhMaroonPrimary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus teks")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TokokuhMaroonPrimary,
                        unfocusedBorderColor = TokokuhBorder,
                        focusedContainerColor = TokokuhWhite,
                        unfocusedContainerColor = TokokuhSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.onCategorySelect(null) },
                        label = { Text("Semua (${menus.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                }
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat.id,
                        onClick = { viewModel.onCategorySelect(cat.id) },
                        label = { Text(cat.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                }
            }

            // Menu List
            if (menus.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = TokokuhBorder,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak ada menu yang cocok",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhTextPrimary
                        )
                        Text(
                            text = "Gunakan tombol Tambah Menu untuk menambahkan hidangan baru.",
                            fontSize = 12.sp,
                            color = TokokuhTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(menus, key = { it.id }) { menu ->
                        val catName = categories.firstOrNull { it.id == menu.categoryId }?.name ?: "Umum"
                        MenuListItemCard(
                            menu = menu,
                            categoryName = catName,
                            onToggleAvailable = {
                                viewModel.toggleAvailability(menu.id, menu.isAvailable)
                            },
                            onEdit = {
                                menuToEdit = menu
                                showDialog = true
                            },
                            onDelete = {
                                menuToDelete = menu
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showDialog) {
        AddEditMenuDialog(
            menu = menuToEdit,
            categories = categories,
            onDismiss = { showDialog = false },
            onSave = { entity, photoUri ->
                showDialog = false
            },
            viewModel = viewModel
        )
    }

    // Delete Confirmation Dialog
    menuToDelete?.let { menu ->
        AlertDialog(
            onDismissRequest = { menuToDelete = null },
            title = { Text("Hapus Menu?", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus '${menu.name}'? Data dan foto lokal akan dihapus permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMenu(menu)
                        menuToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhError)
                ) {
                    Text("Hapus", color = TokokuhWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { menuToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun MenuListItemCard(
    menu: MenuEntity,
    categoryName: String,
    onToggleAvailable: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Image / Thumbnail
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TokokuhSurface)
                    .border(1.dp, TokokuhBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!menu.imagePath.isNullOrBlank() && File(menu.imagePath).exists()) {
                    AsyncImage(
                        model = File(menu.imagePath),
                        contentDescription = menu.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Fastfood,
                            contentDescription = null,
                            tint = TokokuhMaroonPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Foto",
                            fontSize = 9.sp,
                            color = TokokuhTextSecondary
                        )
                    }
                }

                if (menu.isFavorite) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        shape = CircleShape,
                        color = TokokuhAccentYellow
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Favorit",
                            tint = TokokuhBlack,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TokokuhSurface
                    ) {
                        Text(
                            text = menu.code,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhMaroonPrimary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = categoryName,
                        fontSize = 11.sp,
                        color = TokokuhTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = menu.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TokokuhTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = CurrencyFormatter.formatRupiah(menu.sellPrice),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TokokuhMaroonPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Stok: ${menu.stock.toInt()} ${menu.unit}",
                        fontSize = 11.sp,
                        color = TokokuhTextSecondary
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (menu.isAvailable) TokokuhSuccessBg else TokokuhErrorBg
                    ) {
                        Text(
                            text = if (menu.isAvailable) "Tersedia" else "Habis",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (menu.isAvailable) TokokuhSuccess else TokokuhError,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Quick Actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Menu",
                        tint = TokokuhMaroonPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus Menu",
                        tint = TokokuhError,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Switch(
                    checked = menu.isAvailable,
                    onCheckedChange = { onToggleAvailable() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TokokuhWhite,
                        checkedTrackColor = TokokuhSuccess
                    ),
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
private fun AddEditMenuDialog(
    menu: MenuEntity?,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (MenuEntity, Uri?) -> Unit,
    viewModel: MenuViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf(menu?.name ?: "") }
    var code by remember { mutableStateOf(menu?.code ?: "") }
    var description by remember { mutableStateOf(menu?.description ?: "") }
    var selectedCatId by remember { mutableStateOf(menu?.categoryId ?: categories.firstOrNull()?.id) }
    var sellPriceStr by remember { mutableStateOf(menu?.sellPrice?.toString() ?: "") }
    var buyPriceStr by remember { mutableStateOf(menu?.buyPrice?.toString() ?: "") }
    var stockStr by remember { mutableStateOf(menu?.stock?.toInt()?.toString() ?: "50") }
    var unit by remember { mutableStateOf(menu?.unit ?: "Porsi") }
    var isAvailable by remember { mutableStateOf(menu?.isAvailable ?: true) }
    var isFavorite by remember { mutableStateOf(menu?.isFavorite ?: false) }
    var imagePath by remember { mutableStateOf(menu?.imagePath) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (menu == null) "Tambah Menu Baru" else "Edit Menu",
                fontWeight = FontWeight.Bold,
                color = TokokuhMaroonDark
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Image preview & selector
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(TokokuhSurface)
                        .border(1.dp, TokokuhBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedUri != null) {
                        AsyncImage(
                            model = selectedUri,
                            contentDescription = "Preview Gambar Baru",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (!imagePath.isNullOrBlank() && File(imagePath!!).exists()) {
                        AsyncImage(
                            model = File(imagePath!!),
                            contentDescription = "Gambar Menu",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = null,
                                tint = TokokuhMaroonPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Pilih Foto Menu (Galeri HP)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TokokuhMaroonPrimary
                            )
                            Text(
                                "Tersimpan 100% offline di perangkat",
                                fontSize = 10.sp,
                                color = TokokuhTextSecondary
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Makanan / Minuman *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Kode (Opsional)") },
                        placeholder = { Text("Auto") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Satuan") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Kategori Chips
                Text("Kategori:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TokokuhTextSecondary)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCatId == cat.id,
                            onClick = { selectedCatId = cat.id },
                            label = { Text(cat.name, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TokokuhMaroonPrimary,
                                selectedLabelColor = TokokuhWhite
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = sellPriceStr,
                        onValueChange = { sellPriceStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Harga Jual (Rp) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = buyPriceStr,
                        onValueChange = { buyPriceStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Harga Beli (Modal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = stockStr,
                    onValueChange = { stockStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Stok Porsi") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tersedia untuk dijual", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = TokokuhSuccess)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tandai sebagai Favorit", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = isFavorite,
                        onCheckedChange = { isFavorite = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = TokokuhAccentYellow)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank() || sellPriceStr.isBlank()) return@Button
                    isSaving = true
                    coroutineScope.launch {
                        var savedPath = imagePath
                        selectedUri?.let { uri ->
                            val localSaved = viewModel.saveImage(context, uri)
                            if (localSaved != null) {
                                savedPath = localSaved
                            }
                        }

                        viewModel.saveMenu(
                            id = menu?.id ?: 0L,
                            code = code,
                            name = name,
                            description = description,
                            categoryId = selectedCatId,
                            buyPrice = buyPriceStr.toLongOrNull() ?: 0L,
                            sellPrice = sellPriceStr.toLongOrNull() ?: 0L,
                            stock = stockStr.toDoubleOrNull() ?: 50.0,
                            unit = unit,
                            isAvailable = isAvailable,
                            isFavorite = isFavorite,
                            imagePath = savedPath
                        )
                        isSaving = false
                        onDismiss()
                    }
                },
                enabled = !isSaving && name.isNotBlank() && sellPriceStr.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary)
            ) {
                Text(if (isSaving) "Menyimpan..." else "Simpan Menu", color = TokokuhWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
