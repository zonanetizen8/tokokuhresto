package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class KitchenItemStatus(val displayName: String) {
    PENDING("Menunggu"),
    COOKING("Dimasak"),
    READY("Siap"),
    SERVED("Disajikan")
}

@Entity(
    tableName = "order_items",
    foreignKeys = [
        ForeignKey(
            entity = OrderEntity::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("orderId"),
        Index("menuId")
    ]
)
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val menuId: Long,
    val menuName: String,
    val quantity: Int,
    val unitPrice: Long,   // Unit sell price at time of order
    val buyPrice: Long,    // Unit buy price for calculating exact profit later
    val discount: Long = 0L,
    val subtotal: Long,    // (unitPrice * quantity) - discount
    val note: String = "",
    val kitchenStatus: KitchenItemStatus = KitchenItemStatus.PENDING
)
