package com.example.ui.settings

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderType
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.PaymentStatus
import com.example.data.local.entities.RestaurantProfileEntity
import com.example.data.util.CurrencyFormatter
import com.example.data.util.ImageStorageManager
import com.example.data.util.ReceiptPdfGenerator
import com.example.ui.components.ReceiptHeaderView
import com.example.ui.components.TokokuhDangerButton
import com.example.ui.components.TokokuhPrimaryButton
import com.example.ui.components.TokokuhSecondaryButton
import com.example.ui.components.tokokuhTextFieldColors
import com.example.ui.theme.TokokuhAccentYellow
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhBorder
import com.example.ui.theme.TokokuhError
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
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var slogan by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var receiptHeader by remember { mutableStateOf("") }
    var receiptFooter by remember { mutableStateOf("") }
    var taxText by remember { mutableStateOf("0.0") }
    var serviceText by remember { mutableStateOf("0.0") }
    var printerWidth by remember { mutableIntStateOf(58) }

    var isUploadingLogo by remember { mutableStateOf(false) }
    var showReceiptPreviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(profile) {
        profile?.let { p ->
            name = p.name
            slogan = p.brandTagline
            address = p.address
            phone = p.phone
            receiptHeader = p.receiptHeader
            receiptFooter = p.receiptFooter
            taxText = p.defaultTaxPercent.toString()
            serviceText = p.defaultServicePercent.toString()
            printerWidth = p.paperWidthMm
        }
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Photo picker for Restaurant Logo
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isUploadingLogo = true
                val oldLogoPath = profile?.logoPath
                val newPath = ImageStorageManager.saveLogoImageFromUri(context, uri)
                if (newPath != null) {
                    if (!oldLogoPath.isNullOrBlank() && oldLogoPath != newPath) {
                        ImageStorageManager.deleteImageFile(oldLogoPath)
                    }
                    viewModel.updateLogo(newPath)
                } else {
                    snackbarHostState.showSnackbar("Gagal memproses gambar logo.")
                }
                isUploadingLogo = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Pengaturan Restoran",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite
                        )
                        Text(
                            text = "Profil, Logo Resto, Struk Kasir & Printer Thermal",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = TokokuhWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Dashboard",
                            tint = TokokuhWhite
                        )
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = { showReceiptPreviewDialog = true },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("preview_receipt_top_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = TokokuhWhite.copy(alpha = 0.15f),
                            contentColor = TokokuhWhite
                        ),
                        border = BorderStroke(1.dp, TokokuhWhite.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TokokuhAccentYellow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Preview Struk", color = TokokuhWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
            // ================= 1. LOGO RESTORAN SECTION =================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, TokokuhBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = TokokuhMaroonPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Logo Restoran",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TokokuhTextPrimary
                            )
                        }

                        Text(
                            text = "Logo ini akan ditampilkan di bagian paling atas struk kasir (Preview, PDF, dan Cetak Thermal). Gambar disimpan secara lokal dan aman di perangkat.",
                            fontSize = 12.sp,
                            color = TokokuhTextSecondary,
                            lineHeight = 17.sp
                        )

                        // Logo Preview Area
                        val currentLogoPath = profile?.logoPath
                        val logoBitmap = remember(currentLogoPath) {
                            if (!currentLogoPath.isNullOrBlank()) {
                                val file = File(currentLogoPath)
                                if (file.exists()) {
                                    try {
                                        BitmapFactory.decodeFile(file.absolutePath)
                                    } catch (e: Exception) {
                                        null
                                    }
                                } else null
                            } else null
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(TokokuhSurface)
                                .border(1.dp, TokokuhBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploadingLogo) {
                                CircularProgressIndicator(color = TokokuhMaroonPrimary)
                            } else if (logoBitmap != null) {
                                Image(
                                    bitmap = logoBitmap.asImageBitmap(),
                                    contentDescription = "Logo Restoran Terpasang",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(12.dp)
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Store,
                                        contentDescription = null,
                                        tint = TokokuhTextSecondary,
                                        modifier = Modifier.size(42.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Belum Ada Logo Restoran Khusus",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = TokokuhTextPrimary
                                    )
                                    Text(
                                        text = "Menggunakan identitas resmi TOKOKUH RESTO",
                                        fontSize = 11.sp,
                                        color = TokokuhTextSecondary
                                    )
                                }
                            }
                        }

                        // Logo Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (currentLogoPath.isNullOrBlank()) {
                                TokokuhPrimaryButton(
                                    text = "Upload Logo Restoran",
                                    icon = Icons.Default.AddPhotoAlternate,
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    testTag = "upload_logo_button"
                                )
                            } else {
                                TokokuhPrimaryButton(
                                    text = "Ganti Logo",
                                    icon = Icons.Default.Edit,
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    testTag = "change_logo_button"
                                )

                                TokokuhDangerButton(
                                    text = "Hapus Logo",
                                    icon = Icons.Default.Delete,
                                    onClick = {
                                        coroutineScope.launch {
                                            ImageStorageManager.deleteImageFile(currentLogoPath)
                                            viewModel.updateLogo(null)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    testTag = "delete_logo_button"
                                )
                            }
                        }
                    }
                }
            }

            // ================= 2. PROFIL RESTORAN =================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, TokokuhBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = TokokuhMaroonPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Profil Restoran", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TokokuhTextPrimary)
                        }

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nama Restoran") },
                            placeholder = { Text("Contoh: TOKOKUH RESTO NUSANTARA") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("setting_resto_name"),
                            singleLine = true,
                            colors = tokokuhTextFieldColors()
                        )

                        OutlinedTextField(
                            value = slogan,
                            onValueChange = { slogan = it },
                            label = { Text("Slogan / Sub-judul") },
                            placeholder = { Text("Contoh: Cita Rasa Autentik Nusantara") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = tokokuhTextFieldColors()
                        )

                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Alamat Restoran") },
                            placeholder = { Text("Contoh: Jl. Diponegoro No. 88, Jakarta") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            colors = tokokuhTextFieldColors()
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Nomor Telepon / WhatsApp") },
                            placeholder = { Text("Contoh: 0812-3456-7890") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = tokokuhTextFieldColors()
                        )
                    }
                }
            }

            // ================= 3. STRUK KASIR SECTION =================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, TokokuhBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = TokokuhMaroonPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kustomisasi Teks Struk Kasir", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TokokuhTextPrimary)
                        }

                        OutlinedTextField(
                            value = receiptHeader,
                            onValueChange = { receiptHeader = it },
                            label = { Text("Header Struk (Teks Sapaan Atas)") },
                            placeholder = { Text("Contoh: Selamat Menikmati Hidangan Kami") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = tokokuhTextFieldColors()
                        )

                        OutlinedTextField(
                            value = receiptFooter,
                            onValueChange = { receiptFooter = it },
                            label = { Text("Footer Struk (Ucapan Terima Kasih Bawah)") },
                            placeholder = { Text("Contoh: Terima kasih atas kunjungan Anda! Datang kembali ya.") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            colors = tokokuhTextFieldColors()
                        )
                    }
                }
            }

            // ================= 4. PAJAK & LAYANAN =================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, TokokuhBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Percent, contentDescription = null, tint = TokokuhMaroonPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pajak & Layanan Resto", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TokokuhTextPrimary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = taxText,
                                onValueChange = { taxText = it },
                                label = { Text("Pajak Resto (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = tokokuhTextFieldColors()
                            )

                            OutlinedTextField(
                                value = serviceText,
                                onValueChange = { serviceText = it },
                                label = { Text("Biaya Layanan (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = tokokuhTextFieldColors()
                            )
                        }
                    }
                }
            }

            // ================= 5. PRINTER THERMAL =================
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, TokokuhBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = TokokuhMaroonPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Konfigurasi Printer Thermal", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TokokuhTextPrimary)
                        }

                        Text("Format Lebar Kertas Struk:", color = TokokuhTextSecondary, fontSize = 12.sp)

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FilterChip(
                                selected = printerWidth == 58,
                                onClick = { printerWidth = 58 },
                                label = {
                                    Text(
                                        "58 mm (Portabel / Bluetooth)",
                                        color = if (printerWidth == 58) TokokuhWhite else TokokuhTextPrimary
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TokokuhMaroonPrimary,
                                    selectedLabelColor = TokokuhWhite,
                                    containerColor = TokokuhSurface,
                                    labelColor = TokokuhTextPrimary
                                )
                            )

                            FilterChip(
                                selected = printerWidth == 80,
                                onClick = { printerWidth = 80 },
                                label = {
                                    Text(
                                        "80 mm (Desktop / USB)",
                                        color = if (printerWidth == 80) TokokuhWhite else TokokuhTextPrimary
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
            }

            // ================= 6. PREVIEW & SAVE BUTTONS =================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    TokokuhSecondaryButton(
                        text = "Lihat Preview Struk Kasir",
                        icon = Icons.Default.ReceiptLong,
                        onClick = { showReceiptPreviewDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "preview_receipt_bottom_btn"
                    )

                    TokokuhPrimaryButton(
                        text = "Simpan Semua Pengaturan",
                        icon = Icons.Default.Save,
                        onClick = {
                            val tax = taxText.toDoubleOrNull() ?: 10.0
                            val service = serviceText.toDoubleOrNull() ?: 5.0
                            viewModel.updateProfile(
                                name = name,
                                slogan = slogan,
                                address = address,
                                phone = phone,
                                receiptHeader = receiptHeader,
                                receiptFooter = receiptFooter,
                                taxPercent = tax,
                                servicePercent = service,
                                printerWidthMm = printerWidth
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "save_settings_button"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ================= RECEIPT PREVIEW DIALOG =================
    if (showReceiptPreviewDialog) {
        // Preview reflects the current form values, including unsaved edits.
        // It uses a sample transaction only for layout preview; no sample data is saved.
        val currentProfile = (profile ?: RestaurantProfileEntity()).copy(
            name = name,
            brandTagline = slogan,
            address = address,
            phone = phone,
            receiptHeader = receiptHeader,
            receiptFooter = receiptFooter,
            defaultTaxPercent = taxText.toDoubleOrNull() ?: 0.0,
            defaultServicePercent = serviceText.toDoubleOrNull() ?: 0.0,
            paperWidthMm = printerWidth
        )

        // Mock sample transaction to test full receipt layout
        val sampleOrder = OrderEntity(
            id = 9999L,
            orderNumber = "TRX-DEMO-001",
            tableId = 1L,
            tableName = "Meja 05",
            orderType = OrderType.DINE_IN,
            status = OrderStatus.COMPLETED,
            paymentStatus = PaymentStatus.PAID,
            paymentMethod = PaymentMethod.CASH,
            cashierName = "Kasir Resto",
            customerName = "Pelanggan Demo",
            subtotal = 60000L,
            discountAmount = 0L,
            taxPercent = currentProfile.defaultTaxPercent,
            taxAmount = (60000L * currentProfile.defaultTaxPercent / 100).toLong(),
            servicePercent = currentProfile.defaultServicePercent,
            serviceAmount = (60000L * currentProfile.defaultServicePercent / 100).toLong(),
            totalAmount = 60000L + (60000L * (currentProfile.defaultTaxPercent + currentProfile.defaultServicePercent) / 100).toLong(),
            paidAmount = 100000L,
            changeAmount = 100000L - (60000L + (60000L * (currentProfile.defaultTaxPercent + currentProfile.defaultServicePercent) / 100).toLong()),
            createdAt = System.currentTimeMillis()
        )

        val sampleItems = listOf(
            OrderItemEntity(
                id = 1L,
                orderId = 9999L,
                menuId = 1L,
                menuName = "Nasi Goreng Spesial Resto",
                quantity = 2,
                unitPrice = 25000L,
                subtotal = 50000L,
                note = "Pedas sedang, telur mata sapi"
            ),
            OrderItemEntity(
                id = 2L,
                orderId = 9999L,
                menuId = 2L,
                menuName = "Es Teh Manis Segar",
                quantity = 2,
                unitPrice = 5000L,
                subtotal = 10000L
            )
        )

        AlertDialog(
            onDismissRequest = { showReceiptPreviewDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Preview Struk Kasir",
                        fontWeight = FontWeight.Bold,
                        color = TokokuhMaroonDark,
                        fontSize = 16.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = TokokuhMaroonDark
                    ) {
                        Text(
                            "${currentProfile.paperWidthMm} mm",
                            color = TokokuhWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            },
            text = {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    border = BorderStroke(1.dp, TokokuhBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Custom restaurant logo or brand badge at VERY TOP
                        ReceiptHeaderView(profile = currentProfile)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = TokokuhBorder)

                        // Meta details
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("No. Struk:", fontSize = 11.sp, color = TokokuhTextSecondary)
                            Text(sampleOrder.orderNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TokokuhTextPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Waktu:", fontSize = 11.sp, color = TokokuhTextSecondary)
                            Text(CurrencyFormatter.formatDateTime(sampleOrder.createdAt), fontSize = 11.sp, color = TokokuhTextPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tipe / Meja:", fontSize = 11.sp, color = TokokuhTextSecondary)
                            Text("${sampleOrder.orderType.displayName} ${sampleOrder.tableName}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TokokuhTextPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kasir:", fontSize = 11.sp, color = TokokuhTextSecondary)
                            Text(sampleOrder.cashierName, fontSize = 11.sp, color = TokokuhTextPrimary)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = TokokuhBorder)

                        // Item list
                        sampleItems.forEach { itm ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(itm.menuName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TokokuhTextPrimary)
                                    Text("${itm.quantity}x @ ${CurrencyFormatter.formatRupiah(itm.unitPrice)}", fontSize = 10.sp, color = TokokuhTextSecondary)
                                    if (!itm.note.isNullOrBlank()) {
                                        Text("  * ${itm.note}", fontSize = 10.sp, color = TokokuhTextSecondary)
                                    }
                                }
                                Text(CurrencyFormatter.formatRupiah(itm.subtotal), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TokokuhTextPrimary)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = TokokuhBorder)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", fontSize = 11.sp, color = TokokuhTextPrimary)
                            Text(CurrencyFormatter.formatRupiah(sampleOrder.subtotal), fontSize = 11.sp, color = TokokuhTextPrimary)
                        }
                        if (sampleOrder.taxAmount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pajak Restoran (${sampleOrder.taxPercent.toInt()}%)", fontSize = 11.sp, color = TokokuhTextPrimary)
                                Text(CurrencyFormatter.formatRupiah(sampleOrder.taxAmount), fontSize = 11.sp, color = TokokuhTextPrimary)
                            }
                        }
                        if (sampleOrder.serviceAmount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Biaya Layanan (${sampleOrder.servicePercent.toInt()}%)", fontSize = 11.sp, color = TokokuhTextPrimary)
                                Text(CurrencyFormatter.formatRupiah(sampleOrder.serviceAmount), fontSize = 11.sp, color = TokokuhTextPrimary)
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("TOTAL BAYAR", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = TokokuhMaroonPrimary)
                            Text(CurrencyFormatter.formatRupiah(sampleOrder.totalAmount), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TokokuhMaroonPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Bayar (${sampleOrder.paymentMethod.displayName})", fontSize = 11.sp, color = TokokuhTextPrimary)
                            Text(CurrencyFormatter.formatRupiah(sampleOrder.paidAmount), fontSize = 11.sp, color = TokokuhTextPrimary)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Kembalian", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TokokuhSuccess)
                            Text(CurrencyFormatter.formatRupiah(sampleOrder.changeAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TokokuhSuccess)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = currentProfile.receiptFooter.ifBlank { "Terima kasih atas kunjungan Anda!" },
                            fontSize = 10.sp,
                            color = TokokuhTextSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.logo_credit_struk),
                            contentDescription = "TOKOKUH.COM RESTO",
                            modifier = Modifier
                                .height(24.dp)
                                .fillMaxWidth(0.6f)
                        )
                    }
                }
            },
            confirmButton = {
                TokokuhPrimaryButton(
                    text = "Tutup Preview",
                    onClick = { showReceiptPreviewDialog = false }
                )
            },
            dismissButton = {
                TokokuhSecondaryButton(
                    text = "Cetak / Bagikan PDF",
                    icon = Icons.Default.Share,
                    onClick = {
                        coroutineScope.launch {
                            val pdf = ReceiptPdfGenerator.generateReceiptPdf(
                                context = context,
                                profile = currentProfile,
                                order = sampleOrder,
                                items = sampleItems
                            )
                            if (pdf != null) {
                                ReceiptPdfGenerator.shareReceiptPdf(context, pdf)
                            } else {
                                snackbarHostState.showSnackbar("Gagal membuat berkas PDF struk.")
                            }
                        }
                    }
                )
            }
        )
    }
}
