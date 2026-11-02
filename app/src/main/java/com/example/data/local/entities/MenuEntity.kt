package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "menus",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("categoryId"),
        Index("code", unique = true)
    ]
)
data class MenuEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // Automatic code e.g. "MNU-001"
    val name: String,
    val description: String = "",
    val categoryId: Long?,
    val buyPrice: Long = 0L,   // Harga Beli (Rp) using Long for exact money calculation
    val sellPrice: Long = 0L,  // Harga Jual (Rp) using Long
    val stock: Double = 999.0, // Available portion count or quantity
    val unit: String = "Porsi",
    val isAvailable: Boolean = true,
    val isFavorite: Boolean = false,
    val imagePath: String? = null, // Local absolute path inside internal storage filesDir/menu_images
    val createdAt: Long = System.currentTimeMillis()
) {
    val profitPerUnit: Long
        get() = sellPrice - buyPrice
}
