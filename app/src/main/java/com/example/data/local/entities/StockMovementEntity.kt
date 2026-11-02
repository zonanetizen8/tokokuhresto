package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class MovementType(val displayName: String) {
    IN("Stok Masuk"),
    OUT("Stok Keluar / Terjual"),
    ADJUSTMENT("Penyesuaian Opname"),
    WASTE("Kerusakan / Basi")
}

@Entity(
    tableName = "stock_movements",
    foreignKeys = [
        ForeignKey(
            entity = IngredientEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("ingredientId"),
        Index("timestamp")
    ]
)
data class StockMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ingredientId: Long,
    val type: MovementType,
    val quantity: Double,
    val previousStock: Double,
    val currentStock: Double,
    val note: String = "",
    val recordedBy: String = "Sistem",
    val timestamp: Long = System.currentTimeMillis()
)
