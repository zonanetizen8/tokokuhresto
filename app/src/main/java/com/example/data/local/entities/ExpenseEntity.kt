package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [Index("date"), Index("category")]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String, // "Bahan Baku", "Listrik & Air", "Gaji Karyawan", "Operasional", "Lainnya"
    val amount: Long,     // Using Long for money calculations
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val shiftId: Long? = null,
    val createdBy: String = "Kasir"
)
