package com.example.ui.navigation

sealed class Screen(val route: String) {
    // 15 Standard Navigation Drawer items in exact user-specified order:
    object Dashboard : Screen("dashboard")
    object Cashier : Screen("cashier")
    object Tables : Screen("tables")
    object Kitchen : Screen("kitchen")
    object MenuManagement : Screen("menu_management")
    object CategoryMenu : Screen("category_menu")
    object Inventory : Screen("inventory")
    object Employees : Screen("employees")
    object Shifts : Screen("shifts")
    object Transactions : Screen("transactions")
    object Reports : Screen("reports")
    object Expenses : Screen("expenses")
    object BackupRestore : Screen("backup_restore")
    object Settings : Screen("settings")
    object About : Screen("about")

    // Future internal sub-screens can be added here when implemented.
}
