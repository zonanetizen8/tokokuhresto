package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TableStatus(val displayName: String) {
    VACANT("Kosong"),
    OCCUPIED("Terisi"),
    WAITING_ORDER("Menunggu Pesanan"),
    SERVING("Sedang Dilayani"),
    WAITING_PAYMENT("Menunggu Pembayaran")
}

@Entity(
    tableName = "tables",
    indices = [Index("tableNumber", unique = true)]
)
data class TableEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tableNumber: String, // e.g. "Meja 01", "Meja 02"
    val capacity: Int = 4,
    val status: TableStatus = TableStatus.VACANT,
    val currentOrderId: Long? = null,
    val note: String = ""
)
