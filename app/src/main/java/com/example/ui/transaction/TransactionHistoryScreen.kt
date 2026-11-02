package com.example.ui.transaction

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderType
import com.example.data.local.entities.OrderWithItems
import com.example.data.local.entities.PaymentStatus
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
fun TransactionHistoryScreen(
    viewModel: TransactionViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val selectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var orderToCancel by remember { mutableStateOf<OrderWithItems?>(null) }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedback()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Riwayat Transaksi",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite
                        )
                        Text(
                            text = "Seluruh Pesanan & Struk Penjualan Offline",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = TokokuhWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("trx_back_button")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search and filter area
            Surface(
                color = TokokuhWhite,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Cari no. struk, nama pelanggan, kasir...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TokokuhTextSecondary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = TokokuhTextSecondary)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("trx_search_field"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TokokuhMaroonPrimary,
                            unfocusedBorderColor = TokokuhBorder
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = filter == TransactionFilter.ALL,
                                onClick = { viewModel.setFilter(TransactionFilter.ALL) },
                                label = { Text("Semua Transaksi") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TokokuhMaroonPrimary,
                                    selectedLabelColor = TokokuhWhite
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = filter == TransactionFilter.PAID,
                                onClick = { viewModel.setFilter(TransactionFilter.PAID) },
                                label = { Text("Selesai (Dibayar)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TokokuhMaroonPrimary,
                                    selectedLabelColor = TokokuhWhite
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = filter == TransactionFilter.CANCELLED,
                                onClick = { viewModel.setFilter(TransactionFilter.CANCELLED) },
                                label = { Text("Dibatalkan") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = TokokuhMaroonPrimary,
                                    selectedLabelColor = TokokuhWhite
                                )
                            )
                        }
                    }
                }
            }

            // List of transactions
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = TokokuhTextSecondary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tidak Ada Data Transaksi",
                            fontWeight = FontWeight.Bold,
                            color = TokokuhTextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Belum ada transaksi yang sesuai dengan filter pencarian.",
                            color = TokokuhTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(transactions, key = { it.order.id }) { item ->
                        TransactionCard(
                            orderWithItems = item,
                            onClick = { viewModel.selectOrder(item) }
                        )
                    }
                }
            }
        }
    }

    // Detail Receipt Dialog
    selectedOrder?.let { orderWithItems ->
        TransactionDetailDialog(
            orderWithItems = orderWithItems,
            onDismiss = { viewModel.selectOrder(null) },
            onCancelOrder = { orderToCancel = orderWithItems }
        )
    }

    // Cancel Confirmation Dialog
    orderToCancel?.let { orderWithItems ->
        var reasonText by remember { mutableStateOf("Salah input pesanan pelanggan") }
        AlertDialog(
            onDismissRequest = { orderToCancel = null },
            title = {
                Text(
                    "Batalkan Transaksi ${orderWithItems.order.orderNumber}?",
                    fontWeight = FontWeight.Bold,
                    color = TokokuhError
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Transaksi yang dibatalkan akan ditandai CANCELLED dan meja terkait akan dikosongkan.",
                        fontSize = 13.sp,
                        color = TokokuhTextPrimary
                    )
                    OutlinedTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = { Text("Alasan Pembatalan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cancelOrder(orderWithItems.order.id, reasonText)
                        orderToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TokokuhError)
                ) {
                    Text("Batalkan Transaksi")
                }
            },
            dismissButton = {
                TextButton(onClick = { orderToCancel = null }) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
private fun TransactionCard(
    orderWithItems: OrderWithItems,
    onClick: () -> Unit
) {
    val order = orderWithItems.order
    val isCancelled = order.status == OrderStatus.CANCELLED
    val isPaid = order.paymentStatus == PaymentStatus.PAID

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("trx_item_${order.id}"),
        colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhBorder))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.orderNumber,
                    fontWeight = FontWeight.Bold,
                    color = TokokuhMaroonPrimary,
                    fontSize = 14.sp
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when {
                        isCancelled -> TokokuhErrorBg
                        isPaid -> TokokuhSuccessBg
                        else -> TokokuhWarningBg
                    }
                ) {
                    Text(
                        text = when {
                            isCancelled -> "DIBATALKAN"
                            isPaid -> "SELESAI"
                            else -> "BELUM BAYAR"
                        },
                        color = when {
                            isCancelled -> TokokuhError
                            isPaid -> TokokuhSuccess
                            else -> TokokuhWarning
                        },
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${order.tableName ?: "Take Away"} • ${order.customerName}",
                    color = TokokuhTextPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
                Text(
                    text = CurrencyFormatter.formatDateTime(order.createdAt),
                    color = TokokuhTextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = TokokuhSurface
                ) {
                    Text(
                        text = "${orderWithItems.items.size} Menu • Met: ${order.paymentMethod.name}",
                        color = TokokuhTextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = CurrencyFormatter.formatRupiah(order.totalAmount),
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isCancelled) TokokuhTextSecondary else TokokuhMaroonPrimary,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
private fun TransactionDetailDialog(
    orderWithItems: OrderWithItems,
    onDismiss: () -> Unit,
    onCancelOrder: () -> Unit
) {
    val order = orderWithItems.order
    val isCancelled = order.status == OrderStatus.CANCELLED

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = TokokuhWhite,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Official logo container on deep maroon
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TokokuhMaroonPrimary, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_tokokuh_resto),
                        contentDescription = "TOKOKUH RESTO",
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(34.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "BUKTI TRANSAKSI DIGITAL",
                    fontWeight = FontWeight.Bold,
                    color = TokokuhTextPrimary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "100% Offline POS System",
                    color = TokokuhTextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                ReceiptRow(label = "No. Transaksi:", value = order.orderNumber)
                ReceiptRow(label = "Tanggal & Jam:", value = CurrencyFormatter.formatDateTime(order.createdAt))
                ReceiptRow(label = "Tipe Layanan:", value = if (order.orderType == OrderType.DINE_IN) "Dine In (${order.tableName ?: "Meja"})" else "Take Away")
                ReceiptRow(label = "Pelanggan:", value = order.customerName)
                ReceiptRow(label = "Kasir:", value = order.cashierName ?: "Kasir")
                ReceiptRow(label = "Metode Bayar:", value = order.paymentMethod.name)

                Spacer(modifier = Modifier.height(10.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                // Item list
                orderWithItems.items.forEach { item ->
                    Column(modifier = Modifier.padding(vertical = 3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.menuName}",
                                fontWeight = FontWeight.SemiBold,
                                color = TokokuhTextPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = CurrencyFormatter.formatRupiah(item.subtotal),
                                fontWeight = FontWeight.Bold,
                                color = TokokuhTextPrimary,
                                fontSize = 12.sp
                            )
                        }
                        if (!item.note.isNullOrBlank()) {
                            Text(
                                text = "  Catatan: ${item.note}",
                                color = TokokuhTextSecondary,
                                fontSize = 11.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                DashedDivider()
                Spacer(modifier = Modifier.height(10.dp))

                ReceiptRow(label = "Subtotal:", value = CurrencyFormatter.formatRupiah(order.subtotal))
                if (order.discountAmount > 0) {
                    ReceiptRow(label = "Diskon:", value = "-${CurrencyFormatter.formatRupiah(order.discountAmount)}")
                }
                if (order.taxAmount > 0) {
                    ReceiptRow(label = "Pajak Restoran:", value = CurrencyFormatter.formatRupiah(order.taxAmount))
                }
                if (order.serviceAmount > 0) {
                    ReceiptRow(label = "Biaya Layanan:", value = CurrencyFormatter.formatRupiah(order.serviceAmount))
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TOTAL BAYAR:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TokokuhMaroonPrimary)
                    Text(CurrencyFormatter.formatRupiah(order.totalAmount), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TokokuhMaroonPrimary)
                }

                if (order.paidAmount > 0) {
                    ReceiptRow(label = "Uang Diterima:", value = CurrencyFormatter.formatRupiah(order.paidAmount))
                    ReceiptRow(label = "Kembalian:", value = CurrencyFormatter.formatRupiah(order.changeAmount))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom credit logo
                Image(
                    painter = painterResource(id = R.drawable.logo_credit_struk),
                    contentDescription = "TOKOKUH.COM Credit",
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(26.dp)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Tutup", color = TokokuhTextPrimary)
                    }

                    if (!isCancelled) {
                        Button(
                            onClick = onCancelOrder,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = TokokuhError),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Batalkan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TokokuhTextSecondary, fontSize = 12.sp)
        Text(text = value, fontWeight = FontWeight.Medium, color = TokokuhTextPrimary, fontSize = 12.sp)
    }
}

@Composable
private fun DashedDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(TokokuhBorder)
    )
}
