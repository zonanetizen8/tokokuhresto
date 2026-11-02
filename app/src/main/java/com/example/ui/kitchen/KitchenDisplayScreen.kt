package com.example.ui.kitchen

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.OutdoorGrill
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderWithItems
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
import com.example.ui.theme.TokokuhWarning
import com.example.ui.theme.TokokuhWarningBg
import com.example.ui.theme.TokokuhWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenDisplayScreen(
    viewModel: KitchenViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val kitchenOrders by viewModel.kitchenOrders.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.statusFilter.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = TokokuhAccentYellow, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Kitchen Display (KDS Dapur)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("kds_back_btn")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali ke Dashboard", tint = TokokuhWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TokokuhMaroonPrimary)
            )
        },
        containerColor = TokokuhBackground
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filter Tabs
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { viewModel.setFilter(null) },
                        label = { Text("Semua Antrean (${kitchenOrders.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                }
                items(listOf(OrderStatus.PENDING, OrderStatus.COOKING, OrderStatus.READY)) { status ->
                    FilterChip(
                        selected = selectedFilter == status,
                        onClick = { viewModel.setFilter(status) },
                        label = { Text(status.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TokokuhMaroonPrimary,
                            selectedLabelColor = TokokuhWhite
                        )
                    )
                }
            }

            // Orders Queue List
            if (kitchenOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = TokokuhSuccess, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Semua Pesanan Dapur Selesai!", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TokokuhTextPrimary)
                        Text("Pesanan baru dari kasir akan muncul di sini secara real-time.", fontSize = 12.sp, color = TokokuhTextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(kitchenOrders, key = { it.order.id }) { item ->
                        KitchenOrderCard(
                            orderWithItems = item,
                            onStartCooking = { viewModel.updateOrderStatus(item.order.id, OrderStatus.COOKING) },
                            onMarkReady = { viewModel.updateOrderStatus(item.order.id, OrderStatus.READY) },
                            onMarkServed = { viewModel.updateOrderStatus(item.order.id, OrderStatus.COMPLETED) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun KitchenOrderCard(
    orderWithItems: OrderWithItems,
    onStartCooking: () -> Unit,
    onMarkReady: () -> Unit,
    onMarkServed: () -> Unit
) {
    val order = orderWithItems.order
    val items = orderWithItems.items

    val minutesAgo = ((System.currentTimeMillis() - order.createdAt) / (1000 * 60)).coerceAtLeast(0)

    val cardBorderColor = when (order.status) {
        OrderStatus.PENDING -> TokokuhError
        OrderStatus.COOKING -> TokokuhWarning
        OrderStatus.READY -> TokokuhSuccess
        else -> TokokuhBorder
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, cardBorderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Order Number, Table / Take Away, Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.orderNumber, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = TokokuhBlack)
                    Text(
                        "${order.orderType.displayName} ${order.tableName?.let { "• $it" } ?: ""}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhMaroonDark
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (minutesAgo > 15) TokokuhErrorBg else TokokuhSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = if (minutesAgo > 15) TokokuhError else TokokuhTextSecondary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "$minutesAgo menit lalu",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (minutesAgo > 15) TokokuhError else TokokuhTextPrimary
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = TokokuhBorder)

            // Items List
            items.forEach { itm ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = TokokuhMaroonPrimary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("${itm.quantity}x", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TokokuhWhite)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(itm.menuName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TokokuhTextPrimary)
                            if (itm.note.isNotBlank()) {
                                Text("Catatan: ${itm.note}", fontSize = 11.sp, color = TokokuhError, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons based on status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (order.status) {
                    OrderStatus.PENDING -> {
                        Button(
                            onClick = onStartCooking,
                            colors = ButtonDefaults.buttonColors(containerColor = TokokuhWarning)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.OutdoorGrill, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mulai Masak", fontWeight = FontWeight.Bold, color = TokokuhBlack)
                            }
                        }
                    }
                    OrderStatus.COOKING -> {
                        Button(
                            onClick = onMarkReady,
                            colors = ButtonDefaults.buttonColors(containerColor = TokokuhSuccess)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Siap Disajikan", fontWeight = FontWeight.Bold, color = TokokuhWhite)
                            }
                        }
                    }
                    OrderStatus.READY -> {
                        Button(
                            onClick = onMarkServed,
                            colors = ButtonDefaults.buttonColors(containerColor = TokokuhMaroonPrimary)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Selesai Disajikan", fontWeight = FontWeight.Bold, color = TokokuhWhite)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
