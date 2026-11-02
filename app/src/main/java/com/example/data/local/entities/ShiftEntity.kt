package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ShiftStatus {
    OPEN,
    CLOSED
}

@Entity(tableName = "shifts")
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val userName: String,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val startingCash: Long = 0L,     // Modal awal kasir
    val expectedCash: Long = 0L,     // Modal awal + total penerimaan tunai - pengeluaran kas
    val actualCash: Long = 0L,       // Uang fisik yang dihitung
    val difference: Long = 0L,       // actualCash - expectedCash (selisih)
    val totalCashSales: Long = 0L,
    val totalNonCashSales: Long = 0L,
    val totalExpense: Long = 0L,
    val transactionCount: Int = 0,
    val note: String = "",
    val status: ShiftStatus = ShiftStatus.OPEN
)
