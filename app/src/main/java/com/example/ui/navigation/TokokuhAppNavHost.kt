package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppContainer
import com.example.ui.about.AboutScreen
import com.example.ui.backup.BackupRestoreScreen
import com.example.ui.backup.BackupViewModel
import com.example.ui.cashier.CashierScreen
import com.example.ui.cashier.CashierViewModel
import com.example.ui.category.CategoryScreen
import com.example.ui.category.CategoryViewModel
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.dashboard.DashboardViewModel
import com.example.ui.employee.EmployeeScreen
import com.example.ui.employee.EmployeeViewModel
import com.example.ui.expense.ExpenseScreen
import com.example.ui.expense.ExpenseViewModel
import com.example.ui.inventory.InventoryScreen
import com.example.ui.inventory.InventoryViewModel
import com.example.ui.kitchen.KitchenDisplayScreen
import com.example.ui.kitchen.KitchenViewModel
import com.example.ui.menu.MenuManagementScreen
import com.example.ui.menu.MenuViewModel
import com.example.ui.report.ReportViewModel
import com.example.ui.report.SalesReportScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.settings.SettingsViewModel
import com.example.ui.shift.ShiftScreen
import com.example.ui.shift.ShiftViewModel
import com.example.ui.tables.TableManagementScreen
import com.example.ui.tables.TableViewModel
import com.example.ui.transaction.TransactionHistoryScreen
import com.example.ui.transaction.TransactionViewModel

@Composable
fun TokokuhAppNavHost(
    container: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current

    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(container.dashboardRepository)
    )
    val cashierViewModel: CashierViewModel = viewModel(
        factory = CashierViewModel.Factory(
            menuDao = container.database.menuDao(),
            restaurantRepository = container.restaurantRepository
        )
    )
    val tableViewModel: TableViewModel = viewModel(
        factory = TableViewModel.Factory(container.restaurantRepository)
    )
    val kitchenViewModel: KitchenViewModel = viewModel(
        factory = KitchenViewModel.Factory(container.restaurantRepository)
    )
    val menuViewModel: MenuViewModel = viewModel(
        factory = MenuViewModel.Factory(
            menuDao = container.database.menuDao(),
            categoryDao = container.database.categoryDao()
        )
    )
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModel.Factory(container.restaurantRepository)
    )
    val inventoryViewModel: InventoryViewModel = viewModel(
        factory = InventoryViewModel.Factory(container.restaurantRepository)
    )
    val employeeViewModel: EmployeeViewModel = viewModel(
        factory = EmployeeViewModel.Factory(container.restaurantRepository)
    )
    val shiftViewModel: ShiftViewModel = viewModel(
        factory = ShiftViewModel.Factory(container.restaurantRepository)
    )
    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModel.Factory(container.restaurantRepository)
    )
    val reportViewModel: ReportViewModel = viewModel(
        factory = ReportViewModel.Factory(container.restaurantRepository)
    )
    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModel.Factory(container.restaurantRepository)
    )
    val backupViewModel: BackupViewModel = viewModel(
        factory = BackupViewModel.Factory(context, container.restaurantRepository)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.Factory(container.restaurantRepository)
    )

    // Mandate: Launch directly to Dashboard without internet or mandatory login blockers
    val startDestination = Screen.Dashboard.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // 1. Dashboard
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                currentUser = null,
                onNavigateTo = { route ->
                    navController.navigate(route)
                }
            )
        }

        // 2. Kasir (POS)
        composable(Screen.Cashier.route) {
            CashierScreen(
                viewModel = cashierViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 3. Meja Restoran
        composable(Screen.Tables.route) {
            TableManagementScreen(
                viewModel = tableViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 4. Dapur (Kitchen Display)
        composable(Screen.Kitchen.route) {
            KitchenDisplayScreen(
                viewModel = kitchenViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 5. Kelola Menu
        composable(Screen.MenuManagement.route) {
            MenuManagementScreen(
                viewModel = menuViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 6. Kategori Menu
        composable(Screen.CategoryMenu.route) {
            CategoryScreen(
                viewModel = categoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 7. Inventaris
        composable(Screen.Inventory.route) {
            InventoryScreen(
                viewModel = inventoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 8. Karyawan
        composable(Screen.Employees.route) {
            EmployeeScreen(
                viewModel = employeeViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 9. Shift dan Kas
        composable(Screen.Shifts.route) {
            ShiftScreen(
                viewModel = shiftViewModel,
                currentUser = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 10. Transaksi
        composable(Screen.Transactions.route) {
            TransactionHistoryScreen(
                viewModel = transactionViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 11. Laporan Penjualan
        composable(Screen.Reports.route) {
            SalesReportScreen(
                viewModel = reportViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 12. Pengeluaran
        composable(Screen.Expenses.route) {
            ExpenseScreen(
                viewModel = expenseViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 13. Backup dan Restore
        composable(Screen.BackupRestore.route) {
            BackupRestoreScreen(
                viewModel = backupViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 14. Pengaturan
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 15. Tentang TOKOKUH RESTO
        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

    }
}
