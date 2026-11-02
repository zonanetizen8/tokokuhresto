package com.example.data.repository

import androidx.room.withTransaction
import com.example.data.local.AppDatabase
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.ExpenseEntity
import com.example.data.local.entities.IngredientEntity
import com.example.data.local.entities.KitchenItemStatus
import com.example.data.local.entities.MovementType
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderWithItems
import com.example.data.local.entities.PaymentStatus
import com.example.data.local.entities.RestaurantProfileEntity
import com.example.data.local.entities.ShiftEntity
import com.example.data.local.entities.ShiftStatus
import com.example.data.local.entities.StockMovementEntity
import com.example.data.local.entities.TableEntity
import com.example.data.local.entities.TableStatus
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class RestaurantRepository(private val database: AppDatabase) {

    private val orderDao = database.orderDao()
    private val tableDao = database.tableDao()
    private val menuDao = database.menuDao()
    private val categoryDao = database.categoryDao()
    private val stockDao = database.stockDao()
    private val shiftExpenseDao = database.shiftExpenseDao()
    private val profileDao = database.restaurantProfileDao()
    private val userDao = database.userDao()

    // ==================== CASHIER & CHECKOUT ====================
    suspend fun checkoutTransaction(
        order: OrderEntity,
        items: List<OrderItemEntity>
    ): Long = database.withTransaction {
        val orderId = orderDao.insertOrder(order)
        val itemsWithId = items.map { it.copy(orderId = orderId) }
        orderDao.insertOrderItems(itemsWithId)

        // Deduct menu stock
        for (item in items) {
            menuDao.reduceStock(item.menuId, item.quantity.toDouble())

            // Recipe auto-deduction if available
            val recipes = stockDao.getRecipesForMenu(item.menuId).firstOrNull() ?: emptyList()
            for (rec in recipes) {
                val deduction = rec.amountRequired * item.quantity
                stockDao.adjustStock(rec.ingredientId, -deduction)
                val ing = stockDao.getIngredientById(rec.ingredientId)
                if (ing != null) {
                    stockDao.insertStockMovement(
                        StockMovementEntity(
                            ingredientId = rec.ingredientId,
                            type = MovementType.OUT,
                            quantity = deduction,
                            previousStock = ing.stock + deduction,
                            currentStock = ing.stock,
                            note = "Terjual: ${item.menuName} (${item.quantity} porsi)",
                            recordedBy = order.cashierName
                        )
                    )
                }
            }
        }

        // Update table if dine in
        order.tableId?.let { tableId ->
            val status = if (order.paymentStatus == PaymentStatus.PAID) {
                TableStatus.OCCUPIED // Occupied eating, will clear when left
            } else {
                TableStatus.WAITING_PAYMENT
            }
            tableDao.updateTableStatus(tableId, status, orderId)
        }

        orderId
    }

    fun getAllOrders(): Flow<List<OrderWithItems>> = orderDao.getAllOrders()

    fun getOrderWithItems(id: Long): Flow<OrderWithItems?> = orderDao.getOrderWithItemsById(id)

    suspend fun cancelOrder(orderId: Long, reason: String) {
        orderDao.updateOrderStatus(orderId, OrderStatus.CANCELLED, System.currentTimeMillis())
        val order = orderDao.getOrderWithItemsByIdSync(orderId)
        order?.order?.tableId?.let { tableId ->
            tableDao.updateTableStatus(tableId, TableStatus.VACANT, null)
        }
    }

    // ==================== TABLES ====================
    fun getAllTables(): Flow<List<TableEntity>> = tableDao.getAllTables()

    suspend fun insertTable(table: TableEntity): Long = tableDao.insertTable(table)

    suspend fun updateTable(table: TableEntity) = tableDao.updateTable(table)

    suspend fun deleteTable(id: Long) = tableDao.deleteTable(id)

    suspend fun updateTableStatus(tableId: Long, status: TableStatus, orderId: Long?) =
        tableDao.updateTableStatus(tableId, status, orderId)

    suspend fun clearTable(tableId: Long) {
        tableDao.updateTableStatus(tableId, TableStatus.VACANT, null)
    }

    suspend fun moveTable(fromTableId: Long, toTableId: Long) = database.withTransaction {
        val from = tableDao.getTableById(fromTableId).firstOrNull() ?: return@withTransaction
        val orderId = from.currentOrderId ?: return@withTransaction
        tableDao.updateTableStatus(fromTableId, TableStatus.VACANT, null)
        tableDao.updateTableStatus(toTableId, from.status, orderId)
    }

    // ==================== KITCHEN DISPLAY ====================
    fun getActiveKitchenOrders(): Flow<List<OrderWithItems>> = orderDao.getActiveKitchenOrders()

    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus) {
        val completedAt = if (status == OrderStatus.COMPLETED) System.currentTimeMillis() else null
        orderDao.updateOrderStatus(orderId, status, completedAt)
    }

    suspend fun updateOrderItemStatus(itemId: Long, status: KitchenItemStatus) {
        orderDao.updateOrderItemStatus(itemId, status)
    }

    // ==================== CATEGORIES ====================
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun insertCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(categoryId: Long): Result<Boolean> {
        val inUseCount = categoryDao.getMenuCountForCategory(categoryId)
        if (inUseCount > 0) {
            return Result.failure(Exception("Kategori ini masih digunakan oleh $inUseCount menu makanan/minuman."))
        }
        categoryDao.deleteCategory(categoryId)
        return Result.success(true)
    }

    // ==================== INVENTORY ====================
    fun getAllIngredients(): Flow<List<IngredientEntity>> = stockDao.getAllIngredients()

    fun getLowStockIngredients(): Flow<List<IngredientEntity>> = stockDao.getLowStockIngredients()

    suspend fun insertIngredient(ingredient: IngredientEntity): Long = stockDao.insertIngredient(ingredient)

    suspend fun updateIngredient(ingredient: IngredientEntity) = stockDao.updateIngredient(ingredient)

    suspend fun deleteIngredient(id: Long) = stockDao.deleteIngredient(id)

    suspend fun adjustStock(
        ingredientId: Long,
        qty: Double,
        type: MovementType,
        note: String,
        recordedBy: String
    ) = database.withTransaction {
        val ing = stockDao.getIngredientById(ingredientId) ?: return@withTransaction
        val change = if (type == MovementType.OUT || type == MovementType.WASTE) -qty else qty
        stockDao.adjustStock(ingredientId, change)
        stockDao.insertStockMovement(
            StockMovementEntity(
                ingredientId = ingredientId,
                type = type,
                quantity = qty,
                previousStock = ing.stock,
                currentStock = ing.stock + change,
                note = note,
                recordedBy = recordedBy
            )
        )
    }

    // ==================== EMPLOYEES ====================
    fun getAllUsers(): Flow<List<UserEntity>> = userDao.getAllUsers()

    suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)

    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)

    suspend fun deleteUser(id: Long) = userDao.deleteUser(id)

    // ==================== SHIFTS & EXPENSES ====================
    fun getActiveShift(): Flow<ShiftEntity?> = shiftExpenseDao.getActiveShift()

    fun getAllShifts(): Flow<List<ShiftEntity>> = shiftExpenseDao.getAllShifts()

    suspend fun openShift(userId: Long, userName: String, startingCash: Long): Long {
        val shift = ShiftEntity(
            userId = userId,
            userName = userName,
            startTime = System.currentTimeMillis(),
            startingCash = startingCash,
            expectedCash = startingCash,
            actualCash = 0L,
            difference = 0L,
            status = ShiftStatus.OPEN
        )
        return shiftExpenseDao.insertShift(shift)
    }

    suspend fun closeShift(shiftId: Long, actualCash: Long, note: String) {
        val allShifts = shiftExpenseDao.getAllShifts().firstOrNull() ?: emptyList()
        val current = allShifts.firstOrNull { it.id == shiftId } ?: return
        val diff = actualCash - current.expectedCash
        val updated = current.copy(
            endTime = System.currentTimeMillis(),
            actualCash = actualCash,
            difference = diff,
            note = note,
            status = ShiftStatus.CLOSED
        )
        shiftExpenseDao.updateShift(updated)
    }

    fun getAllExpenses(): Flow<List<ExpenseEntity>> = shiftExpenseDao.getAllExpenses()

    suspend fun addExpense(expense: ExpenseEntity): Long = shiftExpenseDao.insertExpense(expense)

    suspend fun deleteExpense(id: Long) = shiftExpenseDao.deleteExpense(id)

    // ==================== SETTINGS & PROFILE ====================
    fun getProfile(): Flow<RestaurantProfileEntity?> = profileDao.getProfile()

    suspend fun getProfileSync(): RestaurantProfileEntity? = profileDao.getProfileSync()

    suspend fun updateProfile(profile: RestaurantProfileEntity) = profileDao.upsertProfile(profile)

    // ==================== STATS FOR BACKUP ====================
    suspend fun getMenuCount(): Int = menuDao.getMenuCount()
    suspend fun getOrderCount(): Int = orderDao.getOrderCount()
    suspend fun getTableCount(): Int = tableDao.getTableCount()
    suspend fun getIngredientCount(): Int = stockDao.getIngredientCount()
}
