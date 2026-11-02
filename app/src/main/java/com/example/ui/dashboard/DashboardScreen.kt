package com.example.ui.dashboard

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TableBar
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.dao.TopSellerItem
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.UserEntity
import com.example.data.util.CurrencyFormatter
import com.example.ui.navigation.Screen
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    currentUser: UserEntity?,
    onNavigateTo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val todayRevenue by viewModel.todayRevenue.collectAsStateWithLifecycle()
    val todayOrderCount by viewModel.todayOrderCount.collectAsStateWithLifecycle()
    val weeklyRevenue by viewModel.weeklyRevenue.collectAsStateWithLifecycle()
    val monthlyRevenue by viewModel.monthlyRevenue.collectAsStateWithLifecycle()
    val activeMenuCount by viewModel.activeMenuCount.collectAsStateWithLifecycle()
    val occupiedTableCount by viewModel.occupiedTableCount.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val topSellingMenus by viewModel.topSellingMenus.collectAsStateWithLifecycle()
    val recentOrders by viewModel.recentOrders.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = TokokuhWhite,
                modifier = Modifier.width(310.dp)
            ) {
                // Official Drawer Header: Deep Maroon #720000 background
                // Ensures the white and yellow logo text pops clearly!
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TokokuhMaroonPrimary)
                        .padding(horizontal = 20.dp, vertical = 22.dp)
                ) {
                    Column {
                        Image(
                            painter = painterResource(id = R.drawable.logo_tokokuh_resto),
                            contentDescription = "TOKOKUH.COM RESTO",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth(0.9f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = TokokuhMaroonDark
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = TokokuhAccentYellow,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "100% OFFLINE",
                                        color = TokokuhWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "v1.0 • SQLite Room",
                                color = TokokuhWhite.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                HorizontalDivider(color = TokokuhBorder)

                // 15 Standard Navigation Drawer Items in Exact Order Requested by User
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // 1. Dashboard
                    item {
                        OfficialDrawerItem(
                            number = 1,
                            icon = Icons.Default.Assessment,
                            title = "Dashboard",
                            isSelected = true,
                            onClick = { scope.launch { drawerState.close() } }
                        )
                    }
                    // 2. Kasir (POS)
                    item {
                        OfficialDrawerItem(
                            number = 2,
                            icon = Icons.Default.PointOfSale,
                            title = "Kasir (POS)",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Cashier.route)
                                }
                            }
                        )
                    }
                    // 3. Meja Restoran
                    item {
                        OfficialDrawerItem(
                            number = 3,
                            icon = Icons.Default.TableBar,
                            title = "Meja Restoran",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Tables.route)
                                }
                            }
                        )
                    }
                    // 4. Dapur (Kitchen Display)
                    item {
                        OfficialDrawerItem(
                            number = 4,
                            icon = Icons.Default.Restaurant,
                            title = "Dapur (Kitchen Display)",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Kitchen.route)
                                }
                            }
                        )
                    }
                    // 5. Kelola Menu
                    item {
                        OfficialDrawerItem(
                            number = 5,
                            icon = Icons.Default.Fastfood,
                            title = "Kelola Menu",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.MenuManagement.route)
                                }
                            }
                        )
                    }
                    // 6. Kategori Menu
                    item {
                        OfficialDrawerItem(
                            number = 6,
                            icon = Icons.Default.Category,
                            title = "Kategori Menu",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.CategoryMenu.route)
                                }
                            }
                        )
                    }
                    // 7. Inventaris
                    item {
                        OfficialDrawerItem(
                            number = 7,
                            icon = Icons.Default.Inventory,
                            title = "Inventaris",
                            badgeText = if (lowStockCount > 0) "$lowStockCount" else null,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Inventory.route)
                                }
                            }
                        )
                    }
                    // 8. Karyawan
                    item {
                        OfficialDrawerItem(
                            number = 8,
                            icon = Icons.Default.People,
                            title = "Karyawan",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Employees.route)
                                }
                            }
                        )
                    }
                    // 9. Shift dan Kas
                    item {
                        OfficialDrawerItem(
                            number = 9,
                            icon = Icons.Default.Schedule,
                            title = "Shift dan Kas",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Shifts.route)
                                }
                            }
                        )
                    }
                    // 10. Transaksi
                    item {
                        OfficialDrawerItem(
                            number = 10,
                            icon = Icons.AutoMirrored.Filled.ReceiptLong,
                            title = "Transaksi",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Transactions.route)
                                }
                            }
                        )
                    }
                    // 11. Laporan Penjualan
                    item {
                        OfficialDrawerItem(
                            number = 11,
                            icon = Icons.Default.ReceiptLong,
                            title = "Laporan Penjualan",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Reports.route)
                                }
                            }
                        )
                    }
                    // 12. Pengeluaran
                    item {
                        OfficialDrawerItem(
                            number = 12,
                            icon = Icons.Default.Payments,
                            title = "Pengeluaran",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Expenses.route)
                                }
                            }
                        )
                    }
                    // 13. Backup dan Restore
                    item {
                        OfficialDrawerItem(
                            number = 13,
                            icon = Icons.Default.Backup,
                            title = "Backup dan Restore",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.BackupRestore.route)
                                }
                            }
                        )
                    }
                    // 14. Pengaturan
                    item {
                        OfficialDrawerItem(
                            number = 14,
                            icon = Icons.Default.Settings,
                            title = "Pengaturan",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.Settings.route)
                                }
                            }
                        )
                    }
                    // 15. Tentang TOKOKUH RESTO
                    item {
                        OfficialDrawerItem(
                            number = 15,
                            icon = Icons.Default.Info,
                            title = "Tentang TOKOKUH RESTO",
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    onNavigateTo(Screen.About.route)
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                // Official Top App Bar: Deep Maroon #720000 background
                // Featuring the official TOKOKUH RESTO image logo directly in the header!
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_tokokuh_resto),
                                contentDescription = "TOKOKUH RESTO",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .height(34.dp)
                                    .widthIn(max = 165.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = TokokuhMaroonDark
                            ) {
                                Text(
                                    text = "100% Offline POS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TokokuhAccentYellow,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Buka Menu",
                                tint = TokokuhWhite
                            )
                        }
                    },
                    actions = {
                        Surface(
                            shape = CircleShape,
                            color = TokokuhMaroonDark,
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(TokokuhAccentYellow)
                                )
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
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // 1. Kartu Omzet Hari Ini (Hero Card Deep Maroon #720000)
                item {
                    TodayRevenueHeroCard(
                        revenue = todayRevenue,
                        orderCount = todayOrderCount
                    )
                }

                // 2. Tombol Akses Cepat (Kasir, Meja, Dapur, Menu)
                item {
                    Text(
                        text = "AKSES CEPAT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhTextSecondary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                    )
                    QuickActionGrid(
                        onOpenCashier = { onNavigateTo(Screen.Cashier.route) },
                        onOpenTables = { onNavigateTo(Screen.Tables.route) },
                        onOpenKitchen = { onNavigateTo(Screen.Kitchen.route) },
                        onOpenMenu = { onNavigateTo(Screen.MenuManagement.route) }
                    )
                }

                // 3. Kartu Statistik Tertata Rapi
                item {
                    Text(
                        text = "RINGKASAN OPERASIONAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhTextSecondary,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Menu Aktif",
                            value = "$activeMenuCount",
                            subtitle = "Item siap saji",
                            icon = Icons.Default.Fastfood,
                            iconColor = TokokuhMaroonPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Meja Terisi",
                            value = "$occupiedTableCount / 10",
                            subtitle = if (occupiedTableCount > 0) "Sedang aktif" else "Semua kosong",
                            icon = Icons.Default.TableBar,
                            iconColor = if (occupiedTableCount > 0) TokokuhMaroonPrimary else TokokuhTextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            title = "Stok Menipis",
                            value = "$lowStockCount",
                            subtitle = if (lowStockCount > 0) "Perlu restock!" else "Aman",
                            icon = Icons.Default.Warning,
                            iconColor = if (lowStockCount > 0) TokokuhWarning else TokokuhSuccess,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Transaksi Hari Ini",
                            value = "$todayOrderCount",
                            subtitle = "Selesai",
                            icon = Icons.Default.ReceiptLong,
                            iconColor = TokokuhSuccess,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 4. Ringkasan Pendapatan Mingguan & Bulanan
                item {
                    RevenueComparisonCard(
                        weeklyRevenue = weeklyRevenue,
                        monthlyRevenue = monthlyRevenue
                    )
                }

                // 5. Menu Paling Laris
                item {
                    TopSellingMenuCard(topSellers = topSellingMenus)
                }

                // 6. Transaksi Terakhir
                item {
                    RecentOrdersCard(
                        orders = recentOrders,
                        onViewAllOrders = { onNavigateTo(Screen.Transactions.route) }
                    )
                }

                // 7. Footer TOKOKUH.COM (dengan latar merah marun kartu agar logo terlihat jelas)
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = TokokuhMaroonPrimary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_tokokuh_resto),
                                contentDescription = "TOKOKUH.COM RESTO",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .height(30.dp)
                                    .fillMaxWidth(0.6f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Aplikasi Kasir dan Manajemen Restoran Tanpa Internet",
                                fontSize = 11.sp,
                                color = TokokuhWhite.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun OfficialDrawerItem(
    number: Int,
    icon: ImageVector,
    title: String,
    isSelected: Boolean = false,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                icon,
                contentDescription = title,
                tint = if (isSelected) TokokuhWhite else TokokuhTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) TokokuhWhite else TokokuhTextPrimary
                )
                badgeText?.let {
                    Surface(
                        shape = CircleShape,
                        color = TokokuhWarning,
                        modifier = Modifier.size(18.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = it,
                                fontSize = 9.sp,
                                color = TokokuhWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = TokokuhMaroonPrimary,
            unselectedContainerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.height(44.dp)
    )
}

@Composable
private fun TodayRevenueHeroCard(
    revenue: Long,
    orderCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhMaroonPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = TokokuhAccentYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OMZET PENJUALAN HARI INI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhWhite.copy(alpha = 0.9f),
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TokokuhMaroonDark
                ) {
                    Text(
                        text = "$orderCount Transaksi",
                        color = TokokuhAccentYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = CurrencyFormatter.formatRupiah(revenue),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TokokuhWhite
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tersimpan 100% lokal • Aman tanpa internet",
                fontSize = 11.sp,
                color = TokokuhWhite.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuickActionGrid(
    onOpenCashier: () -> Unit,
    onOpenTables: () -> Unit,
    onOpenKitchen: () -> Unit,
    onOpenMenu: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        QuickActionButton(
            title = "Kasir (POS)",
            icon = Icons.Default.PointOfSale,
            modifier = Modifier.weight(1f),
            onClick = onOpenCashier
        )
        QuickActionButton(
            title = "Meja",
            icon = Icons.Default.TableBar,
            modifier = Modifier.weight(1f),
            onClick = onOpenTables
        )
        QuickActionButton(
            title = "Dapur",
            icon = Icons.Default.Restaurant,
            modifier = Modifier.weight(1f),
            onClick = onOpenKitchen
        )
        QuickActionButton(
            title = "Menu",
            icon = Icons.Default.Fastfood,
            modifier = Modifier.weight(1f),
            onClick = onOpenMenu
        )
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        color = TokokuhSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = TokokuhMaroonPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TokokuhTextPrimary
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = TokokuhTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TokokuhTextPrimary
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = iconColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RevenueComparisonCard(
    weeklyRevenue: Long,
    monthlyRevenue: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Ringkasan Pendapatan",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TokokuhMaroonDark
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Minggu Ini",
                        fontSize = 12.sp,
                        color = TokokuhTextSecondary
                    )
                    Text(
                        text = CurrencyFormatter.formatRupiah(weeklyRevenue),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhTextPrimary
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bulan Ini",
                        fontSize = 12.sp,
                        color = TokokuhTextSecondary
                    )
                    Text(
                        text = CurrencyFormatter.formatRupiah(monthlyRevenue),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhTextPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun TopSellingMenuCard(
    topSellers: List<TopSellerItem>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = TokokuhAccentYellow,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Menu Paling Laris",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TokokuhMaroonDark
                    )
                }
                Text(
                    text = "Top 5",
                    fontSize = 11.sp,
                    color = TokokuhTextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (topSellers.isEmpty()) {
                Text(
                    text = "Belum ada pesanan diselesaikan.",
                    fontSize = 12.sp,
                    color = TokokuhTextSecondary,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            } else {
                topSellers.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (index == 0) TokokuhMaroonPrimary else TokokuhBorder,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (index == 0) TokokuhWhite else TokokuhTextPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = item.menuName,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TokokuhTextPrimary
                            )
                        }

                        Text(
                            text = "${item.totalQty} porsi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TokokuhMaroonDark
                        )
                    }
                    if (index < topSellers.lastIndex) {
                        HorizontalDivider(color = TokokuhBorder)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentOrdersCard(
    orders: List<OrderEntity>,
    onViewAllOrders: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = TokokuhSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, TokokuhBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi Terakhir",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TokokuhMaroonDark
                )
                Text(
                    text = "Lihat Semua",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TokokuhMaroonPrimary,
                    modifier = Modifier.clickable { onViewAllOrders() }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (orders.isEmpty()) {
                Text(
                    text = "Belum ada transaksi.",
                    fontSize = 12.sp,
                    color = TokokuhTextSecondary,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            } else {
                orders.forEachIndexed { index, order ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = order.orderNumber,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TokokuhTextPrimary
                            )
                            Text(
                                text = "${order.tableName ?: order.orderType.displayName} • ${order.cashierName}",
                                fontSize = 11.sp,
                                color = TokokuhTextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = CurrencyFormatter.formatRupiah(order.totalAmount),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TokokuhMaroonPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (order.status) {
                                    OrderStatus.COMPLETED -> TokokuhSuccessBg
                                    OrderStatus.COOKING -> TokokuhWarningBg
                                    OrderStatus.CANCELLED -> TokokuhErrorBg
                                    else -> TokokuhSuccessBg
                                }
                            ) {
                                Text(
                                    text = order.status.displayName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = when (order.status) {
                                        OrderStatus.COMPLETED -> TokokuhSuccess
                                        OrderStatus.COOKING -> TokokuhWarning
                                        OrderStatus.CANCELLED -> TokokuhError
                                        else -> TokokuhSuccess
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    if (index < orders.lastIndex) {
                        HorizontalDivider(color = TokokuhBorder)
                    }
                }
            }
        }
    }
}
