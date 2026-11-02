package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.local.entities.KitchenItemStatus
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.OrderStatus
import com.example.data.local.entities.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Transaction
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    fun getOrderWithItemsById(orderId: Long): Flow<OrderWithItems?>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderWithItemsByIdSync(orderId: Long): OrderWithItems?

    @Transaction
    @Query("SELECT * FROM orders WHERE status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY createdAt ASC")
    fun getActiveKitchenOrders(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt ASC")
    fun getOrdersByStatus(status: OrderStatus): Flow<List<OrderWithItems>>

    @Query("SELECT * FROM orders WHERE createdAt >= :startTime AND createdAt <= :endTime ORDER BY createdAt DESC")
    fun getOrdersBetween(startTime: Long, endTime: Long): Flow<List<OrderEntity>>

    @Query("SELECT COALESCE(SUM(totalAmount), 0) FROM orders WHERE paymentStatus = 'PAID' AND createdAt >= :startTime AND createdAt <= :endTime")
    fun getRevenueBetween(startTime: Long, endTime: Long): Flow<Long>

    @Query("SELECT COUNT(*) FROM orders WHERE paymentStatus = 'PAID' AND createdAt >= :startTime AND createdAt <= :endTime")
    fun getTransactionCountBetween(startTime: Long, endTime: Long): Flow<Int>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentOrders(limit: Int = 5): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItemEntity>)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, completedAt = :completedAt WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus, completedAt: Long? = null)

    @Query("UPDATE order_items SET kitchenStatus = :status WHERE id = :itemId")
    suspend fun updateOrderItemStatus(itemId: Long, status: KitchenItemStatus)

    @Query("SELECT menuName, SUM(quantity) as totalQty FROM order_items GROUP BY menuId ORDER BY totalQty DESC LIMIT :limit")
    fun getTopSellingMenus(limit: Int = 5): Flow<List<TopSellerItem>>

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getOrderCount(): Int
}

data class TopSellerItem(
    val menuName: String,
    val totalQty: Int
)
