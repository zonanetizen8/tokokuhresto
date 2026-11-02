package com.example.ui.report

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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entities.PaymentMethod
import com.example.ui.theme.TokokuhAccentYellow
import com.example.ui.theme.TokokuhBackground
import com.example.ui.theme.TokokuhBorder
import com.example.ui.theme.TokokuhMaroonDark
import com.example.ui.theme.TokokuhMaroonPrimary
import com.example.ui.theme.TokokuhSuccess
import com.example.ui.theme.TokokuhSurface
import com.example.ui.theme.TokokuhTextPrimary
import com.example.ui.theme.TokokuhTextSecondary
import com.example.ui.theme.TokokuhWhite
import com.example.data.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesReportScreen(
    viewModel: ReportViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val analytics by viewModel.analytics.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Laporan Penjualan",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhWhite
                        )
                        Text(
                            text = "Analitik Pendapatan & Menu Terlaris Offline",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = TokokuhWhite.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("report_back_button")
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
        containerColor = TokokuhBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Period selector
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ReportPeriod.values()) { period ->
                        FilterChip(
                            selected = selectedPeriod == period,
                            onClick = { viewModel.setPeriod(period) },
                            label = { Text(period.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TokokuhMaroonPrimary,
                                selectedLabelColor = TokokuhWhite
                            )
                        )
                    }
                }
            }

            // Hero Revenue Card
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
                                text = "TOTAL OMZET (${selectedPeriod.label.uppercase()})",
                                color = TokokuhAccentYellow,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = TokokuhAccentYellow,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = CurrencyFormatter.formatRupiah(analytics.totalRevenue),
                            color = TokokuhWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Transaksi", color = TokokuhWhite.copy(alpha = 0.75f), fontSize = 11.sp)
                                Text(
                                    "${analytics.totalTransactions} pesanan",
                                    color = TokokuhWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Column {
                                Text("Rata-rata Transaksi", color = TokokuhWhite.copy(alpha = 0.75f), fontSize = 11.sp)
                                Text(
                                    CurrencyFormatter.formatRupiah(analytics.averageTicket),
                                    color = TokokuhWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Column {
                                Text("Item Terjual", color = TokokuhWhite.copy(alpha = 0.75f), fontSize = 11.sp)
                                Text(
                                    "${analytics.totalItemsSold} porsi",
                                    color = TokokuhWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Payment Methods Breakdown
            item {
                Text(
                    text = "Metode Pembayaran",
                    fontWeight = FontWeight.Bold,
                    color = TokokuhTextPrimary,
                    fontSize = 16.sp
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhBorder))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        analytics.paymentBreakdowns.forEach { pb ->
                            val percent = if (analytics.totalRevenue > 0) {
                                (pb.totalAmount.toFloat() / analytics.totalRevenue.toFloat()).coerceIn(0f, 1f)
                            } else 0f

                            val icon = when (pb.method) {
                                PaymentMethod.CASH -> Icons.Default.Payments
                                PaymentMethod.QRIS -> Icons.Default.QrCode
                                PaymentMethod.TRANSFER, PaymentMethod.DEBIT -> Icons.Default.CreditCard
                            }

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = TokokuhMaroonPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = pb.method.name,
                                            fontWeight = FontWeight.Bold,
                                            color = TokokuhTextPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = " (${pb.count}x)",
                                            color = TokokuhTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Text(
                                        text = CurrencyFormatter.formatRupiah(pb.totalAmount),
                                        fontWeight = FontWeight.Bold,
                                        color = TokokuhTextPrimary,
                                        fontSize = 13.sp
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { percent },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = TokokuhMaroonPrimary,
                                    trackColor = TokokuhSurface
                                )
                            }
                        }
                    }
                }
            }

            // Top Selling Menus
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "5 Menu Terlaris",
                        fontWeight = FontWeight.Bold,
                        color = TokokuhTextPrimary,
                        fontSize = 16.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = null,
                        tint = TokokuhMaroonPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (analytics.topMenus.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Belum ada transaksi pada periode ${selectedPeriod.label}.",
                            color = TokokuhTextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            } else {
                items(analytics.topMenus.mapIndexed { index, item -> Pair(index + 1, item) }) { (rank, item) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = TokokuhWhite),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(TokokuhBorder))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (rank == 1) TokokuhAccentYellow else TokokuhSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$rank",
                                    fontWeight = FontWeight.Bold,
                                    color = if (rank == 1) TokokuhMaroonDark else TokokuhTextPrimary,
                                    fontSize = 12.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.menuName,
                                    fontWeight = FontWeight.Bold,
                                    color = TokokuhTextPrimary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${item.totalQty} porsi terjual",
                                    color = TokokuhTextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = CurrencyFormatter.formatRupiah(item.totalRevenue),
                                fontWeight = FontWeight.Bold,
                                color = TokokuhMaroonPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Offline computation badge
            item {
                Surface(
                    color = TokokuhSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = TokokuhSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Laporan dihitung 100% lokal dari SQLite Room Database tanpa ketergantungan internet.",
                            color = TokokuhTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
