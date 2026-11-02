package com.example.ui.cashier

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.ui.components.ReceiptHeaderView
import com.example.ui.components.TokokuhEmptyState
import com.example.ui.components.TokokuhPrimaryButton
import com.example.ui.components.TokokuhSecondaryButton
import com.example.ui.components.tokokuhTextFieldColors
import com.example.data.util.ReceiptPdfGenerator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.R
import com.example.data.local.entities.MenuEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderType
import com.example.data.local.entities.PaymentMethod
import com.example.data.local.entities.RestaurantProfileEntity
import com.example.data.local.entities.TableEntity
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
fun CashierScreen(
    viewModel: CashierViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val menus by viewModel.availableMenus.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val tables by viewModel.tables.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryId.collectAsStateWithLifecycle()

    var showPaymentModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Kasir (POS)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite
                        )
                        Text(
                            "${uiState.cartItems.sumOf { it.quantity }} item di keranjang",
                            fontSize = 11.sp,
                            color = TokokuhWhite.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("cashier_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Dashboard",
                            tint = TokokuhWhite
                        )
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clearCart() }) {
                            Text("Kosongkan", color = TokokuhAccentYellow, fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TokokuhMaroonPrimary
                )
            )
        },
        containerColor = TokokuhBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search & Category Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Cari produk...", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TokokuhMaroonPrimary, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Hapus", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = tokokuhTextFieldColors(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Category Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.onCategorySelect(null) },
                        label = { Text("Semua", fontSize = 12.sp) },
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
                        label = { Text(cat.name, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                }
            }

            // Products Grid
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                if (menus.isEmpty()) {
                    TokokuhEmptyState(
                        icon = Icons.Default.Fastfood,
                        title = "Menu Belum Tersedia",
                        message = "Daftarkan menu makanan dan minuman pada menu Kelola Menu untuk memulai transaksi kasir."
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(menus, key = { it.id }) { menu ->
                            val inCart = uiState.cartItems.firstOrNull { it.menu.id == menu.id }
                            ProductCashierCard(
                                menu = menu,
                                cartQty = inCart?.quantity ?: 0,
                                onClick = { viewModel.addToCart(menu) }
                            )
                        }
                    }
                }
            }

            // Bottom Cart Summary Bar
            CartBottomBar(
                cartItems = uiState.cartItems,
                onCheckoutClick = { showPaymentModal = true }
            )
        }
    }

    // Payment Modal Dialog
    if (showPaymentModal) {
        CashPaymentDialog(
            cartItems = uiState.cartItems,
            orderType = uiState.orderType,
            selectedTable = uiState.selectedTable,
            tables = tables,
            profile = profile,
            isCheckingOut = uiState.isCheckingOut,
            onOrderTypeChange = { viewModel.setOrderType(it) },
            onTableChange = { viewModel.setSelectedTable(it) },
            onIncrementItem = { viewModel.incrementQty(it) },
            onDecrementItem = { viewModel.decrementQty(it) },
            onRemoveItem = { viewModel.removeFromCart(it) },
            onConfirmPayment = { method, paid ->
                viewModel.checkout(method, paid)
                showPaymentModal = false
            },
            onDismiss = { showPaymentModal = false }
        )
    }

    // Receipt Modal Dialog
    if (uiState.showReceiptDialog && uiState.lastCompletedOrder != null) {
        ReceiptPreviewDialog(
            order = uiState.lastCompletedOrder!!,
            items = uiState.lastCompletedItems,
            profile = profile,
            onDismiss = { viewModel.dismissReceipt() }
        )
    }
}

@Composable
private fun ProductCashierCard(
    menu: MenuEntity,
    cartQty: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
        border = androidx.compose.foundation.BorderStroke(
            if (cartQty > 0) 2.dp else 1.dp,
            if (cartQty > 0) TokokuhMaroonPrimary else TokokuhBorder
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(TokokuhWhite),
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
                    Icon(
                        Icons.Default.Fastfood,
                        contentDescription = null,
                        tint = TokokuhMaroonPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                if (cartQty > 0) {
                    Surface(
                        shape = CircleShape,
                        color = TokokuhMaroonPrimary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$cartQty",
                                color = TokokuhWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = menu.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TokokuhTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = CurrencyFormatter.formatRupiah(menu.sellPrice),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TokokuhMaroonPrimary
                )
                Text(
                    text = "${menu.stock.toInt()} ${menu.unit}",
                    fontSize = 10.sp,
                    color = TokokuhTextSecondary
                )
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    cartItems: List<CartItem>,
    onCheckoutClick: () -> Unit
) {
    val totalQty = cartItems.sumOf { it.quantity }
    val subtotal = cartItems.sumOf { it.subtotal }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = TokokuhWhite,
        shadowElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$totalQty Item Dipilih",
                    fontSize = 12.sp,
                    color = TokokuhTextSecondary
                )
                Text(
                    text = CurrencyFormatter.formatRupiah(subtotal),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TokokuhMaroonPrimary
                )
            }

            Button(
                onClick = onCheckoutClick,
                enabled = cartItems.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TokokuhMaroonPrimary,
                    contentColor = TokokuhWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("checkout_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bayar (Checkout)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CashPaymentDialog(
    cartItems: List<CartItem>,
    orderType: OrderType,
    selectedTable: TableEntity?,
    tables: List<TableEntity>,
    profile: RestaurantProfileEntity?,
    isCheckingOut: Boolean,
    onOrderTypeChange: (OrderType) -> Unit,
    onTableChange: (TableEntity?) -> Unit,
    onIncrementItem: (Long) -> Unit,
    onDecrementItem: (Long) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onConfirmPayment: (PaymentMethod, Long) -> Unit,
    onDismiss: () -> Unit
) {
    val subtotal = cartItems.sumOf { it.subtotal }
    val taxRate = (profile?.defaultTaxPercent ?: 10.0) / 100.0
    val serviceRate = (profile?.defaultServicePercent ?: 5.0) / 100.0
    val taxAmount = (subtotal * taxRate).toLong()
    val serviceAmount = (subtotal * serviceRate).toLong()
    val grandTotal = subtotal + taxAmount + serviceAmount

    var selectedMethod by remember { mutableStateOf(PaymentMethod.CASH) }
    var cashPaidStr by remember { mutableStateOf(grandTotal.toString()) }
    var tableExpanded by remember { mutableStateOf(false) }

    val cashPaid = cashPaidStr.toLongOrNull() ?: 0L
    val change = if (cashPaid >= grandTotal) cashPaid - grandTotal else 0L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Konfirmasi & Pembayaran", fontWeight = FontWeight.Bold, color = TokokuhMaroonDark)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Order Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = orderType == OrderType.DINE_IN,
                        onClick = { onOrderTypeChange(OrderType.DINE_IN) },
                        label = { Text("Dine In (Makan di Tempat)") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                    FilterChip(
                        selected = orderType == OrderType.TAKE_AWAY,
                        onClick = { onOrderTypeChange(OrderType.TAKE_AWAY) },
                        label = { Text("Take Away (Bungkus)") },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                }

                // Table Selector if Dine In
                if (orderType == OrderType.DINE_IN) {
                    ExposedDropdownMenuBox(
                        expanded = tableExpanded,
                        onExpandedChange = { tableExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedTable?.tableNumber ?: "Pilih Meja",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Nomor Meja") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tableExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = tableExpanded,
                            onDismissRequest = { tableExpanded = false }
                        ) {
                            tables.forEach { tbl ->
                                DropdownMenuItem(
                                    text = { Text("${tbl.tableNumber} (${tbl.status.displayName})") },
                                    onClick = {
                                        onTableChange(tbl)
                                        tableExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Itemized summary in cart
                Text("Daftar Pesanan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TokokuhTextSecondary)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = TokokuhSurface)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        cartItems.forEach { ci ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(ci.menu.name, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("${ci.quantity}x @ ${CurrencyFormatter.formatRupiah(ci.menu.sellPrice)}", fontSize = 10.sp, color = TokokuhTextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { onDecrementItem(ci.menu.id) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                    Text("${ci.quantity}", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                                    IconButton(onClick = { onIncrementItem(ci.menu.id) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(CurrencyFormatter.formatRupiah(ci.subtotal), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TokokuhMaroonPrimary)
                                }
                            }
                        }
                    }
                }

                // Price Breakdown
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", fontSize = 12.sp, color = TokokuhTextSecondary)
                    Text(CurrencyFormatter.formatRupiah(subtotal), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pajak Resto (${profile?.defaultTaxPercent?.toInt() ?: 10}%)", fontSize = 12.sp, color = TokokuhTextSecondary)
                    Text(CurrencyFormatter.formatRupiah(taxAmount), fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Biaya Layanan (${profile?.defaultServicePercent?.toInt() ?: 5}%)", fontSize = 12.sp, color = TokokuhTextSecondary)
                    Text(CurrencyFormatter.formatRupiah(serviceAmount), fontSize = 12.sp)
                }
                HorizontalDivider(color = TokokuhBorder)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL BAYAR", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TokokuhBlack)
                    Text(CurrencyFormatter.formatRupiah(grandTotal), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TokokuhMaroonPrimary)
                }

                // Payment Method Tabs
                Text("Metode Pembayaran:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TokokuhTextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentMethod.values().forEach { method ->
                        FilterChip(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            label = { Text(method.displayName, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TokokuhMaroonPrimary,
                                selectedLabelColor = TokokuhWhite
                            )
                        )
                    }
                }

                if (selectedMethod == PaymentMethod.CASH) {
                    OutlinedTextField(
                        value = cashPaidStr,
                        onValueChange = { cashPaidStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Uang Tunai Diterima (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = tokokuhTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick Cash Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(grandTotal, 20000L, 50000L, 100000L).forEach { amt ->
                            if (amt >= grandTotal) {
                                Surface(
                                    onClick = { cashPaidStr = amt.toString() },
                                    shape = RoundedCornerShape(8.dp),
                                    color = TokokuhSurface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Text(
                                        if (amt == grandTotal) "Uang Pas" else CurrencyFormatter.formatRupiah(amt),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Change calculation display
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(TokokuhSuccessBg, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Kembalian:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TokokuhSuccess)
                        Text(CurrencyFormatter.formatRupiah(change), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TokokuhSuccess)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmPayment(selectedMethod, cashPaid)
                },
                enabled = !isCheckingOut && (selectedMethod != PaymentMethod.CASH || cashPaid >= grandTotal),
                colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary)
            ) {
                if (isCheckingOut) {
                    CircularProgressIndicator(color = TokokuhWhite, modifier = Modifier.size(18.dp))
                } else {
                    Text("Selesaikan Transaksi", color = TokokuhWhite, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

@Composable
private fun ReceiptPreviewDialog(
    order: OrderEntity,
    items: List<OrderItemEntity>,
    profile: RestaurantProfileEntity?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Struk Transaksi", fontWeight = FontWeight.Bold, color = TokokuhMaroonDark)
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = TokokuhSuccessBg
                ) {
                    Text("LUNAS", color = TokokuhSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
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
                border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Receipt Header with custom restaurant logo at VERY TOP
                    ReceiptHeaderView(profile = profile)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = TokokuhBorder)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("No. Struk:", fontSize = 11.sp, color = TokokuhTextSecondary)
                        Text(order.orderNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Waktu:", fontSize = 11.sp, color = TokokuhTextSecondary)
                        Text(CurrencyFormatter.formatDateTime(order.createdAt), fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tipe / Meja:", fontSize = 11.sp, color = TokokuhTextSecondary)
                        Text("${order.orderType.displayName} ${order.tableName ?: ""}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kasir:", fontSize = 11.sp, color = TokokuhTextSecondary)
                        Text(order.cashierName, fontSize = 11.sp)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = TokokuhBorder)

                    // Items list
                    items.forEach { itm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(itm.menuName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("${itm.quantity}x @ ${CurrencyFormatter.formatRupiah(itm.unitPrice)}", fontSize = 10.sp, color = TokokuhTextSecondary)
                            }
                            Text(CurrencyFormatter.formatRupiah(itm.subtotal), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = TokokuhBorder)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", fontSize = 11.sp)
                        Text(CurrencyFormatter.formatRupiah(order.subtotal), fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pajak (${order.taxPercent.toInt()}%)", fontSize = 11.sp)
                        Text(CurrencyFormatter.formatRupiah(order.taxAmount), fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Layanan (${order.servicePercent.toInt()}%)", fontSize = 11.sp)
                        Text(CurrencyFormatter.formatRupiah(order.serviceAmount), fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        Text(CurrencyFormatter.formatRupiah(order.totalAmount), fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TokokuhMaroonPrimary)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Bayar (${order.paymentMethod.displayName})", fontSize = 11.sp)
                        Text(CurrencyFormatter.formatRupiah(order.paidAmount), fontSize = 11.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kembalian", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TokokuhSuccess)
                        Text(CurrencyFormatter.formatRupiah(order.changeAmount), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TokokuhSuccess)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(profile?.receiptFooter ?: "Terima Kasih Atas Kunjungan Anda!", fontSize = 10.sp, color = TokokuhTextSecondary, textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo_credit_struk),
                        contentDescription = "TOKOKUH.COM RESTO",
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary, contentColor = TokokuhWhite)
            ) {
                Text("Transaksi Baru", color = TokokuhWhite, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            OutlinedButton(
                onClick = {
                    coroutineScope.launch {
                        val pdf = ReceiptPdfGenerator.generateReceiptPdf(
                            context = context,
                            profile = profile,
                            order = order,
                            items = items
                        )
                        if (pdf != null) {
                            ReceiptPdfGenerator.shareReceiptPdf(context, pdf)
                        }
                    }
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cetak / PDF Struk")
                }
            }
        }
    )
}
