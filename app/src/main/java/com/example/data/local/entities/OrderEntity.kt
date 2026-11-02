package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class OrderType(val displayName: String) {
    DINE_IN("Dine In"),
    TAKE_AWAY("Take Away"),
    DIRECT("Pesanan Langsung"),
    SCHEDULED("Pesanan Terjadwal")
}

enum class OrderStatus(val displayName: String) {
    PENDING("Pesanan Masuk"),
    COOKING("Sedang Dimasak"),
    READY("Siap Disajikan"),
    SERVED("Sudang Disajikan"),
    COMPLETED("Selesai"),
    CANCELLED("Dibatalkan")
}

enum class PaymentStatus(val displayName: String) {
    UNPAID("Belum Lunas"),
    PARTIAL("Sebagian"),
    PAID("Lunas")
}

enum class PaymentMethod(val displayName: String) {
    CASH("Tunai"),
    QRIS("QRIS"),
    DEBIT("Kartu Debit"),
    TRANSFER("Transfer Bank")
}

@Entity(
    tableName = "orders",
    indices = [
        Index("orderNumber", unique = true),
        Index("createdAt"),
        Index("status"),
        Index("tableId")
    ]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String, // e.g. "ORD-20261102-001"
    val orderType: OrderType = OrderType.DINE_IN,
    val tableId: Long? = null,
    val tableName: String? = null,
    val customerName: String = "Pelanggan",
    val customerPhone: String = "",
    val cashierId: Long = 0,
    val cashierName: String = "Kasir",
    val status: OrderStatus = OrderStatus.PENDING,
    val subtotal: Long = 0L,
    val discountAmount: Long = 0L,
    val discountPercent: Double = 0.0,
    val taxAmount: Long = 0L,
    val taxPercent: Double = 0.0,
    val serviceAmount: Long = 0L,
    val servicePercent: Double = 0.0,
    val totalAmount: Long = 0L,
    val paidAmount: Long = 0L,
    val changeAmount: Long = 0L,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val note: String = "",
    val cancelReason: String = "",
    val shiftId: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
