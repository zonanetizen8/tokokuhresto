package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ingredients",
    indices = [Index("code", unique = true)]
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // e.g. "BHN-001"
    val name: String, // e.g. "Beras Ramos", "Minyak Goreng"
    val stock: Double = 0.0,
    val unit: String = "Kg", // "Kg", "Gram", "Liter", "Pcs"
    val buyPrice: Long = 0L,
    val minStock: Double = 5.0,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isLowStock: Boolean
        get() = stock <= minStock
}
